package org.telegram.messenger;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.util.Pair;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/** Owns copies until the picker result is handed to the editor. One worker per import. */
public final class SystemPhotoPickerImport implements AutoCloseable {
    private final ContentResolver resolver;
    private final File cacheRoot;
    private final File directory;
    private final long sizeLimit;
    private final CancellationSignal cancellation = new CancellationSignal();
    private final ArrayList<File> files = new ArrayList<>();
    private Closeable activeInput;
    private boolean cancelled;
    private boolean released;

    public SystemPhotoPickerImport(ContentResolver resolver, File cacheRoot) {
        this(resolver, cacheRoot, FileLoader.DEFAULT_MAX_FILE_SIZE_PREMIUM);
    }

    SystemPhotoPickerImport(ContentResolver resolver, File cacheRoot, long sizeLimit) {
        if (sizeLimit <= 0) throw new IllegalArgumentException("Invalid picker size limit");
        this.resolver = resolver;
        this.cacheRoot = cacheRoot;
        this.directory = new File(cacheRoot, "sharing");
        this.sizeLimit = Math.min(sizeLimit, FileLoader.DEFAULT_MAX_FILE_SIZE_PREMIUM);
    }

    public ArrayList<MediaController.PhotoEntry> load(ArrayList<Uri> uris) throws IOException {
        ArrayList<MediaController.PhotoEntry> entries = new ArrayList<>();
        try {
            for (Uri uri : uris) {
                SendMessagesHelper.SendingMediaInfo info = copy(uri);
                Pair<Integer, Integer> orientation = info.isVideo ? new Pair<>(0, 0) : AndroidUtilities.getImageOrientation(info.path);
                MediaController.PhotoEntry entry = new MediaController.PhotoEntry(0, entries.size(), 0,
                        info.path, 0, info.isVideo, 0, 0, new File(info.path).length()).setOrientation(orientation);
                entry.canDeleteAfter = true;
                entries.add(entry);
            }
            return entries;
        } catch (IOException | RuntimeException e) {
            synchronized (this) {
                for (File file : files) file.delete();
                files.clear();
            }
            throw e;
        }
    }

    public SendMessagesHelper.SendingMediaInfo copy(Uri uri) throws IOException {
        cancellation.throwIfCanceled();
        if (!"content".equals(uri.getScheme()) || AndroidUtilities.isInternalUri(uri)) {
            throw new IOException("Invalid picker URI");
        }
        String mime = resolver.getType(uri);
        String name = null;
        try (Cursor cursor = resolver.query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null, cancellation)) {
            if (cursor != null && cursor.moveToFirst()) {
                int column = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (column >= 0) name = cursor.getString(column);
            }
        }
        String extension = extensionFor(mime, name);
        if (mime == null || "application/octet-stream".equals(mime)) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (mime == null || !(mime.startsWith("image/") || mime.startsWith("video/"))) {
            throw new IOException("Unknown picker media type");
        }
        File file;
        synchronized (this) {
            cancellation.throwIfCanceled();
            if (cancelled || released) throw new OperationCanceledException();
            // This is our trusted cache, not an incoming URI. Reject an escaping sharing symlink.
            File root = cacheRoot.getCanonicalFile();
            File target = directory.getCanonicalFile();
            if (!root.equals(target.getParentFile())) throw new IOException("Picker directory outside cache");
            if (!target.isDirectory() && !target.mkdirs()) throw new IOException("Cannot create picker cache");
            file = File.createTempFile("picker_", "." + extension, target);
            files.add(file);
        }
        boolean copied = false;
        try {
            try (AssetFileDescriptor asset = resolver.openAssetFileDescriptor(uri, "r", cancellation)) {
                if (asset == null) throw new IOException("Cannot open picker content");
                activate(asset);
                if (AndroidUtilities.isInternalUri(asset.getParcelFileDescriptor().getFd())) {
                    throw new IOException("Internal picker descriptor");
                }
                if (asset.getLength() > sizeLimit) throw new IOException("Picker content exceeds size limit");
                try (InputStream input = asset.createInputStream(); FileOutputStream output = openOutput(file)) {
                    activate(input);
                    byte[] buffer = new byte[32 * 1024];
                    long copiedBytes = 0;
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        cancellation.throwIfCanceled();
                        // Provider lengths can be unknown or inaccurate. Bound every write without overflow.
                        if (count > sizeLimit - copiedBytes) throw new IOException("Picker content exceeds size limit");
                        output.write(buffer, 0, count);
                        copiedBytes += count;
                    }
                }
            }
            cancellation.throwIfCanceled();
            if (file.length() == 0) throw new IOException("Empty picker content");
            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
            info.path = file.getAbsolutePath();
            info.uri = uri; // Ownership: this is a disposable copy, not the original URI.
            info.isVideo = mime.startsWith("video/");
            copied = true;
            return info;
        } finally {
            synchronized (this) {
                activeInput = null;
                if (!copied || cancelled) {
                    file.delete();
                    files.remove(file);
                }
            }
        }
    }

    private synchronized FileOutputStream openOutput(File file) throws IOException {
        // Cancellation unlinks reserved files. Never recreate one after cancellation.
        if (cancelled) throw new OperationCanceledException();
        return new FileOutputStream(file);
    }

    private void activate(Closeable input) throws IOException {
        synchronized (this) {
            if (!cancelled) {
                activeInput = input;
                return;
            }
        }
        input.close();
        throw new OperationCanceledException();
    }

    public synchronized boolean isCancelled() {
        return cancelled;
    }

    /** Transfer successful copies to the editor/sender, which now owns their lifetime. */
    public synchronized void release() {
        if (cancelled) throw new OperationCanceledException();
        released = true;
        files.clear();
    }

    @Override
    public void close() {
        Closeable input;
        synchronized (this) {
            if (released || cancelled) return;
            cancelled = true;
            input = activeInput;
            activeInput = null;
            for (File file : files) file.delete();
            files.clear();
        }
        try {
            cancellation.cancel(); // Cancels a provider still opening/downloading the URI.
        } finally {
            if (input != null) {
                try { input.close(); } catch (IOException e) { FileLog.e(e); }
            }
        }
    }

    static String extensionFor(String mime, String name) throws IOException {
        String extension = null;
        if (name != null && name.lastIndexOf('.') >= 0) {
            extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String namedType = extension == null ? null : map.getMimeTypeFromExtension(extension);
        if (mime == null || "application/octet-stream".equals(mime)) {
            if (namedType != null && (namedType.startsWith("image/") || namedType.startsWith("video/"))) return extension;
            throw new IOException("No media format for picker content");
        }
        if (mime.equals(namedType)) return extension;
        switch (mime) {
            case "image/heic": return "heic";
            case "image/heif": return "heif";
            case "image/jpeg": return "jpg";
            case "image/png": return "png";
            case "image/webp": return "webp";
            case "video/quicktime": return "mov";
            case "video/mp4": return "mp4";
            default:
                extension = map.getExtensionFromMimeType(mime);
                if (extension != null && (mime.startsWith("image/") || mime.startsWith("video/"))) return extension;
                throw new IOException("Unsupported picker media format");
        }
    }
}

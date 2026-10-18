package org.telegram.messenger;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Pair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, application = Application.class, shadows = {
        SystemPhotoPickerImportTest.ShadowUtilities.class, SystemPhotoPickerMediaTest.ShadowLog.class
})
public class SystemPhotoPickerImportTest {
    private File cache;
    private File cacheRoot;
    private File sources;
    private MediaProvider provider;
    private final ArrayList<SystemPhotoPickerImport> imports = new ArrayList<>();

    @Before public void setUp() throws Exception {
        ApplicationLoader.applicationContext = RuntimeEnvironment.getApplication();
        ShadowUtilities.internalDescriptor = false;
        ShadowUtilities.internalUri = false;
        cacheRoot = Files.createTempDirectory("picker-copies").toFile();
        cache = new File(cacheRoot, "sharing");
        assertTrue(cache.mkdir());
        sources = Files.createTempDirectory("picker-originals").toFile();
        provider = new MediaProvider();
        ProviderInfo info = new ProviderInfo();
        info.authority = "cloud.test";
        provider.attachInfo(ApplicationLoader.applicationContext, info);
        ShadowContentResolver.registerProviderInternal(info.authority, provider);
    }

    @After public void tearDown() {
        for (SystemPhotoPickerImport job : imports) job.close();
        for (File directory : new File[]{cache, sources}) {
            for (File file : directory.listFiles()) file.delete();
            directory.delete();
        }
        cacheRoot.delete();
    }

    private SystemPhotoPickerImport job() {
        return job(ApplicationLoader.applicationContext.getContentResolver());
    }

    private SystemPhotoPickerImport job(ContentResolver resolver) {
        SystemPhotoPickerImport job = new SystemPhotoPickerImport(resolver, cacheRoot);
        imports.add(job);
        return job;
    }

    private Uri media(String mime, String name, byte[] bytes) throws IOException {
        Uri uri = Uri.parse("content://cloud.test/" + provider.items.size());
        File original = new File(sources, "original-" + provider.items.size());
        Files.write(original.toPath(), bytes);
        provider.items.put(uri, new Item(mime, name, original));
        return uri;
    }

    @Test public void createsSharingDirectoryInsideTrustedCacheWithoutUriValidation() throws Exception {
        assertTrue(cache.delete());
        File copy = new File(job().copy(media("image/png", null, new byte[]{1, 2})).path);
        assertEquals(cache.getCanonicalFile(), copy.getCanonicalFile().getParentFile());
        assertEquals(cacheRoot.getCanonicalFile(), copy.getCanonicalFile().getParentFile().getParentFile());
    }

    @Test public void rejectsSharingSymlinkOutsideCacheWithoutTouchingTarget() throws Exception {
        File outside = Files.createTempDirectory("picker-outside").toFile();
        File original = new File(outside, "original.png");
        Files.write(original.toPath(), new byte[]{9});
        assertTrue(cache.delete());
        Files.createSymbolicLink(cache.toPath(), outside.toPath());
        try {
            try { job().copy(media("image/png", null, new byte[]{1})); fail(); } catch (IOException expected) { }
            assertEquals(0, provider.opens);
            assertEquals(1, outside.list().length);
            assertArrayEquals(new byte[]{9}, Files.readAllBytes(original.toPath()));
        } finally {
            Files.delete(cache.toPath());
            assertTrue(cache.mkdir());
            original.delete();
            outside.delete();
        }
    }

    @Test public void invalidCacheDirectoryDoesNotRemoveExistingFile() throws Exception {
        assertTrue(cache.delete());
        Files.write(cache.toPath(), new byte[]{9});
        try {
            try { job().copy(media("image/png", null, new byte[]{1})); fail(); } catch (IOException expected) { }
            assertArrayEquals(new byte[]{9}, Files.readAllBytes(cache.toPath()));
            assertEquals(0, provider.opens);
        } finally {
            assertTrue(cache.delete());
            assertTrue(cache.mkdir());
        }
    }

    @Test public void onlyOwnedFilesAreRemovedOnCancelOrFailure() throws Exception {
        File unrelated = new File(cache, "unrelated.png");
        Files.write(unrelated.toPath(), new byte[]{9});
        Uri good = media("image/png", null, new byte[]{1});
        SystemPhotoPickerImport cancelled = job();
        cancelled.copy(good);
        cancelled.close();
        Uri bad = media("image/png", null, new byte[]{2});
        provider.items.get(bad).failRead = true;
        try { job(ContentResolver.wrap(provider)).load(new ArrayList<>(Arrays.asList(good, bad))); fail(); } catch (IOException expected) { }
        assertEquals(1, cache.list().length);
        assertArrayEquals(new byte[]{9}, Files.readAllBytes(unrelated.toPath()));
    }

    @Test public void stillRejectsIncomingInternalAndNonContentUris() throws Exception {
        Uri uri = media("image/png", null, new byte[]{1});
        ShadowUtilities.internalUri = true;
        try { job().copy(uri); fail(); } catch (IOException expected) { }
        try { job().copy(Uri.fromFile(provider.items.get(uri).source)); fail(); } catch (IOException expected) { }
        assertEquals(0, provider.opens);
        assertEquals(0, cache.list().length);
    }

    @Test public void preservesAllRequestedFormatsAndBytesForUnnamedUris() throws Exception {
        String[][] formats = {{"image/heic","heic"},{"image/jpeg","jpg"},{"image/png","png"},
                {"image/webp","webp"},{"video/quicktime","mov"},{"video/mp4","mp4"}};
        for (String[] format : formats) {
            byte[] original = new byte[]{1, 2, 3, (byte) format[1].length()};
            Uri uri = media(format[0], null, original);
            SendMessagesHelper.SendingMediaInfo info = job().copy(uri);
            assertTrue(info.path, info.path.endsWith("." + format[1]));
            assertEquals(format[0].startsWith("video/"), info.isVideo);
            assertArrayEquals(original, Files.readAllBytes(new File(info.path).toPath()));
        }
    }

    @Test public void preservesCorrectJpegAliasAndUsesMimeForMisleadingName() throws Exception {
        assertTrue(job().copy(media("image/jpeg", "image.JPEG", new byte[]{1})).path.endsWith(".jpeg"));
        assertTrue(job().copy(media("image/png", "wrong.jpg", new byte[]{2})).path.endsWith(".png"));
    }

    @Test public void unknownFormatFailsWithoutGuessingJpegOrMp4() throws Exception {
        try {
            job().copy(media(null, null, new byte[]{1}));
            fail("Missing format must be reported");
        } catch (IOException expected) { }
        assertEquals(0, cache.list().length);
    }

    @Test public void typedNameCanSupplyMissingMimeWithoutChangingBytes() throws Exception {
        SendMessagesHelper.SendingMediaInfo info = job().copy(media(null, "movie.mov", new byte[]{1, 2}));
        assertTrue(info.isVideo);
        assertTrue(info.path.endsWith(".mov"));
        assertArrayEquals(new byte[]{1, 2}, Files.readAllBytes(new File(info.path).toPath()));
    }

    @Test public void respectsAssetOffsetAndLength() throws Exception {
        Uri uri = media("image/png", null, new byte[]{9, 9, 1, 2, 3, 9});
        provider.items.get(uri).offset = 2;
        provider.items.get(uri).length = 3;
        assertArrayEquals(new byte[]{1, 2, 3}, Files.readAllBytes(new File(job().copy(uri).path).toPath()));
    }

    @Test public void batchFailureCleansEarlierCopiesAndPartialFile() throws Exception {
        Uri good = media("image/png", null, new byte[]{1, 2});
        Uri bad = media("image/png", null, new byte[]{3});
        provider.items.get(bad).failRead = true;
        // Direct local-provider routing preserves the controlled stream subclass.
        // Normal resolver routing wraps the descriptor (covered by the format/offset tests).
        try { job(ContentResolver.wrap(provider)).load(new ArrayList<>(Arrays.asList(good, bad))); fail(); } catch (IOException expected) { }
        assertEquals(0, cache.list().length);
        assertEquals(2, sources.list().length);
    }

    @Test public void refusesInternalDescriptorWithoutDeletingOriginal() throws Exception {
        Uri uri = media("image/png", null, new byte[]{1, 2});
        ShadowUtilities.internalDescriptor = true;
        try { job().copy(uri); fail(); } catch (IOException expected) { }
        assertEquals(0, cache.list().length);
        assertArrayEquals(new byte[]{1, 2}, Files.readAllBytes(provider.items.get(uri).source.toPath()));
    }

    @Test public void emptyContentIsRejectedAndCleaned() throws Exception {
        try { job().copy(media("image/png", null, new byte[0])); fail(); } catch (IOException expected) { }
        assertEquals(0, cache.list().length);
    }

    @Test public void cancelledBeforeImportDoesNotContactProvider() throws Exception {
        SystemPhotoPickerImport job = job();
        job.close();
        try { job.copy(media("image/png", null, new byte[]{1})); fail(); } catch (OperationCanceledException expected) { }
        assertEquals(0, provider.opens);
        assertEquals(0, cache.list().length);
    }

    @Test public void cancelsProviderWhileOpeningCloudAsset() throws Exception {
        Uri uri = media("image/png", null, new byte[]{1});
        Item item = provider.items.get(uri);
        item.blockOpen = true;
        SystemPhotoPickerImport job = job();
        FutureTask<Object> task = new FutureTask<>(() -> job.copy(uri));
        new Thread(task).start();
        try {
            assertTrue(item.reading.await(10, TimeUnit.SECONDS));
            job.close();
            assertTrue(item.providerCancellation.await(5, TimeUnit.SECONDS));
            expectCancelled(task);
            assertEquals(0, cache.list().length);
        } finally { item.unblock.countDown(); try { task.get(10, TimeUnit.SECONDS); } catch (ExecutionException expected) { } }
    }

    @Test public void cancelsActiveReadClosesStreamAndUnlinksPartialImmediately() throws Exception {
        Uri uri = media("image/png", null, new byte[]{1});
        Item item = provider.items.get(uri);
        item.blockRead = true;
        SystemPhotoPickerImport job = job(ContentResolver.wrap(provider));
        FutureTask<Object> task = new FutureTask<>(() -> job.copy(uri));
        new Thread(task).start();
        try {
            assertTrue(item.reading.await(10, TimeUnit.SECONDS));
            assertEquals(1, cache.list().length);
            job.close();
            assertEquals(0, cache.list().length);
            assertTrue(item.closed.await(5, TimeUnit.SECONDS));
            expectCancelled(task);
        } finally { item.unblock.countDown(); try { task.get(10, TimeUnit.SECONDS); } catch (ExecutionException expected) { } }
    }

    @Test public void retryDoesNotSharePathsAndCancellingOldImportKeepsNewCopy() throws Exception {
        Uri first = media("image/png", "same.png", new byte[]{1});
        Item item = provider.items.get(first);
        item.blockRead = true;
        Uri second = media("image/png", "same.png", new byte[]{2, 3});
        SystemPhotoPickerImport oldJob = job(ContentResolver.wrap(provider));
        FutureTask<Object> task = new FutureTask<>(() -> oldJob.copy(first));
        new Thread(task).start();
        try {
            assertTrue(item.reading.await(10, TimeUnit.SECONDS));
            SystemPhotoPickerImport newJob = job();
            String path = newJob.copy(second).path;
            oldJob.close();
            expectCancelled(task);
            assertEquals(1, cache.list().length);
            assertArrayEquals(new byte[]{2, 3}, Files.readAllBytes(new File(path).toPath()));
            newJob.release();
            newJob.close();
            assertTrue(new File(path).exists());
        } finally { item.unblock.countDown(); try { task.get(10, TimeUnit.SECONDS); } catch (ExecutionException expected) { } }
    }

    @Test public void completedCopiesRemainOwnedUntilHandoff() throws Exception {
        SystemPhotoPickerImport job = job();
        File path = new File(job.copy(media("image/png", null, new byte[]{1})).path);
        job.close();
        assertFalse(path.exists());
    }

    @Test public void batchAssignsUniqueIdsOwnershipAndVideoMetadata() throws Exception {
        Uri image = media("image/heic", null, new byte[]{1});
        Uri video = media("video/quicktime", null, new byte[]{2});
        ArrayList<MediaController.PhotoEntry> entries = job().load(new ArrayList<>(Arrays.asList(image, video)));
        assertNotEquals(entries.get(0).imageId, entries.get(1).imageId);
        assertTrue(entries.get(0).canDeleteAfter);
        assertTrue(entries.get(1).canDeleteAfter);
        assertEquals(90, entries.get(0).orientation);
        assertTrue(entries.get(1).isVideo);
        assertEquals(0, entries.get(1).duration);
        assertEquals(0, entries.get(1).orientation);
    }

    private void expectCancelled(FutureTask<?> task) throws Exception {
        try { task.get(10, TimeUnit.SECONDS); fail("Import must not succeed after cancellation"); }
        catch (ExecutionException e) {
            assertTrue(e.getCause().toString(), e.getCause() instanceof OperationCanceledException || e.getCause() instanceof IOException);
        }
    }

    static class Item {
        final String mime, name;
        final File source;
        boolean blockOpen, blockRead, failRead;
        long offset, length = AssetFileDescriptor.UNKNOWN_LENGTH;
        final CountDownLatch reading = new CountDownLatch(1), unblock = new CountDownLatch(1), closed = new CountDownLatch(1), providerCancellation = new CountDownLatch(1);
        Item(String mime, String name, File source) { this.mime = mime; this.name = name; this.source = source; }
    }

    public static class MediaProvider extends ContentProvider {
        final HashMap<Uri, Item> items = new HashMap<>();
        int opens;
        public boolean onCreate() { return true; }
        public String getType(Uri uri) { return items.get(uri).mime; }
        public Cursor query(Uri uri, String[] projection, String selection, String[] args, String sort) {
            MatrixCursor result = new MatrixCursor(new String[]{OpenableColumns.DISPLAY_NAME});
            result.addRow(new Object[]{items.get(uri).name});
            return result;
        }
        @Override public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeType, android.os.Bundle options, CancellationSignal signal) throws FileNotFoundException {
            return openAssetFile(uri, "r", signal);
        }
        @Override public AssetFileDescriptor openAssetFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
            opens++;
            Item item = items.get(uri);
            signal.setOnCancelListener(item.providerCancellation::countDown);
            if (item.blockOpen) {
                item.reading.countDown();
                try {
                    if (!item.providerCancellation.await(10, TimeUnit.SECONDS)) throw new FileNotFoundException("Cancellation was not delivered");
                } catch (InterruptedException e) { throw new FileNotFoundException(e.toString()); }
                signal.throwIfCanceled();
            }
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(item.source, ParcelFileDescriptor.MODE_READ_ONLY);
            return new AssetFileDescriptor(fd, item.offset, item.length) {
                @Override public java.io.FileInputStream createInputStream() throws IOException {
                    if (!(item.blockRead || item.failRead)) return super.createInputStream();
                    // A provider-backed read that only exits when its stream is closed.
                    return new ParcelFileDescriptor.AutoCloseInputStream(fd) {
                        int count;
                        @Override public int read(byte[] bytes) throws IOException {
                            if (count++ == 0) { bytes[0] = 1; return 1; }
                            if (item.failRead) throw new IOException("Cloud download failed");
                            item.reading.countDown();
                            try { item.unblock.await(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new IOException(e); }
                            throw new IOException("Cloud stream closed");
                        }
                        @Override public void close() throws IOException {
                            item.closed.countDown(); item.unblock.countDown(); super.close();
                        }
                    };
                }
            };
        }
        public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
        public int delete(Uri uri, String selection, String[] args) { throw new UnsupportedOperationException(); }
        public int update(Uri uri, ContentValues values, String selection, String[] args) { throw new UnsupportedOperationException(); }
    }

    @Implements(AndroidUtilities.class) public static class ShadowUtilities {
        static boolean internalDescriptor;
        static boolean internalUri;
        @Implementation protected static void __staticInitializer__() { }
        @Implementation protected static boolean isInternalUri(Uri uri) {
            if ("file".equals(uri.getScheme())) throw new AssertionError("Trusted cache must not use incoming-URI validation");
            return internalUri;
        }
        @Implementation protected static boolean isInternalUri(int fd) { return internalDescriptor; }
        @Implementation protected static Pair<Integer, Integer> getImageOrientation(String path) { return new Pair<>(90, 0); }
    }
}

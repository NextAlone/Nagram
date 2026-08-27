package org.telegram.messenger.car;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Covers the gating that decides whether a voice note is handed to the car as playable
 * audio. The car host is not involved, so this runs on the JVM.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = android.app.Application.class)
public class CarVoiceAttachmentTest {

    private Context context;
    private String authority;
    private File voiceFile;

    @Before
    public void setUp() throws IOException {
        context = ApplicationProvider.getApplicationContext();
        authority = context.getPackageName() + ".provider";
        // provider_paths.xml maps cache-media to the cache directory's media/ subtree.
        File mediaDir = new File(context.getCacheDir(), "media");
        assertEquals(true, mediaDir.mkdirs() || mediaDir.isDirectory());
        voiceFile = new File(mediaDir, "voice.ogg");
        writeBytes(voiceFile, 32);
    }

    @Test
    public void resolvesUriForDownloadedVoiceNote() {
        Uri uri = CarVoiceAttachment.resolveUri(context, authority, voiceFile, true, false);

        assertNotNull(uri);
        assertEquals("content", uri.getScheme());
        assertEquals(authority, uri.getAuthority());
    }

    @Test
    public void doesNotAttachWhenPreviewsAreSuppressed() {
        // The body already reads as a placeholder here; playing the recording would leak
        // exactly what suppressing previews is meant to hide.
        assertNull(CarVoiceAttachment.resolveUri(context, authority, voiceFile, false, false));
    }

    @Test
    public void doesNotAttachWhileLocked() {
        assertNull(CarVoiceAttachment.resolveUri(context, authority, voiceFile, true, true));
    }

    @Test
    public void doesNotAttachWhenFileIsMissing() {
        File absent = new File(context.getCacheDir(), "media/not-downloaded-yet.ogg");
        assertNull(CarVoiceAttachment.resolveUri(context, authority, absent, true, false));
    }

    @Test
    public void doesNotAttachWhenFileIsEmpty() throws IOException {
        // FileLoader#getPathToMessage never returns null; an interrupted download can leave
        // a zero-length file behind, which would surface as a silent, broken player.
        File empty = new File(context.getCacheDir(), "media/partial.ogg");
        writeBytes(empty, 0);
        assertNull(CarVoiceAttachment.resolveUri(context, authority, empty, true, false));
    }

    @Test
    public void doesNotAttachForNullFile() {
        assertNull(CarVoiceAttachment.resolveUri(context, authority, null, true, false));
    }

    @Test
    public void returnsNullInsteadOfThrowingForUnservedPath() {
        // Outside every path declared in provider_paths.xml, FileProvider throws; the
        // message must still be delivered as text.
        File outside = new File("/proc/version");
        assertNull(CarVoiceAttachment.resolveUri(context, authority, outside, true, false));
    }

    @Test
    public void mimeTypeMatchesTelegramVoiceEncoding() {
        assertEquals("audio/ogg", CarVoiceAttachment.MIME_TYPE);
    }

    private static void writeBytes(File file, int count) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(new byte[count]);
        }
    }
}

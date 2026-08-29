package org.telegram.messenger.car;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

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
    public void setUp() throws Exception {
        // FileProvider memoises a PathStrategy per authority in a static map, and the
        // strategy holds absolute roots. Robolectric hands each test method its own data
        // directory, so a strategy cached by an earlier test points at a directory this one
        // does not use, and getUriForFile then rejects a perfectly valid file. Production
        // never sees this: there, the data directory is fixed for the process lifetime.
        clearFileProviderCache();

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
        String state = "path=" + voiceFile.getAbsolutePath()
                + " exists=" + voiceFile.exists()
                + " length=" + voiceFile.length()
                + " authority=" + authority
                + " packageName=" + context.getPackageName()
                + " cacheDir=" + context.getCacheDir();

        Uri uri = CarVoiceAttachment.resolveUri(context, authority, voiceFile, true, false);

        assertNotNull(state, uri);
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
    public void returnsNullInsteadOfThrowingForUnservedPath() throws IOException {
        // provider_paths.xml serves the cache directory's logs/ and media/ subtrees, not its
        // root, so a real file sitting directly in it makes FileProvider throw. The message
        // must still go out as text rather than the exception escaping.
        File unserved = new File(context.getCacheDir(), "unserved.ogg");
        writeBytes(unserved, 16);
        assertEquals(true, unserved.exists());

        assertNull(CarVoiceAttachment.resolveUri(context, authority, unserved, true, false));
    }

    @Test
    public void disclosureIsAllowedOnlyWithPreviewsAndUnlocked() {
        assertEquals(true, CarVoiceAttachment.isDisclosureAllowed(true, false));
        assertEquals(false, CarVoiceAttachment.isDisclosureAllowed(false, false));
        assertEquals(false, CarVoiceAttachment.isDisclosureAllowed(true, true));
        assertEquals(false, CarVoiceAttachment.isDisclosureAllowed(false, true));
    }

    @Test
    public void disclosureGateAgreesWithResolveUri() {
        // The caller checks the gate separately to decide whether an absent file is worth
        // downloading, so the two must not drift apart.
        boolean[] flags = {true, false};
        for (boolean preview : flags) {
            for (boolean locked : flags) {
                boolean allowed = CarVoiceAttachment.isDisclosureAllowed(preview, locked);
                Uri uri = CarVoiceAttachment.resolveUri(context, authority, voiceFile, preview, locked);
                assertEquals("preview=" + preview + " locked=" + locked, allowed, uri != null);
            }
        }
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

    private static void clearFileProviderCache() throws Exception {
        Field cache = FileProvider.class.getDeclaredField("sCache");
        cache.setAccessible(true);
        ((Map<?, ?>) cache.get(null)).clear();
    }
}

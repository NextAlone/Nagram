package org.telegram.messenger;

import static org.junit.Assert.*;

import android.app.Application;
import android.net.Uri;
import android.util.Pair;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import static org.robolectric.Shadows.shadowOf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, application = Application.class, shadows = {
        SystemPhotoPickerMediaTest.ShadowUtilities.class,
        SystemPhotoPickerMediaTest.ShadowLog.class
})
public class SystemPhotoPickerMediaTest {
    private static File sharingDirectory;
    private final Uri uri = Uri.parse("content://cloud.provider/picker/cloud.jpg");

    @Before
    public void setUp() throws Exception {
        ApplicationLoader.applicationContext = RuntimeEnvironment.getApplication();
        sharingDirectory = Files.createTempDirectory("picker-test").toFile();
    }

    @After
    public void tearDown() {
        for (File file : sharingDirectory.listFiles()) file.delete();
        sharingDirectory.delete();
    }

    @Test
    public void copiesCloudBytesAndPreservesExistingFileWithSameName() throws Exception {
        File existing = new File(sharingDirectory, "cloud.jpg");
        Files.write(existing.toPath(), new byte[]{9});
        shadowOf(ApplicationLoader.applicationContext.getContentResolver()).registerInputStream(uri, new ByteArrayInputStream(new byte[]{1, 2, 3}));
        String path = MediaController.copyFileToCache(uri, "jpg");
        assertNotNull(path);
        assertNotEquals(existing.getAbsolutePath(), path);
        assertArrayEquals(new byte[]{1, 2, 3}, Files.readAllBytes(new File(path).toPath()));
        assertArrayEquals(new byte[]{9}, Files.readAllBytes(existing.toPath()));
    }

    @Test
    public void removesFileWhenSizeLimitIsExceeded() {
        shadowOf(ApplicationLoader.applicationContext.getContentResolver()).registerInputStream(uri, new ByteArrayInputStream(new byte[32]));
        assertNull(MediaController.copyFileToCache(uri, "jpg", 8));
        assertEquals(0, sharingDirectory.list().length);
    }

    @Implements(AndroidUtilities.class)
    public static class ShadowUtilities {
        @Implementation protected static void __staticInitializer__() {}
        @Implementation protected static File getSharingDirectory() { return sharingDirectory; }
        @Implementation protected static boolean isInternalUri(Uri uri) { return false; }
        @Implementation protected static Pair<Integer, Integer> getImageOrientation(String path) {
            return new Pair<>(0, 0);
        }
    }

    @Implements(FileLog.class)
    public static class ShadowLog {
        @Implementation protected static void e(Throwable error) {}
    }
}

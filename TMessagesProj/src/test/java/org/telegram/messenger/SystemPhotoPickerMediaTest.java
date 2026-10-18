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
import org.telegram.ui.ChatActivity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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
    public void concurrentImportsReserveDifferentPathsBeforeOpeningCloudContent() throws Exception {
        Uri secondUri = Uri.parse("content://cloud.provider/second/cloud.jpg");
        CountDownLatch opened = new CountDownLatch(1);
        CountDownLatch resume = new CountDownLatch(1);
        shadowOf(ApplicationLoader.applicationContext.getContentResolver()).registerInputStreamSupplier(uri, () -> {
            opened.countDown();
            try {
                if (!resume.await(10, TimeUnit.SECONDS)) throw new AssertionError("Timed out opening cloud content");
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
            return new ByteArrayInputStream(new byte[]{1});
        });
        shadowOf(ApplicationLoader.applicationContext.getContentResolver()).registerInputStream(secondUri,
                new ByteArrayInputStream(new byte[]{2}));
        FutureTask<String> first = new FutureTask<>(() -> MediaController.copyFileToCache(uri, "jpg"));
        new Thread(first).start();
        String secondPath;
        try {
            assertTrue(opened.await(10, TimeUnit.SECONDS));
            secondPath = MediaController.copyFileToCache(secondUri, "jpg");
        } finally {
            resume.countDown();
        }
        String firstPath = first.get(10, TimeUnit.SECONDS);
        assertNotNull(firstPath);
        assertNotNull(secondPath);
        assertNotEquals(firstPath, secondPath);
        assertArrayEquals(new byte[]{1}, Files.readAllBytes(new File(firstPath).toPath()));
        assertArrayEquals(new byte[]{2}, Files.readAllBytes(new File(secondPath).toPath()));
    }

    @Test
    public void removesPartialFileWhenCloudReadFails() {
        shadowOf(ApplicationLoader.applicationContext.getContentResolver()).registerInputStream(uri, new InputStream() {
            private int remaining = 32;
            @Override
            public int read() throws IOException {
                if (remaining-- > 0) return 1;
                throw new IOException("Cloud download interrupted");
            }
        });
        assertNull(MediaController.copyFileToCache(uri, "jpg"));
        assertEquals(0, sharingDirectory.list().length);
    }

    @Test
    public void removesReservedFileWhenUriCannotBeOpened() {
        shadowOf(ApplicationLoader.applicationContext.getContentResolver()).registerInputStreamSupplier(uri, () -> {
            throw new SecurityException("URI permission revoked");
        });
        assertNull(MediaController.copyFileToCache(uri, "jpg"));
        assertEquals(0, sharingDirectory.list().length);
    }

    @Test
    public void removesFileWhenSizeLimitIsExceeded() {
        shadowOf(ApplicationLoader.applicationContext.getContentResolver()).registerInputStream(uri, new ByteArrayInputStream(new byte[32]));
        assertNull(MediaController.copyFileToCache(uri, "jpg", 8));
        assertEquals(0, sharingDirectory.list().length);
    }

    @Test
    public void cancellingEditorDeletesOnlyImportedCopies() throws Exception {
        File original = new File(sharingDirectory, "original.jpg");
        File imported = new File(sharingDirectory, "imported.jpg");
        Files.write(original.toPath(), new byte[]{1});
        Files.write(imported.toPath(), new byte[]{2});
        ArrayList<MediaController.PhotoEntry> entries = new ArrayList<>();
        MediaController.PhotoEntry sourceEntry = new MediaController.PhotoEntry(0, 0, 0,
                original.getAbsolutePath(), 0, false, 0, 0, 1);
        MediaController.PhotoEntry importedEntry = new MediaController.PhotoEntry(0, 1, 0,
                imported.getAbsolutePath(), 0, false, 0, 0, 1);
        importedEntry.canDeleteAfter = true;
        entries.add(sourceEntry);
        entries.add(importedEntry);
        Method cleanup = ChatActivity.class.getDeclaredMethod("deletePickerEntries", ArrayList.class);
        cleanup.setAccessible(true);
        cleanup.invoke(null, entries);
        assertTrue(original.exists());
        assertFalse(imported.exists());
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

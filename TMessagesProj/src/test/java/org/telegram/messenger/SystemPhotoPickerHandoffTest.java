package org.telegram.messenger;

import static org.junit.Assert.*;

import android.app.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.util.ReflectionHelpers;
import org.telegram.ui.Components.SystemPhotoPickerSelection;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, application = Application.class, shadows = SystemPhotoPickerHandoffTest.ShadowHelper.class)
public class SystemPhotoPickerHandoffTest {
    private File directory, copy, original;
    private ArrayList<MediaController.PhotoEntry> entries;
    private SystemPhotoPickerSelection selection;
    private TestQueue queue;

    @Before public void setUp() throws Exception {
        directory = Files.createTempDirectory("picker-handoff").toFile();
        copy = new File(directory, "picker.png");
        original = new File(directory, "original.png");
        Files.write(copy.toPath(), new byte[]{1});
        Files.write(original.toPath(), new byte[]{9});
        entries = new ArrayList<>();
        MediaController.PhotoEntry imported = new MediaController.PhotoEntry(0, 0, 0, copy.getPath(), 0, false, 0, 0, 1);
        imported.canDeleteAfter = true;
        entries.add(imported);
        entries.add(new MediaController.PhotoEntry(0, 1, 0, original.getPath(), 0, false, 0, 0, 1));
        selection = new SystemPhotoPickerSelection(entries);
        queue = new TestQueue();
        ReflectionHelpers.setStaticField(SendMessagesHelper.class, "mediaSendQueue", queue);
    }

    @After public void tearDown() {
        for (File file : directory.listFiles()) file.delete();
        directory.delete();
    }

    private boolean prepare(ArrayList<SendMessagesHelper.SendingMediaInfo> media) {
        // Exercise the public overload and both real delegations to DispatchQueue.postRunnable.
        return SendMessagesHelper.prepareSendingMedia(null, media, 1L, null, null, null, null,
                false, true, null, true, 0, 0, 0, false, null, null, 0, false, 0, 0, null);
    }

    private ArrayList<SendMessagesHelper.SendingMediaInfo> media() {
        ArrayList<SendMessagesHelper.SendingMediaInfo> result = new ArrayList<>();
        SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
        info.path = copy.getPath();
        info.canDeleteAfter = true;
        result.add(info);
        return result;
    }

    @Test public void emptyMediaDoesNotTransferOwnershipOrPostWork() {
        assertFalse(prepare(new ArrayList<>()));
        assertEquals(0, queue.posts);
        selection.cleanup();
        assertFalse(copy.exists());
        assertTrue(original.exists());
    }

    @Test public void rejectedHandoffDeletesOnlyOwnedCopiesAndEdits() throws Exception {
        File edited = new File(directory, "edited.png");
        Files.write(edited.toPath(), new byte[]{2});
        entries.get(0).imagePath = edited.getPath();
        queue.accept = false;
        assertTrue(selection.beginSend());
        if (prepare(media())) selection.markQueued();
        assertFalse(selection.isQueued());
        selection.cleanup();
        assertFalse(copy.exists());
        assertFalse(edited.exists());
        assertTrue(original.exists());
        assertFalse(selection.beginSend());
        assertEquals(1, queue.posts);
    }

    @Test public void exceptionBeforeQueueAcceptanceKeepsCallerResponsibleForCleanup() {
        queue.fail = true;
        assertTrue(selection.beginSend());
        try { prepare(media()); fail(); } catch (IllegalStateException expected) { }
        assertFalse(selection.isQueued());
        selection.cleanup();
        assertFalse(copy.exists());
        assertTrue(original.exists());
        assertFalse(selection.beginSend());
    }

    @Test public void acceptedFilesSurviveEditorCloseAndLaterUiFailureWithoutDuplicateSend() throws Exception {
        assertTrue(selection.beginSend());
        assertTrue(prepare(media()));
        selection.markQueued();
        assertNotNull(queue.work);
        assertTrue(copy.exists()); // Work has not even run: acceptance is not upload completion.
        try { throw new IllegalStateException("UI bookkeeping failed after handoff"); }
        catch (IllegalStateException expected) { selection.cleanup(); }
        selection.cleanup(); // Editor close / fragment destruction must not unlink queue input.
        assertArrayEquals(new byte[]{1}, Files.readAllBytes(copy.toPath()));
        assertTrue(original.exists());
        assertFalse(selection.beginSend());
        assertEquals(1, queue.posts);
    }

    @Test public void cancellingEditorBeforeSendDeletesOnlyImportedCopies() {
        selection.cleanup();
        selection.cleanup();
        assertFalse(copy.exists());
        assertTrue(original.exists());
        assertEquals(0, queue.posts);
    }

    @Test public void emptySelectionCanBeReselectedBeforeFirstSendAttempt() {
        selection.toggle(0); selection.toggle(1);
        assertFalse(selection.beginSend());
        selection.toggle(0);
        assertTrue(selection.beginSend());
        assertFalse(selection.beginSend());
    }

    private static class TestQueue extends DispatchQueue {
        boolean accept = true, fail;
        int posts;
        Runnable work;
        TestQueue() { super("testMediaSendQueue", false); }
        @Override public boolean postRunnable(Runnable runnable) {
            posts++;
            if (fail) throw new IllegalStateException("Queue unavailable");
            if (accept) work = runnable;
            return accept;
        }
    }

    // Avoid native/account startup; the production overloads and enqueue logic run unchanged.
    @Implements(SendMessagesHelper.class) public static class ShadowHelper {
        @Implementation protected static void __staticInitializer__() { }
    }
}

package org.telegram.ui.Components;

import static org.junit.Assert.*;

import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.util.ReflectionHelpers;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PhotoViewer;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, application = Application.class, shadows = SystemPhotoPickerSelectionTest.ShadowViewer.class)
public class SystemPhotoPickerSelectionTest {
    private ArrayList<MediaController.PhotoEntry> entries() {
        ArrayList<MediaController.PhotoEntry> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) result.add(new MediaController.PhotoEntry(0, i, 0, "copy-"+i, 0, i != 0, 0, 0, 1));
        return result;
    }

    @Test public void emptySelectionDoesNotImplicitlySendCurrentPhoto() {
        SystemPhotoPickerSelection selection = new SystemPhotoPickerSelection(entries());
        for (int i = 0; i < 3; i++) selection.toggle(i);
        assertTrue(selection.prepareSend(0, null, false, null, null).isEmpty());
        assertTrue(selection.order().isEmpty());
        assertTrue(selection.selected().isEmpty());
    }

    @Test public void reselectingMovesItemToEndWithoutDuplicates() {
        ArrayList<MediaController.PhotoEntry> entries = entries();
        SystemPhotoPickerSelection selection = new SystemPhotoPickerSelection(entries);
        assertEquals(1, selection.toggle(1));
        assertEquals(2, selection.toggle(1));
        assertEquals(Arrays.asList(0, 2, 1), selection.order());
        assertEquals(Arrays.asList(entries.get(0), entries.get(2), entries.get(1)), selection.prepareSend(0, null, false, null, null));
        selection.toggle(1);
        selection.toggle(1);
        assertEquals(3, selection.selected().size());
        assertEquals(Arrays.asList(0, 2, 1), selection.order());
    }

    @Test public void selectionToggleCannotOverwriteAnotherVideosEdit() {
        ArrayList<MediaController.PhotoEntry> entries = entries();
        VideoEditedInfo firstEdit = new VideoEditedInfo(), secondEdit = new VideoEditedInfo();
        entries.get(1).editedInfo = firstEdit;
        entries.get(2).editedInfo = secondEdit;
        SystemPhotoPickerSelection selection = new SystemPhotoPickerSelection(entries);
        selection.toggle(2);
        selection.toggle(2);
        assertSame(firstEdit, entries.get(1).editedInfo);
        assertSame(secondEdit, entries.get(2).editedInfo);
        selection.prepareSend(1, null, false, null, null);
        assertSame(firstEdit, entries.get(1).editedInfo);
        assertSame(secondEdit, entries.get(2).editedInfo);
    }

    @Test public void currentVideoEditIsAppliedOnlyToSelectedCurrentItem() {
        ArrayList<MediaController.PhotoEntry> entries = entries();
        SystemPhotoPickerSelection selection = new SystemPhotoPickerSelection(entries);
        VideoEditedInfo edit = new VideoEditedInfo();
        selection.prepareSend(1, edit, false, null, null);
        assertSame(edit, entries.get(1).editedInfo);
        assertNull(entries.get(2).editedInfo);
        selection.toggle(2);
        selection.prepareSend(2, edit, false, null, null);
        assertNull(entries.get(2).editedInfo);
    }

    @Test public void commonCaptionAndEntitiesFollowFirstSelectedItem() {
        ArrayList<MediaController.PhotoEntry> entries = entries();
        entries.get(2).caption = "stale";
        entries.get(2).entities = new ArrayList<>();
        SystemPhotoPickerSelection selection = new SystemPhotoPickerSelection(entries);
        selection.toggle(0);
        ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
        entities.add(new TLRPC.TL_messageEntityBold());
        ArrayList<MediaController.PhotoEntry> send = selection.prepareSend(2, null, true, "common", entities);
        assertEquals("common", send.get(0).caption);
        assertSame(entities, send.get(0).entities);
        assertNull(send.get(1).caption);
        assertNull(send.get(1).entities);
        selection.toggle(1);
        selection.toggle(1);
        send = selection.prepareSend(2, null, true, "changed", entities);
        assertEquals(2, send.get(0).imageId);
        assertEquals("changed", send.get(0).caption);
        assertNull(send.get(1).caption);
        assertNull(send.get(1).entities);
    }

    @Test public void individualCaptionsAndEntitiesSurviveReordering() {
        ArrayList<MediaController.PhotoEntry> entries = entries();
        entries.get(1).caption = "first";
        entries.get(2).caption = "second";
        entries.get(2).entities = new ArrayList<>();
        SystemPhotoPickerSelection selection = new SystemPhotoPickerSelection(entries);
        selection.toggle(1); selection.toggle(1);
        ArrayList<MediaController.PhotoEntry> send = selection.prepareSend(2, null, false, "ignored", null);
        assertEquals("second", send.get(1).caption);
        assertNotNull(send.get(1).entities);
        assertEquals("first", send.get(2).caption);
    }

    @Test public void temporaryViewerLimitsRestoreAndCannotClobberLaterAttachmentSession() {
        PhotoViewer viewer = new PhotoViewer();
        viewer.setMaxSelectedPhotos(10, false);
        Runnable restore = viewer.useTemporarySelectionLimits(3, true);
        assertEquals(3, (int) ReflectionHelpers.getField(viewer, "maxSelectedPhotos"));
        assertTrue((boolean) ReflectionHelpers.getField(viewer, "allowOrder"));
        restore.run();
        assertEquals(10, (int) ReflectionHelpers.getField(viewer, "maxSelectedPhotos"));
        assertFalse((boolean) ReflectionHelpers.getField(viewer, "allowOrder"));
        viewer.setMaxSelectedPhotos(1, false);
        restore.run();
        assertEquals(1, (int) ReflectionHelpers.getField(viewer, "maxSelectedPhotos"));
    }

    @Test public void emptyProviderStopsSendBeforeMutatingViewerOrCallingSender() {
        PhotoViewer viewer = new PhotoViewer();
        ReflectionHelpers.setField(viewer, "placeProvider", new PhotoViewer.EmptyPhotoViewerProvider() {
            @Override public boolean canSendSelectedMedia() { return false; }
            @Override public void sendButtonPressed(int index, VideoEditedInfo edit, boolean notify, int date, int repeat, boolean document) {
                fail("Empty selection must not send");
            }
        });
        ReflectionHelpers.callInstanceMethod(viewer, "sendPressed",
                ReflectionHelpers.ClassParameter.from(boolean.class, true),
                ReflectionHelpers.ClassParameter.from(int.class, 0),
                ReflectionHelpers.ClassParameter.from(int.class, 0),
                ReflectionHelpers.ClassParameter.from(boolean.class, false),
                ReflectionHelpers.ClassParameter.from(boolean.class, false),
                ReflectionHelpers.ClassParameter.from(boolean.class, false));
        assertFalse(viewer.doneButtonPressed);
    }

    @Test public void defaultProvidersKeepExistingEmptySelectionPolicy() {
        assertTrue(new PhotoViewer.EmptyPhotoViewerProvider().canSendSelectedMedia());
    }

    // These tests exercise the real limit setters, not the Android rendering constructor.
    @Implements(PhotoViewer.class) public static class ShadowViewer {
        @Implementation protected void __constructor__() { }
    }
}

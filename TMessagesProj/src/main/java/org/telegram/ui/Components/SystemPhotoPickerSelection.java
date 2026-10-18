package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.HashMap;

/** Selection callbacks for the system picker only; entries remain shared with PhotoViewer. */
public final class SystemPhotoPickerSelection {
    private final ArrayList<MediaController.PhotoEntry> entries;
    private final HashMap<Object, Object> selected = new HashMap<>();
    private final ArrayList<Object> order = new ArrayList<>();

    public SystemPhotoPickerSelection(ArrayList<MediaController.PhotoEntry> entries) {
        this.entries = entries;
        for (int i = 0; i < entries.size(); i++) {
            selected.put(i, entries.get(i));
            order.add(i);
        }
    }

    public int toggle(int index) {
        int position = order.indexOf(index);
        if (selected.remove(index) != null) {
            order.remove(position);
            return position;
        }
        selected.put(index, entries.get(index));
        order.add(index);
        return order.size() - 1;
    }

    public HashMap<Object, Object> selected() { return selected; }
    public ArrayList<Object> order() { return order; }

    public ArrayList<MediaController.PhotoEntry> prepareSend(int currentIndex, VideoEditedInfo currentEdit,
            boolean commonCaption, CharSequence caption, ArrayList<TLRPC.MessageEntity> entities) {
        ArrayList<MediaController.PhotoEntry> result = new ArrayList<>();
        if (order.isEmpty()) return result;
        // The thumbnail strip passes the *current* video's edit even for another index.
        // Toggling only changes selection. PhotoViewer saves edits on switch and send.
        if (currentEdit != null && selected.containsKey(currentIndex)) entries.get(currentIndex).editedInfo = currentEdit;
        for (Object key : order) result.add((MediaController.PhotoEntry) selected.get(key));
        if (commonCaption) {
            for (int i = 0; i < result.size(); i++) {
                result.get(i).caption = i == 0 ? caption : null;
                result.get(i).entities = i == 0 ? entities : null;
            }
        }
        return result;
    }
}

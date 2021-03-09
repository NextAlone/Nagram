package tw.nekomimi.nekogram;

import org.apache.commons.lang3.StringUtils;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author luvletter2333
 */
public class PinnedStickerHelper {
    private static final ConcurrentHashMap<Integer, PinnedStickerHelper> _instances = new ConcurrentHashMap<>();

    public static PinnedStickerHelper getInstance(int accountNum) {
        PinnedStickerHelper obj = _instances.get(accountNum);
        if (obj != null)
            return obj;
        synchronized (PinnedStickerHelper.class) {
            if (obj == null) {
                obj = new PinnedStickerHelper(accountNum);
                _instances.put(accountNum, obj);
            }
        }
        return obj;
    }

    public List<Long> pinnedList;
    public final int accountNum;

    private PinnedStickerHelper(int accountNum) {
        this.accountNum = accountNum;
        String[] configs = MessagesController.getMainSettings(accountNum).getString("pinnedStickers", "").split(",");
        this.pinnedList = new ArrayList<>(configs.length);
        boolean hasInvalid = false;
        for (String str : configs) {
            if (str.isEmpty())
                continue;
            try {
                this.pinnedList.add(Long.parseLong(str));
            } catch (NumberFormatException e) {
                hasInvalid = true;
            }
        }
        if (hasInvalid)
            updateConfig();
    }

    private void updateConfig() {
        MessagesController.getMainSettings(accountNum)
                .edit()
                .putString("pinnedStickers", StringUtils.join(this.pinnedList, ","))
                .apply();
    }

    public boolean isPinned(long sticker_id) {
        return this.pinnedList.contains(sticker_id);
    }

    public List<Long> pinNewSticker(long stickerID) {
        if (!this.pinnedList.contains(stickerID)) {
            this.pinnedList.add(0, stickerID);
            updateConfig();
        }
        return this.pinnedList;
    }

    public void removePinnedStickerLocal(long stickerID) {
        this.pinnedList.remove(stickerID);
        updateConfig();
    }

    /**
     * This method will reorder pinned Stickers in given list, according to the order of the local pinnedList
     * This method accepts a list of TLRPC.StickerSet
     * <p>
     * PLEASE KEEP THESE TWO METHODS WITH THE SAME BEHAVIOR
     *
     * @param stickerSets this list will be reordered
     * @param removeLocal if true, stickerSets in local but not in given list will be removed from local pinned list
     * @return return whether a syncing is needed
     **/
    public synchronized boolean reorderPinnedStickersForSS(List<TLRPC.StickerSet> stickerSets, boolean removeLocal) {
        if (removeLocal) {
            boolean removed = this.pinnedList.removeIf(id -> stickerSets.stream().noneMatch(ss -> ss.id == id));
            if (removed && stickerSets.size() > 0) // prevent poor network causing losing pinned lsit
                updateConfig();
        }
        List<TLRPC.StickerSet> pinnedList = stickerSets.stream()
                .filter(ss -> isPinned(ss.id))
                .sorted((o1, o2) -> this.pinnedList.indexOf(o1.id) - this.pinnedList.indexOf(o2.id))
                .collect(Collectors.toList());
        boolean needSync = !isOrdered(stickerSets, pinnedList);
        if (needSync) { // order is not correct
            stickerSets.removeAll(pinnedList);
            stickerSets.addAll(0, pinnedList);
        }
        return needSync;
    }

    /**
     * This method will reorder pinned Stickers in given list, according to the order of the local pinnedList
     * This method accepts a list of TLRPC.TL_messages_stickerSet
     * <p>
     * PLEASE KEEP THESE TWO METHODS WITH THE SAME BEHAVIOR
     *
     * @param stickerSets this list will be reordered
     * @param removeLocal if true, stickerSets in local but not in given list will be removed from local pinned list
     * @return return whether a syncing is needed
     **/
    public synchronized boolean reorderPinnedStickers(List<TLRPC.TL_messages_stickerSet> stickerSets, boolean removeLocal) {
        if (removeLocal) {
            boolean removed = this.pinnedList.removeIf(id -> stickerSets.stream().noneMatch(TLM_ss -> TLM_ss.set.id == id));
            if (removed && stickerSets.size() > 0) // prevent poor network causing losing pinned lsit
                updateConfig();
        }
        List<TLRPC.TL_messages_stickerSet> pinnedList = stickerSets.stream()
                .filter(TLM_stickerSet -> isPinned(TLM_stickerSet.set.id))
                .sorted((o1, o2) -> this.pinnedList.indexOf(o1.set.id) - this.pinnedList.indexOf(o2.set.id))
                .collect(Collectors.toList());
        boolean needSync = !isOrdered(stickerSets, pinnedList);
        if (needSync) { // order is not correct
            stickerSets.removeAll(pinnedList);
            stickerSets.addAll(0, pinnedList);
        }
        return needSync;
    }

    public boolean reorderPinnedStickers(List<TLRPC.TL_messages_stickerSet> stickerSets) {
        return reorderPinnedStickers(stickerSets, true);
    }

    public void swap(int index1, int index2) {
        if (index1 >= 0 && index1 < pinnedList.size() && index2 >= 0 && index2 < pinnedList.size()) {
            Collections.swap(pinnedList, index1, index2);
            updateConfig();
        }
    }

    /**
     * check whether list2 is included in list1, with the same order, starting from list1.get(0)
     */
    private static boolean isOrdered(List list1, List list2) {
        for (int i = 0; i < list2.size(); i++)
            if (!list1.get(i).equals(list2.get(i)))
                return false;
        return true;
    }

    public void sendOrderSyncForSS(List<TLRPC.StickerSet> stickerSetIDs) {
        TLRPC.TL_messages_reorderStickerSets req = new TLRPC.TL_messages_reorderStickerSets();
        req.masks = false; // only image sticker
        for (int a = 0; a < stickerSetIDs.size(); a++) {
            req.order.add(stickerSetIDs.get(a).id);
        }
        ConnectionsManager.getInstance(accountNum).sendRequest(req, (response, error) -> {
        });
    }

    public void sendOrderSync(List<TLRPC.TL_messages_stickerSet> stickerSetIDs) {
        TLRPC.TL_messages_reorderStickerSets req = new TLRPC.TL_messages_reorderStickerSets();
        req.masks = false; // only image sticker
        for (int a = 0; a < stickerSetIDs.size(); a++) {
            req.order.add(stickerSetIDs.get(a).set.id);
        }
        ConnectionsManager.getInstance(accountNum).sendRequest(req, (response, error) -> {
        });
    }
}

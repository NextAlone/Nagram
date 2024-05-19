/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package tw.nekomimi.nekogram.helpers;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.TranscribeButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import xyz.nextalone.nagram.NaConfig;

public class AyuFilter {
    public static ArrayList<FilterModel> getRegexFilters() {
        var str = NaConfig.INSTANCE.getRegexFiltersData().String();

        FilterModel[] arr = new Gson().fromJson(str, FilterModel[].class);

        return new ArrayList<>(Arrays.asList(arr));
    }

    public static void addFilter(String text, boolean caseInsensitive) {
        var list = getRegexFilters();

        FilterModel filterModel = new FilterModel();
        filterModel.regex = text;
        filterModel.caseInsensitive = caseInsensitive;
        filterModel.enabledGroups = new ArrayList<>();
        filterModel.disabledGroups = new ArrayList<>();
        filterModel.enabledGroups.add(0L);
        list.add(0, filterModel);
        var str = new Gson().toJson(list);
        NaConfig.INSTANCE.getRegexFiltersData().setConfigString(str);

        AyuFilter.rebuildCache();
    }

    public static void editFilter(int filterIdx, String text, boolean caseInsensitive) {
        var list = getRegexFilters();

        FilterModel filterModel = list.get(filterIdx);
        filterModel.regex = text;
        filterModel.caseInsensitive = caseInsensitive;

        var str = new Gson().toJson(list);
        NaConfig.INSTANCE.getRegexFiltersData().setConfigString(str);

        AyuFilter.rebuildCache();
    }

    public static void saveFilter(ArrayList<FilterModel> filterModels1) {
        var str = new Gson().toJson(filterModels1);
        NaConfig.INSTANCE.getRegexFiltersData().setConfigString(str);

        AyuFilter.rebuildCache();
    }

    public static void removeFilter(int filterIdx) {
        var list = getRegexFilters();
        list.remove(filterIdx);

        var str = new Gson().toJson(list);
        NaConfig.INSTANCE.getRegexFiltersData().setConfigString(str);

        AyuFilter.rebuildCache();
    }

    public static CharSequence getMessageText(MessageObject selectedObject, MessageObject.GroupedMessages selectedObjectGroup) {
        CharSequence messageTextToTranslate = null;
        if (selectedObject.type != MessageObject.TYPE_EMOJIS && selectedObject.type != MessageObject.TYPE_ANIMATED_STICKER && selectedObject.type != MessageObject.TYPE_STICKER) {
            messageTextToTranslate = getMessageCaption(selectedObject, selectedObjectGroup);
            if (messageTextToTranslate == null && selectedObject.isPoll()) {
                try {
                    TLRPC.Poll poll = ((TLRPC.TL_messageMediaPoll) selectedObject.messageOwner.media).poll;
                    StringBuilder pollText = new StringBuilder(poll.question.text).append("\n");
                    for (TLRPC.PollAnswer answer : poll.answers)
                        pollText.append("\n\uD83D\uDD18 ").append(answer.text.text);
                    messageTextToTranslate = pollText.toString();
                } catch (Exception ignored) {
                }
            }
            if (messageTextToTranslate == null && MessageObject.isMediaEmpty(selectedObject.messageOwner)) {
                messageTextToTranslate = getMessageContent(selectedObject);
            }
            if (messageTextToTranslate != null && Emoji.fullyConsistsOfEmojis(messageTextToTranslate)) {
                messageTextToTranslate = null;
            }
        }
        if (selectedObject.translated || selectedObject.isRestrictedMessage) {
            messageTextToTranslate = null;
        }
        return messageTextToTranslate;
    }

    private static CharSequence getMessageCaption(MessageObject messageObject, MessageObject.GroupedMessages group) {
        String restrictionReason = MessagesController.getRestrictionReason(messageObject.messageOwner.restriction_reason);
        if (!TextUtils.isEmpty(restrictionReason)) {
            return restrictionReason;
        }
        if (messageObject.isVoiceTranscriptionOpen() && !TranscribeButton.isTranscribing(messageObject)) {
            return messageObject.getVoiceTranscription();
        }
        if (messageObject.caption != null) {
            return messageObject.caption;
        }
        if (group == null) {
            return null;
        }
        CharSequence caption = null;
        for (int a = 0, N = group.messages.size(); a < N; a++) {
            MessageObject message = group.messages.get(a);
            if (message.caption != null) {
                if (caption != null) {
                    return null;
                }
                caption = message.caption;
            }
        }
        return caption;
    }

    private static CharSequence getMessageContent(MessageObject messageObject) {
        SpannableStringBuilder str = new SpannableStringBuilder();
        String restrictionReason = MessagesController.getRestrictionReason(messageObject.messageOwner.restriction_reason);
        if (!TextUtils.isEmpty(restrictionReason)) {
            str.append(restrictionReason);
        } else if (messageObject.caption != null) {
            str.append(messageObject.caption);
        } else {
            str.append(messageObject.messageText);
        }
        return str.toString();
    }

    private static ArrayList<FilterModel> filterModels;
    private static LongSparseArray<HashMap<Integer, Boolean>> filteredCache;

    public static void rebuildCache() {
        filterModels = getRegexFilters();

        for (var filter : filterModels) {
            filter.buildPattern();
        }

        filteredCache = new LongSparseArray<>();
    }

    private static boolean isFiltered(CharSequence text, long dialogId) {
        if (!NaConfig.INSTANCE.getRegexFiltersEnabled().Bool()) {
            return false;
        }

        if (TextUtils.isEmpty(text)) {
            return false;
        }

        for (var pattern : filterModels) {
            if (!pattern.isEnabled(dialogId)) {
                continue;
            }
            if (pattern.pattern.matcher(text).find()) {
                return true;
            }
        }

        return false;
    }

    public static boolean isFiltered(MessageObject msg, MessageObject.GroupedMessages group) {
        if (!NaConfig.INSTANCE.getRegexFiltersEnabled().Bool()) {
            return false;
        }

        if (msg == null) {
            return false;
        }

        if (filterModels == null) {
            rebuildCache();
        }

        Boolean res;

        long dialogId = msg.getDialogId();
        var cached = filteredCache.get(dialogId);
        if (cached != null) {
            res = cached.get(msg.getId());
            if (res != null) {
                return res;
            }
        }

        res = isFiltered(getMessageText(msg, group), dialogId);

        if (cached == null) {
            cached = new HashMap<>();
            filteredCache.put(dialogId, cached);
        }

        cached.put(msg.getId(), res);

        if (group != null && group.messages != null && !group.messages.isEmpty()) {
            for (var m : group.messages) {
                cached.put(m.getId(), res);
            }
        }

        return res;
    }

    public static class FilterModel {
        @Expose
        public String regex;
        @Expose
        public boolean caseInsensitive;
        @Expose
        public ArrayList<Long> enabledGroups;
        @Expose
        public ArrayList<Long> disabledGroups;
        public Pattern pattern;

        public void buildPattern() {
            var flags = Pattern.MULTILINE;
            if (caseInsensitive) {
                flags |= Pattern.CASE_INSENSITIVE;
            }
            pattern = Pattern.compile(regex, flags);
        }

        public boolean defaultStatus() {
            return enabledGroups.contains(0L);
        }

        public boolean isEnabled(Long id) {
            boolean status = defaultStatus();
            if (id == 0L) return status;
            if (status) {
                if (disabledGroups.contains(id)) {
                    return false;
                }
            } else {
                if (enabledGroups.contains(id)) {
                    return true;
                }
            }
            return status;
        }

        public void setEnabled(boolean enabled, Long id) {
            enabledGroups.remove(id);
            disabledGroups.remove(id);
            if (enabled) {
                enabledGroups.add(id);
            } else {
                disabledGroups.add(id);
            }
        }
    }
}

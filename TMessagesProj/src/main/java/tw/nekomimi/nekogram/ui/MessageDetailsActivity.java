package tw.nekomimi.nekogram.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.SpannableString;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.ProfileActivity;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tw.nekomimi.nekogram.ui.MessageDetailsActivity.MessageDetailItem.ItemType;

public class MessageDetailsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private final MessageObject messageObject;
    private TLRPC.Chat fromChat;
    private TLRPC.User fromUser;
    private String filePath;
    private String fileName;
    private StringBuilder fwdBuilder;
    private UndoView copyTooltip;

    public static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .setExclusionStrategies(new CustomExclusionStrategy()).create();
    public static final Gson prettyGson = new GsonBuilder()
            .registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .setPrettyPrinting()
            .setExclusionStrategies(new CustomExclusionStrategy()).create();

    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
        }
    }

    private static class CustomExclusionStrategy implements com.google.gson.ExclusionStrategy {
        @Override
        public boolean shouldSkipField(com.google.gson.FieldAttributes f) {
            String name = f.getName();
            if ("parentRichText".equals(name) || "mChangingConfigurations".equals(name)) {
                return true;
            }
            return name.equals("text") && f.getDeclaringClass() != null
                    && f.getDeclaringClass().getName().equals("org.telegram.tgnet.tl.TL_iv$RichText");
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    public MessageDetailsActivity(MessageObject messageObject) {
        this.messageObject = messageObject;
        if (messageObject.messageOwner.peer_id != null && messageObject.messageOwner.peer_id.channel_id != 0) {
            fromChat = getMessagesController().getChat(messageObject.messageOwner.peer_id.channel_id);
        } else if (messageObject.messageOwner.peer_id != null && messageObject.messageOwner.peer_id.chat_id != 0) {
            fromChat = getMessagesController().getChat(messageObject.messageOwner.peer_id.chat_id);
        }
        if (messageObject.messageOwner.from_id.user_id != 0) {
            fromUser = getMessagesController().getUser(messageObject.messageOwner.from_id.user_id);
        }
        filePath = messageObject.messageOwner.attachPath;
        if (!TextUtils.isEmpty(filePath)) {
            File temp = new File(filePath);
            if (!temp.exists()) {
                filePath = null;
            }
        }
        if (TextUtils.isEmpty(filePath)) {
            filePath = FileLoader.getInstance(currentAccount).getPathToMessage(messageObject.messageOwner).toString();
            File temp = new File(filePath);
            if (!temp.exists()) {
                filePath = null;
            }
        }
        if (TextUtils.isEmpty(filePath)) {
            filePath = FileLoader.getInstance(currentAccount).getPathToAttach(messageObject.getDocument(), true).toString();
            File temp = new File(filePath);
            if (!temp.isFile()) {
                filePath = null;
            }
        }
        if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.document != null) {
            if (TextUtils.isEmpty(messageObject.messageOwner.media.document.file_name)) {
                for (int a = 0; a < messageObject.messageOwner.media.document.attributes.size(); a++) {
                    if (messageObject.messageOwner.media.document.attributes.get(a) instanceof TLRPC.TL_documentAttributeFilename) {
                        fileName = messageObject.messageOwner.media.document.attributes.get(a).file_name;
                    }
                }
            } else {
                fileName = messageObject.messageOwner.media.document.file_name;
            }
        }
        if (messageObject.isForwarded()) {
            fwdBuilder = new StringBuilder();
            if (messageObject.messageOwner.fwd_from.from_id == null) {
                fwdBuilder.append(messageObject.messageOwner.fwd_from.from_name);
            } else {
                if (messageObject.messageOwner.fwd_from.from_id.channel_id != 0) {
                    TLRPC.Chat chat = getMessagesController().getChat(messageObject.messageOwner.fwd_from.from_id.channel_id);
                    fwdBuilder.append(chat.title);
                    fwdBuilder.append("\n");
                    if (!TextUtils.isEmpty(chat.username)) {
                        fwdBuilder.append("@");
                        fwdBuilder.append(chat.username);
                        fwdBuilder.append("\n");
                    }
                    fwdBuilder.append(chat.id);
                } else if (messageObject.messageOwner.fwd_from.from_id.user_id != 0) {
                    TLRPC.User user = getMessagesController().getUser(messageObject.messageOwner.fwd_from.from_id.channel_id);
                    if(user!=null){
                        fwdBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                        fwdBuilder.append("\n");
                        if (!TextUtils.isEmpty(user.username)) {
                            fwdBuilder.append("@");
                            fwdBuilder.append(user.username);
                            fwdBuilder.append("\n");
                        }
                        fwdBuilder.append(user.id);
                    } else fwdBuilder.append("null user");
                } else if (!TextUtils.isEmpty(messageObject.messageOwner.fwd_from.from_name)) {
                    fwdBuilder.append(messageObject.messageOwner.fwd_from.from_name);
                }
            }
        }
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    @SuppressLint({"NewApi", "RtlHardcoded"})
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString(R.string.MessageDetails));

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position, x, y) -> {
            MessageDetailItem item = listAdapter.getItemAtPosition(position);
            if (item == null) return;
            
            switch (item.viewType) {
                case ItemType.VIEW_TYPE_EXPORT:
                    final TLRPC.Message exportMessage = messageObject.messageOwner;
                    org.telegram.messenger.Utilities.globalQueue.postRunnable(() -> {
                        String exported;
                        try {
                            exported = gson.toJson(exportMessage);
                        } catch (Throwable e) {
                            FileLog.e(e);
                            exported = "";
                        }
                        final String finalExported = exported;
                        AndroidUtilities.runOnUIThread(() -> {
                            try {
                                AndroidUtilities.addToClipboard(finalExported);
                                BulletinFactory.of(this).createCopyBulletin(LocaleController.formatString(R.string.TextCopied)).show();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        });
                    });
                    break;
                    
                case ItemType.VIEW_TYPE_DETAIL:
                    TextDetailSettingsCell textCell = (TextDetailSettingsCell) view;
                    try {
                        AndroidUtilities.addToClipboard(textCell.getValueTextView().getText());
                        BulletinFactory.of(this).createCopyBulletin(LocaleController.formatString(R.string.TextCopied)).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    break;
            }
        });
        listView.setOnItemLongClickListener((view, position) -> {
            MessageDetailItem item = listAdapter.getItemAtPosition(position);
            if (item == null) return false;
            
            switch (item.viewType) {
                case ItemType.VIEW_TYPE_DETAIL:
                    if (item.actionType == ActionType.ACTION_SHARE_FILE) {
                        AndroidUtilities.runOnUIThread(() -> {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("application/octet-stream");
                            File f = new File(filePath);
                            if (Build.VERSION.SDK_INT >= 24) {
                                try {
                                    Uri uri = FileProvider.getUriForFile(getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", f);
                                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                                    intent.setClipData(ClipData.newRawUri(null, uri));
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                } catch (Exception ignore) {
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                                }
                            } else {
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.ShareFile)), 500);
                        });
                        return true;
                    } else if (item.actionType == ActionType.ACTION_OPEN_CHAT) {
                        if (fromChat != null) {
                            Bundle args = new Bundle();
                            args.putLong("chat_id", fromChat.id);
                            ProfileActivity fragment = new ProfileActivity(args);
                            presentFragment(fragment);
                        }
                        return true;
                    } else if (item.actionType == ActionType.ACTION_OPEN_USER) {
                        if (fromUser != null) {
                            Bundle args = new Bundle();
                            args.putLong("user_id", fromUser.id);
                            ProfileActivity fragment = new ProfileActivity(args);
                            presentFragment(fragment);
                        }
                        return true;
                    }
                    break;
            }
            return false;
        });
        listView.setSections(true);

        copyTooltip = new UndoView(context);
        copyTooltip.setInfoText(LocaleController.getString(R.string.TextCopied));
        frameLayout.addView(copyTooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override
    public void onPause() {
        super.onPause();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            if (listView != null) {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    View child = listView.getChildAt(i);
                    if (child instanceof JsonTextSettingsCell) {
                        JsonTextSettingsCell cell = (JsonTextSettingsCell) child;
                        cell.refreshHighlighting();
                        if (listAdapter != null) {
                            cell.cacheIfReady(listAdapter.getHighlightedChunks());
                        }
                    }
                }
            }
        }
    }

    private void updateRows() {
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        return themeDescriptions;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    private enum ActionType {
        NONE,
        ACTION_SHARE_FILE,
        ACTION_OPEN_CHAT,
        ACTION_OPEN_USER
    }

    static class MessageDetailItem {
        static class ItemType {
            static final int VIEW_TYPE_DIVIDER = 0;
            static final int VIEW_TYPE_DETAIL = 1;
            static final int VIEW_TYPE_EXPORT = 2;
            static final int VIEW_TYPE_INFO = 3;
        }

        int viewType;
        String title;
        CharSequence value;
        ActionType actionType;
        boolean showDivider;
        boolean multilineDetail;
        boolean isFirstChunk;
        boolean isLastChunk;

        public MessageDetailItem(String title, CharSequence value, boolean showDivider, ActionType actionType) {
            this.viewType = ItemType.VIEW_TYPE_DETAIL;
            this.title = title;
            this.value = value;
            this.showDivider = showDivider;
            this.actionType = actionType;
        }

        public MessageDetailItem() {
            this.viewType = ItemType.VIEW_TYPE_DIVIDER;
        }

        public MessageDetailItem(int viewType) {
            this.viewType = viewType;
        }

        public MessageDetailItem(int viewType, String title, String value, boolean multilineDetail) {
            this.viewType = viewType;
            this.title = title;
            this.value = value;
            this.multilineDetail = multilineDetail;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context mContext;
        private final List<MessageDetailItem> items = new ArrayList<>();
        private final java.util.HashMap<String, SpannableString> highlightedChunks = new java.util.HashMap<>();
        private String fullJsonText = "";

        public java.util.HashMap<String, SpannableString> getHighlightedChunks() {
            return highlightedChunks;
        }

        public ListAdapter(Context context) {
            mContext = context;
            buildItems();
        }

        public void buildItems() {
            items.clear();
            items.add(new MessageDetailItem("ID", String.valueOf(messageObject.messageOwner.id), true, ActionType.NONE));
            
            if (messageObject.scheduled) {
                items.add(new MessageDetailItem("Scheduled", "Yes", true, ActionType.NONE));
            }
            
            if (!TextUtils.isEmpty(messageObject.messageOwner.message)) {
                items.add(new MessageDetailItem("Message", messageObject.messageOwner.message, true, ActionType.NONE));
            }

            if (fromChat != null) {
                if (!fromChat.broadcast) {
                    items.add(new MessageDetailItem("Group", fromChat.title, true, ActionType.ACTION_OPEN_CHAT));
                } else {
                    items.add(new MessageDetailItem("Channel", fromChat.title, true, ActionType.ACTION_OPEN_CHAT));
                }
            }
            
            if (fromUser != null || messageObject.messageOwner.post_author != null) {
                String fromText = fromUser != null ? fromUser.first_name : messageObject.messageOwner.post_author;
                items.add(new MessageDetailItem("From", fromText, true, ActionType.ACTION_OPEN_USER));
            }
            
            if (fromUser != null && fromUser.bot) {
                items.add(new MessageDetailItem("Bot", "Yes", true, ActionType.NONE));
            }
            var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            if (messageObject.messageOwner.date != 0) {
                items.add(new MessageDetailItem("Date", format.format(new Date(messageObject.messageOwner.date * 1000L)), true, ActionType.NONE));
            }
            
            if (messageObject.messageOwner.edit_date != 0) {
                items.add(new MessageDetailItem("Edited", format.format(new Date(messageObject.messageOwner.edit_date * 1000L)), true, ActionType.NONE));
            }
            
            if (messageObject.isForwarded() && fwdBuilder != null) {
                items.add(new MessageDetailItem("Forward from", fwdBuilder.toString(), true, ActionType.NONE));
            }
            
            if (!TextUtils.isEmpty(fileName)) {
                items.add(new MessageDetailItem("File Name", fileName, true, ActionType.NONE));
            }
            
            if (!TextUtils.isEmpty(filePath)) {
                items.add(new MessageDetailItem("File Path", filePath, true, ActionType.ACTION_SHARE_FILE));
            }
            
            if (messageObject.getSize() != 0) {
                String sizeStr = String.format("%s (%d)", AndroidUtilities.formatFileSize(messageObject.getSize()), messageObject.getSize());
                items.add(new MessageDetailItem("File Size", sizeStr, true, ActionType.NONE));
            }
            
            if (messageObject.messageOwner.media != null && (
                    (messageObject.messageOwner.media.photo != null && messageObject.messageOwner.media.photo.dc_id > 0) ||
                            (messageObject.messageOwner.media.document != null && messageObject.messageOwner.media.document.dc_id > 0)
            )) {
                int dcId = 0;
                if (messageObject.messageOwner.media.photo != null) {
                    dcId = messageObject.messageOwner.media.photo.dc_id;
                } else if (messageObject.messageOwner.media.document != null) {
                    dcId = messageObject.messageOwner.media.document.dc_id;
                }
                items.add(new MessageDetailItem("DC", String.valueOf(dcId), true, ActionType.NONE));
            }
            
            if (messageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) {
                items.add(new MessageDetailItem("Reply Markup", "Inline", true, ActionType.NONE));
            }
            
            items.add(new MessageDetailItem());

            MessageDetailItem jsonPlaceholder = new MessageDetailItem(ItemType.VIEW_TYPE_INFO, "JSON", "", true);
            jsonPlaceholder.isFirstChunk = true;
            jsonPlaceholder.isLastChunk = true;
            items.add(jsonPlaceholder);
            jsonItemIndex = items.size() - 1;

            items.add(new MessageDetailItem(ItemType.VIEW_TYPE_EXPORT));

            items.add(new MessageDetailItem());

            final int placeholderIndex = jsonItemIndex;
            final TLRPC.Message message = messageObject.messageOwner;
            org.telegram.messenger.Utilities.globalQueue.postRunnable(() -> {
                String jsonText;
                try {
                    jsonText = prettyGson.toJson(message);
                } catch (Throwable e) {
                    FileLog.e(e);
                    jsonText = "";
                }
                final String finalJson = jsonText == null ? "" : jsonText;
                final List<String> chunks = splitJsonIntoChunks(finalJson, JSON_CHUNK_SIZE, JSON_CHUNK_THRESHOLD);
                AndroidUtilities.runOnUIThread(() -> {
                    if (listAdapter == null || listAdapter != this) return;
                    if (placeholderIndex < 0 || placeholderIndex >= items.size()) return;
                    MessageDetailItem current = items.get(placeholderIndex);
                    if (current.viewType != ItemType.VIEW_TYPE_INFO) return;

                    fullJsonText = finalJson;

                    items.remove(placeholderIndex);
                    if (chunks.isEmpty()) {
                        notifyItemRemoved(placeholderIndex);
                        return;
                    }
                    int insertCount = chunks.size();
                    for (int i = 0; i < insertCount; i++) {
                        MessageDetailItem chunkItem = new MessageDetailItem(ItemType.VIEW_TYPE_INFO, i == 0 ? "JSON" : null, chunks.get(i), true);
                        chunkItem.isFirstChunk = (i == 0);
                        chunkItem.isLastChunk = (i == insertCount - 1);
                        items.add(placeholderIndex + i, chunkItem);
                    }
                    notifyItemChanged(placeholderIndex);
                    if (insertCount != 1) {
                        notifyItemRangeInserted(placeholderIndex + 1, insertCount - 1);
                    }
                });
            });
        }

        private int jsonItemIndex = -1;
        private static final int JSON_CHUNK_SIZE = 4000;
        private static final int JSON_CHUNK_THRESHOLD = 12000;

        private List<String> splitJsonIntoChunks(String json, int maxLen, int threshold) {
            List<String> result = new ArrayList<>();
            if (json == null || json.isEmpty()) {
                return result;
            }
            int len = json.length();
            if (len <= threshold) {
                result.add(json);
                return result;
            }
            int start = 0;
            while (start < len) {
                int end = Math.min(start + maxLen, len);
                if (end < len) {
                    int nl = json.lastIndexOf('\n', end);
                    if (nl > start) {
                        end = nl;
                    }
                }
                String piece = json.substring(start, end);
                if (!piece.isEmpty()) {
                    result.add(piece);
                }
                if (end < len && json.charAt(end) == '\n') {
                    start = end + 1;
                } else {
                    start = end;
                }
            }
            return result;
        }

        public String getFullJsonText() {
            return fullJsonText;
        }

        public MessageDetailItem getItemAtPosition(int position) {
            if (position >= 0 && position < items.size()) {
                return items.get(position);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageDetailItem item = items.get(position);
            
            switch (item.viewType) {
                case MessageDetailItem.ItemType.VIEW_TYPE_DIVIDER:
                    if (position + 1 >= items.size() || items.get(position + 1).viewType == MessageDetailItem.ItemType.VIEW_TYPE_DIVIDER) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                    
                case MessageDetailItem.ItemType.VIEW_TYPE_DETAIL:
                    TextDetailSettingsCell textCell = (TextDetailSettingsCell) holder.itemView;
                    textCell.setMultilineDetail(true);
                    boolean hasNextDivider = (position + 1 < items.size()) && items.get(position + 1).viewType != MessageDetailItem.ItemType.VIEW_TYPE_DIVIDER;
                    textCell.setTextAndValue(item.title, item.value, hasNextDivider);
                    break;
                    
                case MessageDetailItem.ItemType.VIEW_TYPE_EXPORT:
                    TextSettingsCell exportCell = (TextSettingsCell) holder.itemView;
                    exportCell.setText(LocaleController.getString(R.string.ExportAsJson), false);
                    exportCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                    break;
                    
                case MessageDetailItem.ItemType.VIEW_TYPE_INFO:
                    JsonTextSettingsCell jsonCell = (JsonTextSettingsCell) holder.itemView;
                    boolean jsonHasNextDivider = item.isLastChunk &&
                            (position + 1 < items.size()) &&
                            items.get(position + 1).viewType != MessageDetailItem.ItemType.VIEW_TYPE_DIVIDER;
                    jsonCell.setTitle(item.isFirstChunk ? item.title : null);
                    jsonCell.setChunkLayout(item.isFirstChunk, item.isLastChunk);
                    String chunkText = item.value == null ? "" : item.value.toString();
                    SpannableString cached = highlightedChunks.get(chunkText);
                    jsonCell.setJsonChunk(chunkText, cached, jsonHasNextDivider, highlightedChunks);
                    break;
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (position < 0 || position >= items.size()) {
                return false;
            }
            MessageDetailItem item = items.get(position);
            return item.viewType != MessageDetailItem.ItemType.VIEW_TYPE_DIVIDER && 
                   item.viewType != MessageDetailItem.ItemType.VIEW_TYPE_EXPORT;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case MessageDetailItem.ItemType.VIEW_TYPE_DIVIDER:
                    view = new ShadowSectionCell(mContext);
                    break;
                    
                case MessageDetailItem.ItemType.VIEW_TYPE_DETAIL:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                    
                case MessageDetailItem.ItemType.VIEW_TYPE_INFO:
                    JsonTextSettingsCell jsonCellNew = new JsonTextSettingsCell(mContext);
                    jsonCellNew.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    jsonCellNew.setFullJsonProvider(new JsonTextSettingsCell.FullJsonProvider() {
                        @Override
                        public String getFullJson() {
                            return fullJsonText;
                        }

                        @Override
                        public void onFullJsonCopied() {
                            try {
                                BulletinFactory.of(MessageDetailsActivity.this)
                                        .createCopyBulletin(LocaleController.formatString(R.string.TextCopied))
                                        .show();
                            } catch (Throwable ignored) {
                            }
                        }
                    });
                    view = jsonCellNew;
                    break;
                    
                case MessageDetailItem.ItemType.VIEW_TYPE_EXPORT:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= 0 && position < items.size()) {
                return items.get(position).viewType;
            }
            return MessageDetailItem.ItemType.VIEW_TYPE_DIVIDER;
        }
    }
}

package tw.nekomimi.nekogram.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.Unit;
import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.PopupBuilder;
import tw.nekomimi.nkmr.NekomuraConfig;
import tw.nekomimi.nkmr.Cells;
import tw.nekomimi.nkmr.Cells.*;

@SuppressLint("RtlHardcoded")
public class NekoChatSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private ActionBarMenuItem menuItem;
    private StickerSizeCell stickerSizeCell;

    private ArrayList<NekomuraTGCell> rows = new ArrayList<>();
    private Cells nkmrCells = new Cells(this, rows);

    private final NekomuraTGCell header0 = addNekomuraTGCell(nkmrCells.new NekomuraTGHeader(LocaleController.getString("StickerSize")));
    private final NekomuraTGCell stickerSizeRow = addNekomuraTGCell(nkmrCells.new NekomuraTGCustom(998, true));
    private final NekomuraTGCell divider0 = addNekomuraTGCell(nkmrCells.new NekomuraTGDivider());

    private final NekomuraTGCell header1 = addNekomuraTGCell(nkmrCells.new NekomuraTGHeader(LocaleController.getString("Chat")));
    private final NekomuraTGCell unreadBadgeOnBackButton = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.unreadBadgeOnBackButton));
    private final NekomuraTGCell ignoreBlockedRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.ignoreBlocked, LocaleController.getString("IgnoreBlockedAbout")));
    private final NekomuraTGCell ignoreMutedCountRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.ignoreMutedCount));
    private final NekomuraTGCell disableChatActionRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableChatAction));
    private final NekomuraTGCell disablePhotoSideActionRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disablePhotoSideAction));
    private final NekomuraTGCell hideKeyboardOnChatScrollRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.hideKeyboardOnChatScroll));
    private final NekomuraTGCell disableVibrationRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableVibration));
    private final NekomuraTGCell skipOpenLinkConfirmRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.skipOpenLinkConfirm));
    private final NekomuraTGCell rearVideoMessagesRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.rearVideoMessages));
    private final NekomuraTGCell confirmAVRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.confirmAVMessage));
    private final NekomuraTGCell useChatAttachMediaMenuRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.useChatAttachMediaMenu, LocaleController.getString("UseChatAttachEnterMenuNotice")));
    private final NekomuraTGCell disableLinkPreviewByDefaultRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableLinkPreviewByDefault, LocaleController.getString("DisableLinkPreviewByDefaultNotice")));
    private final NekomuraTGCell sendCommentAfterForwardRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.sendCommentAfterForward));
    private final NekomuraTGCell disableProximityEventsRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableProximityEvents));
    private final NekomuraTGCell disableTrendingRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableTrending));
    private final NekomuraTGCell dontSendGreetingStickerRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.dontSendGreetingSticker));
    private final NekomuraTGCell hideTimeForStickerRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.hideTimeForSticker));
    private final NekomuraTGCell takeGIFasVideoRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.takeGIFasVideo));
    private final NekomuraTGCell maxRecentStickerCountRow = addNekomuraTGCell(nkmrCells.new NekomuraTGCustom(Cells.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final NekomuraTGCell disableSwipeToNextRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableSwipeToNext));
    //private final NekomuraTGCell disableChoosingStickerRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableChoosingSticker));
    private final NekomuraTGCell disableRemoteEmojiInteractionsRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableRemoteEmojiInteractions));
    private final NekomuraTGCell mapPreviewRow = addNekomuraTGCell(nkmrCells.new NekomuraTGSelectBox(null, NekomuraConfig.mapPreviewProvider,
            new String[]{
                    LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram),
                    LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex),
                    LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody)
            }, null));
    private final NekomuraTGCell messageMenuRow = addNekomuraTGCell(nkmrCells.new NekomuraTGSelectBox(LocaleController.getString("MessageMenu"), null, null, () -> {
        showMessageMenuAlert();
    }));
    private final NekomuraTGCell divider1 = addNekomuraTGCell(nkmrCells.new NekomuraTGDivider());

    private final NekomuraTGCell header2 = addNekomuraTGCell(nkmrCells.new NekomuraTGHeader(LocaleController.getString("AutoDownload")));
    private final NekomuraTGCell win32Row = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableAutoDownloadingWin32Executable));
    private final NekomuraTGCell archiveRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.disableAutoDownloadingArchive));
    private final NekomuraTGCell divider2 = addNekomuraTGCell(nkmrCells.new NekomuraTGDivider());

    private final NekomuraTGCell header3 = addNekomuraTGCell(nkmrCells.new NekomuraTGHeader(LocaleController.getString("Folder")));
    private final NekomuraTGCell showTabsOnForwardRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.showTabsOnForward));
    private final NekomuraTGCell hideAllTabRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.hideAllTab, LocaleController.getString("HideAllTabAbout")));
    private final NekomuraTGCell pressTitleToOpenAllChatsRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekomuraConfig.pressTitleToOpenAllChats));
    private final NekomuraTGCell tabsTitleTypeRow = addNekomuraTGCell(nkmrCells.new NekomuraTGSelectBox(null, NekomuraConfig.tabsTitleType,
            new String[]{
                    LocaleController.getString("TabTitleTypeText", R.string.TabTitleTypeText),
                    LocaleController.getString("TabTitleTypeIcon", R.string.TabTitleTypeIcon),
                    LocaleController.getString("TabTitleTypeMix", R.string.TabTitleTypeMix)
            }, null));
    private final NekomuraTGCell divider3 = addNekomuraTGCell(nkmrCells.new NekomuraTGDivider());

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateRows();

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("Chat", R.string.Chat));

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }

        ActionBarMenu menu = actionBar.createMenu();
        menuItem = menu.addItem(0, R.drawable.ic_ab_other);
        menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        menuItem.addSubItem(1, R.drawable.msg_reset, LocaleController.getString("ResetStickerSize", R.string.ResetStickerSize));
        menuItem.setVisibility(NekomuraConfig.stickerSize.Float() != 14.0f ? View.VISIBLE : View.GONE);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == 1) {
                    NekomuraConfig.stickerSize.setConfigFloat(14.0f);
                    menuItem.setVisibility(View.GONE);
                    stickerSizeCell.invalidate();
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

        // Fragment: Set OnClick Callbacks
        listView.setOnItemClickListener((view, position, x, y) -> {
            NekomuraTGCell a = rows.get(position);
            if (a instanceof NekomuraTGTextCheck) {
                ((NekomuraTGTextCheck) a).onClick((TextCheckCell) view);
            } else if (a instanceof NekomuraTGSelectBox) {
                ((NekomuraTGSelectBox) a).onClick();
            } else if (a instanceof NekomuraTGTextInput) {
                ((NekomuraTGTextInput) a).onClick();
            } else if (a instanceof NekomuraTGTextDetail) {
                RecyclerListView.OnItemClickListener o = ((NekomuraTGTextDetail) a).onItemClickListener;
                if (o != null) {
                    try {
                        o.onItemClick(view, position);
                    } catch (Exception e) {
                    }
                }
            } else if (a instanceof NekomuraTGCustom) { // Custom onclick
                if (position == rows.indexOf(maxRecentStickerCountRow)) {
                    final int[] counts = {20, 30, 40, 50, 80, 100, 120, 150, 180, 200};
                    List<String> types = Arrays.stream(counts)
                            .filter(i -> i <= getMessagesController().maxRecentStickersCount)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.toList());
                    PopupBuilder builder = new PopupBuilder(view);
                    builder.setItems(types, (i, str) -> {
                        NekomuraConfig.maxRecentStickerCount.setConfigInt(Integer.parseInt(str.toString()));
                        listAdapter.notifyItemChanged(position);
                        return Unit.INSTANCE;
                    });
                    builder.show();
                }
            }
        });

        // Cells: Set OnSettingChanged Callbacks
        nkmrCells.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NekomuraConfig.hideAllTab.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);
            } else if (key.equals(NekomuraConfig.pressTitleToOpenAllChats.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);
            } else if (key.equals(NekomuraConfig.tabsTitleType.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);
            } else if (key.equals(NekomuraConfig.disableProximityEvents.getKey())) {
                MediaController.getInstance().recreateProximityWakeLock();
            }
        };

        //Cells: Set ListAdapter
        nkmrCells.setListAdapter(listView, listAdapter);

        return fragmentView;
    }

    public NekomuraTGCell addNekomuraTGCell(NekomuraTGCell a) {
        rows.add(a);
        return a;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
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

    private void showMessageMenuAlert() {
        if (getParentActivity() == null) {
            return;
        }
        Context context = getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("MessageMenu", R.string.MessageMenu));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int count = NekoXConfig.developerMode ? 10 : 9;
        for (int a = 0; a < count; a++) {
            TextCheckCell textCell = new TextCheckCell(context);
            switch (a) {
                case 0: {
                    textCell.setTextAndCheck(LocaleController.getString("DeleteDownloadedFile", R.string.DeleteDownloadedFile), NekomuraConfig.showDeleteDownloadedFile.Bool(), false);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheck(LocaleController.getString("AddToSavedMessages", R.string.AddToSavedMessages), NekomuraConfig.showAddToSavedMessages.Bool(), false);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheck(LocaleController.getString("Repeat", R.string.Repeat), NekomuraConfig.showRepeat.Bool(), false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheck(LocaleController.getString("ViewHistory", R.string.ViewHistory), NekomuraConfig.showViewHistory.Bool(), false);
                    break;
                }
                case 4: {
                    textCell.setTextAndCheck(LocaleController.getString("Translate", R.string.Translate), NekomuraConfig.showTranslate.Bool(), false);
                    break;
                }
                case 5: {
                    textCell.setTextAndCheck(LocaleController.getString("ReportChat", R.string.ReportChat), NekomuraConfig.showReport.Bool(), false);
                    break;
                }
                case 6: {
                    textCell.setTextAndCheck(LocaleController.getString("EditAdminRights", R.string.EditAdminRights), NekomuraConfig.showAdminActions.Bool(), false);
                    break;
                }
                case 7: {
                    textCell.setTextAndCheck(LocaleController.getString("ChangePermissions", R.string.ChangePermissions), NekomuraConfig.showChangePermissions.Bool(), false);
                    break;
                }
                case 8: {
                    textCell.setTextAndCheck(LocaleController.getString("Hide", R.string.Hide), NekomuraConfig.showMessageHide.Bool(), false);
                    break;
                }
                case 9: {
                    textCell.setTextAndCheck(LocaleController.getString("MessageDetails", R.string.MessageDetails), NekomuraConfig.showMessageDetails.Bool(), false);
                    break;
                }
            }
            textCell.setTag(a);
            textCell.setBackground(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(v2 -> {
                Integer tag = (Integer) v2.getTag();
                switch (tag) {
                    case 0: {
                        textCell.setChecked(NekomuraConfig.showDeleteDownloadedFile.toggleConfigBool());
                        break;
                    }
                    case 1: {
                        textCell.setChecked(NekomuraConfig.showAddToSavedMessages.toggleConfigBool());
                        break;
                    }
                    case 2: {
                        textCell.setChecked(NekomuraConfig.showRepeat.toggleConfigBool());
                        break;
                    }
                    case 3: {
                        textCell.setChecked(NekomuraConfig.showViewHistory.toggleConfigBool());
                        break;
                    }
                    case 4: {
                        textCell.setChecked(NekomuraConfig.showTranslate.toggleConfigBool());
                        break;
                    }
                    case 5: {
                        textCell.setChecked(NekomuraConfig.showReport.toggleConfigBool());
                        break;
                    }
                    case 6: {
                        textCell.setChecked(NekomuraConfig.showAdminActions.toggleConfigBool());
                        break;
                    }
                    case 7: {
                        textCell.setChecked(NekomuraConfig.showChangePermissions.toggleConfigBool());
                        break;
                    }
                    case 8: {
                        textCell.setChecked(NekomuraConfig.showMessageHide.toggleConfigBool());
                        break;
                    }
                    case 9: {
                        textCell.setChecked(NekomuraConfig.showMessageDetails.toggleConfigBool());
                        break;
                    }
                }
            });
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setView(linearLayout);
        showDialog(builder.create());
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
       /* if (id == NotificationCenter.emojiDidLoad) {
            if (listView != null) {
                listView.invalidateViews();
            }
        }*/
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
    }

    private class StickerSizeCell extends FrameLayout {

        private StickerSizePreviewMessagesCell messagesCell;
        private SeekBarView sizeBar;
        private int startStickerSize = 2;
        private int endStickerSize = 20;

        private TextPaint textPaint;

        public StickerSizeCell(Context context) {
            super(context);

            setWillNotDraw(false);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(AndroidUtilities.dp(16));

            sizeBar = new SeekBarView(context);
            sizeBar.setReportChanges(true);
            sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public void onSeekBarDrag(boolean stop, float progress) {
                    NekomuraConfig.stickerSize.setConfigFloat(startStickerSize + (endStickerSize - startStickerSize) * progress);
                    StickerSizeCell.this.invalidate();
                    menuItem.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSeekBarPressed(boolean pressed) {

                }
            });
            addView(sizeBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.LEFT | Gravity.TOP, 9, 5, 43, 11));

            messagesCell = new StickerSizePreviewMessagesCell(context, parentLayout);
            addView(messagesCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 53, 0, 0));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText("" + Math.round(NekomuraConfig.stickerSize.Float()), getMeasuredWidth() - AndroidUtilities.dp(39), AndroidUtilities.dp(28), textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            sizeBar.setProgress((NekomuraConfig.stickerSize.Float() - startStickerSize) / (float) (endStickerSize - startStickerSize));
        }

        @Override
        public void invalidate() {
            super.invalidate();
            messagesCell.invalidate();
            sizeBar.invalidate();
        }
    }

    //impl ListAdapter
    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            NekomuraTGCell a = rows.get(position);
            if (a != null) {
                return a.isEnabled();
            }
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            NekomuraTGCell a = rows.get(position);
            if (a != null) {
                return a.getType();
            }
            return Cells.ITEM_TYPE_TEXT_DETAIL;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            NekomuraTGCell a = rows.get(position);
            if (a != null) {
                if (a instanceof NekomuraTGCustom) {
                    // Custom binds
                    if (holder.itemView instanceof TextSettingsCell) {
                        TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                        if (position == rows.indexOf(maxRecentStickerCountRow)) {
                            textCell.setTextAndValue(LocaleController.getString("maxRecentStickerCount", R.string.maxRecentStickerCount), String.valueOf(NekomuraConfig.maxRecentStickerCount.Int()), true);
                        }
                    }
                } else {
                    // Default binds
                    a.onBindViewHolder(holder);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case Cells.ITEM_TYPE_DIVIDER:
                    view = new ShadowSectionCell(mContext);
                    break;
                case Cells.ITEM_TYPE_TEXT_SETTINGS_CELL:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case Cells.ITEM_TYPE_TEXT_CHECK:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case Cells.ITEM_TYPE_HEADER:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case Cells.ITEM_TYPE_TEXT_DETAIL:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case Cells.ITEM_TYPE_TEXT:
                    view = new TextInfoPrivacyCell(mContext);
                    // view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 998:
                    view = stickerSizeCell = new StickerSizeCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }
    }
}
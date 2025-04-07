package tw.nekomimi.nekogram.settings;

import static tw.nekomimi.nekogram.settings.BaseNekoSettingsActivity.PARTIAL;

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
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.UndoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import kotlin.Unit;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.ConfigCellCustom;
import tw.nekomimi.nekogram.config.cell.ConfigCellDivider;
import tw.nekomimi.nekogram.config.cell.ConfigCellHeader;
import tw.nekomimi.nekogram.config.cell.ConfigCellSelectBox;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextCheck;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextDetail;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextInput;
import tw.nekomimi.nekogram.helpers.remote.EmojiHelper;
import tw.nekomimi.nekogram.ui.PopupBuilder;
import xyz.nextalone.nagram.NaConfig;
import xyz.nextalone.nagram.helper.DoubleTap;

@SuppressLint("RtlHardcoded")
public class NekoChatSettingsActivity extends BaseNekoXSettingsActivity implements NotificationCenter.NotificationCenterDelegate, EmojiHelper.EmojiPacksLoadedListener {

    private final CellGroup cellGroup = new CellGroup(this);

    // Sticker Size
    private final AbstractConfigCell header0 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("StickerSize")));
    private final AbstractConfigCell stickerSizeRow = cellGroup.appendCell(new ConfigCellCustom("StickerSize", ConfigCellCustom.CUSTOM_ITEM_StickerSize, true));
    private final AbstractConfigCell divider0 = cellGroup.appendCell(new ConfigCellDivider());

    // Chats
    private final AbstractConfigCell header1 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Chat")));
    private final AbstractConfigCell emojiSetsRow = cellGroup.appendCell(new ConfigCellCustom("EmojiSet", ConfigCellCustom.CUSTOM_ITEM_EmojiSet, true));
    private final AbstractConfigCell unreadBadgeOnBackButton = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.unreadBadgeOnBackButton));
    private final AbstractConfigCell sendCommentAfterForwardRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.sendCommentAfterForward));
    private final AbstractConfigCell useChatAttachMediaMenuRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useChatAttachMediaMenu, LocaleController.getString("UseChatAttachEnterMenuNotice")));
    private final AbstractConfigCell disableLinkPreviewByDefaultRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableLinkPreviewByDefault, LocaleController.getString("DisableLinkPreviewByDefaultNotice")));
    private final AbstractConfigCell takeGIFasVideoRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.takeGIFasVideo));
    private final AbstractConfigCell showSmallGifRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowSmallGIF()));
    private final AbstractConfigCell showSeconds = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.showSeconds));
    private final AbstractConfigCell showBottomActionsWhenSelectingRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.showBottomActionsWhenSelecting));
    private final AbstractConfigCell labelChannelUserRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.labelChannelUser));
    private final AbstractConfigCell hideSendAsChannelRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideSendAsChannel));
    private final AbstractConfigCell showSpoilersDirectlyRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.showSpoilersDirectly));
    private final AbstractConfigCell messageMenuRow = cellGroup.appendCell(new ConfigCellSelectBox("MessageMenu", null, null, () -> {
        if (getParentActivity() == null) return;
        showDialog(showConfigMenuAlert(getParentActivity(), "MessageMenu", new ArrayList<>() {{
            add(new ConfigCellTextCheck(NekoConfig.showDeleteDownloadedFile, null, LocaleController.getString(R.string.DeleteDownloadedFile)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowCopyPhoto()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowNoQuoteForward()));
            add(new ConfigCellTextCheck(NekoConfig.showAddToSavedMessages, null, LocaleController.getString(R.string.AddToSavedMessages)));
            add(new ConfigCellTextCheck(NekoConfig.showRepeat, null, LocaleController.getString(R.string.Repeat)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowRepeatAsCopy()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowInvertReply()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowGreatOrPoor(), null, NaConfig.INSTANCE.getCustomGreat().String()));
            add(new ConfigCellTextCheck(NekoConfig.showViewHistory, null, LocaleController.getString(R.string.ViewHistory)));
            add(new ConfigCellTextCheck(NekoConfig.showTranslate, null, LocaleController.getString(R.string.Translate)));
            add(new ConfigCellTextCheck(NekoConfig.showReport, null, LocaleController.getString(R.string.ReportChat)));
            add(new ConfigCellTextCheck(NekoConfig.showAdminActions, null, LocaleController.getString(R.string.EditAdminRights)));
            add(new ConfigCellTextCheck(NekoConfig.showChangePermissions, null, LocaleController.getString(R.string.ChangePermissions)));
            add(new ConfigCellTextCheck(NekoConfig.showMessageHide, null, LocaleController.getString(R.string.Hide)));
            add(new ConfigCellTextCheck(NekoConfig.showShareMessages, null, LocaleController.getString(R.string.ShareMessages)));
            add(new ConfigCellTextCheck(NekoConfig.showMessageDetails, null, LocaleController.getString(R.string.MessageDetails)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowSetReminder()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowReactions()));
        }}));
    }));
    private final AbstractConfigCell defaultDeleteMenuRow = cellGroup.appendCell(new ConfigCellSelectBox("DefaultDeleteMenu", null, null, () -> {
        if (getParentActivity() == null) return;
        showDialog(showConfigMenuAlert(getParentActivity(), NaConfig.INSTANCE.getDefaultDeleteMenu().getKey(), new ArrayList<>() {{
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDefaultDeleteMenuBanUsers()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDefaultDeleteMenReportSpam()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDefaultDeleteMenuDeleteAll()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDefaultDeleteMenuDoActionsInCommonGroups()));
        }}));
    }));
    private final AbstractConfigCell disableActionBarButtonRow = cellGroup.appendCell(new ConfigCellSelectBox(NaConfig.INSTANCE.getDisableActionBarButton().getKey(), null, null, () -> {
        if (getParentActivity() == null) return;
        showDialog(showConfigMenuAlert(getParentActivity(), NaConfig.INSTANCE.getDisableActionBarButton().getKey(), new ArrayList<>() {{
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableActionBarButtonReply()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableActionBarButtonEdit()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableActionBarButtonSelectBetween()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableActionBarButtonCopy()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableActionBarButtonForward()));
        }}));
    }));
    private final AbstractConfigCell customGreatRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getCustomGreat(), LocaleController.getString(R.string.CustomGreatHint), null,(input) -> input.isEmpty() ? (String) NaConfig.INSTANCE.getCustomGreat().defaultValue : input));
    private final AbstractConfigCell customPoorRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getCustomPoor(), LocaleController.getString(R.string.CustomPoorHint), null,(input) -> input.isEmpty() ? (String) NaConfig.INSTANCE.getCustomPoor().defaultValue : input));
    private final AbstractConfigCell customEditedMessageRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getCustomEditedMessage(), "", null));
    private final AbstractConfigCell showServicesTime = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowServicesTime()));
//    private final AbstractConfigCell combineMessageRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getCombineMessage(), new String[]{
//            LocaleController.getString("combineMessageEnabledWithReply", R.string.CombineMessageEnabledWithReply),
//            LocaleController.getString("combineMessageEnabled", R.string.CombineMessageEnabled),
//            LocaleController.getString("combineMessageDisabled", R.string.CombineMessageDisabled)
//    }, null));
    private final AbstractConfigCell dateOfForwardMsgRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDateOfForwardedMsg()));
    private final AbstractConfigCell showMessageIDRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowMessageID()));
    private final AbstractConfigCell showPremiumStarInChatRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowPremiumStarInChat()));
    private final AbstractConfigCell showPremiumAvatarAnimationRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowPremiumAvatarAnimation()));
    private final AbstractConfigCell alwaysSaveChatOffsetRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getAlwaysSaveChatOffset()));
    private final AbstractConfigCell autoInsertGIFCaptionRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getAutoInsertGIFCaption()));
    private final AbstractConfigCell defaultMonoLanguageRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getDefaultMonoLanguage(),
            null, null,
            (input) -> input.isEmpty() ? (String) NaConfig.INSTANCE.getDefaultMonoLanguage().defaultValue : input));
    private final AbstractConfigCell disableGlobalSearchRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableGlobalSearch()));
//    private final AbstractConfigCell mapPreviewRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.mapPreviewProvider,
//            new String[]{
//                    LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram),
//                    LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex),
//                    LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody)
//            }, null));
    private final AbstractConfigCell doubleTapActionRow = cellGroup.appendCell(new ConfigCellCustom("DoubleTapAction", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell autoReplaceRepeatRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getAutoReplaceRepeat()));
    private final AbstractConfigCell textStyleRow = cellGroup.appendCell(new ConfigCellSelectBox("TextStyle", null, null, () -> {
        if (getParentActivity() == null) return;
        showDialog(showConfigMenuAlert(getParentActivity(), "TextStyle", new ArrayList<>() {{
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextBold(), null, LocaleController.getString(R.string.Bold)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextItalic(), null, LocaleController.getString(R.string.Italic)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextMono(), null, LocaleController.getString(R.string.Mono)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextStrikethrough(), null, LocaleController.getString(R.string.Strike)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextUnderline(), null, LocaleController.getString(R.string.Underline)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextSpoiler(), null, LocaleController.getString(R.string.Spoiler)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextCreateLink(), null, LocaleController.getString(R.string.CreateLink)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextCreateMention(), null, LocaleController.getString(R.string.CreateMention)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextRegular(), null, LocaleController.getString(R.string.Regular)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextUndoRedo(), null, LocaleController.getString(R.string.TextUndoRedo)));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTextQuote(), null, LocaleController.getString(R.string.Quote)));
        }}));
    }));
    private final AbstractConfigCell disableZalgoSymbolsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getZalgoFilter(), LocaleController.getString("ZalgoFilterNotice", R.string.ZalgoFilterNotice)));
    private final AbstractConfigCell quickToggleAnonymousRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getQuickToggleAnonymous(), LocaleController.getString("QuickToggleAnonymousNotice", R.string.QuickToggleAnonymousNotice)));
    private final AbstractConfigCell showOnlineStatusRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowOnlineStatus(), LocaleController.getString("ShowOnlineStatusNotice", R.string.ShowOnlineStatusNotice)));
    private final AbstractConfigCell showRecentOnlineStatusRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowRecentOnlineStatus()));
    private final AbstractConfigCell disableCustomWallpaperUserRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableCustomWallpaperUser()));
    private final AbstractConfigCell disableCustomWallpaperChannelRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableCustomWallpaperChannel()));
    private final AbstractConfigCell disableRepeatInChannelRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableRepeatInChannel()));
    private final AbstractConfigCell dividerChat = cellGroup.appendCell(new ConfigCellDivider());

    // Interactions
    private final AbstractConfigCell headerInteractions = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("InteractionSettings")));
    private final AbstractConfigCell hideKeyboardOnChatScrollRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideKeyboardOnChatScroll));
    private final AbstractConfigCell rearVideoMessagesRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.rearVideoMessages));
    private final AbstractConfigCell disableInstantCameraRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableInstantCamera));
    private final AbstractConfigCell disableVibrationRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableVibration));
    private final AbstractConfigCell disableProximityEventsRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableProximityEvents));
    private final AbstractConfigCell disableTrendingRow = cellGroup.appendCell(new ConfigCellSelectBox("DisableTrending", null, null, () -> {
        if (getParentActivity() == null) return;
        showDialog(showConfigMenuAlert(getParentActivity(), "DisableTrending", new ArrayList<>() {{
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisablePremiumUpgrade()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisablePremiumExpiring()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisablePremiumChristmas()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableBirthdayContact()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisablePremiumRestore()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableStarsSubscription()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableFavoriteSearchEmojiTags()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableFeatuerdEmojis()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableFeaturedStickers()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableFeaturedGifs()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisablePremiumFavoriteEmojiTags()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableNonPremiumChannelChatShow()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableShortcutTagActions()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisablePhoneSharePrompt()));
        }}));
    }));
    private final AbstractConfigCell disableSwipeToNextRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableSwipeToNext));
    private final AbstractConfigCell disableChannelMuteButtonRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableChannelMuteButton()));
    private final AbstractConfigCell disablePhotoSideActionRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disablePhotoSideAction));
    private final AbstractConfigCell disableClickProfileGalleryViewRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableClickProfileGalleryView()));
    private final AbstractConfigCell disableRemoteEmojiInteractionsRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableRemoteEmojiInteractions));
    private final AbstractConfigCell rememberAllBackMessagesRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.rememberAllBackMessages));
    //    private final AbstractConfigCell reactionsRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.reactions,
//            new String[]{
//                    LocaleController.getString("doubleTapSendReactions", R.string.doubleTapSendReactions),
//                    LocaleController.getString("doubleTapShowReactions", R.string.doubleTapShowReactions),
//                    LocaleController.getString("ReactionsDisabled", R.string.ReactionsDisabled),
//            }, null));
    private final AbstractConfigCell showFullAboutRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowFullAbout()));
    private final AbstractConfigCell hideMessageSeenTooltipcRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getHideMessageSeenTooltip()));
    private final AbstractConfigCell typeMessageHintUseGroupNameRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getTypeMessageHintUseGroupName()));
    private final AbstractConfigCell showSendAsUnderMessageHintRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowSendAsUnderMessageHint()));
    private final AbstractConfigCell hideBotButtonInInputFieldRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getHideBotButtonInInputField()));
    private final AbstractConfigCell doNotUnarchiveBySwipeRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDoNotUnarchiveBySwipe()));
    private final AbstractConfigCell disableMarkdownRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableMarkdown()));
    private final AbstractConfigCell disableClickCommandToSendRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableClickCommandToSend(), LocaleController.getString(R.string.DisableClickCommandToSendHint)));
    private final AbstractConfigCell showQuickReplyInBotCommandsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowQuickReplyInBotCommands()));
    private final AbstractConfigCell disablePreviewVideoSoundShortcutRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisablePreviewVideoSoundShortcut(), LocaleController.getString(R.string.DisablePreviewVideoSoundShortcutNotice)));
    private final AbstractConfigCell showTimeHintRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowTimeHint()));
    private final AbstractConfigCell disableBotOpenButtonRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableBotOpenButton()));
    private final AbstractConfigCell dividerInteractions = cellGroup.appendCell(new ConfigCellDivider());

    // Sticker
    private final AbstractConfigCell headerSticker = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("StickerSettings", R.string.StickerSettings)));
    private final AbstractConfigCell dontSendGreetingStickerRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.dontSendGreetingSticker));
    private final AbstractConfigCell hideTimeForStickerRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideTimeForSticker));
    private final AbstractConfigCell stickersOnlyShowReadRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getRealHideTimeForSticker()));
    private final AbstractConfigCell hideGroupStickerRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideGroupSticker));
    private final AbstractConfigCell disablePremiumStickerAnimationRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disablePremiumStickerAnimation));
    private final AbstractConfigCell maxRecentStickerCountRow = cellGroup.appendCell(new ConfigCellCustom("MaxRecentStickerCount", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell dividerSticker = cellGroup.appendCell(new ConfigCellDivider());

    // Reaction
    private final AbstractConfigCell headerReaction = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("ReactionSettings", R.string.ReactionSettings)));
//    private final AbstractConfigCell reactionsRow = cellGroup.appendCell(new ConfigCellSelectBox(LocaleController.getString("DoubleTapAndReactions", R.string.doubleTapAndReactions),
//            NekoConfig.reactions,
//            new String[]{
//                    LocaleController.getString("doubleTapSendReaction", R.string.doubleTapSendReaction),
//                    LocaleController.getString("doubleTapShowReactionsMenu", R.string.doubleTapShowReactionsMenu),
//                    LocaleController.getString("doubleTapDisable", R.string.doubleTapDisable),
//            }, null));
    private final AbstractConfigCell disableReactionsWhenSelectingRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableReactionsWhenSelecting));
    private final AbstractConfigCell dividerReaction = cellGroup.appendCell(new ConfigCellDivider());

    // Operation Confirmatation
    private final AbstractConfigCell headerConfirms = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("ConfirmSettings")));
    private final AbstractConfigCell askBeforeCallRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.askBeforeCall));
    private final AbstractConfigCell skipOpenLinkConfirmRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.skipOpenLinkConfirm));
    private final AbstractConfigCell confirmAVRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.confirmAVMessage));
    private final AbstractConfigCell repeatConfirmRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.repeatConfirm));
    private final AbstractConfigCell dividerConfirms = cellGroup.appendCell(new ConfigCellDivider());

    // search tag
    private final AbstractConfigCell headerSearchTag = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("SavedTagSearchHint")));
    private final String[] searchPagesString = new String[]{
            LocaleController.getString(R.string.SearchThisChat),
            LocaleController.getString(R.string.SearchMyMessages),
            LocaleController.getString(R.string.SearchPublicPosts),
    };
    private final AbstractConfigCell searchHashtagDefaultPageChannelRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getSearchHashtagDefaultPageChannel(), searchPagesString, null));
    private final AbstractConfigCell searchHashtagDefaultPageChatRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getSearchHashtagDefaultPageChat(), searchPagesString, null));
    private final AbstractConfigCell dividerSearchTag  = cellGroup.appendCell(new ConfigCellDivider());

    private ListAdapter listAdapter;
    private ActionBarMenuItem menuItem;
    private StickerSizeCell stickerSizeCell;
    private EmojiSetCell emojiSetCell;
    private UndoView tooltip;

    public NekoChatSettingsActivity() {
        if (!NekoConfig.showRepeat.Bool() || NaConfig.INSTANCE.getShowRepeatAsCopy().Bool()){
            cellGroup.rows.remove(autoReplaceRepeatRow);
            NaConfig.INSTANCE.getAutoReplaceRepeat().setConfigBool(false);
        }

        addRowsToMap(cellGroup);
    }

    @Override
    public boolean onFragmentCreate() {
        EmojiHelper.getInstance().loadEmojisInfo(this);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);

        super.onFragmentCreate();

//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateRows();

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(getTitle());

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }

        ActionBarMenu menu = actionBar.createMenu();
        menuItem = menu.addItem(0, R.drawable.ic_ab_other);
        menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        menuItem.addSubItem(1, R.drawable.msg_reset, LocaleController.getString("ResetStickerSize", R.string.ResetStickerSize));
        menuItem.setVisibility(NekoConfig.stickerSize.Float() != 14.0f ? View.VISIBLE : View.GONE);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == 1) {
                    NekoConfig.stickerSize.setConfigFloat(14.0f);
                    menuItem.setVisibility(View.GONE);
                    stickerSizeCell.invalidate();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new BlurredRecyclerView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setAdapter(listAdapter);

        // Fragment: Set OnClick Callbacks
        listView.setOnItemClickListener((view, position, x, y) -> {
            AbstractConfigCell a = cellGroup.rows.get(position);
            if (a instanceof ConfigCellTextCheck) {
                ((ConfigCellTextCheck) a).onClick((TextCheckCell) view);
            } else if (a instanceof ConfigCellSelectBox) {
                ((ConfigCellSelectBox) a).onClick(view);
            } else if (a instanceof ConfigCellTextInput) {
                ((ConfigCellTextInput) a).onClick();
            } else if (a instanceof ConfigCellTextDetail) {
                RecyclerListView.OnItemClickListener o = ((ConfigCellTextDetail) a).onItemClickListener;
                if (o != null) {
                    try {
                        o.onItemClick(view, position);
                    } catch (
                            Exception e) {
                    }
                }
            } else if (a instanceof ConfigCellCustom) { // Custom onclick
                if (position == cellGroup.rows.indexOf(maxRecentStickerCountRow)) {
                    final int[] counts = {20, 30, 40, 50, 80, 100, 120, 150, 180, 200};
                    List<String> types = Arrays.stream(counts)
                            .filter(i -> i <= getMessagesController().maxRecentStickersCount)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.toList());
                    PopupBuilder builder = new PopupBuilder(view);
                    builder.setItems(types, (i, str) -> {
                        NekoConfig.maxRecentStickerCount.setConfigInt(Integer.parseInt(str.toString()));
                        listAdapter.notifyItemChanged(position);
                        return Unit.INSTANCE;
                    });
                    builder.show();
                } else if (position == cellGroup.rows.indexOf(doubleTapActionRow)) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    ArrayList<Integer> types = new ArrayList<>();
                    arrayList.add(LocaleController.getString("Disable", R.string.Disable));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_NONE);
                    arrayList.add(LocaleController.getString("SendReactions", R.string.SendReactions));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_SEND_REACTIONS);
                    arrayList.add(LocaleController.getString("ShowReactions", R.string.ShowReactions));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_SHOW_REACTIONS);
                    arrayList.add(LocaleController.getString("TranslateMessage", R.string.TranslateMessage));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_TRANSLATE);
                    arrayList.add(LocaleController.getString("Reply", R.string.Reply));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_REPLY);
                    arrayList.add(LocaleController.getString("AddToSavedMessages", R.string.AddToSavedMessages));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_SAVE);
                    arrayList.add(LocaleController.getString("Repeat", R.string.Repeat));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_REPEAT);
                    arrayList.add(LocaleController.getString("RepeatAsCopy", R.string.RepeatAsCopy));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_REPEAT_AS_COPY);
                    arrayList.add(LocaleController.getString("Edit", R.string.Edit));
                    types.add(DoubleTap.DOUBLE_TAP_ACTION_EDIT);
                    PopupBuilder builder = new PopupBuilder(view);
                    builder.setItems(arrayList, (i, str) -> {
                        NaConfig.INSTANCE.getDoubleTapAction().setConfigInt(types.get(i));
                        listAdapter.notifyItemChanged(position);
                        return Unit.INSTANCE;
                    });
                    builder.show();
                } else if (position == cellGroup.rows.indexOf(emojiSetsRow)) {
                    presentFragment(new NekoEmojiSettingsActivity());
                }
            }
        });
        listView.setOnItemLongClickListener((view, position, x, y) -> {
            var holder = listView.findViewHolderForAdapterPosition(position);
            if (holder != null && listAdapter.isEnabled(holder)) {
                createLongClickDialog(context, NekoChatSettingsActivity.this, "chat", position);
                return true;
            }
            return false;
        });

        // Cells: Set OnSettingChanged Callbacks
        cellGroup.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NekoConfig.hideAllTab.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);
            } else if (key.equals(NekoConfig.tabsTitleType.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);
            } else if (key.equals(NekoConfig.disableProximityEvents.getKey())) {
                MediaController.getInstance().recreateProximityWakeLock();
            } else if (key.equals(NekoConfig.showSeconds.getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getDisableBotOpenButton().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            }
        };

        //Cells: Set ListAdapter
        cellGroup.setListAdapter(listView, listAdapter);

        tooltip = new UndoView(context);
        frameLayout.addView(tooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void updateRows() {
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getDrawable() {
        return R.drawable.menu_chats;
    }

    @Override
    public String getTitle() {
        return LocaleController.getString("Chat", R.string.Chat);
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

    public static AlertDialog showConfigMenuAlert(Context context, String titleKey, ArrayList<ConfigCellTextCheck> configItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(titleKey));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int count = configItems.size();
        for (int a = 0; a < count; a++) {
            ConfigCellTextCheck configItem = configItems.get(a);
            TextCheckCell textCell = new TextCheckCell(context);
            textCell.setTextAndCheck(configItem.getTitle(), configItem.getBindConfig().Bool(), false);
            textCell.setTag(a);
            textCell.setBackground(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            int finalA = a;
            textCell.setOnClickListener(v2 -> {
                Integer tag = (Integer) v2.getTag();
                if (tag == finalA) {
                    textCell.setChecked(configItem.getBindConfig().toggleConfigBool());
                }
            });
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setView(linearLayout);
        return builder.create();
    }

    public static boolean[] getDeleteMenuChecks() {
        return new boolean[]{
                NaConfig.INSTANCE.getDefaultDeleteMenuBanUsers().Bool(),
                NaConfig.INSTANCE.getDefaultDeleteMenReportSpam().Bool(),
                NaConfig.INSTANCE.getDefaultDeleteMenuDeleteAll().Bool(),
                NaConfig.INSTANCE.getDefaultDeleteMenuDoActionsInCommonGroups().Bool(),
        };
    }

    @Override
    public void emojiPacksLoaded(String error) {
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(cellGroup.rows.indexOf(emojiSetsRow), PARTIAL);
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded && listAdapter != null) {
            listAdapter.notifyItemChanged(cellGroup.rows.indexOf(emojiSetsRow), PARTIAL);
        }
       /* if (id == NotificationCenter.emojiDidLoad) {
            if (listView != null) {
                listView.invalidateViews();
            }
        }*/
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        super.onFragmentDestroy();
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
    }

    private class StickerSizeCell extends FrameLayout {

        private final StickerSizePreviewMessagesCell messagesCell;
        private final SeekBarView sizeBar;
        private final int startStickerSize = 2;
        private final int endStickerSize = 20;

        private final TextPaint textPaint;

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
                    NekoConfig.stickerSize.setConfigFloat(startStickerSize + (endStickerSize - startStickerSize) * progress);
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
            canvas.drawText("" + Math.round(NekoConfig.stickerSize.Float()), getMeasuredWidth() - AndroidUtilities.dp(39), AndroidUtilities.dp(28), textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            sizeBar.setProgress((NekoConfig.stickerSize.Float() - startStickerSize) / (float) (endStickerSize - startStickerSize));
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

        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return cellGroup.rows.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            AbstractConfigCell a = cellGroup.rows.get(position);
            if (a != null) {
                return a.isEnabled();
            }
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            AbstractConfigCell a = cellGroup.rows.get(position);
            if (a != null) {
                return a.getType();
            }
            return CellGroup.ITEM_TYPE_TEXT_DETAIL;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            View view = holder.itemView;
            AbstractConfigCell a = cellGroup.rows.get(position);
            if (a != null) {
                if (a instanceof ConfigCellCustom) {
                    // Custom binds
                    if (holder.itemView instanceof TextSettingsCell) {
                        TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                        if (position == cellGroup.rows.indexOf(maxRecentStickerCountRow)) {
                            textCell.setTextAndValue(LocaleController.getString("maxRecentStickerCount", R.string.maxRecentStickerCount), String.valueOf(NekoConfig.maxRecentStickerCount.Int()), true);
                        } else if (position == cellGroup.rows.indexOf(doubleTapActionRow)) {
                            textCell.setTextAndValue(LocaleController.getString("DoubleTapAction", R.string.DoubleTapAction), DoubleTap.doubleTapActionMap.get(NaConfig.INSTANCE.getDoubleTapAction().Int()), true);
                        }
                    } else if (view instanceof EmojiSetCell) {
                        EmojiSetCell v1 =  (EmojiSetCell) view;
                        v1.setData(EmojiHelper.getInstance().getCurrentEmojiPackInfo(), false, true);
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
                case CellGroup.ITEM_TYPE_DIVIDER:
                    view = new ShadowSectionCell(mContext);
                    break;
                case CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case CellGroup.ITEM_TYPE_TEXT_CHECK:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case CellGroup.ITEM_TYPE_HEADER:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case CellGroup.ITEM_TYPE_TEXT_DETAIL:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case CellGroup.ITEM_TYPE_TEXT:
                    view = new TextInfoPrivacyCell(mContext);
                    // view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case ConfigCellCustom.CUSTOM_ITEM_StickerSize:
                    view = stickerSizeCell = new StickerSizeCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case ConfigCellCustom.CUSTOM_ITEM_EmojiSet:
                    view = emojiSetCell = new EmojiSetCell(mContext, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }
    }
}

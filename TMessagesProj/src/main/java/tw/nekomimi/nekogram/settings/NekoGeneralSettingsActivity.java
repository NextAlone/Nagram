package tw.nekomimi.nekogram.settings;

import static tw.nekomimi.nekogram.settings.NekoChatSettingsActivity.showConfigMenuAlert;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.util.OpenPgpApi;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsService;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.Components.UndoView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import kotlin.Unit;
import tw.nekomimi.nekogram.ui.BottomBuilder;
import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.ui.PopupBuilder;
import tw.nekomimi.nekogram.transtale.Translator;
import tw.nekomimi.nekogram.transtale.TranslatorKt;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.PGPUtil;

import tw.nekomimi.nekogram.config.ConfigItem;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.*;
import xyz.nextalone.nagram.NaConfig;

@SuppressLint("RtlHardcoded")
public class NekoGeneralSettingsActivity extends BaseNekoXSettingsActivity {

    private ValueAnimator statusBarColorAnimator;

    private final CellGroup a = cellGroup = new CellGroup(this);

    private final AbstractConfigCell showSquareAvatarRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowSquareAvatar()));
    private final AbstractConfigCell disableProfileAvatarBlurRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableProfileAvatarBlur()));
    private final AbstractConfigCell disableGooeyAvatarAnimationRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableGooeyAvatarAnimation()));
    private final AbstractConfigCell hidePhoneRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hidePhone));
    private final AbstractConfigCell divider0 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerTranslation = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Translate")));
    private final AbstractConfigCell translationProviderRow = cellGroup.appendCell(new ConfigCellCustom("TranslationProvider", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell useTelegramTranslateInChatRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useTelegramTranslateInChat));
    private final AbstractConfigCell translateToLangRow = cellGroup.appendCell(new ConfigCellCustom("TransToLang", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell translateInputToLangRow = cellGroup.appendCell(new ConfigCellCustom("TransInputToLang", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell googleCloudTranslateKeyRow = cellGroup.appendCell(new ConfigCellTextDetail(NekoConfig.googleCloudTranslateKey, (view, position) -> {
        customDialog_BottomInputString(position, NekoConfig.googleCloudTranslateKey, LocaleController.getString("GoogleCloudTransKeyNotice"), "Key");
    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
    private final AbstractConfigCell deepLxCustomApiRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getDeepLxCustomApi(), "", null));
    private final AbstractConfigCell deepLApiKeyRow = cellGroup.appendCell(new ConfigCellTextDetail(NaConfig.INSTANCE.getDeepLApiKey(), (view, position) -> {
        customDialog_BottomInputString(position, NaConfig.INSTANCE.getDeepLApiKey(), LocaleController.getString(R.string.DeepLApiKeyNotice), "Key");
    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
    private final AbstractConfigCell deepLFreeApiKeyRow = cellGroup.appendCell(new ConfigCellTextDetail(NaConfig.INSTANCE.getDeepLFreeApiKey(), (view, position) -> {
        customDialog_BottomInputString(position, NaConfig.INSTANCE.getDeepLFreeApiKey(), LocaleController.getString(R.string.DeepLFreeApiKeyNotice), "Key");
    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
    private final AbstractConfigCell deepLFormalityRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getDeepLFormality(),
            new String[]{
                    LocaleController.getString(R.string.DeepLFormalityDefault),
                    LocaleController.getString(R.string.DeepLFormalityMore),
                    LocaleController.getString(R.string.DeepLFormalityLess),
            }, null));
    private final AbstractConfigCell llmSettingsRow = cellGroup.appendCell(new ConfigCellCustom("LLMSettings", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell hideOriginAfterTranslationRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getHideOriginAfterTranslation()));
    private final AbstractConfigCell autoTranslateRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getAutoTranslate(), LocaleController.getString("AutoTranslateAbout")));
    private final AbstractConfigCell keepFormattingRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getKeepFormatting()));
    private final AbstractConfigCell dividerTranslation = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerAiTools = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString(R.string.PremiumPreviewAIEditor)));
    private final AbstractConfigCell summarizeTextButtonRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getSummarizeTextButton(),
            new String[]{
                    LocaleController.getString(R.string.Default),
                    LocaleController.getString(R.string.SummarizeTextButtonDisable),
                    LocaleController.getString(R.string.SummarizeTextButtonAlways),
            }, null));
    private final AbstractConfigCell disableAiEditorRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableAiEditor()));
    private final AbstractConfigCell dividerAiTools = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerMap = cellGroup.appendCell(new ConfigCellHeader("Map"));
    private final AbstractConfigCell useOSMDroidMapRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useOSMDroidMap));
    private final AbstractConfigCell mapDriftingFixForGoogleMapsRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.mapDriftingFixForGoogleMaps));
    private final AbstractConfigCell mapPreviewRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.mapPreviewProvider,
            new String[]{
                    LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram),
                    LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex),
                    LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody)
            }, null));
    private final AbstractConfigCell dividerMap = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerConnection = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString(R.string.Connection)));
    private final AbstractConfigCell customIpStrategyRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getCustomIpStrategy(),
            new String[]{
                    LocaleController.getString(R.string.Default),
                    LocaleController.getString(R.string.CustomIpStrategyIPV4),
                    LocaleController.getString(R.string.CustomIpStrategyIPV6),
                    LocaleController.getString(R.string.CustomIpStrategyAuto),
            }, null));
    private final AbstractConfigCell useSystemDNSRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useSystemDNS));
    private final AbstractConfigCell disableProxyWhenVpnEnabledRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableProxyWhenVpnEnabled()));
    private final AbstractConfigCell customDoHRow = cellGroup.appendCell(new ConfigCellTextInput(null, NekoConfig.customDoH, "https://1.0.0.1/dns-query", null));
//    private final AbstractConfigCell customPublicProxyIPRow = cellGroup.appendCell(new ConfigCellTextDetail(NekoConfig.customPublicProxyIP, (view, position) -> {
//        customDialog_BottomInputString(position, NekoConfig.customPublicProxyIP, LocaleController.getString("customPublicProxyIPNotice"), "IP");
//    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
private final AbstractConfigCell defaultHlsVideoQualityRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getDefaultHlsVideoQuality(),
        new String[]{
                LocaleController.getString(R.string.QualityAuto),
                LocaleController.getString(R.string.QualityOriginal),
                LocaleController.getString(R.string.Quality1440),
                LocaleController.getString(R.string.Quality1080),
                LocaleController.getString(R.string.Quality720),
                LocaleController.getString(R.string.Quality144),
        }, null));
    private final AbstractConfigCell dividerConnection = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerFolder = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Folder")));
    private final AbstractConfigCell hideAllTabRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideAllTab, LocaleController.getString("HideAllTabAbout")));
    private final AbstractConfigCell openArchiveOnPullRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.openArchiveOnPull));
    private final AbstractConfigCell disablePullDownSearchRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disablePullDownSearch));
    private final AbstractConfigCell ignoreMutedCountRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.ignoreMutedCount));
    private final AbstractConfigCell ignoreFolderCountRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getIgnoreFolderCount()));
    private final AbstractConfigCell tabsTitleTypeRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.tabsTitleType,
            new String[]{
                    LocaleController.getString("TabTitleTypeText", R.string.TabTitleTypeText),
                    LocaleController.getString("TabTitleTypeIcon", R.string.TabTitleTypeIcon),
                    LocaleController.getString("TabTitleTypeMix", R.string.TabTitleTypeMix)
            }, null));
    private final AbstractConfigCell tabStyleStrokeRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getTabStyleStroke()));
    private final AbstractConfigCell tabStyleRow = cellGroup.appendCell(new ConfigCellSelectBox("TabStyle", NaConfig.INSTANCE.getTabStyle(),
            new String[]{
                    LocaleController.getString(R.string.Default),
                    LocaleController.getString(R.string.TabStylePure),
                    LocaleController.getString(R.string.TabStylePills),
            }, null));
    private final AbstractConfigCell hideFilterMuteAllRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getHideFilterMuteAll()));
    private final AbstractConfigCell dividerFolder = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header_notification = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("NekoGeneralNotification")));
    private final AbstractConfigCell disableNotificationBubblesRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableNotificationBubbles));
    private final AbstractConfigCell divider_notification = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header3 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("OpenKayChain")));
    private final AbstractConfigCell pgpAppRow = cellGroup.appendCell(new ConfigCellCustom("OpenPGPApp", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell keyRow = cellGroup.appendCell(new ConfigCellTextDetail(NekoConfig.openPGPKeyId, (view, position) -> {
        requestKey(new Intent(OpenPgpApi.ACTION_GET_SIGN_KEY_ID));
    }, "0"));
    private final AbstractConfigCell divider3 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header4 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("DialogsSettings")));
    private final AbstractConfigCell sortMenuRow = cellGroup.appendCell(new ConfigCellSelectBox("SortMenu", null, null, () -> {
        if (getParentActivity() == null) return;
        showDialog(showConfigMenuAlert(getParentActivity(), "SortMenu", new ArrayList<>() {{
            add(new ConfigCellTextCheck(NekoConfig.sortByUnread, null, LocaleController.getString(R.string.SortByUnread)));
            add(new ConfigCellTextCheck(NekoConfig.sortByUnmuted, null, LocaleController.getString(R.string.SortByUnmuted)));
            add(new ConfigCellTextCheck(NekoConfig.sortByUser, null, LocaleController.getString(R.string.SortByUser)));
            add(new ConfigCellTextCheck(NekoConfig.sortByContacts, null, LocaleController.getString(R.string.SortByContacts)));
        }}));
    }));
    private final AbstractConfigCell mediaPreviewRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.mediaPreview));
    private final AbstractConfigCell showUserIconsInChatsListRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowUserIconsInChatsList()));
    private final AbstractConfigCell disableCommunityChatGroupingRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableCommunityChatGrouping()));
    private final AbstractConfigCell communityAvatarTapActionRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getCommunityAvatarTapAction(),
            new String[]{
                    LocaleController.getString(R.string.CommunityAvatarTapActionChat),
                    LocaleController.getString(R.string.CommunityAvatarTapActionCommunity),
                    LocaleController.getString(R.string.CommunityAvatarTapActionArrow),
            }, null));
    private final AbstractConfigCell divider4 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header5 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Appearance")));
    private final AbstractConfigCell typefaceRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.typeface));
    private final AbstractConfigCell useSystemFontInTitleRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getUseSystemFontInTitle()));
    private final AbstractConfigCell transparentStatusBarRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.transparentStatusBar));
    private final AbstractConfigCell appBarShadowRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableAppBarShadow));
    private final AbstractConfigCell newYearRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.newYear));
    private final AbstractConfigCell alwaysShowDownloadIconRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getAlwaysShowDownloadIcon()));
    private final AbstractConfigCell actionBarDecorationRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.actionBarDecoration, new String[]{
            LocaleController.getString("DependsOnDate", R.string.DependsOnDate),
            LocaleController.getString("Snowflakes", R.string.Snowflakes),
            LocaleController.getString("Fireworks", R.string.Fireworks),
            LocaleController.getString("DecorationNone", R.string.DecorationNone),
    }, null));
    private final AbstractConfigCell iconDecorationRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getIconDecoration(), new String[]{
            LocaleController.getString("DependsOnDate", R.string.DependsOnDate),
            LocaleController.getString("Christmas", R.string.Christmas),
            LocaleController.getString("Valentine", R.string.Valentine),
            LocaleController.getString("HalloWeen", R.string.HalloWeen),
            LocaleController.getString("DecorationNone", R.string.DecorationNone),
    }, null));
    private final AbstractConfigCell chatDecorationRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getChatDecoration(), new String[]{
            LocaleController.getString("DependsOnDate", R.string.DependsOnDate),
            LocaleController.getString("Snowflakes", R.string.Snowflakes),
            LocaleController.getString("DecorationNone", R.string.DecorationNone),
    }, null));
    private final AbstractConfigCell notificationIconRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getNotificationIcon(), new String[]{
            LocaleController.getString("Official", R.string.Official),
            LocaleController.getString("Nagram", R.string.NekoX),
            LocaleController.getString("Nekogram", R.string.Nekogram)
    }, null));
    private final AbstractConfigCell tabletModeRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.tabletMode, new String[]{
            LocaleController.getString(R.string.TabletModeDefault),
            LocaleController.getString(R.string.TabletModeBig),
            LocaleController.getString(R.string.Disable),
            LocaleController.getString(R.string.TabletModeSmall)
    }, null));

    private final AbstractConfigCell forceBlurInChatRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.forceBlurInChat));
    private final AbstractConfigCell header_chatblur = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("ChatBlurAlphaValue")));
    private final AbstractConfigCell chatBlurAlphaValueRow = cellGroup.appendCell(new ConfigCellCustom("ChatBlurAlphaValue", ConfigCellCustom.CUSTOM_ITEM_CharBlurAlpha, NekoConfig.forceBlurInChat.Bool()));

    private final AbstractConfigCell disableDialogsFloatingButtonRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableDialogsFloatingButton()));
    private final AbstractConfigCell centerActionBarTitleRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getCenterActionBarTitle()));
    private final AbstractConfigCell disablePredictiveBackAnimationRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisablePredictiveBackAnimation()));
    private final AbstractConfigCell mainTabsStyleRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getMainTabsStyle(),
            new String[]{
                    LocaleController.getString(R.string.Default),
                    LocaleController.getString(R.string.MainTabsStyleTextFree),
                    LocaleController.getString(R.string.Disable),
            }, null));
    private final AbstractConfigCell hideTabBarPermissionWarningsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getHideTabBarPermissionWarnings()));
    private final AbstractConfigCell showRecentChatsOnTabLongPressRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowRecentChatsOnTabLongPress()));
    private final AbstractConfigCell customDialogsMenuRow = cellGroup.appendCell(new ConfigCellSelectBox(NaConfig.INSTANCE.getCustomDialogsMenu().getKey(), null, null, () -> {
        if (getParentActivity() == null) return;
        showDialog(showConfigMenuAlert(getParentActivity(), NaConfig.INSTANCE.getCustomDialogsMenu().getKey(), new ArrayList<>() {{
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuTheme()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowRecentChatsInSidebar()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuNewGroup()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuNewMessage()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuSavedMessages()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuMyProfile()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuSettings()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuProxy()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuAccount()));
        }}));
    }));
    private final AbstractConfigCell sidebarSettingsActivityRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSidebarSettingsActivity()));
    private final AbstractConfigCell switchStyleRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getSwitchStyle(), new String[]{
            LocaleController.getString(R.string.SwitchStyleTelegram),
            LocaleController.getString(R.string.SwitchStyleModern)
    }, null));
    private final AbstractConfigCell divider5 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header6 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("PrivacyTitle")));
    private final AbstractConfigCell disableSystemAccountRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableSystemAccount));
    private final AbstractConfigCell doNotShareMyPhoneNumberRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDoNotShareMyPhoneNumber()));
    private final AbstractConfigCell disableSuggestionViewRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableSuggestionView()));
    private final AbstractConfigCell disableAutoWebLoginRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableAutoWebLogin()));
    private final AbstractConfigCell sentryAnalyticsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSentryAnalytics()));
    private final AbstractConfigCell divider6 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header7 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("General")));
    private final AbstractConfigCell customTitleRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getCustomTitle(),
            LocaleController.getString("customTitleHint", R.string.CustomTitleHint), null,
            (input) -> input.isEmpty() ? (String) NaConfig.INSTANCE.getCustomTitle().defaultValue : input));
    private final AbstractConfigCell customSavePathRow = cellGroup.appendCell(new ConfigCellTextInput(null, NekoConfig.customSavePath,
            LocaleController.getString("customSavePathHint", R.string.customSavePathHint), null,
            (input) -> input.matches("^[A-za-z0-9.]{1,255}$") || input.isEmpty() ? input : (String) NekoConfig.customSavePath.defaultValue));
    private final AbstractConfigCell customTitleUserNameRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomTitleUserName()));
    private final AbstractConfigCell useSystemUnlockRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getUseSystemUnlock()));
    private final AbstractConfigCell disableUndoRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableUndo));
    private final AbstractConfigCell showIdAndDcRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.showIdAndDc));
    private final AbstractConfigCell inappCameraRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.inappCamera));
    private final AbstractConfigCell autoPauseVideoRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.autoPauseVideo, LocaleController.getString("AutoPauseVideoAbout")));
    private final AbstractConfigCell disableNumberRoundingRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableNumberRounding, "4.8K -> 4777"));
    private final AbstractConfigCell nameOrderRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.nameOrder, new String[]{
            LocaleController.getString("LastFirst", R.string.LastFirst),
            LocaleController.getString("FirstLast", R.string.FirstLast)
    }, null));
    private final AbstractConfigCell usePersianCalendarRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.usePersianCalendar, LocaleController.getString("UsePersiancalendarInfo")));
    private final AbstractConfigCell displayPersianCalendarByLatinRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.displayPersianCalendarByLatin));
    private final AbstractConfigCell divider7 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerPushService = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Notifications", R.string.Notifications)));
    private final AbstractConfigCell pushServiceTypeRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getPushServiceType(), new String[]{
            LocaleController.getString(R.string.PushServiceTypeInApp),
            LocaleController.getString(R.string.PushServiceTypeFCM),
            LocaleController.getString(R.string.PushServiceTypeUnified),
            LocaleController.getString(R.string.PushServiceTypeMicroG),
    }, null));
    private final AbstractConfigCell pushServiceTypeInAppDialogRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getPushServiceTypeInAppDialog()));
    private final AbstractConfigCell pushServiceTypeUnifiedGatewayRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getPushServiceTypeUnifiedGateway(), null, null, (input) -> input.isEmpty() ? (String) NaConfig.INSTANCE.getPushServiceTypeUnifiedGateway().defaultValue : input));
    private final AbstractConfigCell divider8 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerAutoDownload = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("AutoDownload")));
    private final AbstractConfigCell win32Row = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableAutoDownloadingWin32Executable));
    private final AbstractConfigCell archiveRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableAutoDownloadingArchive));
    private final AbstractConfigCell dividerAutoDownload = cellGroup.appendCell(new ConfigCellDivider());

    // blur
    private final AbstractConfigCell headerBlur = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString(R.string.LiteOptionsBlur2)));
    private final AbstractConfigCell disableGlareEffectsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableGlareEffects()));
    private final AbstractConfigCell liquidGlassAngleRow = cellGroup.appendCell(new ConfigCellCustom("LiquidGlassAngle", ConfigCellCustom.CUSTOM_ITEM_LiquidGlassAngle, true));
    private final AbstractConfigCell liquidGlassIntensityRow = cellGroup.appendCell(new ConfigCellCustom("LiquidGlassIntensity", ConfigCellCustom.CUSTOM_ITEM_LiquidGlassIntensity, true));
    private final AbstractConfigCell materialDesign3ChatHeaderRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getMaterialDesign3ChatHeader(), LocaleController.getString(R.string.MaterialDesign3ChatHeaderDes)));
    private final AbstractConfigCell dividerBlur = cellGroup.appendCell(new ConfigCellDivider());

    private ChatBlurAlphaSeekBar chatBlurAlphaSeekbar;

    private final AbstractConfigCell switch1Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1()));
    private final AbstractConfigCell switch2Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch2()));
    private final AbstractConfigCell switch3Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch3()));
    private final AbstractConfigCell switch4Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch4()));
    private final AbstractConfigCell switch5Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch5()));
    private final AbstractConfigCell switch6Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch6()));
    private final AbstractConfigCell switch7Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch7()));
    private final AbstractConfigCell switch8Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch8()));
    private final AbstractConfigCell switch9Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch9()));
    private final AbstractConfigCell switch10Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch10()));
    private final AbstractConfigCell switch11Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch11()));
    private final AbstractConfigCell switch12Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch12()));
    private final AbstractConfigCell switch13Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch13()));
    private final AbstractConfigCell switch14Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch14()));
    private final AbstractConfigCell switch15Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch15()));
    private final AbstractConfigCell switch16Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch16()));
    private final AbstractConfigCell switch17Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch17()));
    private final AbstractConfigCell switch18Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch18()));
    private final AbstractConfigCell switch19Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch19()));
    private final AbstractConfigCell switch20Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch20()));
    private final AbstractConfigCell switch21Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch21()));
    private final AbstractConfigCell switch22Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch22()));
    private final AbstractConfigCell switch23Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch23()));
    private final AbstractConfigCell switch24Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch24()));
    private final AbstractConfigCell switch25Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch25()));
    private final AbstractConfigCell switch26Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch26()));
    private final AbstractConfigCell switch27Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch27()));
    private final AbstractConfigCell switch28Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch28()));
    private final AbstractConfigCell switch29Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch29()));
    private final AbstractConfigCell switch30Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch30()));
    private final AbstractConfigCell switch31Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch31()));
    private final AbstractConfigCell switch32Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch32()));
    private final AbstractConfigCell switch33Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch33()));
    private final AbstractConfigCell switch34Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch34()));
    private final AbstractConfigCell switch35Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch35()));
    private final AbstractConfigCell switch36Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch36()));
    private final AbstractConfigCell switch37Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch37()));
    private final AbstractConfigCell switch38Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch38()));
    private final AbstractConfigCell switch39Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch39()));
    private final AbstractConfigCell switch40Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch40()));
    private final AbstractConfigCell switch41Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch41()));
    private final AbstractConfigCell switch42Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch42()));
    private final AbstractConfigCell switch43Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch43()));
    private final AbstractConfigCell switch44Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch44()));
    private final AbstractConfigCell switch45Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch45()));
    private final AbstractConfigCell switch46Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch46()));
    private final AbstractConfigCell switch47Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch47()));
    private final AbstractConfigCell switch48Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch48()));
    private final AbstractConfigCell switch49Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch49()));
    private final AbstractConfigCell switch50Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch50()));
    private final AbstractConfigCell switch51Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch51()));
    private final AbstractConfigCell switch52Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch52()));
    private final AbstractConfigCell switch53Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch53()));
    private final AbstractConfigCell switch54Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch54()));
    private final AbstractConfigCell switch55Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch55()));
    private final AbstractConfigCell switch56Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch56()));
    private final AbstractConfigCell switch57Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch57()));
    private final AbstractConfigCell switch58Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch58()));
    private final AbstractConfigCell switch59Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch59()));
    private final AbstractConfigCell switch60Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch60()));
    private final AbstractConfigCell switch61Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch61()));
    private final AbstractConfigCell switch62Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch62()));
    private final AbstractConfigCell switch63Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch63()));
    private final AbstractConfigCell switch64Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch64()));
    private final AbstractConfigCell switch65Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch65()));
    private final AbstractConfigCell switch66Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch66()));
    private final AbstractConfigCell switch67Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch67()));
    private final AbstractConfigCell switch68Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch68()));
    private final AbstractConfigCell switch69Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch69()));
    private final AbstractConfigCell switch70Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch70()));
    private final AbstractConfigCell switch71Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch71()));
    private final AbstractConfigCell switch72Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch72()));
    private final AbstractConfigCell switch73Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch73()));
    private final AbstractConfigCell switch74Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch74()));
    private final AbstractConfigCell switch75Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch75()));
    private final AbstractConfigCell switch76Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch76()));
    private final AbstractConfigCell switch77Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch77()));
    private final AbstractConfigCell switch78Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch78()));
    private final AbstractConfigCell switch79Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch79()));
    private final AbstractConfigCell switch80Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch80()));
    private final AbstractConfigCell switch81Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch81()));
    private final AbstractConfigCell switch82Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch82()));
    private final AbstractConfigCell switch83Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch83()));
    private final AbstractConfigCell switch84Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch84()));
    private final AbstractConfigCell switch85Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch85()));
    private final AbstractConfigCell switch86Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch86()));
    private final AbstractConfigCell switch87Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch87()));
    private final AbstractConfigCell switch88Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch88()));
    private final AbstractConfigCell switch89Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch89()));
    private final AbstractConfigCell switch90Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch90()));
    private final AbstractConfigCell switch91Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch91()));
    private final AbstractConfigCell switch92Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch92()));
    private final AbstractConfigCell switch93Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch93()));
    private final AbstractConfigCell switch94Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch94()));
    private final AbstractConfigCell switch95Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch95()));
    private final AbstractConfigCell switch96Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch96()));
    private final AbstractConfigCell switch97Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch97()));
    private final AbstractConfigCell switch98Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch98()));
    private final AbstractConfigCell switch99Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch99()));
    private final AbstractConfigCell switch100Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch100()));
    private final AbstractConfigCell switch101Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch101()));
    private final AbstractConfigCell switch102Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch102()));
    private final AbstractConfigCell switch103Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch103()));
    private final AbstractConfigCell switch104Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch104()));
    private final AbstractConfigCell switch105Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch105()));
    private final AbstractConfigCell switch106Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch106()));
    private final AbstractConfigCell switch107Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch107()));
    private final AbstractConfigCell switch108Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch108()));
    private final AbstractConfigCell switch109Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch109()));
    private final AbstractConfigCell switch110Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch110()));
    private final AbstractConfigCell switch111Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch111()));
    private final AbstractConfigCell switch112Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch112()));
    private final AbstractConfigCell switch113Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch113()));
    private final AbstractConfigCell switch114Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch114()));
    private final AbstractConfigCell switch115Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch115()));
    private final AbstractConfigCell switch116Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch116()));
    private final AbstractConfigCell switch117Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch117()));
    private final AbstractConfigCell switch118Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch118()));
    private final AbstractConfigCell switch119Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch119()));
    private final AbstractConfigCell switch120Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch120()));
    private final AbstractConfigCell switch121Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch121()));
    private final AbstractConfigCell switch122Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch122()));
    private final AbstractConfigCell switch123Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch123()));
    private final AbstractConfigCell switch124Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch124()));
    private final AbstractConfigCell switch125Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch125()));
    private final AbstractConfigCell switch126Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch126()));
    private final AbstractConfigCell switch127Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch127()));
    private final AbstractConfigCell switch128Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch128()));
    private final AbstractConfigCell switch129Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch129()));
    private final AbstractConfigCell switch130Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch130()));
    private final AbstractConfigCell switch131Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch131()));
    private final AbstractConfigCell switch132Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch132()));
    private final AbstractConfigCell switch133Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch133()));
    private final AbstractConfigCell switch134Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch134()));
    private final AbstractConfigCell switch135Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch135()));
    private final AbstractConfigCell switch136Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch136()));
    private final AbstractConfigCell switch137Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch137()));
    private final AbstractConfigCell switch138Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch138()));
    private final AbstractConfigCell switch139Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch139()));
    private final AbstractConfigCell switch140Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch140()));
    private final AbstractConfigCell switch141Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch141()));
    private final AbstractConfigCell switch142Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch142()));
    private final AbstractConfigCell switch143Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch143()));
    private final AbstractConfigCell switch144Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch144()));
    private final AbstractConfigCell switch145Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch145()));
    private final AbstractConfigCell switch146Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch146()));
    private final AbstractConfigCell switch147Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch147()));
    private final AbstractConfigCell switch148Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch148()));
    private final AbstractConfigCell switch149Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch149()));
    private final AbstractConfigCell switch150Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch150()));
    private final AbstractConfigCell switch151Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch151()));
    private final AbstractConfigCell switch152Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch152()));
    private final AbstractConfigCell switch153Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch153()));
    private final AbstractConfigCell switch154Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch154()));
    private final AbstractConfigCell switch155Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch155()));
    private final AbstractConfigCell switch156Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch156()));
    private final AbstractConfigCell switch157Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch157()));
    private final AbstractConfigCell switch158Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch158()));
    private final AbstractConfigCell switch159Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch159()));
    private final AbstractConfigCell switch160Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch160()));
    private final AbstractConfigCell switch161Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch161()));
    private final AbstractConfigCell switch162Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch162()));
    private final AbstractConfigCell switch163Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch163()));
    private final AbstractConfigCell switch164Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch164()));
    private final AbstractConfigCell switch165Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch165()));
    private final AbstractConfigCell switch166Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch166()));
    private final AbstractConfigCell switch167Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch167()));
    private final AbstractConfigCell switch168Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch168()));
    private final AbstractConfigCell switch169Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch169()));
    private final AbstractConfigCell switch170Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch170()));
    private final AbstractConfigCell switch171Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch171()));
    private final AbstractConfigCell switch172Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch172()));
    private final AbstractConfigCell switch173Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch173()));
    private final AbstractConfigCell switch174Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch174()));
    private final AbstractConfigCell switch175Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch175()));
    private final AbstractConfigCell switch176Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch176()));
    private final AbstractConfigCell switch177Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch177()));
    private final AbstractConfigCell switch178Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch178()));
    private final AbstractConfigCell switch179Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch179()));
    private final AbstractConfigCell switch180Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch180()));
    private final AbstractConfigCell switch181Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch181()));
    private final AbstractConfigCell switch182Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch182()));
    private final AbstractConfigCell switch183Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch183()));
    private final AbstractConfigCell switch184Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch184()));
    private final AbstractConfigCell switch185Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch185()));
    private final AbstractConfigCell switch186Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch186()));
    private final AbstractConfigCell switch187Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch187()));
    private final AbstractConfigCell switch188Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch188()));
    private final AbstractConfigCell switch189Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch189()));
    private final AbstractConfigCell switch190Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch190()));
    private final AbstractConfigCell switch191Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch191()));
    private final AbstractConfigCell switch192Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch192()));
    private final AbstractConfigCell switch193Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch193()));
    private final AbstractConfigCell switch194Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch194()));
    private final AbstractConfigCell switch195Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch195()));
    private final AbstractConfigCell switch196Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch196()));
    private final AbstractConfigCell switch197Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch197()));
    private final AbstractConfigCell switch198Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch198()));
    private final AbstractConfigCell switch199Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch199()));
    private final AbstractConfigCell switch200Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch200()));
    private final AbstractConfigCell switch201Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch201()));
    private final AbstractConfigCell switch202Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch202()));
    private final AbstractConfigCell switch203Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch203()));
    private final AbstractConfigCell switch204Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch204()));
    private final AbstractConfigCell switch205Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch205()));
    private final AbstractConfigCell switch206Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch206()));
    private final AbstractConfigCell switch207Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch207()));
    private final AbstractConfigCell switch208Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch208()));
    private final AbstractConfigCell switch209Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch209()));
    private final AbstractConfigCell switch210Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch210()));
    private final AbstractConfigCell switch211Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch211()));
    private final AbstractConfigCell switch212Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch212()));
    private final AbstractConfigCell switch213Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch213()));
    private final AbstractConfigCell switch214Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch214()));
    private final AbstractConfigCell switch215Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch215()));
    private final AbstractConfigCell switch216Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch216()));
    private final AbstractConfigCell switch217Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch217()));
    private final AbstractConfigCell switch218Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch218()));
    private final AbstractConfigCell switch219Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch219()));
    private final AbstractConfigCell switch220Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch220()));
    private final AbstractConfigCell switch221Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch221()));
    private final AbstractConfigCell switch222Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch222()));
    private final AbstractConfigCell switch223Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch223()));
    private final AbstractConfigCell switch224Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch224()));
    private final AbstractConfigCell switch225Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch225()));
    private final AbstractConfigCell switch226Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch226()));
    private final AbstractConfigCell switch227Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch227()));
    private final AbstractConfigCell switch228Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch228()));
    private final AbstractConfigCell switch229Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch229()));
    private final AbstractConfigCell switch230Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch230()));
    private final AbstractConfigCell switch231Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch231()));
    private final AbstractConfigCell switch232Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch232()));
    private final AbstractConfigCell switch233Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch233()));
    private final AbstractConfigCell switch234Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch234()));
    private final AbstractConfigCell switch235Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch235()));
    private final AbstractConfigCell switch236Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch236()));
    private final AbstractConfigCell switch237Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch237()));
    private final AbstractConfigCell switch238Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch238()));
    private final AbstractConfigCell switch239Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch239()));
    private final AbstractConfigCell switch240Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch240()));
    private final AbstractConfigCell switch241Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch241()));
    private final AbstractConfigCell switch242Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch242()));
    private final AbstractConfigCell switch243Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch243()));
    private final AbstractConfigCell switch244Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch244()));
    private final AbstractConfigCell switch245Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch245()));
    private final AbstractConfigCell switch246Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch246()));
    private final AbstractConfigCell switch247Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch247()));
    private final AbstractConfigCell switch248Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch248()));
    private final AbstractConfigCell switch249Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch249()));
    private final AbstractConfigCell switch250Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch250()));
    private final AbstractConfigCell switch251Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch251()));
    private final AbstractConfigCell switch252Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch252()));
    private final AbstractConfigCell switch253Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch253()));
    private final AbstractConfigCell switch254Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch254()));
    private final AbstractConfigCell switch255Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch255()));
    private final AbstractConfigCell switch256Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch256()));
    private final AbstractConfigCell switch257Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch257()));
    private final AbstractConfigCell switch258Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch258()));
    private final AbstractConfigCell switch259Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch259()));
    private final AbstractConfigCell switch260Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch260()));
    private final AbstractConfigCell switch261Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch261()));
    private final AbstractConfigCell switch262Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch262()));
    private final AbstractConfigCell switch263Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch263()));
    private final AbstractConfigCell switch264Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch264()));
    private final AbstractConfigCell switch265Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch265()));
    private final AbstractConfigCell switch266Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch266()));
    private final AbstractConfigCell switch267Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch267()));
    private final AbstractConfigCell switch268Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch268()));
    private final AbstractConfigCell switch269Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch269()));
    private final AbstractConfigCell switch270Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch270()));
    private final AbstractConfigCell switch271Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch271()));
    private final AbstractConfigCell switch272Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch272()));
    private final AbstractConfigCell switch273Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch273()));
    private final AbstractConfigCell switch274Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch274()));
    private final AbstractConfigCell switch275Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch275()));
    private final AbstractConfigCell switch276Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch276()));
    private final AbstractConfigCell switch277Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch277()));
    private final AbstractConfigCell switch278Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch278()));
    private final AbstractConfigCell switch279Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch279()));
    private final AbstractConfigCell switch280Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch280()));
    private final AbstractConfigCell switch281Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch281()));
    private final AbstractConfigCell switch282Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch282()));
    private final AbstractConfigCell switch283Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch283()));
    private final AbstractConfigCell switch284Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch284()));
    private final AbstractConfigCell switch285Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch285()));
    private final AbstractConfigCell switch286Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch286()));
    private final AbstractConfigCell switch287Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch287()));
    private final AbstractConfigCell switch288Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch288()));
    private final AbstractConfigCell switch289Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch289()));
    private final AbstractConfigCell switch290Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch290()));
    private final AbstractConfigCell switch291Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch291()));
    private final AbstractConfigCell switch292Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch292()));
    private final AbstractConfigCell switch293Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch293()));
    private final AbstractConfigCell switch294Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch294()));
    private final AbstractConfigCell switch295Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch295()));
    private final AbstractConfigCell switch296Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch296()));
    private final AbstractConfigCell switch297Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch297()));
    private final AbstractConfigCell switch298Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch298()));
    private final AbstractConfigCell switch299Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch299()));
    private final AbstractConfigCell switch300Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch300()));
    private final AbstractConfigCell switch301Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch301()));
    private final AbstractConfigCell switch302Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch302()));
    private final AbstractConfigCell switch303Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch303()));
    private final AbstractConfigCell switch304Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch304()));
    private final AbstractConfigCell switch305Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch305()));
    private final AbstractConfigCell switch306Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch306()));
    private final AbstractConfigCell switch307Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch307()));
    private final AbstractConfigCell switch308Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch308()));
    private final AbstractConfigCell switch309Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch309()));
    private final AbstractConfigCell switch310Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch310()));
    private final AbstractConfigCell switch311Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch311()));
    private final AbstractConfigCell switch312Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch312()));
    private final AbstractConfigCell switch313Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch313()));
    private final AbstractConfigCell switch314Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch314()));
    private final AbstractConfigCell switch315Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch315()));
    private final AbstractConfigCell switch316Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch316()));
    private final AbstractConfigCell switch317Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch317()));
    private final AbstractConfigCell switch318Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch318()));
    private final AbstractConfigCell switch319Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch319()));
    private final AbstractConfigCell switch320Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch320()));
    private final AbstractConfigCell switch321Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch321()));
    private final AbstractConfigCell switch322Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch322()));
    private final AbstractConfigCell switch323Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch323()));
    private final AbstractConfigCell switch324Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch324()));
    private final AbstractConfigCell switch325Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch325()));
    private final AbstractConfigCell switch326Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch326()));
    private final AbstractConfigCell switch327Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch327()));
    private final AbstractConfigCell switch328Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch328()));
    private final AbstractConfigCell switch329Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch329()));
    private final AbstractConfigCell switch330Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch330()));
    private final AbstractConfigCell switch331Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch331()));
    private final AbstractConfigCell switch332Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch332()));
    private final AbstractConfigCell switch333Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch333()));
    private final AbstractConfigCell switch334Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch334()));
    private final AbstractConfigCell switch335Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch335()));
    private final AbstractConfigCell switch336Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch336()));
    private final AbstractConfigCell switch337Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch337()));
    private final AbstractConfigCell switch338Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch338()));
    private final AbstractConfigCell switch339Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch339()));
    private final AbstractConfigCell switch340Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch340()));
    private final AbstractConfigCell switch341Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch341()));
    private final AbstractConfigCell switch342Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch342()));
    private final AbstractConfigCell switch343Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch343()));
    private final AbstractConfigCell switch344Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch344()));
    private final AbstractConfigCell switch345Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch345()));
    private final AbstractConfigCell switch346Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch346()));
    private final AbstractConfigCell switch347Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch347()));
    private final AbstractConfigCell switch348Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch348()));
    private final AbstractConfigCell switch349Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch349()));
    private final AbstractConfigCell switch350Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch350()));
    private final AbstractConfigCell switch351Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch351()));
    private final AbstractConfigCell switch352Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch352()));
    private final AbstractConfigCell switch353Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch353()));
    private final AbstractConfigCell switch354Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch354()));
    private final AbstractConfigCell switch355Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch355()));
    private final AbstractConfigCell switch356Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch356()));
    private final AbstractConfigCell switch357Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch357()));
    private final AbstractConfigCell switch358Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch358()));
    private final AbstractConfigCell switch359Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch359()));
    private final AbstractConfigCell switch360Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch360()));
    private final AbstractConfigCell switch361Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch361()));
    private final AbstractConfigCell switch362Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch362()));
    private final AbstractConfigCell switch363Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch363()));
    private final AbstractConfigCell switch364Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch364()));
    private final AbstractConfigCell switch365Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch365()));
    private final AbstractConfigCell switch366Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch366()));
    private final AbstractConfigCell switch367Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch367()));
    private final AbstractConfigCell switch368Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch368()));
    private final AbstractConfigCell switch369Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch369()));
    private final AbstractConfigCell switch370Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch370()));
    private final AbstractConfigCell switch371Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch371()));
    private final AbstractConfigCell switch372Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch372()));
    private final AbstractConfigCell switch373Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch373()));
    private final AbstractConfigCell switch374Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch374()));
    private final AbstractConfigCell switch375Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch375()));
    private final AbstractConfigCell switch376Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch376()));
    private final AbstractConfigCell switch377Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch377()));
    private final AbstractConfigCell switch378Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch378()));
    private final AbstractConfigCell switch379Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch379()));
    private final AbstractConfigCell switch380Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch380()));
    private final AbstractConfigCell switch381Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch381()));
    private final AbstractConfigCell switch382Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch382()));
    private final AbstractConfigCell switch383Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch383()));
    private final AbstractConfigCell switch384Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch384()));
    private final AbstractConfigCell switch385Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch385()));
    private final AbstractConfigCell switch386Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch386()));
    private final AbstractConfigCell switch387Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch387()));
    private final AbstractConfigCell switch388Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch388()));
    private final AbstractConfigCell switch389Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch389()));
    private final AbstractConfigCell switch390Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch390()));
    private final AbstractConfigCell switch391Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch391()));
    private final AbstractConfigCell switch392Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch392()));
    private final AbstractConfigCell switch393Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch393()));
    private final AbstractConfigCell switch394Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch394()));
    private final AbstractConfigCell switch395Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch395()));
    private final AbstractConfigCell switch396Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch396()));
    private final AbstractConfigCell switch397Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch397()));
    private final AbstractConfigCell switch398Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch398()));
    private final AbstractConfigCell switch399Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch399()));
    private final AbstractConfigCell switch400Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch400()));
    private final AbstractConfigCell switch401Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch401()));
    private final AbstractConfigCell switch402Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch402()));
    private final AbstractConfigCell switch403Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch403()));
    private final AbstractConfigCell switch404Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch404()));
    private final AbstractConfigCell switch405Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch405()));
    private final AbstractConfigCell switch406Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch406()));
    private final AbstractConfigCell switch407Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch407()));
    private final AbstractConfigCell switch408Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch408()));
    private final AbstractConfigCell switch409Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch409()));
    private final AbstractConfigCell switch410Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch410()));
    private final AbstractConfigCell switch411Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch411()));
    private final AbstractConfigCell switch412Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch412()));
    private final AbstractConfigCell switch413Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch413()));
    private final AbstractConfigCell switch414Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch414()));
    private final AbstractConfigCell switch415Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch415()));
    private final AbstractConfigCell switch416Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch416()));
    private final AbstractConfigCell switch417Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch417()));
    private final AbstractConfigCell switch418Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch418()));
    private final AbstractConfigCell switch419Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch419()));
    private final AbstractConfigCell switch420Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch420()));
    private final AbstractConfigCell switch421Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch421()));
    private final AbstractConfigCell switch422Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch422()));
    private final AbstractConfigCell switch423Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch423()));
    private final AbstractConfigCell switch424Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch424()));
    private final AbstractConfigCell switch425Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch425()));
    private final AbstractConfigCell switch426Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch426()));
    private final AbstractConfigCell switch427Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch427()));
    private final AbstractConfigCell switch428Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch428()));
    private final AbstractConfigCell switch429Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch429()));
    private final AbstractConfigCell switch430Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch430()));
    private final AbstractConfigCell switch431Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch431()));
    private final AbstractConfigCell switch432Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch432()));
    private final AbstractConfigCell switch433Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch433()));
    private final AbstractConfigCell switch434Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch434()));
    private final AbstractConfigCell switch435Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch435()));
    private final AbstractConfigCell switch436Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch436()));
    private final AbstractConfigCell switch437Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch437()));
    private final AbstractConfigCell switch438Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch438()));
    private final AbstractConfigCell switch439Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch439()));
    private final AbstractConfigCell switch440Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch440()));
    private final AbstractConfigCell switch441Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch441()));
    private final AbstractConfigCell switch442Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch442()));
    private final AbstractConfigCell switch443Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch443()));
    private final AbstractConfigCell switch444Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch444()));
    private final AbstractConfigCell switch445Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch445()));
    private final AbstractConfigCell switch446Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch446()));
    private final AbstractConfigCell switch447Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch447()));
    private final AbstractConfigCell switch448Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch448()));
    private final AbstractConfigCell switch449Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch449()));
    private final AbstractConfigCell switch450Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch450()));
    private final AbstractConfigCell switch451Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch451()));
    private final AbstractConfigCell switch452Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch452()));
    private final AbstractConfigCell switch453Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch453()));
    private final AbstractConfigCell switch454Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch454()));
    private final AbstractConfigCell switch455Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch455()));
    private final AbstractConfigCell switch456Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch456()));
    private final AbstractConfigCell switch457Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch457()));
    private final AbstractConfigCell switch458Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch458()));
    private final AbstractConfigCell switch459Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch459()));
    private final AbstractConfigCell switch460Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch460()));
    private final AbstractConfigCell switch461Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch461()));
    private final AbstractConfigCell switch462Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch462()));
    private final AbstractConfigCell switch463Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch463()));
    private final AbstractConfigCell switch464Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch464()));
    private final AbstractConfigCell switch465Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch465()));
    private final AbstractConfigCell switch466Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch466()));
    private final AbstractConfigCell switch467Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch467()));
    private final AbstractConfigCell switch468Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch468()));
    private final AbstractConfigCell switch469Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch469()));
    private final AbstractConfigCell switch470Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch470()));
    private final AbstractConfigCell switch471Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch471()));
    private final AbstractConfigCell switch472Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch472()));
    private final AbstractConfigCell switch473Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch473()));
    private final AbstractConfigCell switch474Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch474()));
    private final AbstractConfigCell switch475Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch475()));
    private final AbstractConfigCell switch476Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch476()));
    private final AbstractConfigCell switch477Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch477()));
    private final AbstractConfigCell switch478Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch478()));
    private final AbstractConfigCell switch479Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch479()));
    private final AbstractConfigCell switch480Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch480()));
    private final AbstractConfigCell switch481Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch481()));
    private final AbstractConfigCell switch482Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch482()));
    private final AbstractConfigCell switch483Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch483()));
    private final AbstractConfigCell switch484Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch484()));
    private final AbstractConfigCell switch485Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch485()));
    private final AbstractConfigCell switch486Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch486()));
    private final AbstractConfigCell switch487Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch487()));
    private final AbstractConfigCell switch488Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch488()));
    private final AbstractConfigCell switch489Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch489()));
    private final AbstractConfigCell switch490Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch490()));
    private final AbstractConfigCell switch491Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch491()));
    private final AbstractConfigCell switch492Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch492()));
    private final AbstractConfigCell switch493Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch493()));
    private final AbstractConfigCell switch494Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch494()));
    private final AbstractConfigCell switch495Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch495()));
    private final AbstractConfigCell switch496Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch496()));
    private final AbstractConfigCell switch497Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch497()));
    private final AbstractConfigCell switch498Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch498()));
    private final AbstractConfigCell switch499Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch499()));
    private final AbstractConfigCell switch500Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch500()));
    private final AbstractConfigCell switch501Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch501()));
    private final AbstractConfigCell switch502Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch502()));
    private final AbstractConfigCell switch503Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch503()));
    private final AbstractConfigCell switch504Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch504()));
    private final AbstractConfigCell switch505Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch505()));
    private final AbstractConfigCell switch506Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch506()));
    private final AbstractConfigCell switch507Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch507()));
    private final AbstractConfigCell switch508Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch508()));
    private final AbstractConfigCell switch509Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch509()));
    private final AbstractConfigCell switch510Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch510()));
    private final AbstractConfigCell switch511Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch511()));
    private final AbstractConfigCell switch512Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch512()));
    private final AbstractConfigCell switch513Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch513()));
    private final AbstractConfigCell switch514Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch514()));
    private final AbstractConfigCell switch515Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch515()));
    private final AbstractConfigCell switch516Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch516()));
    private final AbstractConfigCell switch517Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch517()));
    private final AbstractConfigCell switch518Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch518()));
    private final AbstractConfigCell switch519Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch519()));
    private final AbstractConfigCell switch520Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch520()));
    private final AbstractConfigCell switch521Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch521()));
    private final AbstractConfigCell switch522Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch522()));
    private final AbstractConfigCell switch523Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch523()));
    private final AbstractConfigCell switch524Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch524()));
    private final AbstractConfigCell switch525Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch525()));
    private final AbstractConfigCell switch526Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch526()));
    private final AbstractConfigCell switch527Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch527()));
    private final AbstractConfigCell switch528Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch528()));
    private final AbstractConfigCell switch529Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch529()));
    private final AbstractConfigCell switch530Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch530()));
    private final AbstractConfigCell switch531Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch531()));
    private final AbstractConfigCell switch532Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch532()));
    private final AbstractConfigCell switch533Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch533()));
    private final AbstractConfigCell switch534Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch534()));
    private final AbstractConfigCell switch535Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch535()));
    private final AbstractConfigCell switch536Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch536()));
    private final AbstractConfigCell switch537Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch537()));
    private final AbstractConfigCell switch538Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch538()));
    private final AbstractConfigCell switch539Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch539()));
    private final AbstractConfigCell switch540Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch540()));
    private final AbstractConfigCell switch541Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch541()));
    private final AbstractConfigCell switch542Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch542()));
    private final AbstractConfigCell switch543Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch543()));
    private final AbstractConfigCell switch544Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch544()));
    private final AbstractConfigCell switch545Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch545()));
    private final AbstractConfigCell switch546Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch546()));
    private final AbstractConfigCell switch547Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch547()));
    private final AbstractConfigCell switch548Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch548()));
    private final AbstractConfigCell switch549Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch549()));
    private final AbstractConfigCell switch550Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch550()));
    private final AbstractConfigCell switch551Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch551()));
    private final AbstractConfigCell switch552Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch552()));
    private final AbstractConfigCell switch553Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch553()));
    private final AbstractConfigCell switch554Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch554()));
    private final AbstractConfigCell switch555Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch555()));
    private final AbstractConfigCell switch556Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch556()));
    private final AbstractConfigCell switch557Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch557()));
    private final AbstractConfigCell switch558Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch558()));
    private final AbstractConfigCell switch559Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch559()));
    private final AbstractConfigCell switch560Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch560()));
    private final AbstractConfigCell switch561Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch561()));
    private final AbstractConfigCell switch562Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch562()));
    private final AbstractConfigCell switch563Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch563()));
    private final AbstractConfigCell switch564Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch564()));
    private final AbstractConfigCell switch565Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch565()));
    private final AbstractConfigCell switch566Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch566()));
    private final AbstractConfigCell switch567Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch567()));
    private final AbstractConfigCell switch568Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch568()));
    private final AbstractConfigCell switch569Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch569()));
    private final AbstractConfigCell switch570Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch570()));
    private final AbstractConfigCell switch571Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch571()));
    private final AbstractConfigCell switch572Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch572()));
    private final AbstractConfigCell switch573Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch573()));
    private final AbstractConfigCell switch574Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch574()));
    private final AbstractConfigCell switch575Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch575()));
    private final AbstractConfigCell switch576Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch576()));
    private final AbstractConfigCell switch577Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch577()));
    private final AbstractConfigCell switch578Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch578()));
    private final AbstractConfigCell switch579Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch579()));
    private final AbstractConfigCell switch580Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch580()));
    private final AbstractConfigCell switch581Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch581()));
    private final AbstractConfigCell switch582Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch582()));
    private final AbstractConfigCell switch583Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch583()));
    private final AbstractConfigCell switch584Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch584()));
    private final AbstractConfigCell switch585Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch585()));
    private final AbstractConfigCell switch586Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch586()));
    private final AbstractConfigCell switch587Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch587()));
    private final AbstractConfigCell switch588Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch588()));
    private final AbstractConfigCell switch589Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch589()));
    private final AbstractConfigCell switch590Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch590()));
    private final AbstractConfigCell switch591Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch591()));
    private final AbstractConfigCell switch592Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch592()));
    private final AbstractConfigCell switch593Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch593()));
    private final AbstractConfigCell switch594Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch594()));
    private final AbstractConfigCell switch595Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch595()));
    private final AbstractConfigCell switch596Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch596()));
    private final AbstractConfigCell switch597Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch597()));
    private final AbstractConfigCell switch598Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch598()));
    private final AbstractConfigCell switch599Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch599()));
    private final AbstractConfigCell switch600Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch600()));
    private final AbstractConfigCell switch601Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch601()));
    private final AbstractConfigCell switch602Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch602()));
    private final AbstractConfigCell switch603Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch603()));
    private final AbstractConfigCell switch604Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch604()));
    private final AbstractConfigCell switch605Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch605()));
    private final AbstractConfigCell switch606Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch606()));
    private final AbstractConfigCell switch607Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch607()));
    private final AbstractConfigCell switch608Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch608()));
    private final AbstractConfigCell switch609Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch609()));
    private final AbstractConfigCell switch610Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch610()));
    private final AbstractConfigCell switch611Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch611()));
    private final AbstractConfigCell switch612Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch612()));
    private final AbstractConfigCell switch613Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch613()));
    private final AbstractConfigCell switch614Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch614()));
    private final AbstractConfigCell switch615Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch615()));
    private final AbstractConfigCell switch616Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch616()));
    private final AbstractConfigCell switch617Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch617()));
    private final AbstractConfigCell switch618Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch618()));
    private final AbstractConfigCell switch619Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch619()));
    private final AbstractConfigCell switch620Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch620()));
    private final AbstractConfigCell switch621Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch621()));
    private final AbstractConfigCell switch622Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch622()));
    private final AbstractConfigCell switch623Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch623()));
    private final AbstractConfigCell switch624Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch624()));
    private final AbstractConfigCell switch625Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch625()));
    private final AbstractConfigCell switch626Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch626()));
    private final AbstractConfigCell switch627Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch627()));
    private final AbstractConfigCell switch628Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch628()));
    private final AbstractConfigCell switch629Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch629()));
    private final AbstractConfigCell switch630Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch630()));
    private final AbstractConfigCell switch631Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch631()));
    private final AbstractConfigCell switch632Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch632()));
    private final AbstractConfigCell switch633Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch633()));
    private final AbstractConfigCell switch634Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch634()));
    private final AbstractConfigCell switch635Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch635()));
    private final AbstractConfigCell switch636Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch636()));
    private final AbstractConfigCell switch637Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch637()));
    private final AbstractConfigCell switch638Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch638()));
    private final AbstractConfigCell switch639Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch639()));
    private final AbstractConfigCell switch640Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch640()));
    private final AbstractConfigCell switch641Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch641()));
    private final AbstractConfigCell switch642Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch642()));
    private final AbstractConfigCell switch643Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch643()));
    private final AbstractConfigCell switch644Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch644()));
    private final AbstractConfigCell switch645Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch645()));
    private final AbstractConfigCell switch646Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch646()));
    private final AbstractConfigCell switch647Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch647()));
    private final AbstractConfigCell switch648Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch648()));
    private final AbstractConfigCell switch649Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch649()));
    private final AbstractConfigCell switch650Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch650()));
    private final AbstractConfigCell switch651Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch651()));
    private final AbstractConfigCell switch652Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch652()));
    private final AbstractConfigCell switch653Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch653()));
    private final AbstractConfigCell switch654Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch654()));
    private final AbstractConfigCell switch655Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch655()));
    private final AbstractConfigCell switch656Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch656()));
    private final AbstractConfigCell switch657Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch657()));
    private final AbstractConfigCell switch658Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch658()));
    private final AbstractConfigCell switch659Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch659()));
    private final AbstractConfigCell switch660Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch660()));
    private final AbstractConfigCell switch661Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch661()));
    private final AbstractConfigCell switch662Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch662()));
    private final AbstractConfigCell switch663Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch663()));
    private final AbstractConfigCell switch664Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch664()));
    private final AbstractConfigCell switch665Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch665()));
    private final AbstractConfigCell switch666Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch666()));
    private final AbstractConfigCell switch667Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch667()));
    private final AbstractConfigCell switch668Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch668()));
    private final AbstractConfigCell switch669Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch669()));
    private final AbstractConfigCell switch670Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch670()));
    private final AbstractConfigCell switch671Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch671()));
    private final AbstractConfigCell switch672Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch672()));
    private final AbstractConfigCell switch673Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch673()));
    private final AbstractConfigCell switch674Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch674()));
    private final AbstractConfigCell switch675Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch675()));
    private final AbstractConfigCell switch676Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch676()));
    private final AbstractConfigCell switch677Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch677()));
    private final AbstractConfigCell switch678Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch678()));
    private final AbstractConfigCell switch679Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch679()));
    private final AbstractConfigCell switch680Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch680()));
    private final AbstractConfigCell switch681Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch681()));
    private final AbstractConfigCell switch682Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch682()));
    private final AbstractConfigCell switch683Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch683()));
    private final AbstractConfigCell switch684Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch684()));
    private final AbstractConfigCell switch685Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch685()));
    private final AbstractConfigCell switch686Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch686()));
    private final AbstractConfigCell switch687Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch687()));
    private final AbstractConfigCell switch688Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch688()));
    private final AbstractConfigCell switch689Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch689()));
    private final AbstractConfigCell switch690Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch690()));
    private final AbstractConfigCell switch691Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch691()));
    private final AbstractConfigCell switch692Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch692()));
    private final AbstractConfigCell switch693Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch693()));
    private final AbstractConfigCell switch694Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch694()));
    private final AbstractConfigCell switch695Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch695()));
    private final AbstractConfigCell switch696Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch696()));
    private final AbstractConfigCell switch697Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch697()));
    private final AbstractConfigCell switch698Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch698()));
    private final AbstractConfigCell switch699Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch699()));
    private final AbstractConfigCell switch700Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch700()));
    private final AbstractConfigCell switch701Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch701()));
    private final AbstractConfigCell switch702Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch702()));
    private final AbstractConfigCell switch703Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch703()));
    private final AbstractConfigCell switch704Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch704()));
    private final AbstractConfigCell switch705Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch705()));
    private final AbstractConfigCell switch706Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch706()));
    private final AbstractConfigCell switch707Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch707()));
    private final AbstractConfigCell switch708Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch708()));
    private final AbstractConfigCell switch709Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch709()));
    private final AbstractConfigCell switch710Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch710()));
    private final AbstractConfigCell switch711Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch711()));
    private final AbstractConfigCell switch712Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch712()));
    private final AbstractConfigCell switch713Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch713()));
    private final AbstractConfigCell switch714Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch714()));
    private final AbstractConfigCell switch715Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch715()));
    private final AbstractConfigCell switch716Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch716()));
    private final AbstractConfigCell switch717Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch717()));
    private final AbstractConfigCell switch718Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch718()));
    private final AbstractConfigCell switch719Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch719()));
    private final AbstractConfigCell switch720Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch720()));
    private final AbstractConfigCell switch721Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch721()));
    private final AbstractConfigCell switch722Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch722()));
    private final AbstractConfigCell switch723Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch723()));
    private final AbstractConfigCell switch724Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch724()));
    private final AbstractConfigCell switch725Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch725()));
    private final AbstractConfigCell switch726Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch726()));
    private final AbstractConfigCell switch727Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch727()));
    private final AbstractConfigCell switch728Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch728()));
    private final AbstractConfigCell switch729Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch729()));
    private final AbstractConfigCell switch730Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch730()));
    private final AbstractConfigCell switch731Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch731()));
    private final AbstractConfigCell switch732Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch732()));
    private final AbstractConfigCell switch733Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch733()));
    private final AbstractConfigCell switch734Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch734()));
    private final AbstractConfigCell switch735Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch735()));
    private final AbstractConfigCell switch736Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch736()));
    private final AbstractConfigCell switch737Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch737()));
    private final AbstractConfigCell switch738Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch738()));
    private final AbstractConfigCell switch739Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch739()));
    private final AbstractConfigCell switch740Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch740()));
    private final AbstractConfigCell switch741Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch741()));
    private final AbstractConfigCell switch742Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch742()));
    private final AbstractConfigCell switch743Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch743()));
    private final AbstractConfigCell switch744Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch744()));
    private final AbstractConfigCell switch745Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch745()));
    private final AbstractConfigCell switch746Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch746()));
    private final AbstractConfigCell switch747Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch747()));
    private final AbstractConfigCell switch748Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch748()));
    private final AbstractConfigCell switch749Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch749()));
    private final AbstractConfigCell switch750Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch750()));
    private final AbstractConfigCell switch751Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch751()));
    private final AbstractConfigCell switch752Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch752()));
    private final AbstractConfigCell switch753Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch753()));
    private final AbstractConfigCell switch754Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch754()));
    private final AbstractConfigCell switch755Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch755()));
    private final AbstractConfigCell switch756Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch756()));
    private final AbstractConfigCell switch757Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch757()));
    private final AbstractConfigCell switch758Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch758()));
    private final AbstractConfigCell switch759Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch759()));
    private final AbstractConfigCell switch760Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch760()));
    private final AbstractConfigCell switch761Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch761()));
    private final AbstractConfigCell switch762Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch762()));
    private final AbstractConfigCell switch763Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch763()));
    private final AbstractConfigCell switch764Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch764()));
    private final AbstractConfigCell switch765Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch765()));
    private final AbstractConfigCell switch766Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch766()));
    private final AbstractConfigCell switch767Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch767()));
    private final AbstractConfigCell switch768Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch768()));
    private final AbstractConfigCell switch769Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch769()));
    private final AbstractConfigCell switch770Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch770()));
    private final AbstractConfigCell switch771Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch771()));
    private final AbstractConfigCell switch772Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch772()));
    private final AbstractConfigCell switch773Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch773()));
    private final AbstractConfigCell switch774Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch774()));
    private final AbstractConfigCell switch775Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch775()));
    private final AbstractConfigCell switch776Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch776()));
    private final AbstractConfigCell switch777Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch777()));
    private final AbstractConfigCell switch778Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch778()));
    private final AbstractConfigCell switch779Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch779()));
    private final AbstractConfigCell switch780Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch780()));
    private final AbstractConfigCell switch781Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch781()));
    private final AbstractConfigCell switch782Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch782()));
    private final AbstractConfigCell switch783Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch783()));
    private final AbstractConfigCell switch784Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch784()));
    private final AbstractConfigCell switch785Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch785()));
    private final AbstractConfigCell switch786Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch786()));
    private final AbstractConfigCell switch787Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch787()));
    private final AbstractConfigCell switch788Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch788()));
    private final AbstractConfigCell switch789Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch789()));
    private final AbstractConfigCell switch790Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch790()));
    private final AbstractConfigCell switch791Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch791()));
    private final AbstractConfigCell switch792Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch792()));
    private final AbstractConfigCell switch793Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch793()));
    private final AbstractConfigCell switch794Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch794()));
    private final AbstractConfigCell switch795Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch795()));
    private final AbstractConfigCell switch796Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch796()));
    private final AbstractConfigCell switch797Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch797()));
    private final AbstractConfigCell switch798Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch798()));
    private final AbstractConfigCell switch799Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch799()));
    private final AbstractConfigCell switch800Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch800()));
    private final AbstractConfigCell switch801Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch801()));
    private final AbstractConfigCell switch802Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch802()));
    private final AbstractConfigCell switch803Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch803()));
    private final AbstractConfigCell switch804Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch804()));
    private final AbstractConfigCell switch805Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch805()));
    private final AbstractConfigCell switch806Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch806()));
    private final AbstractConfigCell switch807Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch807()));
    private final AbstractConfigCell switch808Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch808()));
    private final AbstractConfigCell switch809Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch809()));
    private final AbstractConfigCell switch810Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch810()));
    private final AbstractConfigCell switch811Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch811()));
    private final AbstractConfigCell switch812Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch812()));
    private final AbstractConfigCell switch813Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch813()));
    private final AbstractConfigCell switch814Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch814()));
    private final AbstractConfigCell switch815Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch815()));
    private final AbstractConfigCell switch816Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch816()));
    private final AbstractConfigCell switch817Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch817()));
    private final AbstractConfigCell switch818Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch818()));
    private final AbstractConfigCell switch819Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch819()));
    private final AbstractConfigCell switch820Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch820()));
    private final AbstractConfigCell switch821Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch821()));
    private final AbstractConfigCell switch822Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch822()));
    private final AbstractConfigCell switch823Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch823()));
    private final AbstractConfigCell switch824Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch824()));
    private final AbstractConfigCell switch825Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch825()));
    private final AbstractConfigCell switch826Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch826()));
    private final AbstractConfigCell switch827Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch827()));
    private final AbstractConfigCell switch828Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch828()));
    private final AbstractConfigCell switch829Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch829()));
    private final AbstractConfigCell switch830Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch830()));
    private final AbstractConfigCell switch831Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch831()));
    private final AbstractConfigCell switch832Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch832()));
    private final AbstractConfigCell switch833Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch833()));
    private final AbstractConfigCell switch834Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch834()));
    private final AbstractConfigCell switch835Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch835()));
    private final AbstractConfigCell switch836Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch836()));
    private final AbstractConfigCell switch837Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch837()));
    private final AbstractConfigCell switch838Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch838()));
    private final AbstractConfigCell switch839Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch839()));
    private final AbstractConfigCell switch840Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch840()));
    private final AbstractConfigCell switch841Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch841()));
    private final AbstractConfigCell switch842Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch842()));
    private final AbstractConfigCell switch843Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch843()));
    private final AbstractConfigCell switch844Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch844()));
    private final AbstractConfigCell switch845Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch845()));
    private final AbstractConfigCell switch846Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch846()));
    private final AbstractConfigCell switch847Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch847()));
    private final AbstractConfigCell switch848Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch848()));
    private final AbstractConfigCell switch849Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch849()));
    private final AbstractConfigCell switch850Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch850()));
    private final AbstractConfigCell switch851Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch851()));
    private final AbstractConfigCell switch852Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch852()));
    private final AbstractConfigCell switch853Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch853()));
    private final AbstractConfigCell switch854Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch854()));
    private final AbstractConfigCell switch855Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch855()));
    private final AbstractConfigCell switch856Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch856()));
    private final AbstractConfigCell switch857Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch857()));
    private final AbstractConfigCell switch858Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch858()));
    private final AbstractConfigCell switch859Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch859()));
    private final AbstractConfigCell switch860Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch860()));
    private final AbstractConfigCell switch861Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch861()));
    private final AbstractConfigCell switch862Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch862()));
    private final AbstractConfigCell switch863Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch863()));
    private final AbstractConfigCell switch864Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch864()));
    private final AbstractConfigCell switch865Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch865()));
    private final AbstractConfigCell switch866Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch866()));
    private final AbstractConfigCell switch867Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch867()));
    private final AbstractConfigCell switch868Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch868()));
    private final AbstractConfigCell switch869Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch869()));
    private final AbstractConfigCell switch870Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch870()));
    private final AbstractConfigCell switch871Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch871()));
    private final AbstractConfigCell switch872Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch872()));
    private final AbstractConfigCell switch873Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch873()));
    private final AbstractConfigCell switch874Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch874()));
    private final AbstractConfigCell switch875Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch875()));
    private final AbstractConfigCell switch876Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch876()));
    private final AbstractConfigCell switch877Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch877()));
    private final AbstractConfigCell switch878Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch878()));
    private final AbstractConfigCell switch879Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch879()));
    private final AbstractConfigCell switch880Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch880()));
    private final AbstractConfigCell switch881Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch881()));
    private final AbstractConfigCell switch882Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch882()));
    private final AbstractConfigCell switch883Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch883()));
    private final AbstractConfigCell switch884Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch884()));
    private final AbstractConfigCell switch885Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch885()));
    private final AbstractConfigCell switch886Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch886()));
    private final AbstractConfigCell switch887Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch887()));
    private final AbstractConfigCell switch888Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch888()));
    private final AbstractConfigCell switch889Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch889()));
    private final AbstractConfigCell switch890Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch890()));
    private final AbstractConfigCell switch891Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch891()));
    private final AbstractConfigCell switch892Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch892()));
    private final AbstractConfigCell switch893Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch893()));
    private final AbstractConfigCell switch894Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch894()));
    private final AbstractConfigCell switch895Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch895()));
    private final AbstractConfigCell switch896Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch896()));
    private final AbstractConfigCell switch897Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch897()));
    private final AbstractConfigCell switch898Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch898()));
    private final AbstractConfigCell switch899Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch899()));
    private final AbstractConfigCell switch900Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch900()));
    private final AbstractConfigCell switch901Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch901()));
    private final AbstractConfigCell switch902Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch902()));
    private final AbstractConfigCell switch903Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch903()));
    private final AbstractConfigCell switch904Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch904()));
    private final AbstractConfigCell switch905Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch905()));
    private final AbstractConfigCell switch906Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch906()));
    private final AbstractConfigCell switch907Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch907()));
    private final AbstractConfigCell switch908Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch908()));
    private final AbstractConfigCell switch909Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch909()));
    private final AbstractConfigCell switch910Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch910()));
    private final AbstractConfigCell switch911Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch911()));
    private final AbstractConfigCell switch912Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch912()));
    private final AbstractConfigCell switch913Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch913()));
    private final AbstractConfigCell switch914Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch914()));
    private final AbstractConfigCell switch915Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch915()));
    private final AbstractConfigCell switch916Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch916()));
    private final AbstractConfigCell switch917Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch917()));
    private final AbstractConfigCell switch918Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch918()));
    private final AbstractConfigCell switch919Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch919()));
    private final AbstractConfigCell switch920Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch920()));
    private final AbstractConfigCell switch921Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch921()));
    private final AbstractConfigCell switch922Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch922()));
    private final AbstractConfigCell switch923Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch923()));
    private final AbstractConfigCell switch924Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch924()));
    private final AbstractConfigCell switch925Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch925()));
    private final AbstractConfigCell switch926Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch926()));
    private final AbstractConfigCell switch927Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch927()));
    private final AbstractConfigCell switch928Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch928()));
    private final AbstractConfigCell switch929Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch929()));
    private final AbstractConfigCell switch930Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch930()));
    private final AbstractConfigCell switch931Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch931()));
    private final AbstractConfigCell switch932Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch932()));
    private final AbstractConfigCell switch933Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch933()));
    private final AbstractConfigCell switch934Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch934()));
    private final AbstractConfigCell switch935Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch935()));
    private final AbstractConfigCell switch936Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch936()));
    private final AbstractConfigCell switch937Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch937()));
    private final AbstractConfigCell switch938Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch938()));
    private final AbstractConfigCell switch939Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch939()));
    private final AbstractConfigCell switch940Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch940()));
    private final AbstractConfigCell switch941Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch941()));
    private final AbstractConfigCell switch942Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch942()));
    private final AbstractConfigCell switch943Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch943()));
    private final AbstractConfigCell switch944Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch944()));
    private final AbstractConfigCell switch945Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch945()));
    private final AbstractConfigCell switch946Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch946()));
    private final AbstractConfigCell switch947Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch947()));
    private final AbstractConfigCell switch948Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch948()));
    private final AbstractConfigCell switch949Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch949()));
    private final AbstractConfigCell switch950Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch950()));
    private final AbstractConfigCell switch951Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch951()));
    private final AbstractConfigCell switch952Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch952()));
    private final AbstractConfigCell switch953Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch953()));
    private final AbstractConfigCell switch954Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch954()));
    private final AbstractConfigCell switch955Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch955()));
    private final AbstractConfigCell switch956Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch956()));
    private final AbstractConfigCell switch957Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch957()));
    private final AbstractConfigCell switch958Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch958()));
    private final AbstractConfigCell switch959Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch959()));
    private final AbstractConfigCell switch960Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch960()));
    private final AbstractConfigCell switch961Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch961()));
    private final AbstractConfigCell switch962Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch962()));
    private final AbstractConfigCell switch963Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch963()));
    private final AbstractConfigCell switch964Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch964()));
    private final AbstractConfigCell switch965Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch965()));
    private final AbstractConfigCell switch966Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch966()));
    private final AbstractConfigCell switch967Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch967()));
    private final AbstractConfigCell switch968Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch968()));
    private final AbstractConfigCell switch969Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch969()));
    private final AbstractConfigCell switch970Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch970()));
    private final AbstractConfigCell switch971Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch971()));
    private final AbstractConfigCell switch972Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch972()));
    private final AbstractConfigCell switch973Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch973()));
    private final AbstractConfigCell switch974Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch974()));
    private final AbstractConfigCell switch975Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch975()));
    private final AbstractConfigCell switch976Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch976()));
    private final AbstractConfigCell switch977Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch977()));
    private final AbstractConfigCell switch978Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch978()));
    private final AbstractConfigCell switch979Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch979()));
    private final AbstractConfigCell switch980Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch980()));
    private final AbstractConfigCell switch981Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch981()));
    private final AbstractConfigCell switch982Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch982()));
    private final AbstractConfigCell switch983Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch983()));
    private final AbstractConfigCell switch984Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch984()));
    private final AbstractConfigCell switch985Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch985()));
    private final AbstractConfigCell switch986Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch986()));
    private final AbstractConfigCell switch987Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch987()));
    private final AbstractConfigCell switch988Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch988()));
    private final AbstractConfigCell switch989Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch989()));
    private final AbstractConfigCell switch990Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch990()));
    private final AbstractConfigCell switch991Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch991()));
    private final AbstractConfigCell switch992Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch992()));
    private final AbstractConfigCell switch993Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch993()));
    private final AbstractConfigCell switch994Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch994()));
    private final AbstractConfigCell switch995Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch995()));
    private final AbstractConfigCell switch996Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch996()));
    private final AbstractConfigCell switch997Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch997()));
    private final AbstractConfigCell switch998Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch998()));
    private final AbstractConfigCell switch999Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch999()));
    private final AbstractConfigCell switch1000Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1000()));
    private final AbstractConfigCell switch1001Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1001()));
    private final AbstractConfigCell switch1002Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1002()));
    private final AbstractConfigCell switch1003Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1003()));
    private final AbstractConfigCell switch1004Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1004()));
    private final AbstractConfigCell switch1005Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1005()));
    private final AbstractConfigCell switch1006Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1006()));
    private final AbstractConfigCell switch1007Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1007()));
    private final AbstractConfigCell switch1008Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1008()));
    private final AbstractConfigCell switch1009Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1009()));
    private final AbstractConfigCell switch1010Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1010()));
    private final AbstractConfigCell switch1011Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1011()));
    private final AbstractConfigCell switch1012Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1012()));
    private final AbstractConfigCell switch1013Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1013()));
    private final AbstractConfigCell switch1014Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1014()));
    private final AbstractConfigCell switch1015Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1015()));
    private final AbstractConfigCell switch1016Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1016()));
    private final AbstractConfigCell switch1017Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1017()));
    private final AbstractConfigCell switch1018Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1018()));
    private final AbstractConfigCell switch1019Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1019()));
    private final AbstractConfigCell switch1020Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1020()));
    private final AbstractConfigCell switch1021Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1021()));
    private final AbstractConfigCell switch1022Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1022()));
    private final AbstractConfigCell switch1023Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1023()));
    private final AbstractConfigCell switch1024Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1024()));
    private final AbstractConfigCell switch1025Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1025()));
    private final AbstractConfigCell switch1026Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1026()));
    private final AbstractConfigCell switch1027Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1027()));
    private final AbstractConfigCell switch1028Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1028()));
    private final AbstractConfigCell switch1029Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1029()));
    private final AbstractConfigCell switch1030Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1030()));
    private final AbstractConfigCell switch1031Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1031()));
    private final AbstractConfigCell switch1032Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1032()));
    private final AbstractConfigCell switch1033Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1033()));
    private final AbstractConfigCell switch1034Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1034()));
    private final AbstractConfigCell switch1035Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1035()));
    private final AbstractConfigCell switch1036Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1036()));
    private final AbstractConfigCell switch1037Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1037()));
    private final AbstractConfigCell switch1038Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1038()));
    private final AbstractConfigCell switch1039Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1039()));
    private final AbstractConfigCell switch1040Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1040()));
    private final AbstractConfigCell switch1041Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1041()));
    private final AbstractConfigCell switch1042Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1042()));
    private final AbstractConfigCell switch1043Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1043()));
    private final AbstractConfigCell switch1044Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1044()));
    private final AbstractConfigCell switch1045Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1045()));
    private final AbstractConfigCell switch1046Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1046()));
    private final AbstractConfigCell switch1047Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1047()));
    private final AbstractConfigCell switch1048Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1048()));
    private final AbstractConfigCell switch1049Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1049()));
    private final AbstractConfigCell switch1050Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1050()));
    private final AbstractConfigCell switch1051Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1051()));
    private final AbstractConfigCell switch1052Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1052()));
    private final AbstractConfigCell switch1053Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1053()));
    private final AbstractConfigCell switch1054Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1054()));
    private final AbstractConfigCell switch1055Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1055()));
    private final AbstractConfigCell switch1056Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1056()));
    private final AbstractConfigCell switch1057Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1057()));
    private final AbstractConfigCell switch1058Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1058()));
    private final AbstractConfigCell switch1059Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1059()));
    private final AbstractConfigCell switch1060Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1060()));
    private final AbstractConfigCell switch1061Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1061()));
    private final AbstractConfigCell switch1062Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1062()));
    private final AbstractConfigCell switch1063Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1063()));
    private final AbstractConfigCell switch1064Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1064()));
    private final AbstractConfigCell switch1065Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1065()));
    private final AbstractConfigCell switch1066Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1066()));
    private final AbstractConfigCell switch1067Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1067()));
    private final AbstractConfigCell switch1068Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1068()));
    private final AbstractConfigCell switch1069Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1069()));
    private final AbstractConfigCell switch1070Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1070()));
    private final AbstractConfigCell switch1071Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1071()));
    private final AbstractConfigCell switch1072Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1072()));
    private final AbstractConfigCell switch1073Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1073()));
    private final AbstractConfigCell switch1074Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1074()));
    private final AbstractConfigCell switch1075Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1075()));
    private final AbstractConfigCell switch1076Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1076()));
    private final AbstractConfigCell switch1077Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1077()));
    private final AbstractConfigCell switch1078Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1078()));
    private final AbstractConfigCell switch1079Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1079()));
    private final AbstractConfigCell switch1080Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1080()));
    private final AbstractConfigCell switch1081Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1081()));
    private final AbstractConfigCell switch1082Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1082()));
    private final AbstractConfigCell switch1083Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1083()));
    private final AbstractConfigCell switch1084Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1084()));
    private final AbstractConfigCell switch1085Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1085()));
    private final AbstractConfigCell switch1086Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1086()));
    private final AbstractConfigCell switch1087Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1087()));
    private final AbstractConfigCell switch1088Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1088()));
    private final AbstractConfigCell switch1089Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1089()));
    private final AbstractConfigCell switch1090Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1090()));
    private final AbstractConfigCell switch1091Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1091()));
    private final AbstractConfigCell switch1092Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1092()));
    private final AbstractConfigCell switch1093Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1093()));
    private final AbstractConfigCell switch1094Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1094()));
    private final AbstractConfigCell switch1095Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1095()));
    private final AbstractConfigCell switch1096Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1096()));
    private final AbstractConfigCell switch1097Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1097()));
    private final AbstractConfigCell switch1098Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1098()));
    private final AbstractConfigCell switch1099Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1099()));
    private final AbstractConfigCell switch1100Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1100()));
    private final AbstractConfigCell switch1101Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1101()));
    private final AbstractConfigCell switch1102Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1102()));
    private final AbstractConfigCell switch1103Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1103()));
    private final AbstractConfigCell switch1104Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1104()));
    private final AbstractConfigCell switch1105Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1105()));
    private final AbstractConfigCell switch1106Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1106()));
    private final AbstractConfigCell switch1107Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1107()));
    private final AbstractConfigCell switch1108Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1108()));
    private final AbstractConfigCell switch1109Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1109()));
    private final AbstractConfigCell switch1110Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1110()));
    private final AbstractConfigCell switch1111Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1111()));
    private final AbstractConfigCell switch1112Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1112()));
    private final AbstractConfigCell switch1113Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1113()));
    private final AbstractConfigCell switch1114Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1114()));
    private final AbstractConfigCell switch1115Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1115()));
    private final AbstractConfigCell switch1116Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1116()));
    private final AbstractConfigCell switch1117Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1117()));
    private final AbstractConfigCell switch1118Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1118()));
    private final AbstractConfigCell switch1119Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1119()));
    private final AbstractConfigCell switch1120Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1120()));
    private final AbstractConfigCell switch1121Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1121()));
    private final AbstractConfigCell switch1122Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1122()));
    private final AbstractConfigCell switch1123Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1123()));
    private final AbstractConfigCell switch1124Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1124()));
    private final AbstractConfigCell switch1125Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1125()));
    private final AbstractConfigCell switch1126Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1126()));
    private final AbstractConfigCell switch1127Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1127()));
    private final AbstractConfigCell switch1128Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1128()));
    private final AbstractConfigCell switch1129Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1129()));
    private final AbstractConfigCell switch1130Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1130()));
    private final AbstractConfigCell switch1131Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1131()));
    private final AbstractConfigCell switch1132Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1132()));
    private final AbstractConfigCell switch1133Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1133()));
    private final AbstractConfigCell switch1134Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1134()));
    private final AbstractConfigCell switch1135Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1135()));
    private final AbstractConfigCell switch1136Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1136()));
    private final AbstractConfigCell switch1137Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1137()));
    private final AbstractConfigCell switch1138Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1138()));
    private final AbstractConfigCell switch1139Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1139()));
    private final AbstractConfigCell switch1140Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1140()));
    private final AbstractConfigCell switch1141Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1141()));
    private final AbstractConfigCell switch1142Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1142()));
    private final AbstractConfigCell switch1143Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1143()));
    private final AbstractConfigCell switch1144Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1144()));
    private final AbstractConfigCell switch1145Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1145()));
    private final AbstractConfigCell switch1146Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1146()));
    private final AbstractConfigCell switch1147Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1147()));
    private final AbstractConfigCell switch1148Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1148()));
    private final AbstractConfigCell switch1149Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1149()));
    private final AbstractConfigCell switch1150Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1150()));
    private final AbstractConfigCell switch1151Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1151()));
    private final AbstractConfigCell switch1152Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1152()));
    private final AbstractConfigCell switch1153Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1153()));
    private final AbstractConfigCell switch1154Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1154()));
    private final AbstractConfigCell switch1155Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1155()));
    private final AbstractConfigCell switch1156Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1156()));
    private final AbstractConfigCell switch1157Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1157()));
    private final AbstractConfigCell switch1158Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1158()));
    private final AbstractConfigCell switch1159Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1159()));
    private final AbstractConfigCell switch1160Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1160()));
    private final AbstractConfigCell switch1161Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1161()));
    private final AbstractConfigCell switch1162Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1162()));
    private final AbstractConfigCell switch1163Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1163()));
    private final AbstractConfigCell switch1164Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1164()));
    private final AbstractConfigCell switch1165Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1165()));
    private final AbstractConfigCell switch1166Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1166()));
    private final AbstractConfigCell switch1167Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1167()));
    private final AbstractConfigCell switch1168Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1168()));
    private final AbstractConfigCell switch1169Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1169()));
    private final AbstractConfigCell switch1170Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1170()));
    private final AbstractConfigCell switch1171Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1171()));
    private final AbstractConfigCell switch1172Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1172()));
    private final AbstractConfigCell switch1173Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1173()));
    private final AbstractConfigCell switch1174Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1174()));
    private final AbstractConfigCell switch1175Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1175()));
    private final AbstractConfigCell switch1176Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1176()));
    private final AbstractConfigCell switch1177Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1177()));
    private final AbstractConfigCell switch1178Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1178()));
    private final AbstractConfigCell switch1179Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1179()));
    private final AbstractConfigCell switch1180Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1180()));
    private final AbstractConfigCell switch1181Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1181()));
    private final AbstractConfigCell switch1182Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1182()));
    private final AbstractConfigCell switch1183Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1183()));
    private final AbstractConfigCell switch1184Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1184()));
    private final AbstractConfigCell switch1185Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1185()));
    private final AbstractConfigCell switch1186Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1186()));
    private final AbstractConfigCell switch1187Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1187()));
    private final AbstractConfigCell switch1188Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1188()));
    private final AbstractConfigCell switch1189Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1189()));
    private final AbstractConfigCell switch1190Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1190()));
    private final AbstractConfigCell switch1191Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1191()));
    private final AbstractConfigCell switch1192Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1192()));
    private final AbstractConfigCell switch1193Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1193()));
    private final AbstractConfigCell switch1194Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1194()));
    private final AbstractConfigCell switch1195Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1195()));
    private final AbstractConfigCell switch1196Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1196()));
    private final AbstractConfigCell switch1197Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1197()));
    private final AbstractConfigCell switch1198Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1198()));
    private final AbstractConfigCell switch1199Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1199()));
    private final AbstractConfigCell switch1200Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1200()));
    private final AbstractConfigCell switch1201Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1201()));
    private final AbstractConfigCell switch1202Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1202()));
    private final AbstractConfigCell switch1203Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1203()));
    private final AbstractConfigCell switch1204Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1204()));
    private final AbstractConfigCell switch1205Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1205()));
    private final AbstractConfigCell switch1206Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1206()));
    private final AbstractConfigCell switch1207Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1207()));
    private final AbstractConfigCell switch1208Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1208()));
    private final AbstractConfigCell switch1209Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1209()));
    private final AbstractConfigCell switch1210Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1210()));
    private final AbstractConfigCell switch1211Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1211()));
    private final AbstractConfigCell switch1212Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1212()));
    private final AbstractConfigCell switch1213Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1213()));
    private final AbstractConfigCell switch1214Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1214()));
    private final AbstractConfigCell switch1215Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1215()));
    private final AbstractConfigCell switch1216Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1216()));
    private final AbstractConfigCell switch1217Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1217()));
    private final AbstractConfigCell switch1218Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1218()));
    private final AbstractConfigCell switch1219Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1219()));
    private final AbstractConfigCell switch1220Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1220()));
    private final AbstractConfigCell switch1221Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1221()));
    private final AbstractConfigCell switch1222Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1222()));
    private final AbstractConfigCell switch1223Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1223()));
    private final AbstractConfigCell switch1224Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1224()));
    private final AbstractConfigCell switch1225Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1225()));
    private final AbstractConfigCell switch1226Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1226()));
    private final AbstractConfigCell switch1227Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1227()));
    private final AbstractConfigCell switch1228Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1228()));
    private final AbstractConfigCell switch1229Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1229()));
    private final AbstractConfigCell switch1230Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1230()));
    private final AbstractConfigCell switch1231Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1231()));
    private final AbstractConfigCell switch1232Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1232()));
    private final AbstractConfigCell switch1233Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1233()));
    private final AbstractConfigCell switch1234Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1234()));
    private final AbstractConfigCell switch1235Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1235()));
    private final AbstractConfigCell switch1236Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1236()));
    private final AbstractConfigCell switch1237Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1237()));
    private final AbstractConfigCell switch1238Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1238()));
    private final AbstractConfigCell switch1239Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1239()));
    private final AbstractConfigCell switch1240Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1240()));
    private final AbstractConfigCell switch1241Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1241()));
    private final AbstractConfigCell switch1242Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1242()));
    private final AbstractConfigCell switch1243Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1243()));
    private final AbstractConfigCell switch1244Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1244()));
    private final AbstractConfigCell switch1245Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1245()));
    private final AbstractConfigCell switch1246Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1246()));
    private final AbstractConfigCell switch1247Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1247()));
    private final AbstractConfigCell switch1248Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1248()));
    private final AbstractConfigCell switch1249Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1249()));
    private final AbstractConfigCell switch1250Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1250()));
    private final AbstractConfigCell switch1251Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1251()));
    private final AbstractConfigCell switch1252Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1252()));
    private final AbstractConfigCell switch1253Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1253()));
    private final AbstractConfigCell switch1254Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1254()));
    private final AbstractConfigCell switch1255Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1255()));
    private final AbstractConfigCell switch1256Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1256()));
    private final AbstractConfigCell switch1257Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1257()));
    private final AbstractConfigCell switch1258Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1258()));
    private final AbstractConfigCell switch1259Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1259()));
    private final AbstractConfigCell switch1260Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1260()));
    private final AbstractConfigCell switch1261Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1261()));
    private final AbstractConfigCell switch1262Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1262()));
    private final AbstractConfigCell switch1263Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1263()));
    private final AbstractConfigCell switch1264Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1264()));
    private final AbstractConfigCell switch1265Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1265()));
    private final AbstractConfigCell switch1266Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1266()));
    private final AbstractConfigCell switch1267Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1267()));
    private final AbstractConfigCell switch1268Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1268()));
    private final AbstractConfigCell switch1269Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1269()));
    private final AbstractConfigCell switch1270Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1270()));
    private final AbstractConfigCell switch1271Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1271()));
    private final AbstractConfigCell switch1272Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1272()));
    private final AbstractConfigCell switch1273Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1273()));
    private final AbstractConfigCell switch1274Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1274()));
    private final AbstractConfigCell switch1275Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1275()));
    private final AbstractConfigCell switch1276Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1276()));
    private final AbstractConfigCell switch1277Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1277()));
    private final AbstractConfigCell switch1278Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1278()));
    private final AbstractConfigCell switch1279Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1279()));
    private final AbstractConfigCell switch1280Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1280()));
    private final AbstractConfigCell switch1281Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1281()));
    private final AbstractConfigCell switch1282Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1282()));
    private final AbstractConfigCell switch1283Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1283()));
    private final AbstractConfigCell switch1284Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1284()));
    private final AbstractConfigCell switch1285Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1285()));
    private final AbstractConfigCell switch1286Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1286()));
    private final AbstractConfigCell switch1287Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1287()));
    private final AbstractConfigCell switch1288Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1288()));
    private final AbstractConfigCell switch1289Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1289()));
    private final AbstractConfigCell switch1290Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1290()));
    private final AbstractConfigCell switch1291Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1291()));
    private final AbstractConfigCell switch1292Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1292()));
    private final AbstractConfigCell switch1293Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1293()));
    private final AbstractConfigCell switch1294Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1294()));
    private final AbstractConfigCell switch1295Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1295()));
    private final AbstractConfigCell switch1296Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1296()));
    private final AbstractConfigCell switch1297Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1297()));
    private final AbstractConfigCell switch1298Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1298()));
    private final AbstractConfigCell switch1299Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1299()));
    private final AbstractConfigCell switch1300Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1300()));
    private final AbstractConfigCell switch1301Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1301()));
    private final AbstractConfigCell switch1302Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1302()));
    private final AbstractConfigCell switch1303Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1303()));
    private final AbstractConfigCell switch1304Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1304()));
    private final AbstractConfigCell switch1305Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1305()));
    private final AbstractConfigCell switch1306Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1306()));
    private final AbstractConfigCell switch1307Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1307()));
    private final AbstractConfigCell switch1308Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1308()));
    private final AbstractConfigCell switch1309Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1309()));
    private final AbstractConfigCell switch1310Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1310()));
    private final AbstractConfigCell switch1311Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1311()));
    private final AbstractConfigCell switch1312Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1312()));
    private final AbstractConfigCell switch1313Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1313()));
    private final AbstractConfigCell switch1314Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1314()));
    private final AbstractConfigCell switch1315Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1315()));
    private final AbstractConfigCell switch1316Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1316()));
    private final AbstractConfigCell switch1317Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1317()));
    private final AbstractConfigCell switch1318Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1318()));
    private final AbstractConfigCell switch1319Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1319()));
    private final AbstractConfigCell switch1320Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1320()));
    private final AbstractConfigCell switch1321Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1321()));
    private final AbstractConfigCell switch1322Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1322()));
    private final AbstractConfigCell switch1323Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1323()));
    private final AbstractConfigCell switch1324Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1324()));
    private final AbstractConfigCell switch1325Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1325()));
    private final AbstractConfigCell switch1326Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1326()));
    private final AbstractConfigCell switch1327Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1327()));
    private final AbstractConfigCell switch1328Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1328()));
    private final AbstractConfigCell switch1329Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1329()));
    private final AbstractConfigCell switch1330Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1330()));
    private final AbstractConfigCell switch1331Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1331()));
    private final AbstractConfigCell switch1332Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1332()));
    private final AbstractConfigCell switch1333Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1333()));
    private final AbstractConfigCell switch1334Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1334()));
    private final AbstractConfigCell switch1335Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1335()));
    private final AbstractConfigCell switch1336Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1336()));
    private final AbstractConfigCell switch1337Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1337()));
    private final AbstractConfigCell switch1338Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1338()));
    private final AbstractConfigCell switch1339Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1339()));
    private final AbstractConfigCell switch1340Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1340()));
    private final AbstractConfigCell switch1341Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1341()));
    private final AbstractConfigCell switch1342Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1342()));
    private final AbstractConfigCell switch1343Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1343()));
    private final AbstractConfigCell switch1344Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1344()));
    private final AbstractConfigCell switch1345Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1345()));
    private final AbstractConfigCell switch1346Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1346()));
    private final AbstractConfigCell switch1347Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1347()));
    private final AbstractConfigCell switch1348Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1348()));
    private final AbstractConfigCell switch1349Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1349()));
    private final AbstractConfigCell switch1350Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1350()));
    private final AbstractConfigCell switch1351Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1351()));
    private final AbstractConfigCell switch1352Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1352()));
    private final AbstractConfigCell switch1353Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1353()));
    private final AbstractConfigCell switch1354Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1354()));
    private final AbstractConfigCell switch1355Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1355()));
    private final AbstractConfigCell switch1356Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1356()));
    private final AbstractConfigCell switch1357Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1357()));
    private final AbstractConfigCell switch1358Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1358()));
    private final AbstractConfigCell switch1359Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1359()));
    private final AbstractConfigCell switch1360Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1360()));
    private final AbstractConfigCell switch1361Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1361()));
    private final AbstractConfigCell switch1362Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1362()));
    private final AbstractConfigCell switch1363Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1363()));
    private final AbstractConfigCell switch1364Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1364()));
    private final AbstractConfigCell switch1365Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1365()));
    private final AbstractConfigCell switch1366Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1366()));
    private final AbstractConfigCell switch1367Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1367()));
    private final AbstractConfigCell switch1368Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1368()));
    private final AbstractConfigCell switch1369Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1369()));
    private final AbstractConfigCell switch1370Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1370()));
    private final AbstractConfigCell switch1371Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1371()));
    private final AbstractConfigCell switch1372Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1372()));
    private final AbstractConfigCell switch1373Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1373()));
    private final AbstractConfigCell switch1374Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1374()));
    private final AbstractConfigCell switch1375Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1375()));
    private final AbstractConfigCell switch1376Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1376()));
    private final AbstractConfigCell switch1377Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1377()));
    private final AbstractConfigCell switch1378Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1378()));
    private final AbstractConfigCell switch1379Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1379()));
    private final AbstractConfigCell switch1380Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1380()));
    private final AbstractConfigCell switch1381Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1381()));
    private final AbstractConfigCell switch1382Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1382()));
    private final AbstractConfigCell switch1383Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1383()));
    private final AbstractConfigCell switch1384Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1384()));
    private final AbstractConfigCell switch1385Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1385()));
    private final AbstractConfigCell switch1386Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1386()));
    private final AbstractConfigCell switch1387Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1387()));
    private final AbstractConfigCell switch1388Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1388()));
    private final AbstractConfigCell switch1389Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1389()));
    private final AbstractConfigCell switch1390Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1390()));
    private final AbstractConfigCell switch1391Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1391()));
    private final AbstractConfigCell switch1392Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1392()));
    private final AbstractConfigCell switch1393Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1393()));
    private final AbstractConfigCell switch1394Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1394()));
    private final AbstractConfigCell switch1395Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1395()));
    private final AbstractConfigCell switch1396Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1396()));
    private final AbstractConfigCell switch1397Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1397()));
    private final AbstractConfigCell switch1398Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1398()));
    private final AbstractConfigCell switch1399Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1399()));
    private final AbstractConfigCell switch1400Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1400()));
    private final AbstractConfigCell switch1401Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1401()));
    private final AbstractConfigCell switch1402Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1402()));
    private final AbstractConfigCell switch1403Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1403()));
    private final AbstractConfigCell switch1404Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1404()));
    private final AbstractConfigCell switch1405Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1405()));
    private final AbstractConfigCell switch1406Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1406()));
    private final AbstractConfigCell switch1407Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1407()));
    private final AbstractConfigCell switch1408Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1408()));
    private final AbstractConfigCell switch1409Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1409()));
    private final AbstractConfigCell switch1410Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1410()));
    private final AbstractConfigCell switch1411Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1411()));
    private final AbstractConfigCell switch1412Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1412()));
    private final AbstractConfigCell switch1413Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1413()));
    private final AbstractConfigCell switch1414Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1414()));
    private final AbstractConfigCell switch1415Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1415()));
    private final AbstractConfigCell switch1416Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1416()));
    private final AbstractConfigCell switch1417Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1417()));
    private final AbstractConfigCell switch1418Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1418()));
    private final AbstractConfigCell switch1419Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1419()));
    private final AbstractConfigCell switch1420Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1420()));
    private final AbstractConfigCell switch1421Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1421()));
    private final AbstractConfigCell switch1422Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1422()));
    private final AbstractConfigCell switch1423Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1423()));
    private final AbstractConfigCell switch1424Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1424()));
    private final AbstractConfigCell switch1425Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1425()));
    private final AbstractConfigCell switch1426Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1426()));
    private final AbstractConfigCell switch1427Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1427()));
    private final AbstractConfigCell switch1428Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1428()));
    private final AbstractConfigCell switch1429Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1429()));
    private final AbstractConfigCell switch1430Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1430()));
    private final AbstractConfigCell switch1431Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1431()));
    private final AbstractConfigCell switch1432Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1432()));
    private final AbstractConfigCell switch1433Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1433()));
    private final AbstractConfigCell switch1434Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1434()));
    private final AbstractConfigCell switch1435Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1435()));
    private final AbstractConfigCell switch1436Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1436()));
    private final AbstractConfigCell switch1437Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1437()));
    private final AbstractConfigCell switch1438Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1438()));
    private final AbstractConfigCell switch1439Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1439()));
    private final AbstractConfigCell switch1440Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1440()));
    private final AbstractConfigCell switch1441Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1441()));
    private final AbstractConfigCell switch1442Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1442()));
    private final AbstractConfigCell switch1443Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1443()));
    private final AbstractConfigCell switch1444Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1444()));
    private final AbstractConfigCell switch1445Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1445()));
    private final AbstractConfigCell switch1446Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1446()));
    private final AbstractConfigCell switch1447Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1447()));
    private final AbstractConfigCell switch1448Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1448()));
    private final AbstractConfigCell switch1449Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1449()));
    private final AbstractConfigCell switch1450Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1450()));
    private final AbstractConfigCell switch1451Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1451()));
    private final AbstractConfigCell switch1452Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1452()));
    private final AbstractConfigCell switch1453Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1453()));
    private final AbstractConfigCell switch1454Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1454()));
    private final AbstractConfigCell switch1455Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1455()));
    private final AbstractConfigCell switch1456Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1456()));
    private final AbstractConfigCell switch1457Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1457()));
    private final AbstractConfigCell switch1458Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1458()));
    private final AbstractConfigCell switch1459Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1459()));
    private final AbstractConfigCell switch1460Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1460()));
    private final AbstractConfigCell switch1461Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1461()));
    private final AbstractConfigCell switch1462Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1462()));
    private final AbstractConfigCell switch1463Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1463()));
    private final AbstractConfigCell switch1464Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1464()));
    private final AbstractConfigCell switch1465Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1465()));
    private final AbstractConfigCell switch1466Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1466()));
    private final AbstractConfigCell switch1467Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1467()));
    private final AbstractConfigCell switch1468Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1468()));
    private final AbstractConfigCell switch1469Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1469()));
    private final AbstractConfigCell switch1470Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1470()));
    private final AbstractConfigCell switch1471Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1471()));
    private final AbstractConfigCell switch1472Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1472()));
    private final AbstractConfigCell switch1473Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1473()));
    private final AbstractConfigCell switch1474Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1474()));
    private final AbstractConfigCell switch1475Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1475()));
    private final AbstractConfigCell switch1476Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1476()));
    private final AbstractConfigCell switch1477Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1477()));
    private final AbstractConfigCell switch1478Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1478()));
    private final AbstractConfigCell switch1479Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1479()));
    private final AbstractConfigCell switch1480Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1480()));
    private final AbstractConfigCell switch1481Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1481()));
    private final AbstractConfigCell switch1482Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1482()));
    private final AbstractConfigCell switch1483Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1483()));
    private final AbstractConfigCell switch1484Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1484()));
    private final AbstractConfigCell switch1485Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1485()));
    private final AbstractConfigCell switch1486Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1486()));
    private final AbstractConfigCell switch1487Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1487()));
    private final AbstractConfigCell switch1488Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1488()));
    private final AbstractConfigCell switch1489Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1489()));
    private final AbstractConfigCell switch1490Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1490()));
    private final AbstractConfigCell switch1491Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1491()));
    private final AbstractConfigCell switch1492Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1492()));
    private final AbstractConfigCell switch1493Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1493()));
    private final AbstractConfigCell switch1494Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1494()));
    private final AbstractConfigCell switch1495Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1495()));
    private final AbstractConfigCell switch1496Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1496()));
    private final AbstractConfigCell switch1497Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1497()));
    private final AbstractConfigCell switch1498Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1498()));
    private final AbstractConfigCell switch1499Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1499()));
    private final AbstractConfigCell switch1500Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1500()));
    private final AbstractConfigCell switch1501Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1501()));
    private final AbstractConfigCell switch1502Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1502()));
    private final AbstractConfigCell switch1503Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1503()));
    private final AbstractConfigCell switch1504Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1504()));
    private final AbstractConfigCell switch1505Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1505()));
    private final AbstractConfigCell switch1506Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1506()));
    private final AbstractConfigCell switch1507Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1507()));
    private final AbstractConfigCell switch1508Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1508()));
    private final AbstractConfigCell switch1509Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1509()));
    private final AbstractConfigCell switch1510Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1510()));
    private final AbstractConfigCell switch1511Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1511()));
    private final AbstractConfigCell switch1512Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1512()));
    private final AbstractConfigCell switch1513Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1513()));
    private final AbstractConfigCell switch1514Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1514()));
    private final AbstractConfigCell switch1515Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1515()));
    private final AbstractConfigCell switch1516Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1516()));
    private final AbstractConfigCell switch1517Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1517()));
    private final AbstractConfigCell switch1518Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1518()));
    private final AbstractConfigCell switch1519Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1519()));
    private final AbstractConfigCell switch1520Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1520()));
    private final AbstractConfigCell switch1521Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1521()));
    private final AbstractConfigCell switch1522Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1522()));
    private final AbstractConfigCell switch1523Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1523()));
    private final AbstractConfigCell switch1524Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1524()));
    private final AbstractConfigCell switch1525Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1525()));
    private final AbstractConfigCell switch1526Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1526()));
    private final AbstractConfigCell switch1527Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1527()));
    private final AbstractConfigCell switch1528Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1528()));
    private final AbstractConfigCell switch1529Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1529()));
    private final AbstractConfigCell switch1530Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1530()));
    private final AbstractConfigCell switch1531Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1531()));
    private final AbstractConfigCell switch1532Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1532()));
    private final AbstractConfigCell switch1533Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1533()));
    private final AbstractConfigCell switch1534Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1534()));
    private final AbstractConfigCell switch1535Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1535()));
    private final AbstractConfigCell switch1536Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1536()));
    private final AbstractConfigCell switch1537Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1537()));
    private final AbstractConfigCell switch1538Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1538()));
    private final AbstractConfigCell switch1539Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1539()));
    private final AbstractConfigCell switch1540Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1540()));
    private final AbstractConfigCell switch1541Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1541()));
    private final AbstractConfigCell switch1542Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1542()));
    private final AbstractConfigCell switch1543Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1543()));
    private final AbstractConfigCell switch1544Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1544()));
    private final AbstractConfigCell switch1545Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1545()));
    private final AbstractConfigCell switch1546Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1546()));
    private final AbstractConfigCell switch1547Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1547()));
    private final AbstractConfigCell switch1548Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1548()));
    private final AbstractConfigCell switch1549Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1549()));
    private final AbstractConfigCell switch1550Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1550()));
    private final AbstractConfigCell switch1551Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1551()));
    private final AbstractConfigCell switch1552Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1552()));
    private final AbstractConfigCell switch1553Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1553()));
    private final AbstractConfigCell switch1554Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1554()));
    private final AbstractConfigCell switch1555Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1555()));
    private final AbstractConfigCell switch1556Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1556()));
    private final AbstractConfigCell switch1557Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1557()));
    private final AbstractConfigCell switch1558Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1558()));
    private final AbstractConfigCell switch1559Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1559()));
    private final AbstractConfigCell switch1560Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1560()));
    private final AbstractConfigCell switch1561Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1561()));
    private final AbstractConfigCell switch1562Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1562()));
    private final AbstractConfigCell switch1563Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1563()));
    private final AbstractConfigCell switch1564Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1564()));
    private final AbstractConfigCell switch1565Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1565()));
    private final AbstractConfigCell switch1566Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1566()));
    private final AbstractConfigCell switch1567Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1567()));
    private final AbstractConfigCell switch1568Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1568()));
    private final AbstractConfigCell switch1569Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1569()));
    private final AbstractConfigCell switch1570Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1570()));
    private final AbstractConfigCell switch1571Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1571()));
    private final AbstractConfigCell switch1572Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1572()));
    private final AbstractConfigCell switch1573Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1573()));
    private final AbstractConfigCell switch1574Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1574()));
    private final AbstractConfigCell switch1575Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1575()));
    private final AbstractConfigCell switch1576Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1576()));
    private final AbstractConfigCell switch1577Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1577()));
    private final AbstractConfigCell switch1578Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1578()));
    private final AbstractConfigCell switch1579Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1579()));
    private final AbstractConfigCell switch1580Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1580()));
    private final AbstractConfigCell switch1581Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1581()));
    private final AbstractConfigCell switch1582Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1582()));
    private final AbstractConfigCell switch1583Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1583()));
    private final AbstractConfigCell switch1584Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1584()));
    private final AbstractConfigCell switch1585Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1585()));
    private final AbstractConfigCell switch1586Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1586()));
    private final AbstractConfigCell switch1587Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1587()));
    private final AbstractConfigCell switch1588Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1588()));
    private final AbstractConfigCell switch1589Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1589()));
    private final AbstractConfigCell switch1590Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1590()));
    private final AbstractConfigCell switch1591Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1591()));
    private final AbstractConfigCell switch1592Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1592()));
    private final AbstractConfigCell switch1593Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1593()));
    private final AbstractConfigCell switch1594Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1594()));
    private final AbstractConfigCell switch1595Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1595()));
    private final AbstractConfigCell switch1596Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1596()));
    private final AbstractConfigCell switch1597Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1597()));
    private final AbstractConfigCell switch1598Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1598()));
    private final AbstractConfigCell switch1599Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1599()));
    private final AbstractConfigCell switch1600Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1600()));
    private final AbstractConfigCell switch1601Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1601()));
    private final AbstractConfigCell switch1602Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1602()));
    private final AbstractConfigCell switch1603Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1603()));
    private final AbstractConfigCell switch1604Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1604()));
    private final AbstractConfigCell switch1605Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1605()));
    private final AbstractConfigCell switch1606Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1606()));
    private final AbstractConfigCell switch1607Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1607()));
    private final AbstractConfigCell switch1608Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1608()));
    private final AbstractConfigCell switch1609Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1609()));
    private final AbstractConfigCell switch1610Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1610()));
    private final AbstractConfigCell switch1611Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1611()));
    private final AbstractConfigCell switch1612Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1612()));
    private final AbstractConfigCell switch1613Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1613()));
    private final AbstractConfigCell switch1614Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1614()));
    private final AbstractConfigCell switch1615Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1615()));
    private final AbstractConfigCell switch1616Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1616()));
    private final AbstractConfigCell switch1617Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1617()));
    private final AbstractConfigCell switch1618Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1618()));
    private final AbstractConfigCell switch1619Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1619()));
    private final AbstractConfigCell switch1620Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1620()));
    private final AbstractConfigCell switch1621Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1621()));
    private final AbstractConfigCell switch1622Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1622()));
    private final AbstractConfigCell switch1623Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1623()));
    private final AbstractConfigCell switch1624Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1624()));
    private final AbstractConfigCell switch1625Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1625()));
    private final AbstractConfigCell switch1626Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1626()));
    private final AbstractConfigCell switch1627Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1627()));
    private final AbstractConfigCell switch1628Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1628()));
    private final AbstractConfigCell switch1629Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1629()));
    private final AbstractConfigCell switch1630Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1630()));
    private final AbstractConfigCell switch1631Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1631()));
    private final AbstractConfigCell switch1632Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1632()));
    private final AbstractConfigCell switch1633Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1633()));
    private final AbstractConfigCell switch1634Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1634()));
    private final AbstractConfigCell switch1635Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1635()));
    private final AbstractConfigCell switch1636Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1636()));
    private final AbstractConfigCell switch1637Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1637()));
    private final AbstractConfigCell switch1638Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1638()));
    private final AbstractConfigCell switch1639Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1639()));
    private final AbstractConfigCell switch1640Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1640()));
    private final AbstractConfigCell switch1641Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1641()));
    private final AbstractConfigCell switch1642Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1642()));
    private final AbstractConfigCell switch1643Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1643()));
    private final AbstractConfigCell switch1644Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1644()));
    private final AbstractConfigCell switch1645Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1645()));
    private final AbstractConfigCell switch1646Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1646()));
    private final AbstractConfigCell switch1647Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1647()));
    private final AbstractConfigCell switch1648Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1648()));
    private final AbstractConfigCell switch1649Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1649()));
    private final AbstractConfigCell switch1650Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1650()));
    private final AbstractConfigCell switch1651Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1651()));
    private final AbstractConfigCell switch1652Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1652()));
    private final AbstractConfigCell switch1653Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1653()));
    private final AbstractConfigCell switch1654Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1654()));
    private final AbstractConfigCell switch1655Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1655()));
    private final AbstractConfigCell switch1656Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1656()));
    private final AbstractConfigCell switch1657Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1657()));
    private final AbstractConfigCell switch1658Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1658()));
    private final AbstractConfigCell switch1659Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1659()));
    private final AbstractConfigCell switch1660Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1660()));
    private final AbstractConfigCell switch1661Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1661()));
    private final AbstractConfigCell switch1662Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1662()));
    private final AbstractConfigCell switch1663Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1663()));
    private final AbstractConfigCell switch1664Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1664()));
    private final AbstractConfigCell switch1665Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1665()));
    private final AbstractConfigCell switch1666Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1666()));
    private final AbstractConfigCell switch1667Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1667()));
    private final AbstractConfigCell switch1668Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1668()));
    private final AbstractConfigCell switch1669Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1669()));
    private final AbstractConfigCell switch1670Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1670()));
    private final AbstractConfigCell switch1671Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1671()));
    private final AbstractConfigCell switch1672Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1672()));
    private final AbstractConfigCell switch1673Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1673()));
    private final AbstractConfigCell switch1674Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1674()));
    private final AbstractConfigCell switch1675Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1675()));
    private final AbstractConfigCell switch1676Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1676()));
    private final AbstractConfigCell switch1677Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1677()));
    private final AbstractConfigCell switch1678Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1678()));
    private final AbstractConfigCell switch1679Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1679()));
    private final AbstractConfigCell switch1680Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1680()));
    private final AbstractConfigCell switch1681Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1681()));
    private final AbstractConfigCell switch1682Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1682()));
    private final AbstractConfigCell switch1683Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1683()));
    private final AbstractConfigCell switch1684Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1684()));
    private final AbstractConfigCell switch1685Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1685()));
    private final AbstractConfigCell switch1686Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1686()));
    private final AbstractConfigCell switch1687Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1687()));
    private final AbstractConfigCell switch1688Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1688()));
    private final AbstractConfigCell switch1689Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1689()));
    private final AbstractConfigCell switch1690Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1690()));
    private final AbstractConfigCell switch1691Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1691()));
    private final AbstractConfigCell switch1692Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1692()));
    private final AbstractConfigCell switch1693Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1693()));
    private final AbstractConfigCell switch1694Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1694()));
    private final AbstractConfigCell switch1695Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1695()));
    private final AbstractConfigCell switch1696Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1696()));
    private final AbstractConfigCell switch1697Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1697()));
    private final AbstractConfigCell switch1698Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1698()));
    private final AbstractConfigCell switch1699Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1699()));
    private final AbstractConfigCell switch1700Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1700()));
    private final AbstractConfigCell switch1701Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1701()));
    private final AbstractConfigCell switch1702Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1702()));
    private final AbstractConfigCell switch1703Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1703()));
    private final AbstractConfigCell switch1704Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1704()));
    private final AbstractConfigCell switch1705Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1705()));
    private final AbstractConfigCell switch1706Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1706()));
    private final AbstractConfigCell switch1707Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1707()));
    private final AbstractConfigCell switch1708Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1708()));
    private final AbstractConfigCell switch1709Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1709()));
    private final AbstractConfigCell switch1710Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1710()));
    private final AbstractConfigCell switch1711Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1711()));
    private final AbstractConfigCell switch1712Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1712()));
    private final AbstractConfigCell switch1713Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1713()));
    private final AbstractConfigCell switch1714Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1714()));
    private final AbstractConfigCell switch1715Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1715()));
    private final AbstractConfigCell switch1716Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1716()));
    private final AbstractConfigCell switch1717Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1717()));
    private final AbstractConfigCell switch1718Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1718()));
    private final AbstractConfigCell switch1719Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1719()));
    private final AbstractConfigCell switch1720Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1720()));
    private final AbstractConfigCell switch1721Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1721()));
    private final AbstractConfigCell switch1722Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1722()));
    private final AbstractConfigCell switch1723Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1723()));
    private final AbstractConfigCell switch1724Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1724()));
    private final AbstractConfigCell switch1725Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1725()));
    private final AbstractConfigCell switch1726Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1726()));
    private final AbstractConfigCell switch1727Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1727()));
    private final AbstractConfigCell switch1728Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1728()));
    private final AbstractConfigCell switch1729Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1729()));
    private final AbstractConfigCell switch1730Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1730()));
    private final AbstractConfigCell switch1731Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1731()));
    private final AbstractConfigCell switch1732Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1732()));
    private final AbstractConfigCell switch1733Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1733()));
    private final AbstractConfigCell switch1734Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1734()));
    private final AbstractConfigCell switch1735Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1735()));
    private final AbstractConfigCell switch1736Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1736()));
    private final AbstractConfigCell switch1737Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1737()));
    private final AbstractConfigCell switch1738Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1738()));
    private final AbstractConfigCell switch1739Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1739()));
    private final AbstractConfigCell switch1740Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1740()));
    private final AbstractConfigCell switch1741Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1741()));
    private final AbstractConfigCell switch1742Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1742()));
    private final AbstractConfigCell switch1743Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1743()));
    private final AbstractConfigCell switch1744Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1744()));
    private final AbstractConfigCell switch1745Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1745()));
    private final AbstractConfigCell switch1746Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1746()));
    private final AbstractConfigCell switch1747Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1747()));
    private final AbstractConfigCell switch1748Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1748()));
    private final AbstractConfigCell switch1749Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1749()));
    private final AbstractConfigCell switch1750Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1750()));
    private final AbstractConfigCell switch1751Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1751()));
    private final AbstractConfigCell switch1752Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1752()));
    private final AbstractConfigCell switch1753Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1753()));
    private final AbstractConfigCell switch1754Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1754()));
    private final AbstractConfigCell switch1755Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1755()));
    private final AbstractConfigCell switch1756Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1756()));
    private final AbstractConfigCell switch1757Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1757()));
    private final AbstractConfigCell switch1758Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1758()));
    private final AbstractConfigCell switch1759Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1759()));
    private final AbstractConfigCell switch1760Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1760()));
    private final AbstractConfigCell switch1761Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1761()));
    private final AbstractConfigCell switch1762Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1762()));
    private final AbstractConfigCell switch1763Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1763()));
    private final AbstractConfigCell switch1764Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1764()));
    private final AbstractConfigCell switch1765Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1765()));
    private final AbstractConfigCell switch1766Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1766()));
    private final AbstractConfigCell switch1767Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1767()));
    private final AbstractConfigCell switch1768Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1768()));
    private final AbstractConfigCell switch1769Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1769()));
    private final AbstractConfigCell switch1770Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1770()));
    private final AbstractConfigCell switch1771Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1771()));
    private final AbstractConfigCell switch1772Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1772()));
    private final AbstractConfigCell switch1773Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1773()));
    private final AbstractConfigCell switch1774Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1774()));
    private final AbstractConfigCell switch1775Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1775()));
    private final AbstractConfigCell switch1776Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1776()));
    private final AbstractConfigCell switch1777Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1777()));
    private final AbstractConfigCell switch1778Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1778()));
    private final AbstractConfigCell switch1779Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1779()));
    private final AbstractConfigCell switch1780Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1780()));
    private final AbstractConfigCell switch1781Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1781()));
    private final AbstractConfigCell switch1782Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1782()));
    private final AbstractConfigCell switch1783Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1783()));
    private final AbstractConfigCell switch1784Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1784()));
    private final AbstractConfigCell switch1785Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1785()));
    private final AbstractConfigCell switch1786Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1786()));
    private final AbstractConfigCell switch1787Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1787()));
    private final AbstractConfigCell switch1788Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1788()));
    private final AbstractConfigCell switch1789Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1789()));
    private final AbstractConfigCell switch1790Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1790()));
    private final AbstractConfigCell switch1791Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1791()));
    private final AbstractConfigCell switch1792Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1792()));
    private final AbstractConfigCell switch1793Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1793()));
    private final AbstractConfigCell switch1794Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1794()));
    private final AbstractConfigCell switch1795Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1795()));
    private final AbstractConfigCell switch1796Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1796()));
    private final AbstractConfigCell switch1797Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1797()));
    private final AbstractConfigCell switch1798Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1798()));
    private final AbstractConfigCell switch1799Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1799()));
    private final AbstractConfigCell switch1800Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1800()));
    private final AbstractConfigCell switch1801Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1801()));
    private final AbstractConfigCell switch1802Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1802()));
    private final AbstractConfigCell switch1803Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1803()));
    private final AbstractConfigCell switch1804Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1804()));
    private final AbstractConfigCell switch1805Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1805()));
    private final AbstractConfigCell switch1806Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1806()));
    private final AbstractConfigCell switch1807Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1807()));
    private final AbstractConfigCell switch1808Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1808()));
    private final AbstractConfigCell switch1809Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1809()));
    private final AbstractConfigCell switch1810Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1810()));
    private final AbstractConfigCell switch1811Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1811()));
    private final AbstractConfigCell switch1812Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1812()));
    private final AbstractConfigCell switch1813Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1813()));
    private final AbstractConfigCell switch1814Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1814()));
    private final AbstractConfigCell switch1815Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1815()));
    private final AbstractConfigCell switch1816Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1816()));
    private final AbstractConfigCell switch1817Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1817()));
    private final AbstractConfigCell switch1818Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1818()));
    private final AbstractConfigCell switch1819Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1819()));
    private final AbstractConfigCell switch1820Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1820()));
    private final AbstractConfigCell switch1821Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1821()));
    private final AbstractConfigCell switch1822Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1822()));
    private final AbstractConfigCell switch1823Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1823()));
    private final AbstractConfigCell switch1824Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1824()));
    private final AbstractConfigCell switch1825Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1825()));
    private final AbstractConfigCell switch1826Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1826()));
    private final AbstractConfigCell switch1827Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1827()));
    private final AbstractConfigCell switch1828Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1828()));
    private final AbstractConfigCell switch1829Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1829()));
    private final AbstractConfigCell switch1830Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1830()));
    private final AbstractConfigCell switch1831Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1831()));
    private final AbstractConfigCell switch1832Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1832()));
    private final AbstractConfigCell switch1833Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1833()));
    private final AbstractConfigCell switch1834Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1834()));
    private final AbstractConfigCell switch1835Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1835()));
    private final AbstractConfigCell switch1836Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1836()));
    private final AbstractConfigCell switch1837Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1837()));
    private final AbstractConfigCell switch1838Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1838()));
    private final AbstractConfigCell switch1839Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1839()));
    private final AbstractConfigCell switch1840Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1840()));
    private final AbstractConfigCell switch1841Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1841()));
    private final AbstractConfigCell switch1842Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1842()));
    private final AbstractConfigCell switch1843Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1843()));
    private final AbstractConfigCell switch1844Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1844()));
    private final AbstractConfigCell switch1845Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1845()));
    private final AbstractConfigCell switch1846Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1846()));
    private final AbstractConfigCell switch1847Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1847()));
    private final AbstractConfigCell switch1848Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1848()));
    private final AbstractConfigCell switch1849Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1849()));
    private final AbstractConfigCell switch1850Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1850()));
    private final AbstractConfigCell switch1851Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1851()));
    private final AbstractConfigCell switch1852Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1852()));
    private final AbstractConfigCell switch1853Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1853()));
    private final AbstractConfigCell switch1854Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1854()));
    private final AbstractConfigCell switch1855Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1855()));
    private final AbstractConfigCell switch1856Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1856()));
    private final AbstractConfigCell switch1857Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1857()));
    private final AbstractConfigCell switch1858Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1858()));
    private final AbstractConfigCell switch1859Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1859()));
    private final AbstractConfigCell switch1860Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1860()));
    private final AbstractConfigCell switch1861Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1861()));
    private final AbstractConfigCell switch1862Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1862()));
    private final AbstractConfigCell switch1863Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1863()));
    private final AbstractConfigCell switch1864Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1864()));
    private final AbstractConfigCell switch1865Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1865()));
    private final AbstractConfigCell switch1866Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1866()));
    private final AbstractConfigCell switch1867Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1867()));
    private final AbstractConfigCell switch1868Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1868()));
    private final AbstractConfigCell switch1869Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1869()));
    private final AbstractConfigCell switch1870Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1870()));
    private final AbstractConfigCell switch1871Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1871()));
    private final AbstractConfigCell switch1872Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1872()));
    private final AbstractConfigCell switch1873Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1873()));
    private final AbstractConfigCell switch1874Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1874()));
    private final AbstractConfigCell switch1875Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1875()));
    private final AbstractConfigCell switch1876Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1876()));
    private final AbstractConfigCell switch1877Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1877()));
    private final AbstractConfigCell switch1878Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1878()));
    private final AbstractConfigCell switch1879Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1879()));
    private final AbstractConfigCell switch1880Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1880()));
    private final AbstractConfigCell switch1881Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1881()));
    private final AbstractConfigCell switch1882Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1882()));
    private final AbstractConfigCell switch1883Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1883()));
    private final AbstractConfigCell switch1884Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1884()));
    private final AbstractConfigCell switch1885Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1885()));
    private final AbstractConfigCell switch1886Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1886()));
    private final AbstractConfigCell switch1887Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1887()));
    private final AbstractConfigCell switch1888Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1888()));
    private final AbstractConfigCell switch1889Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1889()));
    private final AbstractConfigCell switch1890Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1890()));
    private final AbstractConfigCell switch1891Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1891()));
    private final AbstractConfigCell switch1892Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1892()));
    private final AbstractConfigCell switch1893Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1893()));
    private final AbstractConfigCell switch1894Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1894()));
    private final AbstractConfigCell switch1895Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1895()));
    private final AbstractConfigCell switch1896Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1896()));
    private final AbstractConfigCell switch1897Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1897()));
    private final AbstractConfigCell switch1898Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1898()));
    private final AbstractConfigCell switch1899Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1899()));
    private final AbstractConfigCell switch1900Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1900()));
    private final AbstractConfigCell switch1901Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1901()));
    private final AbstractConfigCell switch1902Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1902()));
    private final AbstractConfigCell switch1903Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1903()));
    private final AbstractConfigCell switch1904Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1904()));
    private final AbstractConfigCell switch1905Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1905()));
    private final AbstractConfigCell switch1906Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1906()));
    private final AbstractConfigCell switch1907Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1907()));
    private final AbstractConfigCell switch1908Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1908()));
    private final AbstractConfigCell switch1909Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1909()));
    private final AbstractConfigCell switch1910Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1910()));
    private final AbstractConfigCell switch1911Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1911()));
    private final AbstractConfigCell switch1912Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1912()));
    private final AbstractConfigCell switch1913Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1913()));
    private final AbstractConfigCell switch1914Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1914()));
    private final AbstractConfigCell switch1915Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1915()));
    private final AbstractConfigCell switch1916Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1916()));
    private final AbstractConfigCell switch1917Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1917()));
    private final AbstractConfigCell switch1918Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1918()));
    private final AbstractConfigCell switch1919Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1919()));
    private final AbstractConfigCell switch1920Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1920()));
    private final AbstractConfigCell switch1921Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1921()));
    private final AbstractConfigCell switch1922Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1922()));
    private final AbstractConfigCell switch1923Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1923()));
    private final AbstractConfigCell switch1924Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1924()));
    private final AbstractConfigCell switch1925Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1925()));
    private final AbstractConfigCell switch1926Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1926()));
    private final AbstractConfigCell switch1927Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1927()));
    private final AbstractConfigCell switch1928Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1928()));
    private final AbstractConfigCell switch1929Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1929()));
    private final AbstractConfigCell switch1930Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1930()));
    private final AbstractConfigCell switch1931Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1931()));
    private final AbstractConfigCell switch1932Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1932()));
    private final AbstractConfigCell switch1933Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1933()));
    private final AbstractConfigCell switch1934Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1934()));
    private final AbstractConfigCell switch1935Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1935()));
    private final AbstractConfigCell switch1936Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1936()));
    private final AbstractConfigCell switch1937Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1937()));
    private final AbstractConfigCell switch1938Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1938()));
    private final AbstractConfigCell switch1939Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1939()));
    private final AbstractConfigCell switch1940Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1940()));
    private final AbstractConfigCell switch1941Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1941()));
    private final AbstractConfigCell switch1942Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1942()));
    private final AbstractConfigCell switch1943Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1943()));
    private final AbstractConfigCell switch1944Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1944()));
    private final AbstractConfigCell switch1945Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1945()));
    private final AbstractConfigCell switch1946Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1946()));
    private final AbstractConfigCell switch1947Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1947()));
    private final AbstractConfigCell switch1948Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1948()));
    private final AbstractConfigCell switch1949Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1949()));
    private final AbstractConfigCell switch1950Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1950()));
    private final AbstractConfigCell switch1951Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1951()));
    private final AbstractConfigCell switch1952Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1952()));
    private final AbstractConfigCell switch1953Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1953()));
    private final AbstractConfigCell switch1954Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1954()));
    private final AbstractConfigCell switch1955Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1955()));
    private final AbstractConfigCell switch1956Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1956()));
    private final AbstractConfigCell switch1957Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1957()));
    private final AbstractConfigCell switch1958Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1958()));
    private final AbstractConfigCell switch1959Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1959()));
    private final AbstractConfigCell switch1960Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1960()));
    private final AbstractConfigCell switch1961Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1961()));
    private final AbstractConfigCell switch1962Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1962()));
    private final AbstractConfigCell switch1963Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1963()));
    private final AbstractConfigCell switch1964Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1964()));
    private final AbstractConfigCell switch1965Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1965()));
    private final AbstractConfigCell switch1966Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1966()));
    private final AbstractConfigCell switch1967Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1967()));
    private final AbstractConfigCell switch1968Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1968()));
    private final AbstractConfigCell switch1969Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1969()));
    private final AbstractConfigCell switch1970Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1970()));
    private final AbstractConfigCell switch1971Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1971()));
    private final AbstractConfigCell switch1972Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1972()));
    private final AbstractConfigCell switch1973Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1973()));
    private final AbstractConfigCell switch1974Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1974()));
    private final AbstractConfigCell switch1975Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1975()));
    private final AbstractConfigCell switch1976Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1976()));
    private final AbstractConfigCell switch1977Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1977()));
    private final AbstractConfigCell switch1978Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1978()));
    private final AbstractConfigCell switch1979Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1979()));
    private final AbstractConfigCell switch1980Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1980()));
    private final AbstractConfigCell switch1981Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1981()));
    private final AbstractConfigCell switch1982Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1982()));
    private final AbstractConfigCell switch1983Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1983()));
    private final AbstractConfigCell switch1984Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1984()));
    private final AbstractConfigCell switch1985Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1985()));
    private final AbstractConfigCell switch1986Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1986()));
    private final AbstractConfigCell switch1987Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1987()));
    private final AbstractConfigCell switch1988Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1988()));
    private final AbstractConfigCell switch1989Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1989()));
    private final AbstractConfigCell switch1990Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1990()));
    private final AbstractConfigCell switch1991Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1991()));
    private final AbstractConfigCell switch1992Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1992()));
    private final AbstractConfigCell switch1993Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1993()));
    private final AbstractConfigCell switch1994Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1994()));
    private final AbstractConfigCell switch1995Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1995()));
    private final AbstractConfigCell switch1996Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1996()));
    private final AbstractConfigCell switch1997Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1997()));
    private final AbstractConfigCell switch1998Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1998()));
    private final AbstractConfigCell switch1999Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch1999()));
    private final AbstractConfigCell switch2000Row = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSwitch2000()));

    public NekoGeneralSettingsActivity() {
        updateRows();
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows();

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        var superView = super.createView(context);

        listAdapter = new ListAdapter(context);

        if (listView.getItemAnimator() != null) {
            ((DefaultItemAnimator) listView.getItemAnimator()).setSupportsChangeAnimations(false);
        }
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
                    } catch (Exception e) {
                    }
                }
            } else if (a instanceof ConfigCellCustom) { // Custom OnClick
                if (position == cellGroup.rows.indexOf(pgpAppRow)) {
                    PopupBuilder builder = new PopupBuilder(view);

                    builder.addSubItem(0, LocaleController.getString("None", R.string.None));

                    LinkedList<String> appsMap = new LinkedList<>();
                    appsMap.add("");

                    Intent intent = new Intent(OpenPgpApi.SERVICE_INTENT_2);
                    List<ResolveInfo> resInfo = getParentActivity().getPackageManager().queryIntentServices(intent, 0);

                    if (resInfo != null) {
                        for (ResolveInfo resolveInfo : resInfo) {
                            if (resolveInfo.serviceInfo == null) {
                                continue;
                            }

                            String packageName = resolveInfo.serviceInfo.packageName;
                            String simpleName = String.valueOf(resolveInfo.serviceInfo.loadLabel(getParentActivity().getPackageManager()));

                            builder.addSubItem(appsMap.size(), simpleName);
                            appsMap.add(packageName);

                        }
                    }

                    builder.setDelegate((i) -> {
                        NekoConfig.openPGPApp.setConfigString(appsMap.get(i));
                        NekoConfig.openPGPKeyId.setConfigLong(0L);
                        listAdapter.notifyItemChanged(cellGroup.rows.indexOf(pgpAppRow));
                        listAdapter.notifyItemChanged(cellGroup.rows.indexOf(keyRow));

                        if (i > 0) PGPUtil.recreateConnection();
                    });

                    builder.show();
                } else if (position == cellGroup.rows.indexOf(translationProviderRow)) {
                    if (!((ConfigCellCustom) a).enabled) return;
                    PopupBuilder builder = new PopupBuilder(view);

                    builder.setItems(new String[]{
                            LocaleController.getString(R.string.ProviderGoogleTranslate),
                            LocaleController.getString(R.string.ProviderGoogleTranslateCN),
                            LocaleController.getString(R.string.ProviderGoogleTranslate) + " 2",
                            LocaleController.getString(R.string.ProviderLingocloud),
                            LocaleController.getString(R.string.ProviderMicrosoftTranslator),
                            LocaleController.getString(R.string.ProviderVolcengineTranslate),
                            LocaleController.getString(R.string.ProviderDeepLxTranslate),
                            LocaleController.getString(R.string.ProviderTelegramAPI),
                            LocaleController.getString(R.string.ProviderTranSmartTranslate),
                            LocaleController.getString(R.string.ProviderLLMTranslate),
                            LocaleController.getString(R.string.ProviderDeepLTranslate),
                            LocaleController.getString(R.string.ProviderDeepLFreeTranslate),
                    }, (i, __) -> {
                        NekoConfig.translationProvider.setConfigInt(i + 1);
                        updateRows();
                        listAdapter.notifyItemChanged(position);
                        return Unit.INSTANCE;
                    });
                    builder.show();
                } else if (position == cellGroup.rows.indexOf(translateToLangRow) || position == cellGroup.rows.indexOf(translateInputToLangRow)) {
                    Translator.showTargetLangSelect(view, position == cellGroup.rows.indexOf(translateInputToLangRow), (locale) -> {
                        if (position == cellGroup.rows.indexOf(translateToLangRow)) {
                            NekoConfig.translateToLang.setConfigString(TranslatorKt.getLocale2code(locale));
                        } else {
                            NekoConfig.translateInputLang.setConfigString(TranslatorKt.getLocale2code(locale));
                        }
                        listAdapter.notifyItemChanged(position);
                        return Unit.INSTANCE;
                    });
                } else if (position == cellGroup.rows.indexOf(llmSettingsRow)) {
                    presentFragment(new NekoLLMSettingsActivity());
                } else if (position == cellGroup.rows.indexOf(nameOrderRow)) {
                    LocaleController.getInstance().recreateFormatters();
                }
            }
        });
        listView.setOnItemLongClickListener((view, position, x, y) -> {
            var holder = listView.findViewHolderForAdapterPosition(position);
            if (holder != null && listAdapter.isEnabled(holder)) {
                createLongClickDialog(view, NekoGeneralSettingsActivity.this, "general", position);
                return true;
            }
            return false;
        });

        // Cells: Set OnSettingChanged Callbacks
        cellGroup.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NaConfig.INSTANCE.getCustomIpStrategy().getKey())) {
                ConnectionsManager.ipStrategy = -1;
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        ConnectionsManager.native_setIpStrategy(a, ConnectionsManager.getIpStrategy());
                    }
                }
            } else if (key.equals(NaConfig.INSTANCE.getSwitchStyle().getKey())) {
                Switch.invalidateAll();
            } else if (key.equals(NekoConfig.inappCamera.getKey())) {
                SharedConfig.setInappCamera((boolean) newValue);
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.hidePhone.getKey())) {
                parentLayout.rebuildAllFragmentViews(false, false);
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            } else if (key.equals(NekoConfig.transparentStatusBar.getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.hideProxySponsorChannel.getKey())) {
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        MessagesController.getInstance(a).checkPromoInfo(true);
                    }
                }
            } else if (key.equals(NaConfig.INSTANCE.getDisableCommunityChatGrouping().getKey())) {
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        MessagesController.getInstance(a).onCommunityChatGroupingChanged();
                    }
                }
            } else if (key.equals(NekoConfig.actionBarDecoration.getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getNotificationIcon().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.tabletMode.getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.newYear.getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.usePersianCalendar.getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.displayPersianCalendarByLatin.getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.typeface.getKey()) || key.equals(NaConfig.INSTANCE.getUseSystemFontInTitle().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.disableSystemAccount.getKey())) {
                if ((boolean) newValue) {
                    getContactsController().deleteUnknownAppAccounts();
                } else {
                    for (int a : SharedConfig.activeAccounts) {
                        ContactsController.getInstance(a).checkAppAccount();
                    }
                }
            } else if (key.equals(NekoConfig.largeAvatarInDrawer.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                updateRows();
            } else if (key.equals(NekoConfig.avatarBackgroundBlur.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            } else if (key.equals(NekoConfig.avatarBackgroundDarken.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            } else if (key.equals(NekoConfig.disableAppBarShadow.getKey())) {
                ActionBarLayout.headerShadowDrawable = (boolean) newValue ? null : parentLayout.getParentActivity().getResources().getDrawable(R.drawable.header_shadow).mutate();
                parentLayout.rebuildFragments(INavigationLayout.REBUILD_FLAG_REBUILD_LAST | INavigationLayout.REBUILD_FLAG_REBUILD_ONLY_LAST);
            } else if (NekoConfig.forceBlurInChat.getKey().equals(key)) {
                boolean enabled = (Boolean) newValue;
                if (chatBlurAlphaSeekbar != null)
                    chatBlurAlphaSeekbar.setEnabled(enabled);
                ((ConfigCellCustom) chatBlurAlphaValueRow).enabled = enabled;
            } else if (NekoConfig.useOSMDroidMap.getKey().equals(key)) {
                boolean enabled = (Boolean) newValue;
                ((ConfigCellTextCheck) mapDriftingFixForGoogleMapsRow).setEnabled(!enabled);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(mapDriftingFixForGoogleMapsRow));
            } else if (key.equals(NekoConfig.useTelegramTranslateInChat.getKey())) {
                var cell = (TextSettingsCell) (listView.findViewHolderForAdapterPosition(cellGroup.rows.indexOf(translationProviderRow)).itemView);
                if (NekoConfig.useTelegramTranslateInChat.Bool()) {
                    NekoConfig.translationProvider.setConfigInt(Translator.providerTelegram);
                    ((ConfigCellCustom) translationProviderRow).setEnabled(false);
                    cell.setEnabled(false);
                } else {
                    ((ConfigCellCustom) translationProviderRow).setEnabled(true);
                    cell.setEnabled(true);
                }
                updateRows();
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(translationProviderRow));
            } else if (key.equals(NaConfig.INSTANCE.getLlmProvider().getKey())) {
                // Rebuild LLM rows to show/hide API format and URL
                updateRows();
                listAdapter.notifyDataSetChanged();
            } else if (key.equals(NaConfig.INSTANCE.getPushServiceType().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getPushServiceTypeInAppDialog().getKey())) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, NotificationsService.class));
                ApplicationLoader.startPushService();
            } else if (key.equals(NaConfig.INSTANCE.getPushServiceTypeUnifiedGateway().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getSentryAnalytics().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getCustomTitleUserName().getKey())) {
                boolean enabled = (Boolean) newValue;
                ((ConfigCellTextInput) customTitleRow).setEnabled(!enabled);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(customTitleRow));
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getSidebarSettingsActivity().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getTabStyleStroke().getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);
            } else if (key.equals(NaConfig.INSTANCE.getMaterialDesign3ChatHeader().getKey())) {
                parentLayout.rebuildAllFragmentViews(false, false);
            }
        };

        //Cells: Set ListAdapter
        cellGroup.setListAdapter(listView, listAdapter);

        return superView;
    }

    private void requestKey(Intent data) {

        PGPUtil.post(() -> PGPUtil.api.executeApiAsync(data, null, null, result -> {

            switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {

                case OpenPgpApi.RESULT_CODE_SUCCESS: {

                    long keyId = result.getLongExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, 0L);
                    NekoConfig.openPGPKeyId.setConfigLong(keyId);

                    listAdapter.notifyItemChanged(cellGroup.rows.indexOf(keyRow));

                    break;
                }
                case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {

                    PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                    try {
                        Activity act = (Activity) getParentActivity();
                        act.startIntentSenderFromChild(
                                act, pi.getIntentSender(),
                                114, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(OpenPgpApi.TAG, "SendIntentException", e);
                    }
                    break;
                }
                case OpenPgpApi.RESULT_CODE_ERROR: {
                    OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);
                    AlertUtil.showToast(error.getMessage());
                    break;
                }
            }

        }));


    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == 114 && resultCode == Activity.RESULT_OK) {
            requestKey(data);
        }
    }

    private static class OpenPgpProviderEntry {
        private String packageName;
        private String simpleName;
        private Intent intent;

        OpenPgpProviderEntry(String packageName, String simpleName) {
            this.packageName = packageName;
            this.simpleName = simpleName;
        }

        OpenPgpProviderEntry(String packageName, String simpleName, Intent intent) {
            this(packageName, simpleName);
            this.intent = intent;
        }

        @Override
        public String toString() {
            return simpleName;
        }
    }

    @Override
    public int getBaseGuid() {
        return 12000;
    }

    @Override
    public int getDrawable() {
        return R.drawable.msg_theme;
    }

    @Override
    public String getTitle() {
        return LocaleController.getString("General", R.string.General);
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

    //impl ListAdapter
    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean partial, boolean divider) {
            AbstractConfigCell a = cellGroup.rows.get(position);
            if (a != null) {
                if (a instanceof ConfigCellCustom) {
                    // Custom binds
                    if (holder.itemView instanceof TextSettingsCell) {
                        TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                        if (position == cellGroup.rows.indexOf(translationProviderRow)) {
                            String value;
                            switch (NekoConfig.translationProvider.Int()) {
                                case Translator.providerGoogle:
                                    value = LocaleController.getString(R.string.ProviderGoogleTranslate);
                                    break;
                                case Translator.providerGoogleCN:
                                    value = LocaleController.getString(R.string.ProviderGoogleTranslateCN);
                                    break;
                                case Translator.providerGoogle2:
                                    value = LocaleController.getString(R.string.ProviderGoogleTranslate) + " 2";
                                    break;
                                case Translator.providerLingo:
                                    value = LocaleController.getString(R.string.ProviderLingocloud);
                                    break;
                                case Translator.providerMicrosoft:
                                    value = LocaleController.getString(R.string.ProviderMicrosoftTranslator);
                                    break;
                                case Translator.providerVolcengine:
                                    value = LocaleController.getString(R.string.ProviderVolcengineTranslate);
                                    break;
                                case Translator.providerDeepL:
                                    value = LocaleController.getString(R.string.ProviderDeepLxTranslate);
                                    break;
                                case Translator.providerTelegram:
                                    value = LocaleController.getString(R.string.ProviderTelegramAPI);
                                    break;
                                case Translator.providerTranSmart:
                                    value = LocaleController.getString(R.string.ProviderTranSmartTranslate);
                                    break;
                                case Translator.providerLLM:
                                    value = LocaleController.getString(R.string.ProviderLLMTranslate);
                                    break;
                                case Translator.providerDeepLOfficial:
                                    value = LocaleController.getString(R.string.ProviderDeepLTranslate);
                                    break;
                                case Translator.providerDeepLFree:
                                    value = LocaleController.getString(R.string.ProviderDeepLFreeTranslate);
                                    break;
                                default:
                                    value = "Unknown";
                            }
                            textCell.setTextAndValue(LocaleController.getString("TranslationProvider", R.string.TranslationProvider), value, divider);
                            textCell.setCanDisable(true);
                            if (NekoConfig.useTelegramTranslateInChat.Bool()) textCell.setEnabled(false);
                        } else if (position == cellGroup.rows.indexOf(pgpAppRow)) {
                            textCell.setTextAndValue(LocaleController.getString(R.string.OpenPGPApp), NekoXConfig.getOpenPGPAppName(), divider);
                        } else if (position == cellGroup.rows.indexOf(translateToLangRow)) {
                            textCell.setTextAndValue(LocaleController.getString(R.string.TransToLang), NekoXConfig.formatLang(NekoConfig.translateToLang.String()), divider);
                        } else if (position == cellGroup.rows.indexOf(translateInputToLangRow)) {
                            textCell.setTextAndValue(LocaleController.getString(R.string.TransInputToLang), NekoXConfig.formatLang(NekoConfig.translateInputLang.String()), divider);
                        } else if (position == cellGroup.rows.indexOf(llmSettingsRow)) {
                            textCell.setTextAndValue(LocaleController.getString("LLMTranslatorSettings", R.string.LLMTranslatorSettings), "", divider);
                        }
                    }
                } else {
                    // Default binds
                    a.onBindViewHolder(holder);
                }
                // Other things
            }
        }

        @Override
        public View onCreateViewHolderView(int viewType) {
            View view = null;
            if (viewType == ConfigCellCustom.CUSTOM_ITEM_CharBlurAlpha) {
                view = chatBlurAlphaSeekbar = new ChatBlurAlphaSeekBar(mContext);
                chatBlurAlphaSeekbar.setEnabled(NekoConfig.forceBlurInChat.Bool());
            }
            if (viewType == ConfigCellCustom.CUSTOM_ITEM_LiquidGlassAngle) {
                view = new LiquidGlassSeekBar(mContext, true);
            } else if (viewType == ConfigCellCustom.CUSTOM_ITEM_LiquidGlassIntensity) {
                view = new LiquidGlassSeekBar(mContext, false);
            }
            if (view != null) {
                return view;
            }
            return super.onCreateViewHolderView(viewType);
        }
    }

    @Override
    protected void setCanNotChange() {
        super.setCanNotChange();

        if (NekoConfig.useOSMDroidMap.Bool())
            ((ConfigCellTextCheck) mapDriftingFixForGoogleMapsRow).setEnabled(false);

        if (NaConfig.INSTANCE.getCustomTitleUserName().Bool())
            ((ConfigCellTextInput) customTitleRow).setEnabled(false);

        if (NekoConfig.useTelegramTranslateInChat.Bool())
            ((ConfigCellCustom) translationProviderRow).setEnabled(false);

        // Control LLM config rows visibility
        boolean isLLMProvider = NekoConfig.translationProvider.Int() == Translator.providerLLM;
        boolean isDeepLProvider = NekoConfig.translationProvider.Int() == Translator.providerDeepL;
        boolean isDeepLOfficialProvider = NekoConfig.translationProvider.Int() == Translator.providerDeepLOfficial;
        boolean isDeepLFreeProvider = NekoConfig.translationProvider.Int() == Translator.providerDeepLFree;
        boolean isGoogleCloudProvider = NekoConfig.translationProvider.Int() == Translator.providerGoogle;

        cellGroup.rows.remove(llmSettingsRow);
        cellGroup.rows.remove(deepLxCustomApiRow);
        cellGroup.rows.remove(deepLApiKeyRow);
        cellGroup.rows.remove(deepLFreeApiKeyRow);
        cellGroup.rows.remove(deepLFormalityRow);
        cellGroup.rows.remove(googleCloudTranslateKeyRow);

        if (isLLMProvider) {
            int insertIndex = cellGroup.rows.indexOf(translateInputToLangRow) + 1;
            cellGroup.rows.add(insertIndex, llmSettingsRow);
        } else if (isDeepLProvider) {
            int insertIndex = cellGroup.rows.indexOf(translateInputToLangRow) + 1;
            cellGroup.rows.add(insertIndex, deepLxCustomApiRow);
            cellGroup.rows.add(insertIndex + 1, deepLFormalityRow);
        } else if (isDeepLOfficialProvider) {
            int insertIndex = cellGroup.rows.indexOf(translateInputToLangRow) + 1;
            cellGroup.rows.add(insertIndex, deepLApiKeyRow);
            cellGroup.rows.add(insertIndex + 1, deepLFormalityRow);
        } else if (isDeepLFreeProvider) {
            int insertIndex = cellGroup.rows.indexOf(translateInputToLangRow) + 1;
            cellGroup.rows.add(insertIndex, deepLFreeApiKeyRow);
            cellGroup.rows.add(insertIndex + 1, deepLFormalityRow);
        } else if (isGoogleCloudProvider) {
            int insertIndex = cellGroup.rows.indexOf(translateInputToLangRow) + 1;
            cellGroup.rows.add(insertIndex, googleCloudTranslateKeyRow);
        }

        addRowsToMap();
    }

    //Custom dialogs

    private void customDialog_BottomInputString(int position, ConfigItem bind, String subtitle, String hint) {
        BottomBuilder builder = new BottomBuilder(getParentActivity());

        builder.addTitle(
                LocaleController.getString(bind.getKey()),
                subtitle
        );

        EditText keyField = builder.addEditText(hint);

        if (StrUtil.isNotBlank(bind.String())) {
            keyField.setText(bind.String());
        }

        builder.addCancelButton();

        builder.addOkButton((it) -> {

            String key = keyField.getText().toString();

            if (StrUtil.isBlank(key)) key = null;

            bind.setConfigString(key);

            listAdapter.notifyItemChanged(position);

            return Unit.INSTANCE;

        });

        builder.show();

        keyField.requestFocus();
        AndroidUtilities.showKeyboard(keyField);
    }

    private class ChatBlurAlphaSeekBar extends FrameLayout {

        private final SeekBarView sizeBar;
        private final TextPaint textPaint;
        private boolean enabled = true;

        public ChatBlurAlphaSeekBar(Context context) {
            super(context);

            setWillNotDraw(false);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(AndroidUtilities.dp(16));

            sizeBar = new SeekBarView(context);
            sizeBar.setReportChanges(true);
            sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public void onSeekBarDrag(boolean stop, float progress) {
                    NekoConfig.chatBlueAlphaValue.setConfigInt(Math.min(255, (int) (255 * progress)));
                    invalidate();
                }

                @Override
                public void onSeekBarPressed(boolean pressed) {

                }
            });
            sizeBar.setOnTouchListener((v, event) -> !enabled);
            sizeBar.setProgress(NekoConfig.chatBlueAlphaValue.Int());
            addView(sizeBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.LEFT | Gravity.TOP, 9, 5, 43, 11));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText(String.valueOf(NekoConfig.chatBlueAlphaValue.Int()), getMeasuredWidth() - AndroidUtilities.dp(39), AndroidUtilities.dp(28), textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            sizeBar.setProgress((NekoConfig.chatBlueAlphaValue.Int() / 255.0f));
        }

        @Override
        public void invalidate() {
            super.invalidate();
            sizeBar.invalidate();
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            this.enabled = enabled;
            sizeBar.setAlpha(enabled ? 1.0f : 0.5f);
            textPaint.setAlpha((int) ((enabled ? 1.0f : 0.3f) * 255));
            this.invalidate();
        }
    }

    private class LiquidGlassSeekBar extends FrameLayout {
        private final SeekBarView bar;
        private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        private final boolean angle;

        LiquidGlassSeekBar(Context context, boolean angle) {
            super(context);
            this.angle = angle;
            setWillNotDraw(false);
            textPaint.setTextSize(AndroidUtilities.dp(14));
            bar = new SeekBarView(context);
            bar.setReportChanges(true);
            bar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public void onSeekBarDrag(boolean stop, float progress) {
                    if (angle) {
                        NaConfig.INSTANCE.getLiquidGlassAngle().setConfigInt(Math.round(progress * 360f));
                    } else {
                        NaConfig.INSTANCE.getLiquidGlassIntensity().setConfigInt(Math.round(progress * 150f));
                    }
                    invalidate();
                }

                @Override
                public void onSeekBarPressed(boolean pressed) {}
            });
            addView(bar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.LEFT | Gravity.TOP, 12, 23, 52, 5));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.drawText(LocaleController.getString(angle ? R.string.LiquidGlassAngle : R.string.LiquidGlassIntensity), AndroidUtilities.dp(21), AndroidUtilities.dp(20), textPaint);
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            String value = angle ? NaConfig.INSTANCE.getLiquidGlassAngle().Int() + "°" : NaConfig.INSTANCE.getLiquidGlassIntensity().Int() + "%";
            canvas.drawText(value, getMeasuredWidth() - AndroidUtilities.dp(47), AndroidUtilities.dp(47), textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66), MeasureSpec.EXACTLY));
            float value = angle ? NaConfig.INSTANCE.getLiquidGlassAngle().Int() / 360f : NaConfig.INSTANCE.getLiquidGlassIntensity().Int() / 150f;
            bar.setProgress(value);
        }
    }
}

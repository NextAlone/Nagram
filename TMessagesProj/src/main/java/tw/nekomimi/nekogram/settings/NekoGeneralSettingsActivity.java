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
    private final AbstractConfigCell hidePhoneRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hidePhone));
    private final AbstractConfigCell divider0 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerTranslation = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Translate")));
    private final AbstractConfigCell translationProviderRow = cellGroup.appendCell(new ConfigCellCustom("TranslationProvider", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell useTelegramTranslateInChatRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useTelegramTranslateInChat));
    private final AbstractConfigCell translateToLangRow = cellGroup.appendCell(new ConfigCellCustom("TranslateToLang", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell translateInputToLangRow = cellGroup.appendCell(new ConfigCellCustom("TranslateInputToLang", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell googleCloudTranslateKeyRow = cellGroup.appendCell(new ConfigCellTextDetail(NekoConfig.googleCloudTranslateKey, (view, position) -> {
        customDialog_BottomInputString(position, NekoConfig.googleCloudTranslateKey, LocaleController.getString("GoogleCloudTransKeyNotice"), "Key");
    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
    private final AbstractConfigCell deepLxCustomApiRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getDeepLxCustomApi(), "", null));
    private final AbstractConfigCell deepLFormalityRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getDeepLFormality(),
            new String[]{
                    LocaleController.getString(R.string.DeepLFormalityDefault),
                    LocaleController.getString(R.string.DeepLFormalityMore),
                    LocaleController.getString(R.string.DeepLFormalityLess),
            }, null));
    private final AbstractConfigCell llmProviderRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getLlmProvider(),
            new String[]{
                    "OpenAI",
                    "Gemini",
                    "Groq",
                    "DeepSeek",
                    "xAI",
                    "ZhipuAI"
            }, null));
    private final AbstractConfigCell llmApiKeysRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getLlmApiKeys(), "", null));
    private final AbstractConfigCell llmApiUrlRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getLlmApiUrl(), "https://api.openai.com/v1/chat/completions", null));
    private final AbstractConfigCell llmModelRow = cellGroup.appendCell(new ConfigCellCustom("LLMModel", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell llmSystemPromptRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getLlmSystemPrompt(), "You are a professional translation engine. Translate the text to {target_language}, keep the format.", null));
    private final AbstractConfigCell llmTemperatureRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getLlmTemperature(), "0.3", null));
    private final AbstractConfigCell hideOriginAfterTranslationRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getHideOriginAfterTranslation()));
    private final AbstractConfigCell autoTranslateRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getAutoTranslate(), LocaleController.getString("AutoTranslateAbout")));
    private final AbstractConfigCell dividerTranslation = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell headerSummarize = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString(R.string.SummarizeText)));
    private final AbstractConfigCell summarizeTextButtonRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getSummarizeTextButton(),
            new String[]{
                    LocaleController.getString(R.string.Default),
                    LocaleController.getString(R.string.SummarizeTextButtonDisable),
                    LocaleController.getString(R.string.SummarizeTextButtonAlways),
            }, null));
    private final AbstractConfigCell dividerSummarize = cellGroup.appendCell(new ConfigCellDivider());

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
    private final AbstractConfigCell ignoreMutedCountRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.ignoreMutedCount));
    private final AbstractConfigCell ignoreFolderCountRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getIgnoreFolderCount()));
    private final AbstractConfigCell tabsTitleTypeRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.tabsTitleType,
            new String[]{
                    LocaleController.getString("TabTitleTypeText", R.string.TabTitleTypeText),
                    LocaleController.getString("TabTitleTypeIcon", R.string.TabTitleTypeIcon),
                    LocaleController.getString("TabTitleTypeMix", R.string.TabTitleTypeMix)
            }, null));
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
    private final AbstractConfigCell pgpAppRow = cellGroup.appendCell(new ConfigCellCustom("PgpApp", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
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
    private final AbstractConfigCell divider4 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header5 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Appearance")));
    private final AbstractConfigCell typefaceRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.typeface));
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
            LocaleController.getString("TabletModeDefault", R.string.TabletModeDefault),
            LocaleController.getString("Enable", R.string.Enable),
            LocaleController.getString("Disable", R.string.Disable)
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
    private final AbstractConfigCell customDialogsMenuRow = cellGroup.appendCell(new ConfigCellSelectBox(NaConfig.INSTANCE.getCustomDialogsMenu().getKey(), null, null, () -> {
        if (getParentActivity() == null) return;
        showDialog(showConfigMenuAlert(getParentActivity(), NaConfig.INSTANCE.getCustomDialogsMenu().getKey(), new ArrayList<>() {{
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuTheme()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowRecentChatsInSidebar()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuNewGroup()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuNewMessage()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuSavedMessages()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuSettings()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuProxy()));
            add(new ConfigCellTextCheck(NaConfig.INSTANCE.getCustomDialogsMenuAccount()));
        }}));
    }));
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

    private ChatBlurAlphaSeekBar chatBlurAlphaSeekbar;

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
                            LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate),
                            LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN),
                            LocaleController.getString("ProviderYandexTranslate", R.string.ProviderYandexTranslate),
                            LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud),
                            LocaleController.getString("ProviderMicrosoftTranslator", R.string.ProviderMicrosoftTranslator),
                            LocaleController.getString("ProviderMicrosoftTranslator", R.string.ProviderYouDao),
                            LocaleController.getString(R.string.ProviderDeepLxTranslate),
                            LocaleController.getString("ProviderTelegramAPI", R.string.ProviderTelegramAPI),
                            LocaleController.getString("ProviderTranSmartTranslate", R.string.ProviderTranSmartTranslate),
                            LocaleController.getString(R.string.ProviderLLMTranslate),
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
                } else if (position == cellGroup.rows.indexOf(llmModelRow)) {
                    BottomBuilder builder = new BottomBuilder(context);

                    ConfigItem modelConfig;
                    switch (NaConfig.INSTANCE.getLlmProvider().Int()) {
                        case 0: // OpenAI
                            modelConfig = NaConfig.INSTANCE.getLlmOpenAIModel();
                            break;
                        case 1: // Gemini
                            modelConfig = NaConfig.INSTANCE.getLlmGeminiModel();
                            break;
                        case 2: // Groq
                            modelConfig = NaConfig.INSTANCE.getLlmGroqModel();
                            break;
                        case 3: // DeepSeek
                            modelConfig = NaConfig.INSTANCE.getLlmDeepSeekModel();
                            break;
                        case 4: // xAI
                            modelConfig = NaConfig.INSTANCE.getLlmXAIModel();
                            break;
                        case 5: // ZhipuAI
                            modelConfig = NaConfig.INSTANCE.getLlmZhipuAIModel();
                            break;
                        default:
                            modelConfig = NaConfig.INSTANCE.getLlmOpenAIModel();
                    }

                    builder.addTitle(LocaleController.getString("LLMModel", R.string.LLMModel), true);
                    EditText editText = builder.addEditText("Model name");
                    editText.setText(modelConfig.String());
                    builder.addOkButton((it) -> {
                        modelConfig.setConfigString(editText.getText().toString());
                        listAdapter.notifyItemChanged(position);
                        builder.dismiss();
                        return Unit.INSTANCE;
                    });
                    builder.addCancelButton();
                    builder.show();
                } else if (position == cellGroup.rows.indexOf(nameOrderRow)) {
                    LocaleController.getInstance().recreateFormatters();
                }
            }
        });
        listView.setOnItemLongClickListener((view, position, x, y) -> {
            var holder = listView.findViewHolderForAdapterPosition(position);
            if (holder != null && listAdapter.isEnabled(holder)) {
                createLongClickDialog(context, NekoGeneralSettingsActivity.this, "general", position);
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
                // Update API URL and model when provider changes
                String apiUrl;
                switch ((Integer) newValue) {
                    case 0: // OpenAI
                        apiUrl = "https://api.openai.com/v1/chat/completions";
                        break;
                    case 1: // Gemini
                        apiUrl = "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";
                        break;
                    case 2: // Groq
                        apiUrl = "https://api.groq.com/openai/v1/chat/completions";
                        break;
                    case 3: // DeepSeek
                        apiUrl = "https://api.deepseek.com/chat/completions";
                        break;
                    case 4: // xAI
                        apiUrl = "https://api.x.ai/v1/chat/completions";
                        break;
                    case 5: // ZhipuAI
                        apiUrl = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
                        break;
                    default:
                        apiUrl = "https://api.openai.com/v1/chat/completions";
                }
                NaConfig.INSTANCE.getLlmApiUrl().setConfigString(apiUrl);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(llmApiUrlRow));
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(llmModelRow));
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
                                    value = LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate);
                                    break;
                                case Translator.providerGoogleCN:
                                    value = LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN);
                                    break;
                                case Translator.providerYandex:
                                    value = LocaleController.getString("ProviderYandexTranslate", R.string.ProviderYandexTranslate);
                                    break;
                                case Translator.providerLingo:
                                    value = LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud);
                                    break;
                                case Translator.providerMicrosoft:
                                    value = LocaleController.getString("ProviderMicrosoftTranslator", R.string.ProviderMicrosoftTranslator);
                                    break;
                                case Translator.providerYouDao:
                                    value = LocaleController.getString("ProviderYouDao", R.string.ProviderYouDao);
                                    break;
                                case Translator.providerDeepL:
                                    value = LocaleController.getString(R.string.ProviderDeepLxTranslate);
                                    break;
                                case Translator.providerTelegram:
                                    value = LocaleController.getString("ProviderTelegramAPI", R.string.ProviderTelegramAPI);
                                    break;
                                case Translator.providerTranSmart:
                                    value = LocaleController.getString("ProviderTranSmartTranslate", R.string.ProviderTranSmartTranslate);
                                    break;
                                case Translator.providerLLM:
                                    value = LocaleController.getString(R.string.ProviderLLMTranslate);
                                    break;
                                default:
                                    value = "Unknown";
                            }
                            textCell.setTextAndValue(LocaleController.getString("TranslationProvider", R.string.TranslationProvider), value, divider);
                            textCell.setCanDisable(true);
                            if (NekoConfig.useTelegramTranslateInChat.Bool()) textCell.setEnabled(false);
                        } else if (position == cellGroup.rows.indexOf(pgpAppRow)) {
                            textCell.setTextAndValue(LocaleController.getString("OpenPGPApp", R.string.OpenPGPApp), NekoXConfig.getOpenPGPAppName(), divider);
                        } else if (position == cellGroup.rows.indexOf(translateToLangRow)) {
                            textCell.setTextAndValue(LocaleController.getString("TransToLang", R.string.TransToLang), NekoXConfig.formatLang(NekoConfig.translateToLang.String()), divider);
                        } else if (position == cellGroup.rows.indexOf(translateInputToLangRow)) {
                            textCell.setTextAndValue(LocaleController.getString("TransInputToLang", R.string.TransInputToLang), NekoXConfig.formatLang(NekoConfig.translateInputLang.String()), divider);
                        } else if (position == cellGroup.rows.indexOf(llmModelRow)) {
                            String modelValue;
                            switch (NaConfig.INSTANCE.getLlmProvider().Int()) {
                                case 0: // OpenAI
                                    modelValue = NaConfig.INSTANCE.getLlmOpenAIModel().String();
                                    break;
                                case 1: // Gemini
                                    modelValue = NaConfig.INSTANCE.getLlmGeminiModel().String();
                                    break;
                                case 2: // Groq
                                    modelValue = NaConfig.INSTANCE.getLlmGroqModel().String();
                                    break;
                                case 3: // DeepSeek
                                    modelValue = NaConfig.INSTANCE.getLlmDeepSeekModel().String();
                                    break;
                                case 4: // xAI
                                    modelValue = NaConfig.INSTANCE.getLlmXAIModel().String();
                                    break;
                                case 5: // ZhipuAI
                                    modelValue = NaConfig.INSTANCE.getLlmZhipuAIModel().String();
                                    break;
                                default:
                                    modelValue = "gpt-4o-mini";
                            }
                            textCell.setTextAndValue(LocaleController.getString(R.string.LLMModel), modelValue, divider);
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
        boolean isGoogleCloudProvider = NekoConfig.translationProvider.Int() == Translator.providerGoogle;

        cellGroup.rows.remove(llmProviderRow);
        cellGroup.rows.remove(llmApiKeysRow);
        cellGroup.rows.remove(llmApiUrlRow);
        cellGroup.rows.remove(llmModelRow);
        cellGroup.rows.remove(llmSystemPromptRow);
        cellGroup.rows.remove(llmTemperatureRow);
        cellGroup.rows.remove(deepLxCustomApiRow);
        cellGroup.rows.remove(deepLFormalityRow);
        cellGroup.rows.remove(googleCloudTranslateKeyRow);

        if (isLLMProvider) {
            int insertIndex = cellGroup.rows.indexOf(translateInputToLangRow) + 1;
            cellGroup.rows.add(insertIndex, llmProviderRow);
            cellGroup.rows.add(insertIndex + 1, llmApiKeysRow);
            cellGroup.rows.add(insertIndex + 2, llmApiUrlRow);
            cellGroup.rows.add(insertIndex + 3, llmModelRow);
            cellGroup.rows.add(insertIndex + 4, llmSystemPromptRow);
            cellGroup.rows.add(insertIndex + 5, llmTemperatureRow);
        } else if (isDeepLProvider) {
            int insertIndex = cellGroup.rows.indexOf(translateInputToLangRow) + 1;
            cellGroup.rows.add(insertIndex, deepLxCustomApiRow);
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
}

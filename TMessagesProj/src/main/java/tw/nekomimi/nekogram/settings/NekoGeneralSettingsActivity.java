package tw.nekomimi.nekogram.settings;

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
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.util.OpenPgpApi;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
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

@SuppressLint("RtlHardcoded")
public class NekoGeneralSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private ValueAnimator statusBarColorAnimator;
    private DrawerProfilePreviewCell profilePreviewCell;

    private final CellGroup cellGroup = new CellGroup(this);

    private final AbstractConfigCell profilePreviewRow = cellGroup.appendCell(new ConfigCellDrawerProfilePreview());
    private final AbstractConfigCell largeAvatarInDrawerRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.largeAvatarInDrawer, LocaleController.getString("valuesLargeAvatarInDrawer"), null));
    private final AbstractConfigCell avatarBackgroundBlurRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.avatarBackgroundBlur));
    private final AbstractConfigCell avatarBackgroundDarkenRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.avatarBackgroundDarken));
    private final AbstractConfigCell hidePhoneRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hidePhone));
    private final AbstractConfigCell divider0 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header1 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Connection")));
    private final AbstractConfigCell useIPv6Row = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useIPv6));
    private final AbstractConfigCell useProxyItemRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useProxyItem));
    private final AbstractConfigCell hideProxyByDefaultRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideProxyByDefault));
    private final AbstractConfigCell autoUpdateSubInfoRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.autoUpdateSubInfo));
    private final AbstractConfigCell useSystemDNSRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useSystemDNS));
    private final AbstractConfigCell customDoHRow = cellGroup.appendCell(new ConfigCellTextInput(null, NekoConfig.customDoH, "https://1.0.0.1/dns-query", null));
    private final AbstractConfigCell customPublicProxyIPRow = cellGroup.appendCell(new ConfigCellTextDetail(NekoConfig.customPublicProxyIP, (view, position) -> {
        customDialog_BottomInputString(position, NekoConfig.customPublicProxyIP, LocaleController.getString("customPublicProxyIPNotice"), "IP");
    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
    private final AbstractConfigCell divider1 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header2 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Translate")));
    private final AbstractConfigCell translationProviderRow = cellGroup.appendCell(new ConfigCellCustom(CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell translateToLangRow = cellGroup.appendCell(new ConfigCellCustom(CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell translateInputToLangRow = cellGroup.appendCell(new ConfigCellCustom(CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell googleCloudTranslateKeyRow = cellGroup.appendCell(new ConfigCellTextDetail(NekoConfig.googleCloudTranslateKey, (view, position) -> {
        customDialog_BottomInputString(position, NekoConfig.googleCloudTranslateKey, LocaleController.getString("GoogleCloudTransKeyNotice"), "Key");
    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
    private final AbstractConfigCell divider2 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header_notification = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("NekoGeneralNotification")));
    private final AbstractConfigCell disableNotificationBubblesRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableNotificationBubbles));
    private final AbstractConfigCell divider_notification = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header3 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("OpenKayChain")));
    private final AbstractConfigCell pgpAppRow = cellGroup.appendCell(new ConfigCellCustom(CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell keyRow = cellGroup.appendCell(new ConfigCellTextDetail(NekoConfig.openPGPKeyId, (view, position) -> {
        requestKey(new Intent(OpenPgpApi.ACTION_GET_SIGN_KEY_ID));
    }, "0"));
    private final AbstractConfigCell divider3 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header4 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("DialogsSettings")));
    private final AbstractConfigCell sortMenuRow = cellGroup.appendCell(new ConfigCellSelectBox(LocaleController.getString("SortMenu"), null, null, () -> {
        showSortMenuAlert();
    }));
    private final AbstractConfigCell divider4 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header5 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Appearance")));
    private final AbstractConfigCell typefaceRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.typeface));
//    private final AbstractConfigCell useDefaultThemeRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useDefaultTheme));
    private final AbstractConfigCell transparentStatusBarRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.transparentStatusBar));
    private final AbstractConfigCell appBarShadowRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableAppBarShadow));
    private final AbstractConfigCell newYearRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.newYear));
    private final AbstractConfigCell actionBarDecorationRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.actionBarDecoration, new String[]{
            LocaleController.getString("DependsOnDate", R.string.DependsOnDate),
            LocaleController.getString("Snowflakes", R.string.Snowflakes),
            LocaleController.getString("Fireworks", R.string.Fireworks)
    }, null));
    private final AbstractConfigCell tabletModeRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.tabletMode, new String[]{
            LocaleController.getString("TabletModeDefault", R.string.TabletModeDefault),
            LocaleController.getString("Enable", R.string.Enable),
            LocaleController.getString("Disable", R.string.Disable)
    }, null));

    private final AbstractConfigCell forceBlurInChatRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.forceBlurInChat));
    private final AbstractConfigCell header_chatblur = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("ChatBlurAlphaValue")));
    private final AbstractConfigCell chatBlurAlphaValueRow = cellGroup.appendCell(new ConfigCellCustom(ConfigCellCustom.CUSTOM_ITEM_CharBlurAlpha, NekoConfig.forceBlurInChat.Bool()));

    private final AbstractConfigCell divider5 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header6 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("PrivacyTitle")));
    private final AbstractConfigCell disableSystemAccountRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableSystemAccount));
    private final AbstractConfigCell divider6 = cellGroup.appendCell(new ConfigCellDivider());

    private final AbstractConfigCell header7 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("General")));
    private final AbstractConfigCell customSavePathRow = cellGroup.appendCell(new ConfigCellTextInput(null, NekoConfig.customSavePath,
            LocaleController.getString("customSavePathHint", R.string.customSavePathHint), null,
            (input) -> input.matches("^[A-za-z0-9.]{1,255}$") || input.isEmpty() ? input : (String) NekoConfig.customSavePath.defaultValue));
    private final AbstractConfigCell disableUndoRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableUndo));
    private final AbstractConfigCell showIdAndDcRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.showIdAndDc));
    private final AbstractConfigCell inappCameraRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.inappCamera));
    private final AbstractConfigCell disableInstantCameraRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableInstantCamera));
    private final AbstractConfigCell hideProxySponsorChannelRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideProxySponsorChannel));
    private final AbstractConfigCell hideSponsoredMessageRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideSponsoredMessage));
    private final AbstractConfigCell askBeforeCallRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.askBeforeCall));
    private final AbstractConfigCell autoPauseVideoRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.autoPauseVideo, LocaleController.getString("AutoPauseVideoAbout")));
    private final AbstractConfigCell disableNumberRoundingRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableNumberRounding, "4.8K -> 4777"));
    private final AbstractConfigCell openArchiveOnPullRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.openArchiveOnPull));
    private final AbstractConfigCell nameOrderRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NekoConfig.nameOrder, new String[]{
            LocaleController.getString("LastFirst", R.string.LastFirst),
            LocaleController.getString("FirstLast", R.string.FirstLast)
    }, null));
    private final AbstractConfigCell usePersianCalendarRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.usePersianCalendar, LocaleController.getString("UsePersiancalendarInfo")));
    private final AbstractConfigCell displayPersianCalendarByLatinRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.displayPersianCalendarByLatin));
    private final AbstractConfigCell divider7 = cellGroup.appendCell(new ConfigCellDivider());

    private ChatBlurAlphaSeekBar chatBlurAlphaSeekbar;
    private UndoView restartTooltip;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows(true);

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("General", R.string.General));

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

        // Before listAdapter
        setCanNotChange();

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        if (listView.getItemAnimator() != null) {
            ((DefaultItemAnimator) listView.getItemAnimator()).setSupportsChangeAnimations(false);
        }
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
                    PopupBuilder builder = new PopupBuilder(view);

                    builder.setItems(new String[]{
                            LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate),
                            LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN),
                            LocaleController.getString("ProviderYandexTranslate", R.string.ProviderYandexTranslate),
                            LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud),
                            LocaleController.getString("ProviderMicrosoftTranslator", R.string.ProviderMicrosoftTranslator),
                            LocaleController.getString("ProviderMicrosoftTranslator", R.string.ProviderYouDao),
                            LocaleController.getString("ProviderMicrosoftTranslator", R.string.ProviderDeepLTranslate)
                    }, (i, __) -> {
                        boolean needReset = NekoConfig.translationProvider.Int() - 1 != i && (NekoConfig.translationProvider.Int() == 1 || i == 0);
                        NekoConfig.translationProvider.setConfigInt(i + 1);
                        if (needReset) {
                            updateRows(true);
                        } else {
                            listAdapter.notifyItemChanged(position);
                        }
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
                } else if (position == cellGroup.rows.indexOf(nameOrderRow)) {
                    LocaleController.getInstance().recreateFormatters();
                }
            }
        });

        // Cells: Set OnSettingChanged Callbacks
        cellGroup.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NekoConfig.useIPv6.getKey())) {
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        ConnectionsManager.native_setIpStrategy(a, ConnectionsManager.getIpStrategy());
                    }
                }
            } else if (key.equals(NekoConfig.inappCamera.getKey())) {
                SharedConfig.setInappCamera((boolean) newValue);
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.hidePhone.getKey())) {
                parentLayout.rebuildAllFragmentViews(false, false);
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(profilePreviewRow));
            } else if (key.equals(NekoConfig.transparentStatusBar.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.hideProxySponsorChannel.getKey())) {
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        MessagesController.getInstance(a).checkPromoInfo(true);
                    }
                }
            } else if (key.equals(NekoConfig.actionBarDecoration.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.tabletMode.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.newYear.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.usePersianCalendar.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekoConfig.displayPersianCalendarByLatin.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
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
                TransitionManager.beginDelayedTransition(profilePreviewCell);
                setCanNotChange();
                listAdapter.notifyDataSetChanged();
            } else if (key.equals(NekoConfig.avatarBackgroundBlur.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(profilePreviewRow));
            } else if (key.equals(NekoConfig.avatarBackgroundDarken.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(profilePreviewRow));
            } else if (key.equals(NekoConfig.disableAppBarShadow.getKey())) {
                ActionBarLayout.headerShadowDrawable = (boolean) newValue ? null : parentLayout.getResources().getDrawable(R.drawable.header_shadow).mutate();
                parentLayout.rebuildAllFragmentViews(true, true);
            } else if (NekoConfig.forceBlurInChat.getKey().equals(key)) {
                boolean enabled = (Boolean) newValue;
                if (chatBlurAlphaSeekbar != null)
                    chatBlurAlphaSeekbar.setEnabled(enabled);
                ((ConfigCellCustom) chatBlurAlphaValueRow).enabled = enabled;
            }
        };

        //Cells: Set ListAdapter
        cellGroup.setListAdapter(listView, listAdapter);

        restartTooltip = new UndoView(context);
        frameLayout.addView(restartTooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));

        return fragmentView;
    }

    private class ConfigCellDrawerProfilePreview extends AbstractConfigCell {
        public int getType() {
            return ConfigCellCustom.CUSTOM_ITEM_ProfilePreview;
        }

        public boolean isEnabled() {
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
            DrawerProfilePreviewCell cell = (DrawerProfilePreviewCell) holder.itemView;
            cell.setUser(getUserConfig().getCurrentUser(), false);
        }
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


    private void showSortMenuAlert() {
        if (getParentActivity() == null) {
            return;
        }
        Context context = getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("SortMenu", R.string.SortMenu));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        int count = 4;
        for (int a = 0; a < count; a++) {
            TextCheckCell textCell = new TextCheckCell(context);
            switch (a) {
                case 0: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByUnread", R.string.SortByUnread), NekoConfig.sortByUnread.Bool(), false);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByUnmuted", R.string.SortByUnmuted), NekoConfig.sortByUnmuted.Bool(), false);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByUser", R.string.SortByUser), NekoConfig.sortByUser.Bool(), false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByContacts", R.string.SortByContacts), NekoConfig.sortByContacts.Bool(), false);
                    break;
                }
            }
            textCell.setTag(a);
            textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            textCell.setOnClickListener(view -> {
                Integer tag = (Integer) view.getTag();
                switch (tag) {
                    case 0: {
                        NekoConfig.sortByUnread.toggleConfigBool();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekoConfig.sortByUnread.Bool());
                        }
                        break;
                    }
                    case 1: {
                        NekoConfig.sortByUnmuted.toggleConfigBool();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekoConfig.sortByUnmuted.Bool());
                        }
                        break;
                    }
                    case 2: {
                        NekoConfig.sortByUser.toggleConfigBool();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekoConfig.sortByUser.Bool());
                        }
                        break;
                    }
                    case 3: {
                        NekoConfig.sortByContacts.toggleConfigBool();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekoConfig.sortByContacts.Bool());
                        }
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
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateRows(boolean notify) {
        if (notify && listAdapter != null) {
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

    //impl ListAdapter
    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

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
            AbstractConfigCell a = cellGroup.rows.get(position);
            if (a != null) {
                if (a instanceof ConfigCellCustom) {
                    // Custom binds
                    if (holder.itemView instanceof TextSettingsCell) {
                        TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                        if (position == cellGroup.rows.indexOf(translationProviderRow)) {
                            String value;
                            switch (NekoConfig.translationProvider.Int()) {
                                case 1:
                                    value = LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate);
                                    break;
                                case 2:
                                    value = LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN);
                                    break;
                                case 3:
                                    value = LocaleController.getString("ProviderYandexTranslate", R.string.ProviderYandexTranslate);
                                    break;
                                case 4:
                                    value = LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud);
                                    break;
                                case 5:
                                    value = LocaleController.getString("ProviderMicrosoftTranslator", R.string.ProviderMicrosoftTranslator);
                                    break;
                                case 6:
                                    value = LocaleController.getString("ProviderYouDao", R.string.ProviderYouDao);
                                    break;
                                case 7:
                                    value = LocaleController.getString("ProviderDeepLTranslate", R.string.ProviderDeepLTranslate);
                                    break;
                                default:
                                    value = "Unknown";
                            }
                            textCell.setTextAndValue(LocaleController.getString("TranslationProvider", R.string.TranslationProvider), value, true);
                        } else if (position == cellGroup.rows.indexOf(pgpAppRow)) {
                            textCell.setTextAndValue(LocaleController.getString("OpenPGPApp", R.string.OpenPGPApp), NekoXConfig.getOpenPGPAppName(), true);
                        } else if (position == cellGroup.rows.indexOf(translateToLangRow)) {
                            textCell.setTextAndValue(LocaleController.getString("TransToLang", R.string.TransToLang), NekoXConfig.formatLang(NekoConfig.translateToLang.String()), true);
                        } else if (position == cellGroup.rows.indexOf(translateInputToLangRow)) {
                            textCell.setTextAndValue(LocaleController.getString("TransInputToLang", R.string.TransInputToLang), NekoXConfig.formatLang(NekoConfig.translateInputLang.String()), true);
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
                case ConfigCellCustom.CUSTOM_ITEM_ProfilePreview:
                    view = profilePreviewCell = new DrawerProfilePreviewCell(mContext);
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case ConfigCellCustom.CUSTOM_ITEM_CharBlurAlpha:
                    view = chatBlurAlphaSeekbar = new ChatBlurAlphaSeekBar(mContext);
                    chatBlurAlphaSeekbar.setEnabled(NekoConfig.forceBlurInChat.Bool());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }
    }

    private void setCanNotChange() {
        if (!NekoXConfig.isDeveloper())
            cellGroup.rows.remove(hideSponsoredMessageRow);

        boolean enabled;

        enabled = NekoConfig.largeAvatarInDrawer.Int() > 0;
        ((ConfigCellTextCheck) avatarBackgroundBlurRow).enabled = enabled;
        ((ConfigCellTextCheck) avatarBackgroundDarkenRow).enabled = enabled;
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
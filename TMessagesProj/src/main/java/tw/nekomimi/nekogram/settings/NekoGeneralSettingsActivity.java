package tw.nekomimi.nekogram.settings;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ResolveInfo;
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
import org.telegram.ui.Components.UndoView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import kotlin.Unit;
import tw.nekomimi.nekogram.BottomBuilder;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.PopupBuilder;
import tw.nekomimi.nekogram.transtale.Translator;
import tw.nekomimi.nekogram.transtale.TranslatorKt;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.PGPUtil;

import tw.nekomimi.nkmr.ConfigItem;
import tw.nekomimi.nkmr.NekomuraConfig;
import tw.nekomimi.nkmr.CellGroup;
import tw.nekomimi.nkmr.cells.AbstractCell;
import tw.nekomimi.nkmr.cells.*;

@SuppressLint("RtlHardcoded")
public class NekoGeneralSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private ValueAnimator statusBarColorAnimator;
    private DrawerProfilePreviewCell profilePreviewCell;
    private final boolean showCensoredFeatures = NekomuraConfig.showCensoredFeatures(getUserConfig().clientUserId);

    private final CellGroup cellGroup = new CellGroup(this);

    private final AbstractCell profilePreviewRow = cellGroup.appendCell(new NkmrDrawerProfilePreviewCell());
    private final AbstractCell largeAvatarInDrawerRow = cellGroup.appendCell(new NekomuraTGSelectBox(null, NekomuraConfig.largeAvatarInDrawer, LocaleController.getString("valuesLargeAvatarInDrawer"), null));
    private final AbstractCell avatarBackgroundBlurRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.avatarBackgroundBlur));
    private final AbstractCell avatarBackgroundDarkenRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.avatarBackgroundDarken));
    private final AbstractCell hidePhoneRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.hidePhone));
    private final AbstractCell divider0 = cellGroup.appendCell(new NekomuraTGDivider());

    private final AbstractCell header1 = cellGroup.appendCell(new NekomuraTGHeader(LocaleController.getString("Connection")));
    private final AbstractCell useIPv6Row = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.useIPv6));
    private final AbstractCell useProxyItemRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.useProxyItem));
    private final AbstractCell hideProxyByDefaultRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.hideProxyByDefault));
    private final AbstractCell autoUpdateSubInfoRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.autoUpdateSubInfo));
    private final AbstractCell useSystemDNSRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.useSystemDNS));
    private final AbstractCell customDoHRow = cellGroup.appendCell(new NekomuraTGTextInput(null, NekomuraConfig.customDoH, "https://1.0.0.1/dns-query", null));
    private final AbstractCell customPublicProxyIPRow = cellGroup.appendCell(new NekomuraTGTextDetail(NekomuraConfig.customPublicProxyIP, (view, position) -> {
        customDialog_BottomInputString(position, NekomuraConfig.customPublicProxyIP, LocaleController.getString("customPublicProxyIPNotice"), "IP");
    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
    private final AbstractCell divider1 = cellGroup.appendCell(new NekomuraTGDivider());

    private final AbstractCell header2 = cellGroup.appendCell(new NekomuraTGHeader(LocaleController.getString("Translate")));
    private final AbstractCell translationProviderRow = cellGroup.appendCell(new NekomuraTGCustom(CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractCell translateToLangRow = cellGroup.appendCell(new NekomuraTGCustom(CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractCell translateInputToLangRow = cellGroup.appendCell(new NekomuraTGCustom(CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractCell googleCloudTranslateKeyRow = cellGroup.appendCell(new NekomuraTGTextDetail(NekomuraConfig.googleCloudTranslateKey, (view, position) -> {
        customDialog_BottomInputString(position, NekomuraConfig.googleCloudTranslateKey, LocaleController.getString("GoogleCloudTransKeyNotice"), "Key");
    }, LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty)));
    private final AbstractCell divider2 = cellGroup.appendCell(new NekomuraTGDivider());

    private final AbstractCell header3 = cellGroup.appendCell(new NekomuraTGHeader(LocaleController.getString("OpenKayChain")));
    private final AbstractCell pgpAppRow = cellGroup.appendCell(new NekomuraTGCustom(CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractCell keyRow = cellGroup.appendCell(new NekomuraTGTextDetail(NekomuraConfig.openPGPKeyId, (view, position) -> {
        requestKey(new Intent(OpenPgpApi.ACTION_GET_SIGN_KEY_ID));
    }, "0"));
    private final AbstractCell divider3 = cellGroup.appendCell(new NekomuraTGDivider());

    private final AbstractCell header4 = cellGroup.appendCell(new NekomuraTGHeader(LocaleController.getString("DialogsSettings")));
    private final AbstractCell sortMenuRow = cellGroup.appendCell(new NekomuraTGSelectBox(LocaleController.getString("SortMenu"), null, null, () -> {
        showSortMenuAlert();
    }));
    private final AbstractCell divider4 = cellGroup.appendCell(new NekomuraTGDivider());

    private final AbstractCell header5 = cellGroup.appendCell(new NekomuraTGHeader(LocaleController.getString("Appearance")));
    private final AbstractCell typefaceRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.typeface));
    private final AbstractCell useDefaultThemeRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.useDefaultTheme));
    private final AbstractCell transparentStatusBarRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.transparentStatusBar));
    private final AbstractCell appBarShadowRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.disableAppBarShadow));
    private final AbstractCell newYearRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.newYear));
    private final AbstractCell actionBarDecorationRow = cellGroup.appendCell(new NekomuraTGSelectBox(null, NekomuraConfig.actionBarDecoration, new String[]{
            LocaleController.getString("DependsOnDate", R.string.DependsOnDate),
            LocaleController.getString("Snowflakes", R.string.Snowflakes),
            LocaleController.getString("Fireworks", R.string.Fireworks)
    }, null));
    private final AbstractCell tabletModeRow = cellGroup.appendCell(new NekomuraTGSelectBox(null, NekomuraConfig.tabletMode, new String[]{
            LocaleController.getString("TabletModeDefault", R.string.TabletModeDefault),
            LocaleController.getString("Enable", R.string.Enable),
            LocaleController.getString("Disable", R.string.Disable)
    }, null));
    private final AbstractCell divider5 = cellGroup.appendCell(new NekomuraTGDivider());

    private final AbstractCell header6 = cellGroup.appendCell(new NekomuraTGHeader(LocaleController.getString("PrivacyTitle")));
    private final AbstractCell disableSystemAccountRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.disableSystemAccount));
    private final AbstractCell divider6 = cellGroup.appendCell(new NekomuraTGDivider());

    private final AbstractCell header7 = cellGroup.appendCell(new NekomuraTGHeader(LocaleController.getString("General")));
    private final AbstractCell disableUndoRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.disableUndo));
    private final AbstractCell showIdAndDcRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.showIdAndDc));
    private final AbstractCell inappCameraRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.inappCamera));
    private final AbstractCell disableInstantCameraRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.disableInstantCamera));
    private final AbstractCell hideProxySponsorChannelRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.hideProxySponsorChannel));
    private final AbstractCell hideSponsoredMessageRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.hideSponsoredMessage));
    private final AbstractCell askBeforeCallRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.askBeforeCall));
    private final AbstractCell autoPauseVideoRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.autoPauseVideo, LocaleController.getString("AutoPauseVideoAbout")));
    private final AbstractCell disableNumberRoundingRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.disableNumberRounding, "4.8K -> 4777"));
    private final AbstractCell openArchiveOnPullRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.openArchiveOnPull));
    private final AbstractCell nameOrderRow = cellGroup.appendCell(new NekomuraTGSelectBox(null, NekomuraConfig.nameOrder, new String[]{
            LocaleController.getString("LastFirst", R.string.LastFirst),
            LocaleController.getString("FirstLast", R.string.FirstLast)
    }, null));
    private final AbstractCell usePersianCalendarRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.usePersianCalendar, LocaleController.getString("UsePersiancalendarInfo")));
    private final AbstractCell displayPersianCalendarByLatinRow = cellGroup.appendCell(new NekomuraTGTextCheck(NekomuraConfig.displayPersianCalendarByLatin));
    private final AbstractCell divider7 = cellGroup.appendCell(new NekomuraTGDivider());


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
            AbstractCell a = cellGroup.rows.get(position);
            if (a instanceof NekomuraTGTextCheck) {
                ((NekomuraTGTextCheck) a).onClick((TextCheckCell) view);
            } else if (a instanceof NekomuraTGSelectBox) {
                ((NekomuraTGSelectBox) a).onClick(view);
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
            } else if (a instanceof NekomuraTGCustom) { // Custom OnClick
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
                        NekomuraConfig.openPGPApp.setConfigString(appsMap.get(i));
                        NekomuraConfig.openPGPKeyId.setConfigLong(0L);
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
                        boolean needReset = NekomuraConfig.translationProvider.Int() - 1 != i && (NekomuraConfig.translationProvider.Int() == 1 || i == 0);
                        NekomuraConfig.translationProvider.setConfigInt(i + 1);
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
                            NekomuraConfig.translateToLang.setConfigString(TranslatorKt.getLocale2code(locale));
                        } else {
                            NekomuraConfig.translateInputLang.setConfigString(TranslatorKt.getLocale2code(locale));
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
            if (key.equals(NekomuraConfig.useIPv6.getKey())) {
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        ConnectionsManager.native_setIpStrategy(a, ConnectionsManager.getIpStrategy());
                    }
                }
            } else if (key.equals(NekomuraConfig.inappCamera.getKey())) {
                SharedConfig.setInappCamera((boolean) newValue);
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekomuraConfig.hidePhone.getKey())) {
                parentLayout.rebuildAllFragmentViews(false, false);
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(profilePreviewRow));
            } else if (key.equals(NekomuraConfig.transparentStatusBar.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekomuraConfig.hideProxySponsorChannel.getKey())) {
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        MessagesController.getInstance(a).checkPromoInfo(true);
                    }
                }
            } else if (key.equals(NekomuraConfig.actionBarDecoration.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekomuraConfig.tabletMode.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekomuraConfig.newYear.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekomuraConfig.usePersianCalendar.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekomuraConfig.displayPersianCalendarByLatin.getKey())) {
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NekomuraConfig.disableSystemAccount.getKey())) {
                if ((boolean) newValue) {
                    getContactsController().deleteUnknownAppAccounts();
                } else {
                    for (int a : SharedConfig.activeAccounts) {
                        ContactsController.getInstance(a).checkAppAccount();
                    }
                }
            } else if (key.equals(NekomuraConfig.largeAvatarInDrawer.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                TransitionManager.beginDelayedTransition(profilePreviewCell);
                setCanNotChange();
                listAdapter.notifyDataSetChanged();
            } else if (key.equals(NekomuraConfig.avatarBackgroundBlur.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(profilePreviewRow));
            } else if (key.equals(NekomuraConfig.avatarBackgroundDarken.getKey())) {
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(cellGroup.rows.indexOf(profilePreviewRow));
            } else if (key.equals(NekomuraConfig.disableAppBarShadow.getKey())) {
                ActionBarLayout.headerShadowDrawable = (boolean) newValue ? null : parentLayout.getResources().getDrawable(R.drawable.header_shadow).mutate();
                parentLayout.rebuildAllFragmentViews(true, true);
            }
        };

        //Cells: Set ListAdapter
        cellGroup.setListAdapter(listView, listAdapter);

        restartTooltip = new UndoView(context);
        frameLayout.addView(restartTooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));

        return fragmentView;
    }

    private class NkmrDrawerProfilePreviewCell extends AbstractCell {
        public int getType() {
            return 999;
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
                    NekomuraConfig.openPGPKeyId.setConfigLong(keyId);

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
                    textCell.setTextAndCheck(LocaleController.getString("SortByUnread", R.string.SortByUnread), NekomuraConfig.sortByUnread.Bool(), false);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByUnmuted", R.string.SortByUnmuted), NekomuraConfig.sortByUnmuted.Bool(), false);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByUser", R.string.SortByUser), NekomuraConfig.sortByUser.Bool(), false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByContacts", R.string.SortByContacts), NekomuraConfig.sortByContacts.Bool(), false);
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
                        NekomuraConfig.sortByUnread.toggleConfigBool();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekomuraConfig.sortByUnread.Bool());
                        }
                        break;
                    }
                    case 1: {
                        NekomuraConfig.sortByUnmuted.toggleConfigBool();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekomuraConfig.sortByUnmuted.Bool());
                        }
                        break;
                    }
                    case 2: {
                        NekomuraConfig.sortByUser.toggleConfigBool();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekomuraConfig.sortByUser.Bool());
                        }
                        break;
                    }
                    case 3: {
                        NekomuraConfig.sortByContacts.toggleConfigBool();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekomuraConfig.sortByContacts.Bool());
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
            AbstractCell a = cellGroup.rows.get(position);
            if (a != null) {
                return a.isEnabled();
            }
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            AbstractCell a = cellGroup.rows.get(position);
            if (a != null) {
                return a.getType();
            }
            return CellGroup.ITEM_TYPE_TEXT_DETAIL;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            AbstractCell a = cellGroup.rows.get(position);
            if (a != null) {
                if (a instanceof NekomuraTGCustom) {
                    // Custom binds
                    if (holder.itemView instanceof TextSettingsCell) {
                        TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                        if (position == cellGroup.rows.indexOf(translationProviderRow)) {
                            String value;
                            switch (NekomuraConfig.translationProvider.Int()) {
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
                            textCell.setTextAndValue(LocaleController.getString("OpenPGPApp", R.string.OpenPGPApp), NekoConfig.getOpenPGPAppName(), true);
                        } else if (position == cellGroup.rows.indexOf(translateToLangRow)) {
                            textCell.setTextAndValue(LocaleController.getString("TransToLang", R.string.TransToLang), NekoConfig.formatLang(NekomuraConfig.translateToLang.String()), true);
                        } else if (position == cellGroup.rows.indexOf(translateInputToLangRow)) {
                            textCell.setTextAndValue(LocaleController.getString("TransInputToLang", R.string.TransInputToLang), NekoConfig.formatLang(NekomuraConfig.translateInputLang.String()), true);
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
                case 999:
                    view = profilePreviewCell = new DrawerProfilePreviewCell(mContext);
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }
    }

    private void setCanNotChange() {
        if (!showCensoredFeatures)
            cellGroup.rows.remove(hideSponsoredMessageRow);

        boolean enabled;

        enabled = NekomuraConfig.largeAvatarInDrawer.Int() > 0;
        ((NekomuraTGTextCheck) avatarBackgroundBlurRow).enabled = enabled;
        ((NekomuraTGTextCheck) avatarBackgroundDarkenRow).enabled = enabled;
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
}
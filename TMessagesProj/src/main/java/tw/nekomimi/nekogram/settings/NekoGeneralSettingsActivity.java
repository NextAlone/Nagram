package tw.nekomimi.nekogram.settings;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ResolveInfo;
import android.os.Build;
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
import org.telegram.messenger.BuildConfig;
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

@SuppressLint("RtlHardcoded")
public class NekoGeneralSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private ValueAnimator statusBarColorAnimator;
    private DrawerProfilePreviewCell profilePreviewCell;

    private int rowCount;

    private int connectionRow;
    private int ipv6Row;
    private int disableProxyWhenVpnEnabledRow;
    private int useProxyItemRow;
    private int hideProxyByDefaultRow;
    private int connection2Row;

    private int drawerRow;
    private int avatarAsDrawerBackgroundRow;
    private int avatarBackgroundBlurRow;
    private int avatarBackgroundDarkenRow;
    private int drawer2Row;

    private int transRow;
    private int translationProviderRow;
    private int translateToLangRow;
    private int translateInputToLangRow;
    private int googleCloudTranslateKeyRow;
    private int trans2Row;

    private int openKeyChainRow;
    private int pgpAppRow;
    private int keyRow;
    private int openKeyChain2Row;

    private int dialogsRow;
    private int sortMenuRow;
    private int acceptSecretChatRow;
    private int dialogs2Row;

    private int appearanceRow;
    private int typefaceRow;
    private int useDefaultThemeRow;
    private int useSystemEmojiRow;
    private int transparentStatusBarRow;
    private int newYearRow;
    private int actionBarDecorationRow;
    private int appBarShadowRow;
    private int tabletModeRow;
    private int appearance2Row;

    private int privacyRow;
    private int disableSystemAccountRow;
    private int privacy2Row;

    private int generalRow;
    private int hidePhoneRow;
    private int disableUndoRow;
    private int showIdAndDcRow;
    private int inappCameraRow;
    private int hideProxySponsorChannelRow;
    private int askBeforeCallRow;
    private int autoPauseVideoRow;
    private int disableNumberRoundingRow;
    private int openArchiveOnPullRow;
    private int nameOrderRow;
    private int usePersianCalendarRow;
    private int displayPersianCalendarByLatinRow;
    private int general2Row;

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

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        if (listView.getItemAnimator() != null) {
            ((DefaultItemAnimator) listView.getItemAnimator()).setSupportsChangeAnimations(false);
        }
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == ipv6Row) {
                NekoConfig.toggleIPv6();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.useIPv6);
                }
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        ConnectionsManager.native_setIpStrategy(a, ConnectionsManager.getIpStrategy());
                    }
                }
            } else if (position == disableProxyWhenVpnEnabledRow) {
                NekoConfig.toggleDisableProxyWhenVpnEnabled();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.disableProxyWhenVpnEnabled);
                }
            } else if (position == useProxyItemRow) {
                NekoConfig.toggleUseProxyItem();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.useProxyItem);
                }
            } else if (position == hideProxyByDefaultRow) {
                NekoConfig.toggleHideProxyByDefault();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.hideProxyByDefault);
                }
            } else if (position == hidePhoneRow) {
                NekoConfig.toggleHidePhone();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.hidePhone);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(drawerRow);
            } else if (position == disableUndoRow) {
                NekoConfig.toggleDisableUndo();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.disableUndo);
                }
            } else if (position == useDefaultThemeRow) {
                NekoConfig.toggleUseDefaultTheme();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.useDefaultTheme);
                }
                restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (position == inappCameraRow) {
                SharedConfig.toggleInappCamera();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.inappCamera);
                }
            } else if (position == transparentStatusBarRow) {
                NekoConfig.toggleTransparentStatusBar();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.transparentStatusBar);
                }
                AndroidUtilities.runOnUIThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, false));
            } else if (position == hideProxySponsorChannelRow) {
                NekoConfig.toggleHideProxySponsorChannel();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.hideProxySponsorChannel);
                }
                for (int a : SharedConfig.activeAccounts) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        MessagesController.getInstance(a).checkPromoInfo(true);
                    }
                }
            } else if (position == useSystemEmojiRow) {
                NekoConfig.toggleUseSystemEmoji();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.useSystemEmoji);
                }
            } else if (position == typefaceRow) {
                NekoConfig.toggleTypeface();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.typeface == 1);
                }
            } else if (position == nameOrderRow) {

                PopupBuilder builder = new PopupBuilder(view);

                builder.setItems(new String[]{
                        LocaleController.getString("FirstLast", R.string.FirstLast),
                        LocaleController.getString("LastFirst", R.string.LastFirst)
                }, (i, __) -> {
                    NekoConfig.setNameOrder(i + 1);
                    listAdapter.notifyItemChanged(position);
                    restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
                    return Unit.INSTANCE;
                });

                builder.show();

            } else if (position == actionBarDecorationRow) {

                PopupBuilder builder = new PopupBuilder(view);

                builder.setItems(new String[]{
                        LocaleController.getString("DependsOnDate", R.string.DependsOnDate),
                        LocaleController.getString("Snowflakes", R.string.Snowflakes),
                        LocaleController.getString("Fireworks", R.string.Fireworks)
                }, (i, __) -> {

                    NekoConfig.setActionBarDecoration(i);
                    listAdapter.notifyItemChanged(position);
                    restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);

                    return null;

                });

                builder.show();

            } else if (position == tabletModeRow) {

                PopupBuilder builder = new PopupBuilder(view);

                builder.setItems(new String[]{
                        LocaleController.getString("TabletModeDefault", R.string.TabletModeDefault),
                        LocaleController.getString("Enable", R.string.Enable),
                        LocaleController.getString("Disable", R.string.Disable)
                }, (i, __) -> {

                    NekoConfig.setTabletMode(i);
                    listAdapter.notifyItemChanged(position);
                    restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);

                    return null;

                });

                builder.show();

            } else if (position == newYearRow) {
                NekoConfig.toggleNewYear();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.newYear);
                    restartTooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
                }
            } else if (position == translationProviderRow) {

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

                    boolean needReset = NekoConfig.translationProvider - 1 != i && (NekoConfig.translationProvider == 1 || i == 0);

                    NekoConfig.setTranslationProvider(i + 1);

                    if (needReset) {

                        updateRows(true);

                    } else {

                        listAdapter.notifyItemChanged(translationProviderRow);

                    }

                    return Unit.INSTANCE;

                });

                builder.show();

            } else if (position == translateToLangRow || position == translateInputToLangRow) {

                Translator.showTargetLangSelect(view, position == translateInputToLangRow, (locale) -> {

                    if (position == translateToLangRow) {

                        NekoConfig.setTranslateToLang(TranslatorKt.getLocale2code(locale));

                    } else {

                        NekoConfig.setTranslateInputToLang(TranslatorKt.getLocale2code(locale));

                    }

                    listAdapter.notifyItemChanged(position);

                    return Unit.INSTANCE;

                });

            } else if (position == googleCloudTranslateKeyRow) {

                BottomBuilder builder = new BottomBuilder(getParentActivity());

                builder.addTitle(
                        LocaleController.getString("GoogleCloudTransKey", R.string.GoogleCloudTransKey),
                        LocaleController.getString("GoogleCloudTransKeyNotice", R.string.GoogleCloudTransKeyNotice)
                );

                EditText keyField = builder.addEditText("Key");

                if (StrUtil.isNotBlank(NekoConfig.googleCloudTranslateKey)) {

                    keyField.setText(NekoConfig.googleCloudTranslateKey);

                }

                builder.addCancelButton();

                builder.addOkButton((it) -> {

                    String key = keyField.getText().toString();

                    if (StrUtil.isBlank(key)) key = null;

                    NekoConfig.setGoogleTranslateKey(key);

                    listAdapter.notifyItemChanged(position);

                    return Unit.INSTANCE;

                });

                builder.show();

                keyField.requestFocus();
                AndroidUtilities.showKeyboard(keyField);

            } else if (position == openArchiveOnPullRow) {
                NekoConfig.toggleOpenArchiveOnPull();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.openArchiveOnPull);
                }
            } else if (position == sortMenuRow) {
                showSortMenuAlert();
            } else if (position == disableSystemAccountRow) {
                NekoConfig.toggleDisableSystemAccount();
                if (NekoConfig.disableSystemAccount) {
                    getContactsController().deleteUnknownAppAccounts();
                } else {
                    for (int a : SharedConfig.activeAccounts) {
                        ContactsController.getInstance(a).checkAppAccount();
                    }
                }
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.disableSystemAccount);
                }
            } else if (position == avatarAsDrawerBackgroundRow) {
                NekoConfig.toggleAvatarAsDrawerBackground();
                NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.avatarAsDrawerBackground);
                }
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                TransitionManager.beginDelayedTransition(profilePreviewCell);
                listAdapter.notifyItemChanged(drawerRow);
                if (NekoConfig.avatarAsDrawerBackground) {
                    updateRows(false);
                    listAdapter.notifyItemRangeInserted(avatarBackgroundBlurRow, 2);
                } else {
                    listAdapter.notifyItemRangeRemoved(avatarBackgroundBlurRow, 2);
                    updateRows(false);
                }
            } else if (position == avatarBackgroundBlurRow) {
                NekoConfig.toggleAvatarBackgroundBlur();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.avatarBackgroundBlur);
                }
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(drawerRow);
            } else if (position == avatarBackgroundDarkenRow) {
                NekoConfig.toggleAvatarBackgroundDarken();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.avatarBackgroundDarken);
                }
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                listAdapter.notifyItemChanged(drawerRow);
            } else if (position == showIdAndDcRow) {
                NekoConfig.toggleShowIdAndDc();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.showIdAndDc);
                }
            } else if (position == askBeforeCallRow) {
                NekoConfig.toggleAskBeforeCall();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.askBeforeCall);
                }
            } else if (position == disableNumberRoundingRow) {
                NekoConfig.toggleDisableNumberRounding();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.disableNumberRounding);
                }
            } else if (position == appBarShadowRow) {
                NekoConfig.toggleDisableAppBarShadow();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.disableAppBarShadow);
                }
                ActionBarLayout.headerShadowDrawable = NekoConfig.disableAppBarShadow ? null : parentLayout.getResources().getDrawable(R.drawable.header_shadow).mutate();
                parentLayout.rebuildAllFragmentViews(true, true);
            } else if (position == usePersianCalendarRow) {
                NekoConfig.toggleUsePersiancalendar();
                LocaleController.reloadPersianCalendarConfig();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(LocaleController.usePersianCalendar);
                }
            } else if (position == displayPersianCalendarByLatinRow) {
                NekoConfig.toggleDisplayPersianCalendarByLatin();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.displayPersianCalendarByLatin);
                }
            } else if (position == pgpAppRow) {

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

                    NekoConfig.setOpenPGPApp(appsMap.get(i));
                    NekoConfig.setOpenPGPKeyId(0L);
                    listAdapter.notifyItemChanged(pgpAppRow);
                    listAdapter.notifyItemChanged(keyRow);

                    if (i > 0) PGPUtil.recreateConnection();

                });

                builder.show();

            } else if (position == keyRow) {

                requestKey(new Intent(OpenPgpApi.ACTION_GET_SIGN_KEY_ID));

            } else if (position == autoPauseVideoRow) {
                NekoConfig.toggleAutoPauseVideo();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.autoPauseVideo);
                }
            } else if (position == acceptSecretChatRow) {
                NekoConfig.toggleAcceptSecretChat();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.acceptSecretChat);
                }
            }

        });

        restartTooltip = new UndoView(context);
        frameLayout.addView(restartTooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));

        return fragmentView;
    }

    private void requestKey(Intent data) {

        PGPUtil.post(() -> PGPUtil.api.executeApiAsync(data, null, null, result -> {

            switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {

                case OpenPgpApi.RESULT_CODE_SUCCESS: {

                    long keyId = result.getLongExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, 0L);
                    NekoConfig.setOpenPGPKeyId(keyId);

                    listAdapter.notifyItemChanged(keyRow);

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
                    textCell.setTextAndCheck(LocaleController.getString("SortByUnread", R.string.SortByUnread), NekoConfig.sortByUnread, false);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByUnmuted", R.string.SortByUnmuted), NekoConfig.sortByUnmuted, false);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByUser", R.string.SortByUser), NekoConfig.sortByUser, false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheck(LocaleController.getString("SortByContacts", R.string.SortByContacts), NekoConfig.sortByContacts, false);
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
                        NekoConfig.toggleSortByUnread();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekoConfig.sortByUnread);
                        }
                        break;
                    }
                    case 1: {
                        NekoConfig.toggleSortByUnmuted();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekoConfig.sortByUnmuted);
                        }
                        break;
                    }
                    case 2: {
                        NekoConfig.toggleSortByUser();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekoConfig.sortByUser);
                        }
                        break;
                    }
                    case 3: {
                        NekoConfig.toggleSortByContacts();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(NekoConfig.sortByContacts);
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
        rowCount = 0;

        //drawerHeaderRow = rowCount++;
        drawerRow = rowCount++;
        avatarAsDrawerBackgroundRow = rowCount++;
        if (NekoConfig.avatarAsDrawerBackground) {
            avatarBackgroundBlurRow = rowCount++;
            avatarBackgroundDarkenRow = rowCount++;
        } else {
            avatarBackgroundBlurRow = -1;
            avatarBackgroundDarkenRow = -1;
        }
        hidePhoneRow = rowCount++;
        drawer2Row = rowCount++;

        connectionRow = rowCount++;
        ipv6Row = rowCount++;
        disableProxyWhenVpnEnabledRow = rowCount++;
        useProxyItemRow = rowCount++;
        hideProxyByDefaultRow = rowCount++;
        connection2Row = rowCount++;

        transRow = rowCount++;
        translationProviderRow = rowCount++;
        translateToLangRow = rowCount++;
        translateInputToLangRow = rowCount++;
        googleCloudTranslateKeyRow = NekoConfig.translationProvider == 1 ? rowCount++ : -1;
        trans2Row = rowCount++;

        openKeyChainRow = rowCount++;
        pgpAppRow = rowCount++;
//        emailRow = rowCount++;
        keyRow = rowCount++;
        openKeyChain2Row = rowCount++;

        dialogsRow = rowCount++;
        sortMenuRow = rowCount++;
        acceptSecretChatRow = rowCount++;
        dialogs2Row = rowCount++;

        appearanceRow = rowCount++;
        typefaceRow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? rowCount++ : -1;
        useDefaultThemeRow = rowCount++;
        useSystemEmojiRow = !BuildConfig.FLAVOR.contains("No") ? rowCount++ : -1;
        transparentStatusBarRow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? rowCount++ : -1;
        appBarShadowRow = rowCount++;
        newYearRow = rowCount++;
        actionBarDecorationRow = rowCount++;
        tabletModeRow = rowCount++;
        appearance2Row = rowCount++;

        privacyRow = rowCount++;
        disableSystemAccountRow = rowCount++;
        privacy2Row = rowCount++;

        generalRow = rowCount++;
        disableUndoRow = rowCount++;
        showIdAndDcRow = rowCount++;
        inappCameraRow = rowCount++;
        hideProxySponsorChannelRow = rowCount++;
        askBeforeCallRow = rowCount++;
        autoPauseVideoRow = rowCount++;
        disableNumberRoundingRow = rowCount++;
        openArchiveOnPullRow = rowCount++;
        nameOrderRow = rowCount++;
        usePersianCalendarRow = rowCount++;
        displayPersianCalendarByLatinRow = rowCount++;
        general2Row = rowCount++;

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

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                }
                case 2: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position == nameOrderRow) {
                        String value;
                        switch (NekoConfig.nameOrder) {
                            case 2:
                                value = LocaleController.getString("LastFirst", R.string.LastFirst);
                                break;
                            case 1:
                            default:
                                value = LocaleController.getString("FirstLast", R.string.FirstLast);
                                break;
                        }
                        textCell.setTextAndValue(LocaleController.getString("NameOrder", R.string.NameOrder), value, true);
                    } else if (position == actionBarDecorationRow) {
                        String value;
                        switch (NekoConfig.actionBarDecoration) {
                            case 1:
                                value = LocaleController.getString("Snowflakes", R.string.Snowflakes);
                                break;
                            case 2:
                                value = LocaleController.getString("Fireworks", R.string.Fireworks);
                                break;
                            case 0:
                            default:
                                value = LocaleController.getString("DependsOnDate", R.string.DependsOnDate);
                        }
                        textCell.setTextAndValue(LocaleController.getString("ActionBarDecoration", R.string.ActionBarDecoration), value, true);
                    } else if (position == tabletModeRow) {
                        String value;
                        switch (NekoConfig.tabletMode) {
                            case 0:
                                value = LocaleController.getString("TabletModeDefault", R.string.TabletModeDefault);
                                break;
                            case 1:
                                value = LocaleController.getString("Enable", R.string.Enable);
                                break;
                            case 2:
                            default:
                                value = LocaleController.getString("Disable", R.string.Disable);
                        }
                        textCell.setTextAndValue(LocaleController.getString("TabletMode", R.string.TabletMode), value, false);
                    } else if (position == sortMenuRow) {
                        textCell.setText(LocaleController.getString("SortMenu", R.string.SortMenu), true);
                    } else if (position == translateToLangRow) {
                        textCell.setTextAndValue(LocaleController.getString("TransToLang", R.string.TransToLang), NekoConfig.formatLang(NekoConfig.translateToLang), true);
                    } else if (position == translateInputToLangRow) {
                        textCell.setTextAndValue(LocaleController.getString("TransInputToLang", R.string.TransInputToLang), NekoConfig.formatLang(NekoConfig.translateInputLang), true);
                    } else if (position == translationProviderRow) {
                        String value;
                        switch (NekoConfig.translationProvider) {
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
                    } else if (position == pgpAppRow) {
                        textCell.setTextAndValue(LocaleController.getString("OpenPGPApp", R.string.OpenPGPApp), NekoConfig.getOpenPGPAppName(), true);
                    } else if (position == keyRow) {
                        textCell.setTextAndValue(LocaleController.getString("OpenPGPKey", R.string.OpenPGPKey), NekoConfig.openPGPKeyId + "", true);
                    }
                    break;
                }
                case 6: {
                    TextDetailSettingsCell textCell = (TextDetailSettingsCell) holder.itemView;
                    if (position == googleCloudTranslateKeyRow) {
                        textCell.setTextAndValue(LocaleController.getString("GoogleCloudTransKey", R.string.GoogleCloudTransKey), StrUtil.isNotBlank(NekoConfig.googleCloudTranslateKey) ? NekoConfig.googleCloudTranslateKey : LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty), false);
                    }
                }
                break;
                case 3: {
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    textCell.setEnabled(true, null);
                    if (position == ipv6Row) {
                        textCell.setTextAndCheck(LocaleController.getString("IPv6", R.string.IPv6), NekoConfig.useIPv6, true);
                    } else if (position == disableProxyWhenVpnEnabledRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisableProxyWhenVpnEnabled", R.string.DisableProxyWhenVpnEnabled), NekoConfig.disableProxyWhenVpnEnabled, true);
                    } else if (position == useProxyItemRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UseProxyItem", R.string.UseProxyItem), NekoConfig.useProxyItem, true);
                    } else if (position == hideProxyByDefaultRow) {
                        textCell.setTextAndCheck(LocaleController.getString("HideProxyByDefault", R.string.HideProxyByDefault), NekoConfig.hideProxyByDefault, false);
                    } else if (position == hidePhoneRow) {
                        textCell.setTextAndCheck(LocaleController.getString("HidePhone", R.string.HidePhone), NekoConfig.hidePhone, false);
                    } else if (position == disableUndoRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisableUndo", R.string.DisableUndo), NekoConfig.disableUndo, true);
                    } else if (position == useDefaultThemeRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UseDefaultTheme", R.string.UseDefaultTheme), NekoConfig.useDefaultTheme, true);
                    } else if (position == inappCameraRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DebugMenuEnableCamera", R.string.DebugMenuEnableCamera), SharedConfig.inappCamera, true);
                    } else if (position == transparentStatusBarRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TransparentStatusBar", R.string.TransparentStatusBar), NekoConfig.transparentStatusBar, true);
                    } else if (position == hideProxySponsorChannelRow) {
                        textCell.setTextAndCheck(LocaleController.getString("HideProxySponsorChannel", R.string.HideProxySponsorChannel), NekoConfig.hideProxySponsorChannel, true);
                    } else if (position == useSystemEmojiRow) {
                        textCell.setTextAndCheck(LocaleController.getString("EmojiUseDefault", R.string.EmojiUseDefault), NekoConfig.useSystemEmoji, true);
                    } else if (position == typefaceRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TypefaceUseDefault", R.string.TypefaceUseDefault), NekoConfig.typeface == 1, true);
                    } else if (position == newYearRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ChristmasHat", R.string.ChristmasHat), NekoConfig.newYear, true);
                    } else if (position == openArchiveOnPullRow) {
                        textCell.setTextAndCheck(LocaleController.getString("OpenArchiveOnPull", R.string.OpenArchiveOnPull), NekoConfig.openArchiveOnPull, true);
                    } else if (position == disableSystemAccountRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisableSystemAccount", R.string.DisableSystemAccount), NekoConfig.disableSystemAccount, false);
                    } else if (position == avatarAsDrawerBackgroundRow) {
                        textCell.setTextAndCheck(LocaleController.getString("AvatarAsBackground", R.string.AvatarAsBackground), NekoConfig.avatarAsDrawerBackground, true);
                    } else if (position == showIdAndDcRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ShowIdAndDc", R.string.ShowIdAndDc), NekoConfig.showIdAndDc, true);
                    } else if (position == askBeforeCallRow) {
                        textCell.setTextAndCheck(LocaleController.getString("AskBeforeCalling", R.string.AskBeforeCalling), NekoConfig.askBeforeCall, true);
                    } else if (position == disableNumberRoundingRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("DisableNumberRounding", R.string.DisableNumberRounding), "4.8K -> 4777", NekoConfig.disableNumberRounding, true, true);
                    } else if (position == appBarShadowRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisableAppBarShadow", R.string.DisableAppBarShadow), NekoConfig.disableAppBarShadow, true);
                    } else if (position == usePersianCalendarRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("UsePersiancalendar", R.string.UsePersiancalendar), LocaleController.getString("UsePersiancalendarInfo", R.string.UsePersiancalendarInfo), LocaleController.usePersianCalendar, true, true);
                    } else if (position == displayPersianCalendarByLatinRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisplayPersianCalendarByLatin", R.string.DisplayPersianCalendarByLatin), NekoConfig.displayPersianCalendarByLatin, false);
                    } else if (position == autoPauseVideoRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("AutoPauseVideo", R.string.AutoPauseVideo), LocaleController.getString("AutoPauseVideoAbout", R.string.AutoPauseVideoAbout), NekoConfig.autoPauseVideo, true, true);
                    } else if (position == acceptSecretChatRow) {
                        textCell.setTextAndCheck(LocaleController.getString("AcceptSecretChat", R.string.AcceptSecretChat), NekoConfig.acceptSecretChat, false);
                    } else if (position == avatarBackgroundBlurRow) {
                        textCell.setTextAndCheck(LocaleController.getString("BlurAvatarBackground", R.string.BlurAvatarBackground), NekoConfig.avatarBackgroundBlur, true);
                    } else if (position == avatarBackgroundDarkenRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DarkenAvatarBackground", R.string.DarkenAvatarBackground), NekoConfig.avatarBackgroundDarken, true);
                    }
                    break;
                }
                case 4: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == generalRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                    } else if (position == connectionRow) {
                        headerCell.setText(LocaleController.getString("Connection", R.string.Connection));
                    } else if (position == appearanceRow) {
                        headerCell.setText(LocaleController.getString("Appearance", R.string.Appearance));
                    } else if (position == transRow) {
                        headerCell.setText(LocaleController.getString("Translate", R.string.Translate));
                    } else if (position == dialogsRow) {
                        headerCell.setText(LocaleController.getString("DialogsSettings", R.string.DialogsSettings));
                    } else if (position == privacyRow) {
                        headerCell.setText(LocaleController.getString("PrivacyTitle", R.string.PrivacyTitle));
                    } else if (position == openKeyChainRow) {
                        headerCell.setText(LocaleController.getString("OpenKayChain", R.string.OpenKayChain));
                    }
                    break;
                }
                case 8: {
                    DrawerProfilePreviewCell cell = (DrawerProfilePreviewCell) holder.itemView;
                    cell.setUser(getUserConfig().getCurrentUser(), false);
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 2 || type == 3 || type == 6;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new NotificationsCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 7:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 8:
                    view = profilePreviewCell = new DrawerProfilePreviewCell(mContext);
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == connection2Row || position == dialogs2Row || position == trans2Row || position == privacy2Row ||
                    position == general2Row || position == appearance2Row || position == openKeyChain2Row || position == drawer2Row) {
                return 1;
            } else if (position == nameOrderRow || position == sortMenuRow || position == translateToLangRow || position == translateInputToLangRow ||
                    position == translationProviderRow || position == actionBarDecorationRow ||
                    position == pgpAppRow || position == keyRow || position == tabletModeRow) {
                return 2;
            } else if (position == connectionRow || position == transRow || position == dialogsRow ||
                    position == privacyRow || position == generalRow || position == appearanceRow || position == openKeyChainRow) {
                return 4;
            } else if (position == googleCloudTranslateKeyRow) {
                return 6;
            } else if (position == drawerRow) {
                return 8;
            }
            return 3;
        }
    }
}

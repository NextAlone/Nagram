package tw.nekomimi.nekogram;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import cn.hutool.core.util.StrUtil;
import kotlin.Unit;
import tw.nekomimi.nekogram.transtale.Translator;
import tw.nekomimi.nekogram.utils.EnvUtil;
import tw.nekomimi.nekogram.utils.PopupBuilder;

@SuppressLint("RtlHardcoded")
public class NekoSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private AnimatorSet animatorSet;

    private boolean sensitiveCanChange = false;
    private boolean sensitiveEnabled = false;

    private int rowCount;

    private int connectionRow;
    private int ipv6Row;
    private int disableProxyWhenVpnEnabledRow;
    private int useProxyItemRow;
    private int hideProxyByDefaultRow;
    private int connection2Row;

    private int dialogsRow;
    private int sortMenuRow;
    private int dialogs2Row;

    private int chatRow;
    private int inappCameraRow;
    private int ignoreMutedCountRow;
    private int disableChatActionRow;
    private int useSystemEmojiRow;
    private int ignoreBlockedRow;
    private int hideProxySponsorChannelRow;
    private int skipOpenLinkConfirm;
    private int pauseMusicOnRecordRow;
    private int disablePhotoSideActionRow;
    private int hideKeyboardOnChatScrollRow;
    private int rearVideoMessagesRow;
    private int hideAllTabRow;
    private int confirmAVRow;
    private int askBeforeCallRow;
    private int disableNumberRoundingRow;
    private int mapPreviewRow;
    private int stickerSizeRow;
    private int messageMenuRow;
    private int tabsTitleTypeRow;
    private int chat2Row;

    private int transRow;
    private int translationProviderRow;
    private int translateToLangRow;
    private int translateInputToLangRow;
    private int googleCloudTranslateKeyRow;
    private int trans2Row;

    private int settingsRow;
    private int typefaceRow;
    private int cachePathRow;
    private int hidePhoneRow;
    private int disableUndoRow;
    private int useDefaultThemeRow;
    private int showIdAndDcRow;
    private int nameOrderRow;
    private int transparentStatusBarRow;
    private int forceTabletRow;
    private int openArchiveOnPullRow;
    private int avatarAsDrawerBackgroundRow;
    private int showTabsOnForwardRow;
    private int removeTitleEmojiRow;
    private int eventTypeRow;
    private int newYearRow;
    private int actionBarDecorationRow;
    private int needRestartRow;

    private int privacyRow;
    private int disableSystemAccountRow;
    private int privacy2Row;

    private int experimentRow;
    private int smoothKeyboardRow;
    private int chatMessageAnimationRow;
    private int disableFilteringRow;
    private int unlimitedFavedStickersRow;
    private int unlimitedPinnedDialogsRow;
    private int deleteAccountRow;
    private int experiment2Row;


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows();

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("NekoSettings", R.string.NekoSettings));

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
            if (position == ipv6Row) {
                NekoConfig.toggleIPv6();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.useIPv6);
                }
                for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        ConnectionsManager.native_setUseIpv6(a, NekoConfig.useIPv6);
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
            } else if (position == inappCameraRow) {
                SharedConfig.toggleInappCamera();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.inappCamera);
                }
            } else if (position == ignoreMutedCountRow) {
                NekoConfig.toggleIgnoredMutedCount();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.ignoreMutedCount);
                }
            } else if (position == disableChatActionRow) {
                NekoConfig.toggleDisableChatAction();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.disableChatAction);
                }
            } else if (position == forceTabletRow) {
                NekoConfig.toggleForceTablet();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.forceTablet);
                }
            } else if (position == ignoreBlockedRow) {
                NekoConfig.toggleIgnoreBlocked();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.ignoreBlocked);
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
                for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        MessagesController.getInstance(a).checkPromoInfo(true);
                    }
                }
            } else if (position == skipOpenLinkConfirm) {
                NekoConfig.toggleSkipOpenLinkConfirm();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.skipOpenLinkConfirm);
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
            } else if (position == mapPreviewRow) {

                PopupBuilder builder = new PopupBuilder(view);

                builder.setItems(new String[]{
                        LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram),
                        LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex),
                        LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody)
                }, (i, __) -> {
                    NekoConfig.setMapPreviewProvider(i);
                    listAdapter.notifyItemChanged(position);
                    return Unit.INSTANCE;
                });

                builder.show();

            } else if (position == nameOrderRow) {

                PopupBuilder builder = new PopupBuilder(view);

                builder.setItems(new String[]{
                        LocaleController.getString("FirstLast", R.string.FirstLast),
                        LocaleController.getString("LastFirst", R.string.LastFirst)
                }, (i, __) -> {
                    NekoConfig.setNameOrder(i + 1);
                    listAdapter.notifyItemChanged(position);
                    return Unit.INSTANCE;
                });

                builder.show();

            } else if (position == eventTypeRow) {

                PopupBuilder builder = new PopupBuilder(view);

                builder.setItems(new String[]{
                        LocaleController.getString("DependsOnDate", R.string.DependsOnDate),
                        LocaleController.getString("Christmas", R.string.Christmas),
                        LocaleController.getString("Valentine", R.string.Valentine)
                }, (i, __) -> {
                    NekoConfig.setEventType(i);
                    listAdapter.notifyItemChanged(position);
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

                    return null;

                });

                builder.show();

            } else if (position == cachePathRow) {

                BottomBuilder builder = new BottomBuilder(getParentActivity());

                builder.addTitle(LocaleController.getString("CachePath", R.string.CachePath));

                AtomicReference<String> target = new AtomicReference<>();

                builder.addRadioItems(EnvUtil.getAvailableDirectories(),
                        (index, path) -> path.equals(NekoConfig.cachePath), (__, path, cell) -> {

                            target.set(path);
                            builder.doRadioCheck(cell);

                            return null;

                        });

                builder.addCancelButton();
                builder.addOkButton((it) -> {

                    if (target.get() != null) {

                        NekoConfig.setCachePath(target.get());
                        ImageLoader.getInstance().checkMediaPaths();
                        listAdapter.notifyItemChanged(position);

                    }

                    builder.dismiss();

                    return Unit.INSTANCE;

                });

                builder.show();

            } else if (position == newYearRow) {
                NekoConfig.toggleNewYear();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.newYear);
                }
            } else if (position == disableFilteringRow) {
                sensitiveEnabled = !sensitiveEnabled;
                TLRPC.TL_account_setContentSettings req = new TLRPC.TL_account_setContentSettings();
                req.sensitive_enabled = sensitiveEnabled;
                AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
                progressDialog.show();
                getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                    progressDialog.dismiss();
                    if (error == null) {
                        if (response instanceof TLRPC.TL_boolTrue && view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(sensitiveEnabled);
                        }
                    } else {
                        AndroidUtilities.runOnUIThread(() -> AlertsCreator.processError(currentAccount, error, this, req));
                    }
                }));
            } else if (position == stickerSizeRow) {
                showStickerSizeAlert();
            } else if (position == unlimitedFavedStickersRow) {
                NekoConfig.toggleUnlimitedFavedStickers();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.unlimitedFavedStickers);
                }
            } else if (position == messageMenuRow) {
                showMessageMenuAlert();
            } else if (position == deleteAccountRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("TosDeclineDeleteAccount", R.string.TosDeclineDeleteAccount));
                builder.setTitle(LocaleController.getString("DeleteAccount", R.string.DeleteAccount));
                builder.setPositiveButton(LocaleController.getString("Deactivate", R.string.Deactivate), (dialog, which) -> {
                    ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>(getMessagesController().getAllDialogs());
                    for (TLRPC.Dialog TLdialog : dialogs) {
                        if (TLdialog instanceof TLRPC.TL_dialogFolder) {
                            continue;
                        }
                        TLRPC.Peer peer = getMessagesController().getPeer((int) TLdialog.id);
                        if (peer.channel_id != 0) {
                            TLRPC.Chat chat = getMessagesController().getChat(peer.channel_id);
                            if (!chat.broadcast) {
                                MessageHelper.getInstance(currentAccount).deleteUserChannelHistoryWithSearch(TLdialog.id, getMessagesController().getUser(getUserConfig().clientUserId));
                            }
                        }
                        if (peer.user_id != 0) {
                            getMessagesController().deleteDialog(TLdialog.id, 0, true);
                        }
                    }
                    AlertDialog.Builder builder12 = new AlertDialog.Builder(getParentActivity());
                    builder12.setMessage(LocaleController.getString("TosDeclineDeleteAccount", R.string.TosDeclineDeleteAccount));
                    builder12.setTitle(LocaleController.getString("DeleteAccount", R.string.DeleteAccount));
                    builder12.setPositiveButton(LocaleController.getString("Deactivate", R.string.Deactivate), (dialogInterface, i) -> {
                        final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
                        progressDialog.setCanCacnel(false);

                        TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
                        req.reason = "Meow";
                        getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            if (response instanceof TLRPC.TL_boolTrue) {
                                getMessagesController().performLogout(0);
                            } else if (error == null || error.code != -1000) {
                                String errorText = LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred);
                                if (error != null) {
                                    errorText += "\n" + error.text;
                                }
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity());
                                builder1.setTitle(LocaleController.getString("NekoX", R.string.NekoX));
                                builder1.setMessage(errorText);
                                builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                builder1.show();
                            }
                        }));
                        progressDialog.show();
                    });
                    builder12.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    AlertDialog dialog12 = builder12.create();
                    showDialog(dialog12);
                    TextView button = (TextView) dialog12.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder.show();
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            } else if (position == translationProviderRow) {

                PopupBuilder builder = new PopupBuilder(view);

                builder.setItems(new String[]{
                        LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate),
                        LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN),
                        LocaleController.getString("ProviderYandex", R.string.ProviderYandex),
                        LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud),

                        LocaleController.getString("ProviderGoogleTranslateWeb", R.string.ProviderGoogleTranslateWeb),
                        LocaleController.getString("ProviderGoogleTranslateCNWeb", R.string.ProviderGoogleTranslateCNWeb),
                        LocaleController.getString("ProviderBaiduFanyiWeb", R.string.ProviderBaiduFanyiWeb),
                        LocaleController.getString("ProviderDeepLWeb", R.string.ProviderDeepLWeb)

                }, (i, __) -> {

                    int target;

                    if (i < 4) {
                        target = i + 1;
                    } else {
                        target = -i + 3;
                    }

                    NekoConfig.setTranslationProvider(target);
                    listAdapter.notifyItemChanged(translationProviderRow);

                    return Unit.INSTANCE;

                });

                builder.show();

            } else if (position == translateToLangRow || position == translateInputToLangRow) {

                Translator.showTargetLangSelect(view, position == translateToLangRow ? 1 : 2, (locale) -> {

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

                    builder.dismiss();

                    return Unit.INSTANCE;

                });

                builder.show();

                keyField.requestFocus();
                AndroidUtilities.showKeyboard(keyField);

            } else if (position == pauseMusicOnRecordRow) {
                SharedConfig.togglePauseMusicOnRecord();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.pauseMusicOnRecord);
                }
            } else if (position == smoothKeyboardRow) {
                SharedConfig.toggleSmoothKeyboard();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.smoothKeyboard);
                }
                if (SharedConfig.smoothKeyboard && getParentActivity() != null) {
                    getParentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                }
            } else if (position == disablePhotoSideActionRow) {
                NekoConfig.toggleDisablePhotoSideAction();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.disablePhotoSideAction);
                }
            } else if (position == unlimitedPinnedDialogsRow) {
                NekoConfig.toggleUnlimitedPinnedDialogs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.unlimitedPinnedDialogs);
                }
            } else if (position == openArchiveOnPullRow) {
                NekoConfig.toggleOpenArchiveOnPull();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.openArchiveOnPull);
                }
            } else if (position == hideKeyboardOnChatScrollRow) {
                NekoConfig.toggleHideKeyboardOnChatScroll();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.hideKeyboardOnChatScroll);
                }
            } else if (position == sortMenuRow) {
                showSortMenuAlert();
            } else if (position == disableSystemAccountRow) {
                NekoConfig.toggleDisableSystemAccount();
                if (NekoConfig.disableSystemAccount) {
                    getContactsController().deleteUnknownAppAccounts();
                } else {
                    for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
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
            } else if (position == removeTitleEmojiRow) {
                NekoConfig.toggleRemoveTitleEmoji();
                NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.removeTitleEmoji);
                }
            } else if (position == showIdAndDcRow) {
                NekoConfig.toggleShowIdAndDc();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.showIdAndDc);
                }
            } else if (position == showTabsOnForwardRow) {
                NekoConfig.toggleShowTabsOnForward();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.showTabsOnForward);
                }
            } else if (position == chatMessageAnimationRow) {
                NekoConfig.toggleChatMessageAnimation();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.chatMessageAnimation);
                }
            } else if (position == rearVideoMessagesRow) {
                NekoConfig.toggleRearVideoMessages();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.rearVideoMessages);
                }
            } else if (position == hideAllTabRow) {
                NekoConfig.toggleHideAllTab();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.hideAllTab);
                }
                getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);
            } else if (position == tabsTitleTypeRow) {

                PopupBuilder builder = new PopupBuilder(view);

                builder.setItems(new String[]{

                        LocaleController.getString("TabTitleTypeText", R.string.TabTitleTypeText),
                        LocaleController.getString("TabTitleTypeIcon", R.string.TabTitleTypeIcon),
                        LocaleController.getString("TabTitleTypeMix", R.string.TabTitleTypeMix)

                }, (i, __) -> {

                    NekoConfig.setTabsTitleType(i);
                    listAdapter.notifyItemChanged(tabsTitleTypeRow);
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);

                    return Unit.INSTANCE;

                });

                builder.show();

            } else if (position == confirmAVRow) {
                NekoConfig.toggleConfirmAVMessage();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.confirmAVMessage);
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
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            checkSensitive();
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateRows() {
        rowCount = 0;

        connectionRow = rowCount++;

        ipv6Row = rowCount++;
        disableProxyWhenVpnEnabledRow = rowCount++;
        useProxyItemRow = rowCount++;
        hideProxyByDefaultRow = rowCount++;
        connection2Row = rowCount++;

        dialogsRow = rowCount++;
        sortMenuRow = rowCount++;
        dialogs2Row = rowCount++;

        chatRow = rowCount++;
        inappCameraRow = rowCount++;
        ignoreMutedCountRow = rowCount++;
        disableChatActionRow = rowCount++;
        useSystemEmojiRow = rowCount++;
        ignoreBlockedRow = rowCount++;
        hideProxySponsorChannelRow = rowCount++;
        skipOpenLinkConfirm = rowCount++;
        pauseMusicOnRecordRow = rowCount++;
        disablePhotoSideActionRow = rowCount++;
        hideKeyboardOnChatScrollRow = rowCount++;
        rearVideoMessagesRow = rowCount++;
        hideAllTabRow = rowCount++;
        confirmAVRow = rowCount++;
        askBeforeCallRow = rowCount++;
        disableNumberRoundingRow = rowCount++;
        mapPreviewRow = rowCount++;
        stickerSizeRow = rowCount++;
        messageMenuRow = rowCount++;
        tabsTitleTypeRow = rowCount++;
        chat2Row = rowCount++;

        transRow = rowCount++;
        translationProviderRow = rowCount++;
        translateToLangRow = rowCount++;
        translateInputToLangRow = rowCount++;
        googleCloudTranslateKeyRow = rowCount++;
        trans2Row = rowCount++;

        settingsRow = rowCount++;
        hidePhoneRow = rowCount++;
        disableUndoRow = rowCount++;
        useDefaultThemeRow = rowCount++;
        showIdAndDcRow = rowCount++;
        typefaceRow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? rowCount++ : -1;
        cachePathRow = rowCount++;
        transparentStatusBarRow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? rowCount++ : -1;
        forceTabletRow = rowCount++;
        openArchiveOnPullRow = rowCount++;
        avatarAsDrawerBackgroundRow = rowCount++;
        showTabsOnForwardRow = rowCount++;
        removeTitleEmojiRow = rowCount++;
        nameOrderRow = rowCount++;
        eventTypeRow = rowCount++;
        newYearRow = rowCount++;
        actionBarDecorationRow = rowCount++;
        needRestartRow = rowCount++;

        privacyRow = rowCount++;
        disableSystemAccountRow = rowCount++;
        privacy2Row = rowCount++;

        experimentRow = rowCount++;
        smoothKeyboardRow = !AndroidUtilities.isTablet() ? rowCount++ : -1;
        chatMessageAnimationRow = rowCount++;
        disableFilteringRow = rowCount++;
        unlimitedFavedStickersRow = rowCount++;
        unlimitedPinnedDialogsRow = rowCount++;
        deleteAccountRow = rowCount++;
        experiment2Row = rowCount++;
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

    private void checkSensitive() {
        TLRPC.TL_account_getContentSettings req = new TLRPC.TL_account_getContentSettings();
        getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (error == null) {
                TLRPC.TL_account_contentSettings settings = (TLRPC.TL_account_contentSettings) response;
                sensitiveEnabled = settings.sensitive_enabled;
                sensitiveCanChange = settings.sensitive_can_change;
                int count = listView.getChildCount();
                ArrayList<Animator> animators = new ArrayList<>();
                for (int a = 0; a < count; a++) {
                    View child = listView.getChildAt(a);
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.getChildViewHolder(child);
                    int position = holder.getAdapterPosition();
                    if (position == disableFilteringRow) {
                        TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                        checkCell.setChecked(sensitiveEnabled);
                        checkCell.setEnabled(sensitiveCanChange, animators);
                        if (sensitiveCanChange) {
                            if (!animators.isEmpty()) {
                                if (animatorSet != null) {
                                    animatorSet.cancel();
                                }
                                animatorSet = new AnimatorSet();
                                animatorSet.playTogether(animators);
                                animatorSet.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        if (animator.equals(animatorSet)) {
                                            animatorSet = null;
                                        }
                                    }
                                });
                                animatorSet.setDuration(150);
                                animatorSet.start();
                            }
                        }

                    }
                }
            } else {
                AndroidUtilities.runOnUIThread(() -> AlertsCreator.processError(currentAccount, error, this, req));
            }
        }));
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

        int count = NekoXConfig.developerMode ? 9 : 8;
        for (int a = 0; a < count; a++) {
            TextCheckCell textCell = new TextCheckCell(context);
            switch (a) {
                case 0: {
                    textCell.setTextAndCheck(LocaleController.getString("DeleteDownloadedFile", R.string.DeleteDownloadedFile), NekoConfig.showDeleteDownloadedFile, false);
                    break;
                }
                case 1: {
                    textCell.setTextAndCheck(LocaleController.getString("AddToSavedMessages", R.string.AddToSavedMessages), NekoConfig.showAddToSavedMessages, false);
                    break;
                }
                case 2: {
                    textCell.setTextAndCheck(LocaleController.getString("Repeat", R.string.Repeat), NekoConfig.showRepeat, false);
                    break;
                }
                case 3: {
                    textCell.setTextAndCheck(LocaleController.getString("ViewHistory", R.string.ViewHistory), NekoConfig.showViewHistory, false);
                    break;
                }
                case 4: {
                    textCell.setTextAndCheck(LocaleController.getString("Translate", R.string.Translate), NekoConfig.showTranslate, false);
                    break;
                }
                case 5: {
                    textCell.setTextAndCheck(LocaleController.getString("ReportChat", R.string.ReportChat), NekoConfig.showReport, false);
                    break;
                }
                case 6: {
                    textCell.setTextAndCheck(LocaleController.getString("EditAdminRights", R.string.EditAdminRights), NekoConfig.showAdminActions, false);
                    break;
                }
                case 7: {
                    textCell.setTextAndCheck(LocaleController.getString("ChangePermissions", R.string.ChangePermissions), NekoConfig.showChangePermissions, false);
                    break;
                }
                case 8: {
                    textCell.setTextAndCheck(LocaleController.getString("MessageDetails", R.string.MessageDetails), NekoConfig.showMessageDetails, false);
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
                        NekoConfig.toggleShowDeleteDownloadedFile();
                        textCell.setChecked(NekoConfig.showDeleteDownloadedFile);
                        break;
                    }
                    case 1: {
                        NekoConfig.toggleShowAddToSavedMessages();
                        textCell.setChecked(NekoConfig.showAddToSavedMessages);
                        break;
                    }
                    case 2: {
                        NekoConfig.toggleShowRepeat();
                        textCell.setChecked(NekoConfig.showRepeat);
                        break;
                    }
                    case 3: {
                        NekoConfig.toggleShowViewHistory();
                        textCell.setChecked(NekoConfig.showViewHistory);
                        break;
                    }
                    case 4: {
                        NekoConfig.toggleShowTranslate();
                        textCell.setChecked(NekoConfig.showTranslate);
                        break;
                    }
                    case 5: {
                        NekoConfig.toggleShowReport();
                        textCell.setChecked(NekoConfig.showReport);
                        break;
                    }
                    case 6: {
                        NekoConfig.toggleShowAdminActions();
                        textCell.setChecked(NekoConfig.showAdminActions);
                        break;
                    }
                    case 7: {
                        NekoConfig.toggleShowChangePermissions();
                        textCell.setChecked(NekoConfig.showChangePermissions);
                        break;
                    }
                    case 8: {
                        NekoConfig.toggleShowMessageDetails();
                        textCell.setChecked(NekoConfig.showMessageDetails);
                        break;
                    }
                }
            });
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setView(linearLayout);
        showDialog(builder.create());
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

    private void showStickerSizeAlert() {
        if (getParentActivity() == null) {
            return;
        }
        Context context = getParentActivity();
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        FrameLayout titleLayout = new FrameLayout(context);
        linearLayout.addView(titleLayout);

        HeaderCell headerCell = new HeaderCell(context, Theme.key_windowBackgroundWhiteBlueHeader, 23, 15, false);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("StickerSize", R.string.StickerSize));
        titleLayout.addView(headerCell);

        ActionBarMenuItem optionsButton = new ActionBarMenuItem(context, null, 0, Theme.getColor(Theme.key_sheet_other));
        optionsButton.setLongClickEnabled(false);
        optionsButton.setSubMenuOpenSide(2);
        optionsButton.setIcon(R.drawable.ic_ab_other);
        optionsButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_player_actionBarSelector), 1));
        optionsButton.addSubItem(1, R.drawable.msg_reset, LocaleController.getString("Reset", R.string.Reset));
        optionsButton.setOnClickListener(v -> optionsButton.toggleSubMenu());
        optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        titleLayout.addView(optionsButton, LayoutHelper.createFrame(40, 40, Gravity.TOP | Gravity.RIGHT, 0, 8, 5, 0));

        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        StickerSizeCell stickerSizeCell = new StickerSizeCell(context);
        optionsButton.setDelegate(id -> {
            if (id == 1) {
                NekoConfig.setStickerSize(14.0f);
                stickerSizeCell.invalidate();
            }
        });
        linearLayoutInviteContainer.addView(stickerSizeCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
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
                    NekoConfig.setStickerSize(startStickerSize + (endStickerSize - startStickerSize) * progress);
                    StickerSizeCell.this.invalidate();
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
            canvas.drawText("" + Math.round(NekoConfig.stickerSize), getMeasuredWidth() - AndroidUtilities.dp(39), AndroidUtilities.dp(28), textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            sizeBar.setProgress((NekoConfig.stickerSize - startStickerSize) / (float) (endStickerSize - startStickerSize));
        }

        @Override
        public void invalidate() {
            super.invalidate();
            listAdapter.notifyItemChanged(stickerSizeRow);
            messagesCell.invalidate();
            sizeBar.invalidate();
        }
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
                    if (position == experiment2Row) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
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
                        textCell.setTextAndValue(LocaleController.getString("NameOrder", R.string.NameOrder), value, eventTypeRow != -1);
                    } else if (position == mapPreviewRow) {
                        String value;
                        switch (NekoConfig.mapPreviewProvider) {
                            case 0:
                                value = LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram);
                                break;
                            case 1:
                                value = LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex);
                                break;
                            case 2:
                            default:
                                value = LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody);
                        }
                        textCell.setTextAndValue(LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), value, true);
                    } else if (position == eventTypeRow) {
                        String value;
                        switch (NekoConfig.eventType) {
                            case 1:
                                value = LocaleController.getString("Christmas", R.string.Christmas);
                                break;
                            case 2:
                                value = LocaleController.getString("Valentine", R.string.Valentine);
                                break;
                            case 0:
                            default:
                                value = LocaleController.getString("DependsOnDate", R.string.DependsOnDate);
                        }
                        textCell.setTextAndValue(LocaleController.getString("EventType", R.string.EventType), value, true);
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
                        textCell.setTextAndValue(LocaleController.getString("ActionBarDecoration", R.string.ActionBarDecoration), value, false);
                    } else if (position == stickerSizeRow) {
                        textCell.setTextAndValue(LocaleController.getString("StickerSize", R.string.StickerSize), String.valueOf(Math.round(NekoConfig.stickerSize)), true);
                    } else if (position == messageMenuRow) {
                        textCell.setText(LocaleController.getString("MessageMenu", R.string.MessageMenu), true);
                    } else if (position == sortMenuRow) {
                        textCell.setText(LocaleController.getString("SortMenu", R.string.SortMenu), true);
                    } else if (position == translateToLangRow) {
                        textCell.setTextAndValue(LocaleController.getString("TransToLang", R.string.TransToLang), NekoConfig.formatLang(NekoConfig.translateToLang), true);
                    } else if (position == translateInputToLangRow) {
                        textCell.setTextAndValue(LocaleController.getString("TransInputToLang", R.string.TransInputToLang), NekoConfig.formatLang(NekoConfig.translateInputLang), true);
                    } else if (position == deleteAccountRow) {
                        textCell.setText(LocaleController.getString("DeleteAccount", R.string.DeleteAccount), false);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                    } else if (position == translationProviderRow) {
                        String value;
                        switch (NekoConfig.translationProvider) {
                            case 1:
                                value = LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate);
                                break;
                            case -1:
                                value = LocaleController.getString("ProviderGoogleTranslateWeb", R.string.ProviderGoogleTranslateWeb);
                                break;
                            case 2:
                                value = LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN);
                                break;
                            case -2:
                                value = LocaleController.getString("ProviderGoogleTranslateCNWeb", R.string.ProviderGoogleTranslateCNWeb);
                                break;
                            case -3:
                                value = LocaleController.getString("ProviderBaiduFanyiWeb", R.string.ProviderBaiduFanyiWeb);
                                break;
                            case -4:
                                value = LocaleController.getString("ProviderDeepLWeb", R.string.ProviderDeepLWeb);
                                break;
                            case 3:
                                value = LocaleController.getString("ProviderYandex", R.string.ProviderYandex);
                                break;
                            case 4:
                                value = LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud);
                                break;
                            default:
                                value = "Unknown";
                        }
                        textCell.setTextAndValue(LocaleController.getString("TranslationProvider", R.string.TranslationProvider), value, false);
                    } else if (position == tabsTitleTypeRow) {
                        String value;
                        switch (NekoConfig.tabsTitleType) {
                            case NekoConfig.TITLE_TYPE_TEXT:
                                value = LocaleController.getString("TabTitleTypeText", R.string.TabTitleTypeText);
                                break;
                            case NekoConfig.TITLE_TYPE_ICON:
                                value = LocaleController.getString("TabTitleTypeIcon", R.string.TabTitleTypeIcon);
                                break;
                            case NekoConfig.TITLE_TYPE_MIX:
                            default:
                                value = LocaleController.getString("TabTitleTypeMix", R.string.TabTitleTypeMix);
                        }
                        textCell.setTextAndValue(LocaleController.getString("TabTitleType", R.string.TabTitleType), value, false);
                    }
                    break;
                }
                case 6: {
                    TextDetailSettingsCell textCell = (TextDetailSettingsCell) holder.itemView;
                    if (position == googleCloudTranslateKeyRow) {
                        textCell.setTextAndValue(LocaleController.getString("GoogleCloudTransKey", R.string.GoogleCloudTransKey), NekoConfig.googleCloudTranslateKey, true);
                    } else if (position == cachePathRow) {
                        textCell.setTextAndValue(LocaleController.getString("CachePath", R.string.CachePath), NekoConfig.cachePath, true);
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
                        textCell.setTextAndCheck(LocaleController.getString("HidePhone", R.string.HidePhone), NekoConfig.hidePhone, true);
                    } else if (position == disableUndoRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisableUndo", R.string.DisableUndo), NekoConfig.disableUndo, true);
                    } else if (position == useDefaultThemeRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UseDefaultTheme", R.string.UseDefaultTheme), NekoConfig.useDefaultTheme, true);
                    } else if (position == inappCameraRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DebugMenuEnableCamera", R.string.DebugMenuEnableCamera), SharedConfig.inappCamera, true);
                    } else if (position == ignoreMutedCountRow) {
                        textCell.setTextAndCheck(LocaleController.getString("IgnoreMutedCount", R.string.IgnoreMutedCount), NekoConfig.ignoreMutedCount, true);
                    } else if (position == transparentStatusBarRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TransparentStatusBar", R.string.TransparentStatusBar), NekoConfig.transparentStatusBar, true);
                    } else if (position == hideProxySponsorChannelRow) {
                        textCell.setTextAndCheck(LocaleController.getString("HideProxySponsorChannel", R.string.HideProxySponsorChannel), NekoConfig.hideProxySponsorChannel, true);
                    } else if (position == skipOpenLinkConfirm) {
                        textCell.setTextAndCheck(LocaleController.getString("SkipOpenLinkConfirm", R.string.SkipOpenLinkConfirm), NekoConfig.skipOpenLinkConfirm, true);
                    } else if (position == disableChatActionRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisableChatAction", R.string.DisableChatAction), NekoConfig.disableChatAction, true);
                    } else if (position == useSystemEmojiRow) {
                        textCell.setTextAndCheck(LocaleController.getString("EmojiUseDefault", R.string.EmojiUseDefault), NekoConfig.useSystemEmoji, true);
                    } else if (position == typefaceRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TypefaceUseDefault", R.string.TypefaceUseDefault), NekoConfig.typeface == 1, true);
                    } else if (position == ignoreBlockedRow) {
                        textCell.setTextAndCheck(LocaleController.getString("IgnoreBlocked", R.string.IgnoreBlocked), NekoConfig.ignoreBlocked, true);
                    } else if (position == forceTabletRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ForceTabletMode", R.string.ForceTabletMode), NekoConfig.forceTablet, true);
                    } else if (position == newYearRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ChristmasHat", R.string.ChristmasHat), NekoConfig.newYear, true);
                    } else if (position == disableFilteringRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("SensitiveDisableFiltering", R.string.SensitiveDisableFiltering), LocaleController.getString("SensitiveAbout", R.string.SensitiveAbout), sensitiveEnabled, true, true);
                        textCell.setEnabled(sensitiveCanChange, null);
                    } else if (position == unlimitedFavedStickersRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("UnlimitedFavoredStickers", R.string.UnlimitedFavoredStickers), LocaleController.getString("UnlimitedFavoredStickersAbout", R.string.UnlimitedFavoredStickersAbout), NekoConfig.unlimitedFavedStickers, true, true);
                    } else if (position == pauseMusicOnRecordRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DebugMenuEnablePauseMusic", R.string.DebugMenuEnablePauseMusic), SharedConfig.pauseMusicOnRecord, true);
                    } else if (position == smoothKeyboardRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DebugMenuEnableSmoothKeyboard", R.string.DebugMenuEnableSmoothKeyboard), SharedConfig.smoothKeyboard, true);
                    } else if (position == disablePhotoSideActionRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisablePhotoViewerSideAction", R.string.DisablePhotoViewerSideAction), NekoConfig.disablePhotoSideAction, true);
                    } else if (position == unlimitedPinnedDialogsRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("UnlimitedPinnedDialogs", R.string.UnlimitedPinnedDialogs), LocaleController.getString("UnlimitedPinnedDialogsAbout", R.string.UnlimitedPinnedDialogsAbout), NekoConfig.unlimitedPinnedDialogs, true, deleteAccountRow != -1);
                    } else if (position == openArchiveOnPullRow) {
                        textCell.setTextAndCheck(LocaleController.getString("OpenArchiveOnPull", R.string.OpenArchiveOnPull), NekoConfig.openArchiveOnPull, true);
                    } else if (position == hideKeyboardOnChatScrollRow) {
                        textCell.setTextAndCheck(LocaleController.getString("HideKeyboardOnChatScroll", R.string.HideKeyboardOnChatScroll), NekoConfig.hideKeyboardOnChatScroll, true);
                    } else if (position == disableSystemAccountRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisableSystemAccount", R.string.DisableSystemAccount), NekoConfig.disableSystemAccount, true);
                    } else if (position == avatarAsDrawerBackgroundRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UseAvatarAsDrawerBackground", R.string.UseAvatarAsDrawerBackground), NekoConfig.avatarAsDrawerBackground, true);
                    } else if (position == removeTitleEmojiRow) {
                        textCell.setTextAndCheck(LocaleController.getString("RemoveTitleEmoji", R.string.RemoveTitleEmoji), NekoConfig.removeTitleEmoji, true);
                    } else if (position == showIdAndDcRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ShowIdAndDc", R.string.ShowIdAndDc), NekoConfig.showIdAndDc, true);
                    } else if (position == showTabsOnForwardRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ShowTabsOnForward", R.string.ShowTabsOnForward), NekoConfig.showTabsOnForward, true);
                    } else if (position == chatMessageAnimationRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ChatMessageAnimation", R.string.ChatMessageAnimation), NekoConfig.chatMessageAnimation, true);
                    } else if (position == rearVideoMessagesRow) {
                        textCell.setTextAndCheck(LocaleController.getString("RearVideoMessages", R.string.RearVideoMessages), NekoConfig.rearVideoMessages, true);
                    } else if (position == hideAllTabRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("HideAllTab", R.string.HideAllTab), LocaleController.getString("HideAllTabAbout", R.string.HideAllTabAbout), NekoConfig.hideAllTab, true, true);
                    } else if (position == confirmAVRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ConfirmAVMessage", R.string.ConfirmAVMessage), NekoConfig.confirmAVMessage, true);
                    } else if (position == askBeforeCallRow) {
                        textCell.setTextAndCheck(LocaleController.getString("AskBeforeCalling", R.string.AskBeforeCalling), NekoConfig.askBeforeCall, true);
                    } else if (position == disableNumberRoundingRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("DisableNumberRounding", R.string.DisableNumberRounding), "4.8K -> 4777", NekoConfig.disableNumberRounding, true, true);
                    }
                    break;
                }
                case 4: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == settingsRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                    } else if (position == connectionRow) {
                        headerCell.setText(LocaleController.getString("Connection", R.string.Connection));
                    } else if (position == chatRow) {
                        headerCell.setText(LocaleController.getString("Chat", R.string.Chat));
                    } else if (position == transRow) {
                        headerCell.setText(LocaleController.getString("Translate", R.string.Translate));
                    } else if (position == experimentRow) {
                        headerCell.setText(LocaleController.getString("Experiment", R.string.Experiment));
                    } else if (position == dialogsRow) {
                        headerCell.setText(LocaleController.getString("DialogsSettings", R.string.DialogsSettings));
                    } else if (position == privacyRow) {
                        headerCell.setText(LocaleController.getString("PrivacyTitle", R.string.PrivacyTitle));
                    }
                    break;
                }
                case 7: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == needRestartRow) {
                        cell.setText(LocaleController.getString("SomeItemsNeedRestart", R.string.SomeItemsNeedRestart));
                    }
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
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == connection2Row || position == dialogs2Row || position == chat2Row || position == trans2Row || position == experiment2Row || position == privacy2Row) {
                return 1;
            } else if (position == nameOrderRow || position == mapPreviewRow || position == stickerSizeRow || position == messageMenuRow ||
                    position == sortMenuRow || position == translateToLangRow || position == translateInputToLangRow ||
                    position == deleteAccountRow || position == translationProviderRow || position == eventTypeRow || position == actionBarDecorationRow ||
                    position == tabsTitleTypeRow) {
                return 2;
            } else if (position == ipv6Row || position == disableProxyWhenVpnEnabledRow ||
                    position == useProxyItemRow || position == hideProxyByDefaultRow ||
                    position == hidePhoneRow || position == disableUndoRow || position == inappCameraRow || position == disableChatActionRow ||
                    position == transparentStatusBarRow || position == hideProxySponsorChannelRow ||
                    position == ignoreBlockedRow || position == useSystemEmojiRow || position == typefaceRow ||
                    position == forceTabletRow || position == newYearRow ||
                    position == unlimitedFavedStickersRow || position == skipOpenLinkConfirm ||
                    position == disableFilteringRow || position == smoothKeyboardRow || position == pauseMusicOnRecordRow ||
                    position == disablePhotoSideActionRow || position == unlimitedPinnedDialogsRow || position == openArchiveOnPullRow ||
                    position == hideKeyboardOnChatScrollRow || position == disableSystemAccountRow || position == avatarAsDrawerBackgroundRow ||
                    position == removeTitleEmojiRow || position == ignoreMutedCountRow ||
                    position == useDefaultThemeRow || position == showIdAndDcRow || position == showTabsOnForwardRow ||
                    position == chatMessageAnimationRow || position == rearVideoMessagesRow || position == hideAllTabRow ||
                    position == confirmAVRow || position == askBeforeCallRow || position == disableNumberRoundingRow) {
                return 3;
            } else if (position == settingsRow || position == connectionRow || position == chatRow || position == transRow || position == experimentRow || position == dialogsRow || position == privacyRow) {
                return 4;
            } else if (position == needRestartRow) {
                return 7;
            }
            return 6;
        }
    }
}

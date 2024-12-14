package tw.nekomimi.nekogram.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import kotlin.Unit;

import tw.nekomimi.nekogram.ui.PopupBuilder;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.ZipUtil;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.*;
import xyz.nextalone.nagram.NaConfig;
import xyz.nextalone.nagram.helper.ExternalStickerCacheHelper;

@SuppressLint("RtlHardcoded")
public class NekoExperimentalSettingsActivity extends BaseNekoXSettingsActivity {

    private ListAdapter listAdapter;
    private AnimatorSet animatorSet;

    private boolean sensitiveCanChange = false;
    private boolean sensitiveEnabled = false;

    private final CellGroup cellGroup = new CellGroup(this);

    private final AbstractConfigCell header1 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Experiment")));
    private final AbstractConfigCell localPremiumRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.localPremium));
//    private final AbstractConfigCell useSystemEmojiRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useSystemEmoji));
//    private final AbstractConfigCell useCustomEmojiRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useCustomEmoji));
    private final AbstractConfigCell localQuoteColorRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getUseLocalQuoteColor()));
    private final AbstractConfigCell channelAliasRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.channelAlias));
    private final AbstractConfigCell customCustomChannelLabelRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getCustomChannelLabel(),
            null, null,
            (input) -> input.isEmpty() ? (String) NaConfig.INSTANCE.getCustomChannelLabel().defaultValue : input));

//    private final AbstractConfigCell smoothKeyboardRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.smoothKeyboard));
    private final AbstractConfigCell enhancedFileLoaderRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.enhancedFileLoader));
    private final AbstractConfigCell mediaPreviewRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.mediaPreview));
//    private final AbstractConfigCell proxyAutoSwitchRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.proxyAutoSwitch));
    private final AbstractConfigCell disableFilteringRow = cellGroup.appendCell(new ConfigCellCustom("DisableFiltering", CellGroup.ITEM_TYPE_TEXT_CHECK, true));
    //    private final NekomuraTGCell ignoreContentRestrictionsRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekoConfig.ignoreContentRestrictions, LocaleController.getString("IgnoreContentRestrictionsNotice")));
    private final AbstractConfigCell unlimitedFavedStickersRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.unlimitedFavedStickers, LocaleController.getString("UnlimitedFavoredStickersAbout")));
    private final AbstractConfigCell unlimitedPinnedDialogsRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.unlimitedPinnedDialogs, LocaleController.getString("UnlimitedPinnedDialogsAbout")));
    private final AbstractConfigCell enableStickerPinRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.enableStickerPin, LocaleController.getString("EnableStickerPinAbout")));
    private final AbstractConfigCell useMediaStreamInVoipRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useMediaStreamInVoip));
    private final AbstractConfigCell customAudioBitrateRow = cellGroup.appendCell(new ConfigCellCustom("CustomAudioBitrate", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell divider0 = cellGroup.appendCell(new ConfigCellDivider());
    
    private final AbstractConfigCell header2 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("N_Config")));
    private final AbstractConfigCell forceCopyRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getForceCopy()));
    private final AbstractConfigCell disableFlagSecureRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableFlagSecure()));
    private final AbstractConfigCell audioEnhanceRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getNoiseSuppressAndVoiceEnhance()));
    private final AbstractConfigCell showRPCErrorRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowRPCError()));
    private final AbstractConfigCell customArtworkApiRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getCustomArtworkApi(), "", null));
    private final AbstractConfigCell fakeHighPerformanceDeviceRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getFakeHighPerformanceDevice()));
    private final AbstractConfigCell disableEmojiDrawLimitRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableEmojiDrawLimit()));
    private final AbstractConfigCell sendMp4DocumentAsVideoRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSendMp4DocumentAsVideo()));
    private final AbstractConfigCell hideProxySponsorChannelRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideProxySponsorChannel));
    private final AbstractConfigCell hideSponsoredMessageRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.hideSponsoredMessage));
    private final AbstractConfigCell ignoreBlockedRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.ignoreBlocked, LocaleController.getString("IgnoreBlockedAbout")));
    private final AbstractConfigCell regexFiltersEnabledRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getRegexFiltersEnabled()));
    private final AbstractConfigCell regexFiltersEnableInChatsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getRegexFiltersEnableInChats()));
    private final AbstractConfigCell disableChatActionRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableChatAction));
    private final AbstractConfigCell disableChoosingStickerRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.disableChoosingSticker));
    private final AbstractConfigCell openUrlOutBotWebViewRegexRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getOpenUrlOutBotWebViewRegex(),
            null, null,
            (input) -> {
                try {
                    Pattern.compile(input, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
                    return input;
                } catch (Exception ignored) {
                    return "";
                }
            }));
    private final AbstractConfigCell divider1 = cellGroup.appendCell(new ConfigCellDivider());

    // Story
    private final AbstractConfigCell headerStory = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Story")));
    private final AbstractConfigCell disableStoriesRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableStories()));
    private final AbstractConfigCell disableSendReadStoriesRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableSendReadStories()));
    private final AbstractConfigCell dividerStory = cellGroup.appendCell(new ConfigCellDivider());

    // Sticker Cache
    private final AbstractConfigCell header3 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString(R.string.ExternalStickerCache)));
    private final AbstractConfigCell externalStickerCacheRow = cellGroup.appendCell(new ConfigCellAutoTextCheck(
            NaConfig.INSTANCE.getExternalStickerCache(), LocaleController.getString(R.string.ExternalStickerCacheHint), this::onExternalStickerCacheButtonClick));
    private final AbstractConfigCell externalStickerCacheAutoSyncRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getExternalStickerCacheAutoRefresh(), LocaleController.getString(R.string.ExternalStickerCacheAutoRefreshHint)));
    private final AbstractConfigCell externalStickerCacheDirNameTypeRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getExternalStickerCacheDirNameType(), new String[]{ "Short name", "ID" }, null));
    private final AbstractConfigCell externalStickerCacheSyncAllRow = cellGroup.appendCell(new ConfigCellText("ExternalStickerCacheRefreshAll", ExternalStickerCacheHelper::syncAllCaches));
    private final AbstractConfigCell externalStickerCacheDeleteAllRow = cellGroup.appendCell(new ConfigCellText("ExternalStickerCacheDeleteAll", ExternalStickerCacheHelper::deleteAllCaches));
    private final AbstractConfigCell divider2 = cellGroup.appendCell(new ConfigCellDivider());

    // Pangu
    private final AbstractConfigCell header4 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString(R.string.Pangu)));
    private final AbstractConfigCell enablePanguOnSendingRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getEnablePanguOnSending(), LocaleController.getString(R.string.PanguInfo)));
    private final AbstractConfigCell enablePanguOnReceivingRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getEnablePanguOnReceiving()));
    private final AbstractConfigCell localeToDBCRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.localeToDBC));
    private final AbstractConfigCell divider3 = cellGroup.appendCell(new ConfigCellDivider());

    private UndoView tooltip;

    private static final int INTENT_PICK_CUSTOM_EMOJI_PACK = 114;
    private static final int INTENT_PICK_EXTERNAL_STICKER_DIRECTORY = 514;

    public NekoExperimentalSettingsActivity() {
        if (!NaConfig.INSTANCE.getShowHiddenFeature().Bool()) {
            cellGroup.rows.remove(localPremiumRow);
            cellGroup.rows.remove(localQuoteColorRow);
            cellGroup.rows.remove(enhancedFileLoaderRow);
            cellGroup.rows.remove(disableFilteringRow);
            cellGroup.rows.remove(unlimitedFavedStickersRow);
            cellGroup.rows.remove(unlimitedPinnedDialogsRow);
            cellGroup.rows.remove(enableStickerPinRow);

            cellGroup.rows.remove(forceCopyRow);
            cellGroup.rows.remove(disableFlagSecureRow);
            cellGroup.rows.remove(hideSponsoredMessageRow);
            cellGroup.rows.remove(ignoreBlockedRow);
            cellGroup.rows.remove(regexFiltersEnabledRow);
            cellGroup.rows.remove(regexFiltersEnableInChatsRow);
            cellGroup.rows.remove(disableChatActionRow);
            cellGroup.rows.remove(disableChoosingStickerRow);

            cellGroup.rows.remove(headerStory);
            cellGroup.rows.remove(disableStoriesRow);
            cellGroup.rows.remove(disableSendReadStoriesRow);
            cellGroup.rows.remove(dividerStory);

            NekoConfig.localPremium.setConfigBool(false);
            NaConfig.INSTANCE.getForceCopy().setConfigBool(false);
            NaConfig.INSTANCE.getDisableFlagSecure().setConfigBool(false);
            NekoConfig.hideSponsoredMessage.setConfigBool(false);
            NekoConfig.ignoreBlocked.setConfigBool(false);
            NaConfig.INSTANCE.getRegexFiltersEnabled().setConfigBool(false);
            NekoConfig.disableChatAction.setConfigBool(false);
            NekoConfig.disableChoosingSticker.setConfigBool(false);
            NaConfig.INSTANCE.getDisableSendReadStories().setConfigBool(false);
        }

        addRowsToMap(cellGroup);
    }

    private void setExternalStickerCacheCellsEnabled(boolean enabled) {
        ((ConfigCellText) externalStickerCacheSyncAllRow).setEnabled(enabled);
        ((ConfigCellText) externalStickerCacheDeleteAllRow).setEnabled(enabled);
    }

    private void refreshExternalStickerStorageState() {
        ConfigCellAutoTextCheck cell = (ConfigCellAutoTextCheck) externalStickerCacheRow;
        setExternalStickerCacheCellsEnabled(!cell.getBindConfig().String().isEmpty());
        Context context = ApplicationLoader.applicationContext;
        ExternalStickerCacheHelper.checkUri(cell, context);
    }

    private void onExternalStickerCacheButtonClick(boolean isChecked) {
        if (isChecked) {
            // clear config
            setExternalStickerCacheCellsEnabled(false);
            ConfigCellAutoTextCheck cell = (ConfigCellAutoTextCheck) externalStickerCacheRow;
            cell.setSubtitle(null);
            NaConfig.INSTANCE.getExternalStickerCache().setConfigString("");
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, INTENT_PICK_EXTERNAL_STICKER_DIRECTORY);
        }
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
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(getTitle());

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

        refreshExternalStickerStorageState(); // Cell (externalStickerCacheRow): Refresh state

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
                if (position == cellGroup.rows.indexOf(regexFiltersEnabledRow) && (LocaleController.isRTL && x > AndroidUtilities.dp(76) || !LocaleController.isRTL && x < (view.getMeasuredWidth() - AndroidUtilities.dp(76)))) {
                    presentFragment(new RegexFiltersSettingActivity());
                    return;
                }
                ((ConfigCellTextCheck) a).onClick((TextCheckCell) view);
            } else if (a instanceof ConfigCellSelectBox) {
                ((ConfigCellSelectBox) a).onClick(view);
            } else if (a instanceof WithOnClick) {
                ((WithOnClick) a).onClick();
            } else if (a instanceof ConfigCellTextInput) {
                ((ConfigCellTextInput) a).onClick();
            } else if (a instanceof ConfigCellAutoTextCheck) {
                ((ConfigCellAutoTextCheck) a).onClick();
            } else if (a instanceof ConfigCellTextDetail) {
                RecyclerListView.OnItemClickListener o = ((ConfigCellTextDetail) a).onItemClickListener;
                if (o != null) {
                    try {
                        o.onItemClick(view, position);
                    } catch (Exception e) {
                    }
                }
            } else if (a instanceof ConfigCellCustom) { // Custom onclick
                if (position == cellGroup.rows.indexOf(disableFilteringRow)) {
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
                } else if (position == cellGroup.rows.indexOf(customAudioBitrateRow)) {
                    PopupBuilder builder = new PopupBuilder(view);
                    builder.setItems(new String[]{
                            "32 (" + LocaleController.getString("Default", R.string.Default) + ")",
                            "64",
                            "128",
                            "192",
                            "256",
                            "320"
                    }, (i, __) -> {
                        switch (i) {
                            case 0:
                                NekoConfig.customAudioBitrate.setConfigInt(32);
                                break;
                            case 1:
                                NekoConfig.customAudioBitrate.setConfigInt(64);
                                break;
                            case 2:
                                NekoConfig.customAudioBitrate.setConfigInt(128);
                                break;
                            case 3:
                                NekoConfig.customAudioBitrate.setConfigInt(192);
                                break;
                            case 4:
                                NekoConfig.customAudioBitrate.setConfigInt(256);
                                break;
                            case 5:
                                NekoConfig.customAudioBitrate.setConfigInt(320);
                                break;
                        }
                        listAdapter.notifyItemChanged(position);
                        return Unit.INSTANCE;
                    });
                    builder.show();
                }
            }
        });
        listView.setOnItemLongClickListener((view, position, x, y) -> {
            var holder = listView.findViewHolderForAdapterPosition(position);
            if (holder != null && listAdapter.isEnabled(holder)) {
                createLongClickDialog(context, NekoExperimentalSettingsActivity.this, "experimental", position);
                return true;
            }
            return false;
        });

        // Cells: Set OnSettingChanged Callbacks
        cellGroup.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NekoConfig.mediaPreview.getKey())) {
                if ((boolean) newValue) {
                    tooltip.setInfoText(AndroidUtilities.replaceTags(LocaleController.formatString("BetaWarning", R.string.BetaWarning)));
                    tooltip.showWithAction(0, UndoView.ACTION_CACHE_WAS_CLEARED, null, null);
                }
            } else if (key.equals(NekoConfig.enableStickerPin.getKey())) {
                if ((boolean) newValue) {
                    tooltip.setInfoText(AndroidUtilities.replaceTags(LocaleController.formatString("EnableStickerPinTip", R.string.EnableStickerPinTip)));
                    tooltip.showWithAction(0, UndoView.ACTION_CACHE_WAS_CLEARED, null, null);
                }
            } else if (key.equals(NekoConfig.useCustomEmoji.getKey())) {
                // Check
                if (!(boolean) newValue) {
                    tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
                    return;
                }
                NekoConfig.useCustomEmoji.setConfigBool(false);

                // Open picker
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/zip");
                Activity act = getParentActivity();
                act.startActivityFromChild(act, intent, INTENT_PICK_CUSTOM_EMOJI_PACK);
            } else if (key.equals(NekoConfig.localeToDBC.getKey())) {
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
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_PICK_CUSTOM_EMOJI_PACK && resultCode == Activity.RESULT_OK) {
            try {
                // copy emoji zip
                Uri uri = data.getData();
                String zipPath = MediaController.copyFileToCache(uri, "file");

                if (zipPath == null || zipPath.isEmpty()) {
                    throw new Exception("zip copy failed");
                }

                //dirs
                File dir = new File(ApplicationLoader.applicationContext.getFilesDir(), "custom_emoji");
                if (dir.exists()) {
                    FileUtil.deleteDirectory(dir);
                }
                dir.mkdir();

                //process zip
                File zipFile = new File(zipPath);
                ZipUtil.unzip(new FileInputStream(zipFile), dir);
                zipFile.delete();
                if (!new File(ApplicationLoader.applicationContext.getFilesDir(), "custom_emoji/emoji/0_0.png").exists()) {
                    throw new Exception(LocaleController.getString("useCustomEmojiInvalid"));
                }

                NekoConfig.useCustomEmoji.setConfigBool(true);
            } catch (Exception e) {
                FileLog.e(e);
                NekoConfig.useCustomEmoji.setConfigBool(false);
                Toast.makeText(ApplicationLoader.applicationContext, "Failed: " + e.toString(), Toast.LENGTH_LONG).show();
            }
            tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
//            listAdapter.notifyItemChanged(cellGroup.rows.indexOf(useCustomEmojiRow));
        } else if (requestCode == INTENT_PICK_EXTERNAL_STICKER_DIRECTORY && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            // reserve permissions
            int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            ApplicationLoader.applicationContext.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            // save config
            NaConfig.INSTANCE.setExternalStickerCacheUri(uri);
            refreshExternalStickerStorageState();
            tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            checkSensitive();
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
    public int getBaseGuid() {
        return 11000;
    }

    @Override
    public int getDrawable() {
        return R.drawable.msg_fave;
    }

    @Override
    public String getTitle() {
        return LocaleController.getString("Experiment", R.string.Experiment);
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
                    if (position == cellGroup.rows.indexOf(disableFilteringRow)) {
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
                    if (holder.itemView instanceof TextCheckCell) {
                        TextCheckCell textCell = (TextCheckCell) holder.itemView;
                        textCell.setEnabled(true, null);
                        if (position == cellGroup.rows.indexOf(disableFilteringRow)) {
                            textCell.setTextAndValueAndCheck(LocaleController.getString("SensitiveDisableFiltering", R.string.SensitiveDisableFiltering), LocaleController.getString("SensitiveAbout", R.string.SensitiveAbout), sensitiveEnabled, true, true);
                            textCell.setEnabled(sensitiveCanChange, null);
                        }
                    } else if (holder.itemView instanceof TextSettingsCell) {
                        TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        if (position == cellGroup.rows.indexOf(customAudioBitrateRow)) {
                            String value = String.valueOf(NekoConfig.customAudioBitrate.Int()) + "kbps";
                            if (NekoConfig.customAudioBitrate.Int() == 32)
                                value += " (" + LocaleController.getString("Default", R.string.Default) + ")";
                            textCell.setTextAndValue(LocaleController.getString("customGroupVoipAudioBitrate", R.string.customGroupVoipAudioBitrate), value, false);
                        }
                    }
                } else {
                    // Default binds
                    a.onBindViewHolder(holder);
//                    if (position == cellGroup.rows.indexOf(smoothKeyboardRow) && AndroidUtilities.isTablet()) {
//                        holder.itemView.setVisibility(View.GONE);
//                    }
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
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }
    }
}
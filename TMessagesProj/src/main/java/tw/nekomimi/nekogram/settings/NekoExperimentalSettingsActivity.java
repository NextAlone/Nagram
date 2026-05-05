package tw.nekomimi.nekogram.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
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
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import kotlin.Unit;

import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.ui.PopupBuilder;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.ZipUtil;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.*;
import tw.nekomimi.nekogram.helpers.remote.InlineBotRulesHelper;
import xyz.nextalone.nagram.NaConfig;
import xyz.nextalone.nagram.helper.ExternalStickerCacheHelper;

@SuppressLint("RtlHardcoded")
public class NekoExperimentalSettingsActivity extends BaseNekoXSettingsActivity {

    private AnimatorSet animatorSet;

    private boolean sensitiveCanChange = false;
    private boolean sensitiveEnabled = false;

    private final CellGroup a = cellGroup = new CellGroup(this);

    private final AbstractConfigCell header1 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Experiment")));
    private final AbstractConfigCell localPremiumRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.localPremium));
//    private final AbstractConfigCell useSystemEmojiRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useSystemEmoji));
//    private final AbstractConfigCell useCustomEmojiRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useCustomEmoji));
    private final AbstractConfigCell localQuoteColorRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getUseLocalQuoteColor()));
    private final AbstractConfigCell channelAliasRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.channelAlias));
    private final AbstractConfigCell externalGhostModeRow = cellGroup.appendCell(new ConfigCellText("GhostMode", () -> {
        presentFragment(new NekoGhostModeActivity());
    }));
    private final AbstractConfigCell customCustomChannelLabelRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getCustomChannelLabel(),
            null, null,
            (input) -> input.isEmpty() ? (String) NaConfig.INSTANCE.getCustomChannelLabel().defaultValue : input));

//    private final AbstractConfigCell smoothKeyboardRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.smoothKeyboard));
    private final AbstractConfigCell enhancedFileLoaderRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.enhancedFileLoader));
//    private final AbstractConfigCell proxyAutoSwitchRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.proxyAutoSwitch));
    private final AbstractConfigCell disableFilteringRow = cellGroup.appendCell(new ConfigCellCustom("DisableFiltering", CellGroup.ITEM_TYPE_TEXT_CHECK, true));
    //    private final NekomuraTGCell ignoreContentRestrictionsRow = addNekomuraTGCell(nkmrCells.new NekomuraTGTextCheck(NekoConfig.ignoreContentRestrictions, LocaleController.getString("IgnoreContentRestrictionsNotice")));
    private final AbstractConfigCell unlimitedFavedStickersRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.unlimitedFavedStickers, LocaleController.getString("UnlimitedFavoredStickersAbout")));
    private final AbstractConfigCell unlimitedPinnedDialogsRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.unlimitedPinnedDialogs, LocaleController.getString("UnlimitedPinnedDialogsAbout")));
    private final AbstractConfigCell enableStickerPinRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.enableStickerPin, LocaleController.getString("EnableStickerPinAbout")));
    private final AbstractConfigCell useMediaStreamInVoipRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.useMediaStreamInVoip));
    private final AbstractConfigCell navigationAnimationSpringRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getNavigationAnimationSpring()));
    private final AbstractConfigCell forceEdgeToEdgeRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getForceEdgeToEdge(), LocaleController.getString(R.string.ForceEdgeToEdgeDesc)));
    private final AbstractConfigCell customAudioBitrateRow = cellGroup.appendCell(new ConfigCellCustom("CustomAudioBitrate", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell divider0 = cellGroup.appendCell(new ConfigCellDivider());
    
    private final AbstractConfigCell header2 = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("N_Config")));
    private final AbstractConfigCell disableSecondAddressRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableSecondAddress()));
    private final AbstractConfigCell forceCopyRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getForceCopy()));
    private final AbstractConfigCell disableFlagSecureRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableFlagSecure()));
    private final AbstractConfigCell audioEnhanceRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getNoiseSuppressAndVoiceEnhance()));
    private final AbstractConfigCell showRPCErrorRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getShowRPCError()));
    private final AbstractConfigCell customArtworkApiRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getCustomArtworkApi(), "", null));
    private final AbstractConfigCell fakeHighPerformanceDeviceRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getFakeHighPerformanceDevice()));
    private final AbstractConfigCell disableEmojiDrawLimitRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getDisableEmojiDrawLimit()));
    private final AbstractConfigCell sendMp4DocumentAsVideoRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getSendMp4DocumentAsVideo()));
    private final AbstractConfigCell defaultHlsVideoQualityRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getPlayerDecoder(),
            new String[]{
                    LocaleController.getString(R.string.PlayerDecoderSoftware),
                    LocaleController.getString(R.string.PlayerDecoderHardware),
                    LocaleController.getString(R.string.PlayerDecoderPerferHW),
            }, null));
    private final AbstractConfigCell enhancedVideoBitrateRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getEnhancedVideoBitrate()));
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
    private final AbstractConfigCell useSystemAiServiceRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getUseSystemAiService(), LocaleController.getString(R.string.UseSystemAiServiceDesc)));
    private final AbstractConfigCell fixUrlPagePreviewRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getFixUrlPagePreview()));
    private final AbstractConfigCell fixUrlAutoInlineBotRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getFixUrlAutoInlineBot()));
    private final AbstractConfigCell fixUrlAutoInlineBotSkipMediaPreviewRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getFixUrlAutoInlineBotSkipMediaPreview(), LocaleController.getString(R.string.FixUrlAutoInlineBotSkipMediaPreviewDesc)));
    private final AbstractConfigCell fixUrlAutoInlineBotRulesRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getFixUrlAutoInlineBotRules(), LocaleController.getString(R.string.FixUrlAutoInlineBotRulesHint), this::showFixUrlAutoInlineBotRulesDialog));
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
    private final AbstractConfigCell enablePanguOnEditingRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getEnablePanguOnEditing()));
    private final AbstractConfigCell enablePanguOnReceivingRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getEnablePanguOnReceiving()));
    private final AbstractConfigCell localeToDBCRow = cellGroup.appendCell(new ConfigCellTextCheck(NekoConfig.localeToDBC));
    private final AbstractConfigCell divider3 = cellGroup.appendCell(new ConfigCellDivider());

    private static final int INTENT_PICK_CUSTOM_EMOJI_PACK = 114;
    private static final int INTENT_PICK_EXTERNAL_STICKER_DIRECTORY = 514;

    public NekoExperimentalSettingsActivity() {
        updateRows();
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

    private void showFixUrlAutoInlineBotRulesDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.FixUrlAutoInlineBotRules));

        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        TextView hintTextView = new TextView(context);
        hintTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        hintTextView.setTextSize(14);
        hintTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        rootLayout.addView(hintTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 0, 24, 8));

        String rules = NaConfig.INSTANCE.getFixUrlAutoInlineBotRules().String();
        ArrayList<InlineBotRulesHelper.InlineBotRule> parsedRules = InlineBotRulesHelper.parseInlineBotRules(rules, false);
        boolean[] advancedMode = new boolean[]{NaConfig.INSTANCE.getFixUrlAutoInlineBotRulesAdvancedMode().Bool() || shouldUseAdvancedFixUrlAutoInlineBotRulesMode(parsedRules)};

        TextCheckCell advancedModeCell = new TextCheckCell(context, 24, true);
        advancedModeCell.setTextAndCheck(
                LocaleController.getString(R.string.FixUrlAutoInlineBotAdvancedMode),
                advancedMode[0],
                false
        );
        advancedModeCell.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(10), Theme.getColor(Theme.key_dialogBackgroundGray)));
        rootLayout.addView(advancedModeCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 0, 24, 8));

        android.widget.ScrollView scrollView = new android.widget.ScrollView(context);
        scrollView.setFillViewport(false);
        LinearLayout rowsContainer = new LinearLayout(context);
        rowsContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(rowsContainer, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP));
        rootLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 300, 0, 0, 0, 8));

        TextView addButton = new TextView(context);
        addButton.setText(LocaleController.getString(R.string.Add));
        addButton.setTextSize(15);
        addButton.setGravity(Gravity.CENTER);
        addButton.setTypeface(AndroidUtilities.bold());
        addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        addButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                AndroidUtilities.dp(8),
                Theme.multAlpha(Theme.getColor(Theme.key_featuredStickers_addButton), 0.12f),
                Theme.multAlpha(Theme.getColor(Theme.key_featuredStickers_addButton), 0.22f)
        ));
        rootLayout.addView(addButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 44, 24, 0, 24, 0));

        ArrayList<FixUrlAutoInlineBotRuleRow> ruleRows = new ArrayList<>();
        for (InlineBotRulesHelper.InlineBotRule rule : parsedRules) {
            addFixUrlAutoInlineBotRuleRow(context, rowsContainer, ruleRows, rule.rule, rule.host, rule.username, advancedMode[0]);
        }
        if (ruleRows.isEmpty()) {
            addFixUrlAutoInlineBotRuleRow(context, rowsContainer, ruleRows, "", "", "", advancedMode[0]);
        }
        updateFixUrlAutoInlineBotRulesDialogMode(context, hintTextView, ruleRows, advancedMode[0], false);

        advancedModeCell.setOnClickListener(v -> {
            boolean newAdvancedMode = !advancedMode[0];
            if (!updateFixUrlAutoInlineBotRulesDialogMode(context, hintTextView, ruleRows, newAdvancedMode, true)) {
                advancedModeCell.setChecked(advancedMode[0]);
                return;
            }
            advancedMode[0] = newAdvancedMode;
            advancedModeCell.setChecked(advancedMode[0]);
        });
        addButton.setOnClickListener(v -> {
            addFixUrlAutoInlineBotRuleRow(context, rowsContainer, ruleRows, "", "", "", advancedMode[0]);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setView(rootLayout);
        AlertDialog dialog = builder.create();
        showDialog(dialog);
        View positiveButton = dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setOnClickListener(v -> {
                ArrayList<InlineBotRulesHelper.InlineBotRule> newRules = new ArrayList<>();
                for (int i = 0; i < ruleRows.size(); i++) {
                    FixUrlAutoInlineBotRuleRow row = ruleRows.get(i);
                    String ruleInput = row.ruleEditText.getText().toString().trim();
                    String username = row.usernameEditText.getText().toString().trim();
                    if (ruleInput.isEmpty() || username.isEmpty()) {
                        continue;
                    }
                    username = InlineBotRulesHelper.normalizeInlineBotUsername(username);
                    if (advancedMode[0]) {
                        try {
                            Pattern.compile(ruleInput, Pattern.CASE_INSENSITIVE);
                        } catch (PatternSyntaxException e) {
                            row.ruleEditText.setError(e.getDescription());
                            row.ruleEditText.requestFocus();
                            Toast.makeText(context, LocaleController.formatString("FixUrlAutoInlineBotRuleInvalidRegex", R.string.FixUrlAutoInlineBotRuleInvalidRegex, i + 1, e.getDescription()), Toast.LENGTH_LONG).show();
                            return;
                        }
                        newRules.add(new InlineBotRulesHelper.InlineBotRule(username, ruleInput, "", false));
                    } else {
                        try {
                            String host = InlineBotRulesHelper.normalizeSimpleHostInput(ruleInput);
                            String rule = InlineBotRulesHelper.buildHostPattern(host);
                            newRules.add(new InlineBotRulesHelper.InlineBotRule(username, rule, host, false));
                        } catch (IllegalArgumentException e) {
                            row.ruleEditText.setError(e.getMessage());
                            row.ruleEditText.requestFocus();
                            Toast.makeText(context, LocaleController.formatString("FixUrlAutoInlineBotRuleInvalidHost", R.string.FixUrlAutoInlineBotRuleInvalidHost, i + 1, e.getMessage()), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                String newValue = InlineBotRulesHelper.serializeInlineBotRules(newRules);
                NaConfig.INSTANCE.getFixUrlAutoInlineBotRules().setConfigString(newValue);
                NaConfig.INSTANCE.getFixUrlAutoInlineBotRulesAdvancedMode().setConfigBool(advancedMode[0]);
                cellGroup.listAdapter.notifyItemChanged(cellGroup.rows.indexOf(fixUrlAutoInlineBotRulesRow));
                cellGroup.thisFragment.getParentLayout().rebuildAllFragmentViews(false, false);
                cellGroup.runCallback(NaConfig.INSTANCE.getFixUrlAutoInlineBotRules().getKey(), newValue);
                cellGroup.runCallback(NaConfig.INSTANCE.getFixUrlAutoInlineBotRulesAdvancedMode().getKey(), advancedMode[0]);
                dialog.dismiss();
            });
        }
    }

    private boolean shouldUseAdvancedFixUrlAutoInlineBotRulesMode(ArrayList<InlineBotRulesHelper.InlineBotRule> rules) {
        for (InlineBotRulesHelper.InlineBotRule rule : rules) {
            if (InlineBotRulesHelper.getHostForRule(rule.rule, rule.host).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean updateFixUrlAutoInlineBotRulesDialogMode(Context context, TextView hintTextView, ArrayList<FixUrlAutoInlineBotRuleRow> ruleRows, boolean advancedMode, boolean convertValues) {
        ArrayList<String> convertedValues = new ArrayList<>();
        if (convertValues) {
            for (FixUrlAutoInlineBotRuleRow row : ruleRows) {
                String value = row.ruleEditText.getText().toString().trim();
                String convertedValue = value;
                if (!value.isEmpty()) {
                    if (advancedMode) {
                        try {
                            convertedValue = InlineBotRulesHelper.buildHostPattern(value);
                        } catch (RuntimeException ignored) {
                            convertedValue = value;
                        }
                    } else {
                        String host = InlineBotRulesHelper.extractHostFromPattern(value);
                        if (host == null) {
                            row.ruleEditText.setError(LocaleController.getString(R.string.FixUrlAutoInlineBotRulesCannotConvertSimple));
                            row.ruleEditText.requestFocus();
                            Toast.makeText(context, LocaleController.getString(R.string.FixUrlAutoInlineBotRulesCannotConvertSimple), Toast.LENGTH_LONG).show();
                            return false;
                        }
                        convertedValue = host;
                    }
                }
                convertedValues.add(convertedValue);
            }
        }
        hintTextView.setText(LocaleController.getString(advancedMode ? R.string.FixUrlAutoInlineBotRulesAdvancedHint : R.string.FixUrlAutoInlineBotRulesSimpleHint));
        for (int i = 0; i < ruleRows.size(); i++) {
            FixUrlAutoInlineBotRuleRow row = ruleRows.get(i);
            if (convertValues) {
                String value = convertedValues.get(i);
                row.ruleEditText.setText(value);
                row.ruleEditText.setSelection(row.ruleEditText.length());
            }
            row.ruleEditText.setHint(LocaleController.getString(advancedMode ? R.string.FixUrlAutoInlineBotRulePatternHint : R.string.FixUrlAutoInlineBotRuleHostHint));
        }
        return true;
    }

    private void addFixUrlAutoInlineBotRuleRow(
            Context context,
            LinearLayout rowsContainer,
            ArrayList<FixUrlAutoInlineBotRuleRow> ruleRows,
            String rule,
            String host,
            String username,
            boolean advancedMode
    ) {
        LinearLayout cardLayout = new LinearLayout(context);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(AndroidUtilities.dp(14), AndroidUtilities.dp(10), AndroidUtilities.dp(14), AndroidUtilities.dp(10));
        cardLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(10), Theme.getColor(Theme.key_dialogBackgroundGray)));

        LinearLayout topRow = new LinearLayout(context);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        cardLayout.addView(topRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        EditTextBoldCursor ruleEditText = new EditTextBoldCursor(context);
        setupFixUrlAutoInlineBotRuleEditText(ruleEditText, LocaleController.getString(advancedMode ? R.string.FixUrlAutoInlineBotRulePatternHint : R.string.FixUrlAutoInlineBotRuleHostHint));
        ruleEditText.setText(advancedMode ? rule : InlineBotRulesHelper.getHostForRule(rule, host));
        topRow.addView(ruleEditText, LayoutHelper.createLinear(0, 48, 1f));

        TextView deleteButton = new TextView(context);
        deleteButton.setText("×");
        deleteButton.setTextSize(22);
        deleteButton.setGravity(Gravity.CENTER);
        deleteButton.setIncludeFontPadding(false);
        deleteButton.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
        deleteButton.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(18), Theme.getColor(Theme.key_text_RedRegular)));
        topRow.addView(deleteButton, LayoutHelper.createLinear(42, 42, 10, 3, 0, 3));

        EditTextBoldCursor usernameEditText = new EditTextBoldCursor(context);
        setupFixUrlAutoInlineBotRuleEditText(usernameEditText, LocaleController.getString(R.string.FixUrlAutoInlineBotUsernameHint));
        usernameEditText.setText(username);
        cardLayout.addView(usernameEditText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 0, 6, 0, 0));

        FixUrlAutoInlineBotRuleRow row = new FixUrlAutoInlineBotRuleRow(ruleEditText, usernameEditText);
        deleteButton.setOnClickListener(v -> {
            ruleRows.remove(row);
            rowsContainer.removeView(cardLayout);
        });

        ruleRows.add(row);
        rowsContainer.addView(cardLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 0, 24, 8));
    }

    private void setupFixUrlAutoInlineBotRuleEditText(EditTextBoldCursor editText, String hint) {
        editText.setSingleLine(true);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        editText.setHint(hint);
        editText.setTextSize(16);
        editText.setGravity(Gravity.CENTER_VERTICAL | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT));
        editText.setMinHeight(AndroidUtilities.dp(48));
        editText.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setCursorColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setBackground(Theme.createEditTextDrawable(editText.getContext(), true));
    }

    private static class FixUrlAutoInlineBotRuleRow {
        final EditTextBoldCursor ruleEditText;
        final EditTextBoldCursor usernameEditText;

        FixUrlAutoInlineBotRuleRow(EditTextBoldCursor ruleEditText, EditTextBoldCursor usernameEditText) {
            this.ruleEditText = ruleEditText;
            this.usernameEditText = usernameEditText;
        }
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
        var superView = super.createView(context);

        refreshExternalStickerStorageState(); // Cell (externalStickerCacheRow): Refresh state

        listAdapter = new ListAdapter(context);

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
                    TL_account.setContentSettings req = new TL_account.setContentSettings();
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
            if (key.equals(NekoConfig.enableStickerPin.getKey())) {
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
            } else if (key.equals(NaConfig.INSTANCE.getPlayerDecoder().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getDisableFlagSecure().getKey())) {
                NekoXConfig.disableFlagSecure = NaConfig.INSTANCE.getDisableFlagSecure().Bool();
            } else if (key.equals(NaConfig.INSTANCE.getNavigationAnimationSpring().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            } else if (key.equals(NaConfig.INSTANCE.getForceEdgeToEdge().getKey())) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESATRT, null, null);
            }
        };

        //Cells: Set ListAdapter
        cellGroup.setListAdapter(listView, listAdapter);

        return superView;
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
        TL_account.getContentSettings req = new TL_account.getContentSettings();
        getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (error == null) {
                TL_account.contentSettings settings = (TL_account.contentSettings) response;
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
                    if (holder.itemView instanceof TextCheckCell) {
                        TextCheckCell textCell = (TextCheckCell) holder.itemView;
                        textCell.setEnabled(true, null);
                        if (position == cellGroup.rows.indexOf(disableFilteringRow)) {
                            textCell.setTextAndValueAndCheck(LocaleController.getString("SensitiveDisableFiltering", R.string.SensitiveDisableFiltering), LocaleController.getString("SensitiveAbout", R.string.SensitiveAbout), sensitiveEnabled, true, divider);
                            textCell.setEnabled(sensitiveCanChange, null);
                        }
                    } else if (holder.itemView instanceof TextSettingsCell) {
                        TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        if (position == cellGroup.rows.indexOf(customAudioBitrateRow)) {
                            String value = String.valueOf(NekoConfig.customAudioBitrate.Int()) + "kbps";
                            if (NekoConfig.customAudioBitrate.Int() == 32)
                                value += " (" + LocaleController.getString("Default", R.string.Default) + ")";
                            textCell.setTextAndValue(LocaleController.getString("customGroupVoipAudioBitrate", R.string.customGroupVoipAudioBitrate), value, divider);
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
    }

    @Override
    protected void setCanNotChange() {
        super.setCanNotChange();

        if (!NaConfig.INSTANCE.getShowHiddenFeature().Bool()) {
            cellGroup.rows.remove(localPremiumRow);
            cellGroup.rows.remove(localQuoteColorRow);
            cellGroup.rows.remove(externalGhostModeRow);
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
            NekoConfig.setGhostMode(false);
            NaConfig.INSTANCE.getForceCopy().setConfigBool(false);
            NaConfig.INSTANCE.getDisableFlagSecure().setConfigBool(false);
            NekoXConfig.disableFlagSecure = false;
            NekoConfig.hideSponsoredMessage.setConfigBool(false);
            NekoConfig.ignoreBlocked.setConfigBool(false);
            NaConfig.INSTANCE.getRegexFiltersEnabled().setConfigBool(false);
            NekoConfig.disableChatAction.setConfigBool(false);
            NekoConfig.disableChoosingSticker.setConfigBool(false);
            NaConfig.INSTANCE.getDisableSendReadStories().setConfigBool(false);
        }

        addRowsToMap();
    }
}

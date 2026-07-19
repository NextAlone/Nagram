package tw.nekomimi.nekogram.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;

import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.ConfigCellCustom;
import tw.nekomimi.nekogram.config.cell.ConfigCellDivider;
import tw.nekomimi.nekogram.config.cell.ConfigCellHeader;
import tw.nekomimi.nekogram.config.cell.ConfigCellSelectBox;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextCheck;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextInput;
import tw.nekomimi.nekogram.transtale.source.LLMTranslator;
import xyz.nextalone.nagram.NaConfig;

@SuppressLint("RtlHardcoded")
public class NekoLLMSettingsActivity extends BaseNekoXSettingsActivity {

    private final CellGroup a = cellGroup = new CellGroup(this);

    // --- Provider section ---
    private final AbstractConfigCell headerProvider = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("LLMProvider", R.string.LLMProvider)));
    private final AbstractConfigCell llmProviderRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getLlmProvider(),
            new String[]{
                    LocaleController.getString("LLMProviderOpenAI", R.string.LLMProviderOpenAI),
                    LocaleController.getString("LLMProviderGemini", R.string.LLMProviderGemini),
                    LocaleController.getString("LLMProviderGroq", R.string.LLMProviderGroq),
                    LocaleController.getString("LLMProviderDeepSeek", R.string.LLMProviderDeepSeek),
                    LocaleController.getString("LLMProviderXAI", R.string.LLMProviderXAI),
                    LocaleController.getString("LLMProviderZhipuAI", R.string.LLMProviderZhipuAI),
                    LocaleController.getString("LLMProviderMistral", R.string.LLMProviderMistral),
                    LocaleController.getString("LLMProviderOpenRouter", R.string.LLMProviderOpenRouter),
                    LocaleController.getString("LLMProviderQwen", R.string.LLMProviderQwen),
                    LocaleController.getString("LLMProviderMoonshot", R.string.LLMProviderMoonshot),
                    LocaleController.getString("LLMProviderSiliconFlow", R.string.LLMProviderSiliconFlow),
                    LocaleController.getString("LLMProviderCustom", R.string.LLMProviderCustom)
            }, null));
    private final AbstractConfigCell llmApiFormatRow = cellGroup.appendCell(new ConfigCellSelectBox(null, NaConfig.INSTANCE.getLlmApiFormat(),
            new String[]{
                    LocaleController.getString("LLMApiFormatOpenAIChat", R.string.LLMApiFormatOpenAIChat),
                    LocaleController.getString("LLMApiFormatOpenAIResponse", R.string.LLMApiFormatOpenAIResponse),
                    LocaleController.getString("LLMApiFormatAnthropic", R.string.LLMApiFormatAnthropic),
                    LocaleController.getString("LLMApiFormatCustom", R.string.LLMApiFormatCustom)
            }, null));
    private final AbstractConfigCell llmApiFormatDescRow = cellGroup.appendCell(new ConfigCellCustom("LLMApiFormatDesc", CellGroup.ITEM_TYPE_TEXT, false));
    private final AbstractConfigCell llmApiUrlRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getLlmApiUrl(), "https://api.openai.com/v1", null));
    private final AbstractConfigCell dividerProvider = cellGroup.appendCell(new ConfigCellDivider());

    // --- Credential section ---
    private final AbstractConfigCell headerCredential = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("LLMApiKey", R.string.LLMApiKey)));
    private final AbstractConfigCell llmApiKeysRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getLlmApiKeys(), "sk-...", null));
    private final AbstractConfigCell llmModelRow = cellGroup.appendCell(new ConfigCellCustom("LLMModel", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell llmFetchModelsRow = cellGroup.appendCell(new ConfigCellCustom("LLMFetchModels", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell dividerCredential = cellGroup.appendCell(new ConfigCellDivider());

    // --- Advanced section ---
    private final AbstractConfigCell headerAdvanced = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("General", R.string.General)));
    private final AbstractConfigCell llmTranslationPromptRow = cellGroup.appendCell(new ConfigCellTextInput(null, NaConfig.INSTANCE.getLlmTranslationPrompt(), LocaleController.getString("LLMTranslationPromptNotice", R.string.LLMTranslationPromptNotice) + "\n\n" + LLMTranslator.DEFAULT_USER_PROMPT, null));
    private final AbstractConfigCell llmUseContextRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getLlmUseContext(), LocaleController.getString("LLMUseContextNotice", R.string.LLMUseContextNotice)));
    private final AbstractConfigCell llmTemperatureRow = cellGroup.appendCell(new ConfigCellCustom("LLMTemperature", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell dividerAdvanced = cellGroup.appendCell(new ConfigCellDivider());

    // --- Test section ---
    private final AbstractConfigCell llmTestConnectionRow = cellGroup.appendCell(new ConfigCellCustom("LLMTestConnection", CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL, true));
    private final AbstractConfigCell dividerTest = cellGroup.appendCell(new ConfigCellDivider());

    @Override
    public String getTitle() {
        return LocaleController.getString("LLMTranslatorSettings", R.string.LLMTranslatorSettings);
    }

    @Override
    public View createView(Context context) {
        View view = super.createView(context);

        setCanNotChange();
        listAdapter = new ListAdapter(context);
        listView.setAdapter(listAdapter);
        cellGroup.setListAdapter(listView, listAdapter);

        listView.setOnItemClickListener((view1, position) -> {
            AbstractConfigCell row = cellGroup.rows.get(position);
            if (row instanceof ConfigCellSelectBox) {
                ((ConfigCellSelectBox) row).onClick(view1);
            } else if (row instanceof ConfigCellTextCheck) {
                ((ConfigCellTextCheck) row).onClick((org.telegram.ui.Cells.TextCheckCell) view1);
            } else if (row instanceof ConfigCellTextInput) {
                ((ConfigCellTextInput) row).onClick();
            } else if (row == llmModelRow) {
                showModelDialog(context, position);
            } else if (row == llmTemperatureRow) {
                showTemperatureDialog(context, position);
            } else if (row == llmFetchModelsRow) {
                fetchModels(context);
            } else if (row == llmTestConnectionRow) {
                testConnection();
            }
        });

        cellGroup.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NaConfig.INSTANCE.getLlmProvider().getKey()) || key.equals(NaConfig.INSTANCE.getLlmApiFormat().getKey())) {
                updateRows();
            }
        };

        addRowsToMap();
        return view;
    }

    @Override
    protected void setCanNotChange() {
        boolean isCustom = NaConfig.INSTANCE.getLlmProvider().Int() == LLMTranslator.PROVIDER_CUSTOM;

        cellGroup.rows.remove(llmApiFormatRow);
        cellGroup.rows.remove(llmApiFormatDescRow);
        cellGroup.rows.remove(llmApiUrlRow);
        cellGroup.rows.remove(llmTemperatureRow);

        if (isCustom) {
            int idx = cellGroup.rows.indexOf(llmProviderRow) + 1;
            cellGroup.rows.add(idx++, llmApiFormatRow);
            cellGroup.rows.add(idx++, llmApiFormatDescRow);
            cellGroup.rows.add(idx, llmApiUrlRow);
        }
        if (getEffectiveApiFormat() != LLMTranslator.API_FORMAT_ANTHROPIC) {
            cellGroup.rows.add(cellGroup.rows.indexOf(dividerAdvanced), llmTemperatureRow);
            normalizeTemperatureConfig();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            updateRows();
        }
    }

    // --- Model dialog ---
    private void showModelDialog(Context context, int position) {
        ConfigItem modelConfig = getModelConfigForProvider(NaConfig.INSTANCE.getLlmProvider().Int());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("LLMModelName", R.string.LLMModelName));

        android.widget.LinearLayout linearLayout = new android.widget.LinearLayout(context);
        linearLayout.setOrientation(android.widget.LinearLayout.VERTICAL);

        org.telegram.ui.Components.EditTextBoldCursor editText = new org.telegram.ui.Components.EditTextBoldCursor(context);
        editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        editText.setHint("gpt-4o-mini");
        editText.setText(modelConfig.String());
        linearLayout.addView(editText, org.telegram.ui.Components.LayoutHelper.createLinear(
            org.telegram.ui.Components.LayoutHelper.MATCH_PARENT,
            org.telegram.ui.Components.LayoutHelper.WRAP_CONTENT,
            AndroidUtilities.dp(8), 0, AndroidUtilities.dp(10), 0));

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (d, v) -> {
            modelConfig.setConfigString(editText.getText().toString());
            listAdapter.notifyItemChanged(position);
        });
        builder.setView(linearLayout);
        showDialog(builder.create());
    }

    private int getEffectiveApiFormat() {
        return NaConfig.INSTANCE.getLlmProvider().Int() == LLMTranslator.PROVIDER_CUSTOM
                ? NaConfig.INSTANCE.getLlmApiFormat().Int()
                : LLMTranslator.API_FORMAT_OPENAI_CHAT;
    }

    private double getNormalizedTemperature() {
        return LLMTranslator.parseTemperature(NaConfig.INSTANCE.getLlmTemperature().String());
    }

    private String formatTemperature(double value) {
        return String.format(java.util.Locale.US, "%.1f", value);
    }

    private void normalizeTemperatureConfig() {
        String normalized = Double.toString(getNormalizedTemperature());
        if (!normalized.equals(NaConfig.INSTANCE.getLlmTemperature().String())) {
            NaConfig.INSTANCE.getLlmTemperature().setConfigString(normalized);
        }
    }

    // --- Temperature dialog ---
    private void showTemperatureDialog(Context context, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("LLMTemperature", R.string.LLMTemperature));

        android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(8), AndroidUtilities.dp(24), AndroidUtilities.dp(8));

        double currentTemp = getNormalizedTemperature();

        android.widget.TextView label = new android.widget.TextView(context);
        label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 16);
        label.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        label.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        label.setText(formatTemperature(currentTemp));
        layout.addView(label, org.telegram.ui.Components.LayoutHelper.createLinear(
            org.telegram.ui.Components.LayoutHelper.WRAP_CONTENT,
            org.telegram.ui.Components.LayoutHelper.WRAP_CONTENT,
            android.view.Gravity.CENTER_HORIZONTAL, 0, 0, 0, 8));

        org.telegram.ui.Components.SeekBarView seekBar = new org.telegram.ui.Components.SeekBarView(context);
        seekBar.setReportChanges(true);
        float[] tempValue = {(float) currentTemp};
        seekBar.setDelegate(new org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate() {
            @Override
            public void onSeekBarDrag(boolean stop, float progress) {
                tempValue[0] = progress * 2.0f;
                label.setText(formatTemperature(tempValue[0]));
            }

            @Override
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar.setProgress((float) (currentTemp / 2.0));
        layout.addView(seekBar, org.telegram.ui.Components.LayoutHelper.createLinear(
            org.telegram.ui.Components.LayoutHelper.MATCH_PARENT, 38));

        builder.setView(layout);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (d, v) -> {
            NaConfig.INSTANCE.getLlmTemperature().setConfigString(formatTemperature(tempValue[0]));
            listAdapter.notifyItemChanged(position);
        });
        showDialog(builder.create());
    }

    // --- Fetch models ---
    private void fetchModels(Context context) {
        int provider = NaConfig.INSTANCE.getLlmProvider().Int();
        String apiKey = NaConfig.INSTANCE.getLlmApiKeys().String();
        if (apiKey == null || apiKey.isEmpty()) {
            BulletinFactory.of(this).createErrorBulletin(
                LocaleController.getString("LLMNoModelsFound", R.string.LLMNoModelsFound)).show();
            return;
        }
        String customUrl = provider == LLMTranslator.PROVIDER_CUSTOM ? NaConfig.INSTANCE.getLlmApiUrl().String() : null;

        new Thread(() -> {
            try {
                java.util.List<String> models = LLMTranslator.INSTANCE
                    .fetchModelsBlocking(provider, apiKey, customUrl);

                AndroidUtilities.runOnUIThread(() -> {
                    if (models == null || models.isEmpty()) {
                        BulletinFactory.of(NekoLLMSettingsActivity.this).createErrorBulletin(
                            LocaleController.getString("LLMNoModelsFound", R.string.LLMNoModelsFound)).show();
                        return;
                    }

                    AlertDialog.Builder modelBuilder = new AlertDialog.Builder(context);
                    modelBuilder.setTitle(LocaleController.getString("LLMSelectModel", R.string.LLMSelectModel));
                    String[] modelArray = models.toArray(new String[0]);
                    modelBuilder.setItems(modelArray, (dialog, which) -> {
                        ConfigItem modelConfig = getModelConfigForProvider(provider);
                        modelConfig.setConfigString(modelArray[which]);
                        listAdapter.notifyItemChanged(cellGroup.rows.indexOf(llmModelRow));
                    });
                    modelBuilder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    modelBuilder.show();
                });
            } catch (Exception e) {
                AndroidUtilities.runOnUIThread(() ->
                    BulletinFactory.of(NekoLLMSettingsActivity.this).createErrorBulletin(
                        LocaleController.getString("LLMNoModelsFound", R.string.LLMNoModelsFound)).show());
            }
        }).start();
    }

    // --- Test connection ---
    private void testConnection() {
        int provider = NaConfig.INSTANCE.getLlmProvider().Int();
        String apiKey = NaConfig.INSTANCE.getLlmApiKeys().String();
        if (apiKey == null || apiKey.isEmpty()) {
            BulletinFactory.of(this).createErrorBulletin(
                String.format(LocaleController.getString("LLMTestFailed", R.string.LLMTestFailed), "API Key is empty")).show();
            return;
        }
        ConfigItem modelConfig = getModelConfigForProvider(provider);
        String model = modelConfig.String();
        if (model == null || model.isEmpty()) {
            String def = LLMTranslator.INSTANCE.getDefaultModel(provider);
            model = def != null ? def : "";
        }
        if (model.isEmpty()) {
            BulletinFactory.of(this).createErrorBulletin(
                String.format(LocaleController.getString("LLMTestFailed", R.string.LLMTestFailed), "Model is empty")).show();
            return;
        }
        String customUrl = provider == LLMTranslator.PROVIDER_CUSTOM ? NaConfig.INSTANCE.getLlmApiUrl().String() : null;
        int apiFormat = NaConfig.INSTANCE.getLlmApiFormat().Int();
        String finalModel = model;

        new Thread(() -> {
            String error = LLMTranslator.INSTANCE
                .testConnectionBlocking(provider, apiKey, finalModel, customUrl, apiFormat);

            AndroidUtilities.runOnUIThread(() -> {
                if (error == null) {
                    BulletinFactory.of(NekoLLMSettingsActivity.this).createSimpleBulletin(
                        R.raw.contact_check,
                        LocaleController.getString("LLMTestSuccess", R.string.LLMTestSuccess)).show();
                } else {
                    String msg = error.length() > 100 ? error.substring(0, 100) + "..." : error;
                    BulletinFactory.of(NekoLLMSettingsActivity.this).createErrorBulletin(
                        String.format(LocaleController.getString("LLMTestFailed", R.string.LLMTestFailed), msg)).show();
                }
            });
        }).start();
    }

    // --- Helpers ---
    private static ConfigItem getModelConfigForProvider(int provider) {
        switch (provider) {
            case 0: return NaConfig.INSTANCE.getLlmOpenAIModel();
            case 1: return NaConfig.INSTANCE.getLlmGeminiModel();
            case 2: return NaConfig.INSTANCE.getLlmGroqModel();
            case 3: return NaConfig.INSTANCE.getLlmDeepSeekModel();
            case 4: return NaConfig.INSTANCE.getLlmXAIModel();
            case 5: return NaConfig.INSTANCE.getLlmZhipuAIModel();
            case 6: return NaConfig.INSTANCE.getLlmMistralModel();
            case 7: return NaConfig.INSTANCE.getLlmOpenRouterModel();
            case 8: return NaConfig.INSTANCE.getLlmQwenModel();
            case 9: return NaConfig.INSTANCE.getLlmMoonshotModel();
            case 10: return NaConfig.INSTANCE.getLlmSiliconFlowModel();
            case 11: return NaConfig.INSTANCE.getLlmCustomModel();
            default: return NaConfig.INSTANCE.getLlmCustomModel();
        }
    }

    // --- Adapter ---
    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean partial, boolean divider) {
            int viewType = holder.getItemViewType();
            AbstractConfigCell row = cellGroup.rows.get(position);

            // Custom cells need manual binding
            if (row == llmModelRow) {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                cell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                ConfigItem modelConfig = getModelConfigForProvider(NaConfig.INSTANCE.getLlmProvider().Int());
                cell.setTextAndValue(LocaleController.getString("LLMModelName", R.string.LLMModelName), modelConfig.String(), divider);
            } else if (row == llmFetchModelsRow) {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                cell.setText(LocaleController.getString("LLMFetchModels", R.string.LLMFetchModels), divider);
                cell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
            } else if (row == llmTemperatureRow) {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                cell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                cell.setTextAndValue(LocaleController.getString("LLMTemperature", R.string.LLMTemperature),
                    formatTemperature(getNormalizedTemperature()), divider);
            } else if (row == llmTestConnectionRow) {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                cell.setText(LocaleController.getString("LLMTestConnection", R.string.LLMTestConnection), divider);
                cell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
            } else if (row == llmApiFormatDescRow) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                int format = NaConfig.INSTANCE.getLlmApiFormat().Int();
                String desc;
                switch (format) {
                    case 1: desc = LocaleController.getString("LLMApiFormatOpenAIResponseDesc", R.string.LLMApiFormatOpenAIResponseDesc); break;
                    case 2: desc = LocaleController.getString("LLMApiFormatAnthropicDesc", R.string.LLMApiFormatAnthropicDesc); break;
                    case 3: desc = LocaleController.getString("LLMApiFormatCustomDesc", R.string.LLMApiFormatCustomDesc); break;
                    default: desc = LocaleController.getString("LLMApiFormatOpenAIChatDesc", R.string.LLMApiFormatOpenAIChatDesc); break;
                }
                cell.setText(desc);
            } else {
                // Default binding for headers, select boxes, text inputs, etc.
                row.onBindViewHolder(holder);
            }
        }
    }
}

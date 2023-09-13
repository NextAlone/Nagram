package tw.nekomimi.nekogram.settings;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.BulletinFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.ConfigCellCustom;
import tw.nekomimi.nekogram.config.cell.ConfigCellSelectBox;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextCheck;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextDetail;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextInput;

public class BaseNekoXSettingsActivity extends BaseFragment {
    protected BlurredRecyclerView listView;
    protected LinearLayoutManager layoutManager;
    protected HashMap<String, Integer> rowMap = new HashMap<>(20);
    protected HashMap<Integer, String> rowMapReverse = new HashMap<>(20);
    protected HashMap<Integer, ConfigItem> rowConfigMapReverse = new HashMap<>(20);

    protected void updateRows() {
    }

    protected void addRowsToMap(CellGroup cellGroup) {
        rowMap.clear();
        rowMapReverse.clear();
        rowConfigMapReverse.clear();
        String key;
        ConfigItem config;
        for (int i = 0; i < cellGroup.rows.size(); i++) {
            config = getBindConfig(cellGroup.rows.get(i));
            key = getRowKey(cellGroup.rows.get(i));
            if (key == null) key = String.valueOf(i);
            rowMap.put(key, i);
            rowMapReverse.put(i, key);
            rowConfigMapReverse.put(i, config);
        }
    }

    protected String getRowKey(int position) {
        if (rowMapReverse.containsKey(position)) {
            return rowMapReverse.get(position);
        }
        return String.valueOf(position);
    }

    protected String getRowValue(int position) {
        ConfigItem config = rowConfigMapReverse.get(position);
        if (config != null) return config.String();
        return null;
    }

    protected ConfigItem getBindConfig(AbstractConfigCell row) {
        if (row instanceof ConfigCellTextCheck) {
            return ((ConfigCellTextCheck) row).getBindConfig();
        } else if (row instanceof ConfigCellSelectBox) {
            return ((ConfigCellSelectBox) row).getBindConfig();
        } else if (row instanceof ConfigCellTextDetail) {
            return ((ConfigCellTextDetail) row).getBindConfig();
        } else if (row instanceof ConfigCellTextInput) {
            return ((ConfigCellTextInput) row).getBindConfig();
        }
        return null;
    }

    protected String getRowKey(AbstractConfigCell row) {
        if (row instanceof ConfigCellTextCheck) {
            return ((ConfigCellTextCheck) row).getKey();
        } else if (row instanceof ConfigCellSelectBox) {
            return ((ConfigCellSelectBox) row).getKey();
        } else if (row instanceof ConfigCellTextDetail) {
            return ((ConfigCellTextDetail) row).getKey();
        } else if (row instanceof ConfigCellTextInput) {
            return ((ConfigCellTextInput) row).getKey();
        } else if (row instanceof ConfigCellCustom) {
            return ((ConfigCellCustom) row).getKey();
        }
        return null;
    }

    protected void createLongClickDialog(Context context, BaseFragment fragment, String prefix,  int position) {
        String key = getRowKey(position);
        String value = getRowValue(position);
        ArrayList<CharSequence> itemsArray = new ArrayList<>();
        itemsArray.add(LocaleController.getString("CopyLink", R.string.CopyLink));
        if (value != null) {
            itemsArray.add(LocaleController.getString("BackupSettings", R.string.BackupSettings));
        }
        CharSequence[] items = itemsArray.toArray(new CharSequence[0]);
        showDialog(new AlertDialog.Builder(context)
                .setItems(
                        items,
                        (dialogInterface, i) -> {
                            switch (i) {
                                case 0:
                                    AndroidUtilities.addToClipboard(String.format(Locale.getDefault(), "https://%s/nasettings/%s?r=%s", getMessagesController().linkPrefix, prefix, key));
                                    BulletinFactory.of(fragment).createCopyLinkBulletin().show();
                                    break;
                                case 1:
                                    AndroidUtilities.addToClipboard(String.format(Locale.getDefault(), "https://%s/nasettings/%s?r=%s&v=%s", getMessagesController().linkPrefix, prefix, key, value));
                                    BulletinFactory.of(fragment).createCopyLinkBulletin().show();
                                    break;
                            }
                        })
                .create());
    }

    public void importToRow(String key, String value, Runnable unknown) {
        int position = -1;
        try {
            position = Integer.parseInt(key);
        } catch (NumberFormatException exception) {
            Integer temp = rowMap.get(key);
            if (temp != null) position = temp;
        }
        ConfigItem config = rowConfigMapReverse.get(position);
        Context context = getParentActivity();
        if (context != null && config != null) {
            Object new_value = config.checkConfigFromString(value);
            if (new_value == null) {
                scrollToRow(key, unknown);
                return;
            }
            var builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString("ImportSettings", R.string.ImportSettings));
            builder.setMessage(LocaleController.getString("ImportSettingsAlert", R.string.ImportSettingsAlert));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString("Import", R.string.Import), (dialogInter, i) -> {
                config.changed(new_value);
                config.saveConfig();
                updateRows();
            });
            builder.show();
        } else {
            scrollToRow(key, unknown);
        }
    }

    public void scrollToRow(String key, Runnable unknown) {
        int position = -1;
        try {
            position = Integer.parseInt(key);
        } catch (NumberFormatException exception) {
            Integer temp = rowMap.get(key);
            if (temp != null) position = temp;
        }
        if (position > -1 && listView != null && layoutManager != null) {
            int finalPosition = position;
            listView.highlightRow(() -> {
                layoutManager.scrollToPositionWithOffset(finalPosition, AndroidUtilities.dp(60));
                return finalPosition;
            });
        } else {
            unknown.run();
        }
    }
}

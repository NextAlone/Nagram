package tw.nekomimi.nekogram.settings;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BlurredRecyclerView;

import java.util.HashMap;

import tw.nekomimi.nekogram.config.CellGroup;
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

    protected void addRowsToMap(CellGroup cellGroup) {
        rowMap.clear();
        rowMapReverse.clear();
        String key;
        for (int i = 0; i < cellGroup.rows.size(); i++) {
            key = getRowKey(cellGroup.rows.get(i));
            if (key != null) {
                rowMap.put(key, i);
                rowMapReverse.put(i, key);
            } else {
                rowMap.put(String.valueOf(i), i);
                rowMapReverse.put(i, String.valueOf(i));
            }
        }
    }

    protected String getRowKey(int position) {
        if (rowMapReverse.containsKey(position)) {
            return rowMapReverse.get(position);
        }
        return String.valueOf(position);
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

    public void scrollToRow(String key, Runnable unknown) {
        int position = -1;
        try {
            position = Integer.parseInt(key);
        } catch (NumberFormatException exception) {
            if (rowMap.containsKey(key)) {
                //noinspection ConstantConditions
                position = rowMap.get(key);
            }
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

package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.TextSettingsCell;
import tw.nekomimi.nekogram.config.CellGroup;

public class ConfigCellText extends AbstractConfigCell implements WithKey, WithOnClick {
    private final String key;
    private final String value;
    private final Runnable onClick;

    public ConfigCellText(String key, String customValue, Runnable onClick) {
        this.key = key;
        this.value = (customValue == null) ? "" : customValue;
        this.onClick = onClick;
    }

    public ConfigCellText(String key, Runnable onClick) {
        this(key, null, onClick);
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL;
    }

    public String getKey() {
        return key;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        TextSettingsCell cell = (TextSettingsCell) holder.itemView;
        String title = LocaleController.getString(key);
        cell.setTextAndValue(title, value, cellGroup.needSetDivider(this));
    }

    public void onClick() {
        if (onClick != null) {
            try {
                onClick.run();
            } catch (Exception ignored) {}
        }
    }
}

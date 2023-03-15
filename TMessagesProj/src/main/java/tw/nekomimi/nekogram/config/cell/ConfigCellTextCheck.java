package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.TextCheckCell;

import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;

public class ConfigCellTextCheck extends AbstractConfigCell {
    private final ConfigItem bindConfig;
    private final String title;
    private final String subtitle;
    private boolean enabled = true;
    public TextCheckCell cell;

    public ConfigCellTextCheck(ConfigItem bind) {
        this.bindConfig = bind;
        this.title = LocaleController.getString(bindConfig.getKey());
        this.subtitle = null;
    }

    public ConfigCellTextCheck(ConfigItem bind, String subtitle) {
        this.bindConfig = bind;
        this.title = LocaleController.getString(bindConfig.getKey());
        this.subtitle = subtitle;
    }

    public ConfigCellTextCheck(ConfigItem bind, String subtitle, String customTitle) {
        this.bindConfig = bind;
        this.title = customTitle;
        this.subtitle = subtitle;
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT_CHECK;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (this.cell != null)
            this.cell.setEnabled(this.enabled);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        TextCheckCell cell = (TextCheckCell) holder.itemView;
        this.cell = cell;
        if (subtitle == null) {
            cell.setTextAndCheck(title, bindConfig.Bool(), cellGroup.needSetDivider(this));
        } else {
            cell.setTextAndValueAndCheck(title, subtitle, bindConfig.Bool(), true, cellGroup.needSetDivider(this));
        }
        cell.setEnabled(enabled, null);
    }

    public void onClick(TextCheckCell cell) {
        if (!enabled) return;

        boolean newV = bindConfig.toggleConfigBool();
        cell.setChecked(newV);

        cellGroup.runCallback(bindConfig.getKey(), newV);
    }
}


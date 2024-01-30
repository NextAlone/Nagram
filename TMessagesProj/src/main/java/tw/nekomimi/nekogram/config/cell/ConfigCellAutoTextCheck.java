package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.TextCheckCell;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;

import java.util.function.Consumer;

public class ConfigCellAutoTextCheck extends AbstractConfigCell {
    private final ConfigItem bindConfig;
    private final Consumer<Boolean> onClick;
    private final String title;
    private String subtitle = null;
    private final String subtitleFallback;
    private boolean enabled = true;
    public TextCheckCell cell;

    public ConfigCellAutoTextCheck(ConfigItem bindConfig, String subtitleFallback, Consumer<Boolean> onClick) {
        this.bindConfig = bindConfig;
        this.title = LocaleController.getString(bindConfig.getKey());
        this.subtitleFallback = subtitleFallback;
        this.onClick = onClick;
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT_CHECK;
    }

    public String getKey() {
        return bindConfig == null ? null : bindConfig.getKey();
    }

    public ConfigItem getBindConfig() {
        return bindConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (this.cell != null)
            this.cell.setEnabled(this.enabled);
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        boolean checked = true;
        if (subtitle == null || subtitle.isBlank()) {
            subtitle = subtitleFallback;
            checked = false;
        }
        if (cell != null) {
            if (subtitle == null) {
                cell.setTextAndCheck(title, checked, cellGroup.needSetDivider(this));
            } else {
                cell.setTextAndValueAndCheck(title, subtitle, checked, true, cellGroup.needSetDivider(this));
            }
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        TextCheckCell cell = (TextCheckCell) holder.itemView;
        this.cell = cell;
        setSubtitle(subtitle);
        cell.setEnabled(enabled, null);
    }

    public void onClick() {
        if (enabled) {
            if (onClick != null) onClick.accept(cell.isChecked());
        }
    }
}
package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;
import org.telegram.ui.Cells.TextCheckCell;
import tw.nekomimi.nekogram.config.CellGroup;

import java.util.function.Consumer;

public class ConfigCellAutoTextCheck extends AbstractConfigCell {
    private final Consumer<Boolean> onClick;
    private final String title;
    private String subtitle = null;
    private final String subtitleFallback;
    private boolean enabled = true;
    public TextCheckCell cell;

    public ConfigCellAutoTextCheck(String title, String subtitleFallback, Consumer<Boolean> onClick) {
        this.title = title;
        this.subtitleFallback = subtitleFallback;
        this.onClick = onClick;
    }

    public ConfigCellAutoTextCheck(String title, Consumer<Boolean> onClick) {
        this(title, null, onClick);
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
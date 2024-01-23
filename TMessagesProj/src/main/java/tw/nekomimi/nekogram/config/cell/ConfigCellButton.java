package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.TextCell;
import tw.nekomimi.nekogram.config.CellGroup;

import java.util.function.Consumer;

public class ConfigCellButton extends AbstractConfigCell {
    private final Consumer<BaseFragment> onClick;
    private final String title;
    private String subtitle = null;
    private final String subtitleFallback;
    private boolean enabled = true;
    public TextCell cell;

    public ConfigCellButton(String title, String subtitleFallback, Consumer<BaseFragment> onClick) {
        this.title = title;
        this.subtitleFallback = subtitleFallback;
        this.onClick = onClick;
    }

    public ConfigCellButton(String title, Consumer<BaseFragment> onClick) {
        this(title, null, onClick);
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT;
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
        if (subtitle == null || subtitle.isBlank()) return;
        this.subtitle = subtitle;
        cell.setSubtitle(subtitle);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        TextCell cell = (TextCell) holder.itemView;
        this.cell = cell;
        cell.setText(title, true);
        if (subtitle == null || subtitle.isBlank()) subtitle = subtitleFallback;
        if (subtitle != null) cell.setSubtitle(subtitle);
        cell.setEnabled(enabled);
    }

    public void onClick(BaseFragment fragment) {
        if (enabled && onClick != null) onClick.accept(fragment);
    }
}
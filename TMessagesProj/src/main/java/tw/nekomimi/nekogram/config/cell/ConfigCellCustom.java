package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;

public class ConfigCellCustom extends AbstractConfigCell {
    public final int type;
    public final boolean enabled;

    public ConfigCellCustom(int type, boolean enabled) {
        this.type = type;
        this.enabled = enabled;
    }

    public int getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        // Not Used
    }
}

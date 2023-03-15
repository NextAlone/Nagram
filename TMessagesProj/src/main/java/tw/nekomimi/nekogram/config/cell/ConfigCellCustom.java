package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;

public class ConfigCellCustom extends AbstractConfigCell {

    public static final int CUSTOM_ITEM_ProfilePreview = 999;
    public static final int CUSTOM_ITEM_StickerSize = 998;
    public static final int CUSTOM_ITEM_CharBlurAlpha = 997;

    public final int type;
    public boolean enabled;

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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        // Not Used
    }
}

package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;

import tw.nekomimi.nekogram.config.ConfigItem;

public class ConfigCellCustom extends AbstractConfigCell {

    public static final int CUSTOM_ITEM_StickerSize = 998;
    public static final int CUSTOM_ITEM_CharBlurAlpha = 997;
    public static final int CUSTOM_ITEM_EmojiSet = 996;
    public static final int CUSTOM_ITEM_LiquidGlassAngle = 995;
    public static final int CUSTOM_ITEM_LiquidGlassIntensity = 994;
    public static final int CUSTOM_ITEM_MessagesPreview = 993;
    public static final int CUSTOM_ITEM_InputAnimationStrength = 992;
    public static final int CUSTOM_ITEM_FilterTabsPreview = 991;
    public static final int CUSTOM_ITEM_StickerShape = 990;
    public static final int CUSTOM_ITEM_DoubleTap = 989;

    public final int type;
    public boolean enabled;
    private final String key;
    private final ConfigItem bindConfig;

    public ConfigCellCustom(String key, int type, boolean enabled) {
        this(key, null, type, enabled);
    }

    public ConfigCellCustom(String key, ConfigItem bindConfig, int type, boolean enabled) {
        this.key = key;
        this.bindConfig = bindConfig;
        this.type = type;
        this.enabled = enabled;
    }

    public int getType() {
        return type;
    }

    public String getKey() {
        return this.key;
    }

    public ConfigItem getBindConfig() {
        return bindConfig;
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

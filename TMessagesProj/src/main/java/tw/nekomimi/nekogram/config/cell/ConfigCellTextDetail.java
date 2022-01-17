package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import cn.hutool.core.util.StrUtil;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;

public class ConfigCellTextDetail extends AbstractConfigCell {
    private final ConfigItem bindConfig;
    private final String title;
    private final String hint;
    public final RecyclerListView.OnItemClickListener onItemClickListener;

    public ConfigCellTextDetail(ConfigItem bind, RecyclerListView.OnItemClickListener onItemClickListener, String hint) {
        this.bindConfig = bind;
        this.title = LocaleController.getString(bindConfig.getKey());
        this.hint = hint == null ? "" : hint;
        this.onItemClickListener = onItemClickListener;
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT_DETAIL;
    }

    public boolean isEnabled() {
        return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        TextDetailSettingsCell cell = (TextDetailSettingsCell) holder.itemView;
        cell.setTextAndValue(title, StrUtil.isNotBlank(bindConfig.String()) ? bindConfig.String() : hint, cellGroup.needSetDivider(this));
    }
}

package tw.nekomimi.nkmr.cells;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import cn.hutool.core.util.StrUtil;
import tw.nekomimi.nkmr.CellGroup;
import tw.nekomimi.nkmr.ConfigItem;
import tw.nekomimi.nkmr.NekomuraConfig;
import tw.nekomimi.nkmr.NekomuraTGCell;

public class NekomuraTGTextDetail extends NekomuraTGCell {
    private final ConfigItem bindConfig;
    private final String title;
    private final String hint;
    public final RecyclerListView.OnItemClickListener onItemClickListener;

    public NekomuraTGTextDetail(ConfigItem bind, RecyclerListView.OnItemClickListener onItemClickListener, String hint) {
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

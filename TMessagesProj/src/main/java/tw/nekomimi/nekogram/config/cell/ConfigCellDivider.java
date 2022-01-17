package tw.nekomimi.nekogram.config.cell;

import androidx.recyclerview.widget.RecyclerView;

import tw.nekomimi.nekogram.config.CellGroup;

public class ConfigCellDivider extends AbstractConfigCell {

    public int getType() {
        return CellGroup.ITEM_TYPE_DIVIDER;
    }

    public boolean isEnabled() {
        return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
    }
}

package tw.nekomimi.nkmr.cells;

import androidx.recyclerview.widget.RecyclerView;

import tw.nekomimi.nkmr.CellGroup;
import tw.nekomimi.nkmr.NekomuraTGCell;

public class NekomuraTGDivider extends NekomuraTGCell {

    public int getType() {
        return CellGroup.ITEM_TYPE_DIVIDER;
    }

    public boolean isEnabled() {
        return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
    }
}

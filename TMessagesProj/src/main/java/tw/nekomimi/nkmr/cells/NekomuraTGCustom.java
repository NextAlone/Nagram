package tw.nekomimi.nkmr.cells;

import androidx.recyclerview.widget.RecyclerView;

import tw.nekomimi.nkmr.CellGroup;
import tw.nekomimi.nkmr.NekomuraTGCell;

public class NekomuraTGCustom extends NekomuraTGCell {
    public final int type;
    public final boolean enabled;

    public NekomuraTGCustom(int type, boolean enabled) {
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

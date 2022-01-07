package tw.nekomimi.nkmr.cells;

import androidx.recyclerview.widget.RecyclerView;

public class NekomuraTGCustom extends AbstractCell {
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

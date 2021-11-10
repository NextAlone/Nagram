package tw.nekomimi.nkmr;

import androidx.recyclerview.widget.RecyclerView;

public abstract class NekomuraTGCell {
    protected CellGroup cellGroup;

    public void bindCellGroup(CellGroup cellGroup) {
        this.cellGroup = cellGroup;
    }

    public abstract int getType();

    public abstract boolean isEnabled();

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder);
}
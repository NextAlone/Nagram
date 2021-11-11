package tw.nekomimi.nkmr.cells;

import androidx.recyclerview.widget.RecyclerView;

import tw.nekomimi.nkmr.CellGroup;

public abstract class AbstractCell {
    // can not be null!
    protected CellGroup cellGroup;

    // called by CellGroup.java
    public void bindCellGroup(CellGroup cellGroup) {
        this.cellGroup = cellGroup;
    }

    public abstract int getType();

    public abstract boolean isEnabled();

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder);
}
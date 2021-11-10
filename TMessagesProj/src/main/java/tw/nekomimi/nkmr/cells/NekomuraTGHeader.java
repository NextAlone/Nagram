package tw.nekomimi.nkmr.cells;

import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import org.telegram.ui.Cells.HeaderCell;

import tw.nekomimi.nkmr.CellGroup;
import tw.nekomimi.nkmr.NekomuraTGCell;

public class NekomuraTGHeader extends NekomuraTGCell {
    private final String title;

    public NekomuraTGHeader(String title) {
        this.title = title;
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_HEADER;
    }

    public boolean isEnabled() {
        return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        HeaderCell headerCell = (HeaderCell) holder.itemView;
        headerCell.setText(title);
    }
}

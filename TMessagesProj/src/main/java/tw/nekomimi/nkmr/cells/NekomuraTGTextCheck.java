package tw.nekomimi.nkmr.cells;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.TextCheckCell;

import tw.nekomimi.nkmr.CellGroup;
import tw.nekomimi.nkmr.ConfigItem;

public class NekomuraTGTextCheck extends AbstractCell {
    private final ConfigItem bindConfig;
    private final String title;
    private final String subtitle;
    public boolean enabled = true;
    public TextCheckCell cell; //TODO getCell() in NekomuraTGCell

    public NekomuraTGTextCheck(ConfigItem bind) {
        this.bindConfig = bind;
        this.title = LocaleController.getString(bindConfig.getKey());
        this.subtitle = null;
    }

    public NekomuraTGTextCheck(ConfigItem bind, String subtitle) {
        this.bindConfig = bind;
        this.title = LocaleController.getString(bindConfig.getKey());
        this.subtitle = subtitle;
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT_CHECK;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        TextCheckCell cell = (TextCheckCell) holder.itemView;
        this.cell = cell;
        if (subtitle == null) {
            cell.setTextAndCheck(title, bindConfig.Bool(), cellGroup.needSetDivider(this));
        } else {
            cell.setTextAndValueAndCheck(title, subtitle, bindConfig.Bool(), true, cellGroup.needSetDivider(this));
        }
        cell.setEnabled(enabled, null);
    }

    public void onClick(TextCheckCell cell) {
        if (!enabled) return;

        boolean newV = bindConfig.toggleConfigBool();
        cell.setChecked(newV);

        cellGroup.runCallback(bindConfig.getKey(), newV);
    }
}


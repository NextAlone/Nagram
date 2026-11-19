package tw.nekomimi.nekogram.config.cell;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextSettingsCell;

import kotlin.Unit;
import tw.nekomimi.nekogram.ui.PopupBuilder;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;

// TextSettingsCell, select from a list
// Can be used without select list（custom）
public class ConfigCellSelectBox extends AbstractConfigCell {
    private final ConfigItem bindConfig;
    private final String[] selectList; // split by \n
    private final String title;
    private final Runnable onClickCustom;
    private Context ctxCustom;
    private final String key;

    // default: customTitle=null customOnClick=null
    public ConfigCellSelectBox(String key, ConfigItem bind, Object selectList_s, Runnable customOnClick) {
        this.bindConfig = bind;
        String key1 = key;
        if (key == null) {
            key1 = bindConfig.getKey();
        }
        this.key = key1;
        if (selectList_s == null) {
            this.selectList = null;
        } else if (selectList_s instanceof String) {
            this.selectList = ((String) selectList_s).split("\n");
        } else if (selectList_s instanceof String[]) {
            this.selectList = (String[]) selectList_s;
        } else {
            this.selectList = null;
        }
        title = LocaleController.getString(this.key);
        this.onClickCustom = customOnClick;
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL;
    }

    public ConfigItem getBindConfig() {
        return this.bindConfig;
    }

    public String getKey() {
        return this.key;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        TextSettingsCell cell = (TextSettingsCell) holder.itemView;
        String valueText = "";
        if (selectList != null && bindConfig.Int() < selectList.length) {
            valueText = selectList[bindConfig.Int()];
        }
        cell.setTextAndValue(title, valueText, cellGroup.needSetDivider(this));
    }

    public void onClickWithDialog(Context ctx) {
        ctxCustom = ctx;
        onClick(null);
    }

    public void onClick(View view) {
        if (onClickCustom != null) {
            try {
                onClickCustom.run();
            } catch (Exception e) {
            }
            return;
        }

        Context context = ctxCustom != null ? ctxCustom : cellGroup.thisFragment.getParentActivity();
        if (context == null) {
            return;
        }

        PopupBuilder builder = view != null ? new PopupBuilder(view) : new PopupBuilder(context);
        builder.setTitle(this.title);
        if (bindConfig != null) {
            builder.setSelectedItem(this.bindConfig.Int());
        }

        builder.setItems(this.selectList, (i, __) -> {
            if (bindConfig != null) {
                bindConfig.setConfigInt(i);
            }

            if (cellGroup.listAdapter != null)
                cellGroup.listAdapter.notifyItemChanged(cellGroup.rows.indexOf(this));
            if (cellGroup.thisFragment != null)
                cellGroup.thisFragment.getParentLayout().rebuildAllFragmentViews(false, false);

            if (bindConfig != null) {
                cellGroup.runCallback(bindConfig.getKey(), i);
            }

            return Unit.INSTANCE;
        });
        builder.show();
    }
}
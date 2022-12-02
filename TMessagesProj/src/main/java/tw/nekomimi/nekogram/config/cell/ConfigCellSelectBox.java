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

    // default: customTitle=null customOnClick=null
    public ConfigCellSelectBox(String customTitle, ConfigItem bind, Object selectList_s, Runnable customOnClick) {
        this.bindConfig = bind;
        if (selectList_s == null) {
            this.selectList = null;
        } else if (selectList_s instanceof String) {
            this.selectList = ((String) selectList_s).split("\n");
        } else if (selectList_s instanceof String[]) {
            this.selectList = (String[]) selectList_s;
        } else {
            this.selectList = null;
        }
        if (customTitle == null) {
            title = LocaleController.getString(bindConfig.getKey());
        } else {
            title = customTitle;
        }
        this.onClickCustom = customOnClick;
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL;
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
        Context context = ctxCustom != null ? ctxCustom : cellGroup.thisFragment.getParentActivity();
        if (context == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context); //TODO Replace with pop-up menu
        builder.setTitle(LocaleController.getString(bindConfig.getKey()));
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        builder.setView(linearLayout);

        for (int i = 0; i < selectList.length; i++) {
            RadioColorCell cell = new RadioColorCell(context);
            cell.setPadding(AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4), 0);
            cell.setTag(i);
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(selectList[i], bindConfig.Int() == i);
            linearLayout.addView(cell);
            cell.setOnClickListener(v -> {
                Integer which = (Integer) v.getTag();
                bindConfig.setConfigInt(which);

                if (cellGroup.listAdapter != null)
                    cellGroup.listAdapter.notifyItemChanged(cellGroup.rows.indexOf(this));
                builder.getDismissRunnable().run();
                if (cellGroup.thisFragment != null)
                    cellGroup.thisFragment.getParentLayout().rebuildAllFragmentViews(false, false);

                cellGroup.runCallback(bindConfig.getKey(), which);
            });
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        if (ctxCustom == null) {
            cellGroup.thisFragment.showDialog(builder.create());
        } else {
            builder.show();
        }
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

        PopupBuilder builder = new PopupBuilder(view);

        builder.setItems(this.selectList, (i, __) -> {
            bindConfig.setConfigInt(i);

            if (cellGroup.listAdapter != null)
                cellGroup.listAdapter.notifyItemChanged(cellGroup.rows.indexOf(this));
            if (cellGroup.thisFragment != null)
                cellGroup.thisFragment.getParentLayout().rebuildAllFragmentViews(false, false);

            cellGroup.runCallback(bindConfig.getKey(), i);

            return Unit.INSTANCE;
        });
        builder.show();


    }
}
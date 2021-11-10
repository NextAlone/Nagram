package tw.nekomimi.nkmr;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

import cn.hutool.core.util.StrUtil;
import tw.nekomimi.nekogram.settings.NekoGeneralSettingsActivity;
import tw.nekomimi.nkmr.cells.NekomuraTGDivider;

public class CellGroup {
    public static final int ITEM_TYPE_DIVIDER = 1;
    public static final int ITEM_TYPE_TEXT_SETTINGS_CELL = 2;
    public static final int ITEM_TYPE_TEXT_CHECK = 3;
    public static final int ITEM_TYPE_HEADER = 4;
    public static final int ITEM_TYPE_TEXT_DETAIL = 5;
    public static final int ITEM_TYPE_TEXT = 6;

    public BaseFragment thisFragment;
    public RecyclerListView listView;
    public RecyclerListView.SelectionAdapter listAdapter;
    public ArrayList<NekomuraTGCell> rows;

    public CallBackSettingsChanged callBackSettingsChanged;

    public CellGroup(BaseFragment thisFragment) {
        this.thisFragment = thisFragment;
        this.rows = new ArrayList<>();
    }

    public void setListAdapter(RecyclerListView lv, RecyclerListView.SelectionAdapter la) {
        this.listView = lv;
        this.listAdapter = la;
    }

    public NekomuraTGCell appendCell(NekomuraTGCell cell) {
        cell.bindCellGroup(this);
        this.rows.add(cell);
        return cell;
    }

    public NekomuraTGCell appendCell(NekomuraTGCell cell, boolean display) {
        cell.bindCellGroup(this);
        if (display)
            this.rows.add(cell);
        return cell;
    }

    public interface CallBackSettingsChanged {
        void run(String key, Object newValue);
    }

    public void runCallback(String key, Object newValue) {
        if (callBackSettingsChanged == null) return;
        try {
            callBackSettingsChanged.run(key, newValue);
        } catch (Exception e) {

        }
    }

    public interface OnBindViewHolder {
        void onBindViewHolder(RecyclerView.ViewHolder holder);
    }

    //Utils
    public static void hideItemFromRecyclerView(View cell, boolean hide) {
        if (cell == null) return;
        if (cell != null) return; //TODO hideItemFromRecyclerView
        ViewGroup.LayoutParams params = cell.getLayoutParams();
        if (hide) {
            cell.setVisibility(View.GONE);
            params.height = 0;
        } else {
            cell.setVisibility(View.VISIBLE);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        cell.setLayoutParams(params);
    }

    public boolean needSetDivider(NekomuraTGCell cell) {
        return !(rows.get(rows.indexOf(cell) + 1) instanceof NekomuraTGDivider);
    }

    //TG Cells


    //TextDetailSettingsCell


}
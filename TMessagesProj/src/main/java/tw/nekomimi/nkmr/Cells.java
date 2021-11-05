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

public class Cells {
    public static final int ITEM_TYPE_DIVIDER = 1;
    public static final int ITEM_TYPE_TEXT_SETTINGS_CELL = 2;
    public static final int ITEM_TYPE_TEXT_CHECK = 3;
    public static final int ITEM_TYPE_HEADER = 4;
    public static final int ITEM_TYPE_TEXT_DETAIL = 5;
    public static final int ITEM_TYPE_TEXT = 6;


    private BaseFragment thisFragment;
    private RecyclerListView listView;
    private RecyclerListView.SelectionAdapter listAdapter;
    private ArrayList<NekomuraTGCell> rows;

    public CallBackSettingsChanged callBackSettingsChanged;

    public Cells(BaseFragment thisFragment, ArrayList<NekomuraTGCell> rows) {
        this.thisFragment = thisFragment;
        this.rows = rows;
    }

    public void setListAdapter(RecyclerListView lv, RecyclerListView.SelectionAdapter la) {
        this.listView = lv;
        this.listAdapter = la;
    }

    public interface CallBackSettingsChanged {
        void run(String key, Object newValue);
    }

    private void runCallback(String key, Object newValue) {
        if (callBackSettingsChanged == null) return;
        try {
            callBackSettingsChanged.run(key, newValue);
        } catch (Exception e) {

        }
    }

    public interface OnBindViewHolder {
        void onBindViewHolder(RecyclerView.ViewHolder holder);
    }

    public interface NekomuraTGCell {
        int getType();

        boolean isEnabled();

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

    public class NekomuraTGDivider implements NekomuraTGCell {
        public int getType() {
            return ITEM_TYPE_DIVIDER;
        }

        public boolean isEnabled() {
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        }
    }

    public class NekomuraTGCustom implements NekomuraTGCell {
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

    public class NekomuraTGHeader implements NekomuraTGCell {
        private final String title;

        public NekomuraTGHeader(String title) {
            this.title = title;
        }

        public int getType() {
            return ITEM_TYPE_HEADER;
        }

        public boolean isEnabled() {
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
            HeaderCell headerCell = (HeaderCell) holder.itemView;
            headerCell.setText(title);
        }
    }

    //TextDetailSettingsCell
    public class NekomuraTGTextDetail implements NekomuraTGCell {
        private final NekomuraConfig.ConfigItem bindConfig;
        private final String title;
        private final String hint;
        public final RecyclerListView.OnItemClickListener onItemClickListener;

        public NekomuraTGTextDetail(NekomuraConfig.ConfigItem bind, RecyclerListView.OnItemClickListener onItemClickListener, String hint) {
            this.bindConfig = bind;
            this.title = LocaleController.getString(bindConfig.getKey());
            this.hint = hint == null ? "" : hint;
            this.onItemClickListener = onItemClickListener;
        }

        public int getType() {
            return ITEM_TYPE_TEXT_DETAIL;
        }

        public boolean isEnabled() {
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
            TextDetailSettingsCell cell = (TextDetailSettingsCell) holder.itemView;
            cell.setTextAndValue(title, StrUtil.isNotBlank(bindConfig.String()) ? bindConfig.String() : hint, needSetDivider(this));
        }
    }

    public class NekomuraTGTextCheck implements NekomuraTGCell {
        private final NekomuraConfig.ConfigItem bindConfig;
        private final String title;
        private final String subtitle;
        public boolean enabled = true;
        public TextCheckCell cell; //TODO getCell() in NekomuraTGCell

        public NekomuraTGTextCheck(NekomuraConfig.ConfigItem bind) {
            this.bindConfig = bind;
            this.title = LocaleController.getString(bindConfig.getKey());
            this.subtitle = null;
        }

        public NekomuraTGTextCheck(NekomuraConfig.ConfigItem bind, String subtitle) {
            this.bindConfig = bind;
            this.title = LocaleController.getString(bindConfig.getKey());
            this.subtitle = subtitle;
        }

        public int getType() {
            return ITEM_TYPE_TEXT_CHECK;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
            TextCheckCell cell = (TextCheckCell) holder.itemView;
            this.cell = cell;
            if (subtitle == null) {
                cell.setTextAndCheck(title, bindConfig.Bool(), needSetDivider(this));
            } else {
                cell.setTextAndValueAndCheck(title, subtitle, bindConfig.Bool(), true, needSetDivider(this));
            }
            cell.setEnabled(enabled, null);
        }

        public void onClick(TextCheckCell cell) {
            if (!enabled) return;

            boolean newV = bindConfig.toggleConfigBool();
            cell.setChecked(newV);

            runCallback(bindConfig.getKey(), newV);
        }
    }

    // TextSettingsCell, select from a list
    // Can be used without select list（custom）
    public class NekomuraTGSelectBox implements NekomuraTGCell {
        private final NekomuraConfig.ConfigItem bindConfig;
        private final String[] selectList; // split by \n
        private final String title;
        private Runnable onClickCustom;


        // default: customTitle=null customOnClick=null
        public NekomuraTGSelectBox(String customTitle, NekomuraConfig.ConfigItem bind, Object selectList_s, Runnable customOnClick) {
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
            return ITEM_TYPE_TEXT_SETTINGS_CELL;
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
            cell.setTextAndValue(title, valueText, needSetDivider(this));
        }

        public void onClick() {
            if (onClickCustom != null) {
                try {
                    onClickCustom.run();
                } catch (Exception e) {
                }
                return;
            }

            Context context = thisFragment.getParentActivity();
            if (context == null) {
                return;
            }

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

                    listAdapter.notifyItemChanged(rows.indexOf(this));
                    builder.getDismissRunnable().run();
                    thisFragment.parentLayout.rebuildAllFragmentViews(false, false);

                    runCallback(bindConfig.getKey(), which);
                });
            }
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            thisFragment.showDialog(builder.create());
        }
    }

    public class NekomuraTGTextInput implements NekomuraTGCell {
        private final NekomuraConfig.ConfigItem bindConfig;
        private final String hint;
        private final String title;
        private Runnable onClickCustom;


        // default: customTitle=null customOnClick=null
        public NekomuraTGTextInput(String customTitle, NekomuraConfig.ConfigItem bind, String hint, Runnable customOnClick) {
            this.bindConfig = bind;
            if (hint == null) {
                this.hint = "";
            } else {
                this.hint = hint;
            }
            if (customTitle == null) {
                title = LocaleController.getString(bindConfig.getKey());
            } else {
                title = customTitle;
            }
            this.onClickCustom = customOnClick;
        }

        public int getType() {
            return ITEM_TYPE_TEXT_SETTINGS_CELL;
        }

        public boolean isEnabled() {
            return true;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
            TextSettingsCell cell = (TextSettingsCell) holder.itemView;
            cell.setTextAndValue(title, bindConfig.String(), needSetDivider(this));
        }

        public void onClick() {
            if (onClickCustom != null) {
                try {
                    onClickCustom.run();
                } catch (Exception e) {
                }
                return;
            }

            Context context = thisFragment.getParentActivity();
            if (context == null) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString(bindConfig.getKey()));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            EditTextBoldCursor editText = new EditTextBoldCursor(context);
            editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            editText.setHint(hint);
            editText.setText(bindConfig.String());
            linearLayout.addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(10), 0));

            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (d, v) -> {
                String newV = editText.getText().toString();
                bindConfig.setConfigString(newV);

                //refresh
                listAdapter.notifyItemChanged(rows.indexOf(this));
                builder.getDismissRunnable().run();
                thisFragment.parentLayout.rebuildAllFragmentViews(false, false);

                runCallback(bindConfig.getKey(), newV);
            });
            builder.setView(linearLayout);
            thisFragment.showDialog(builder.create());
        }
    }
}
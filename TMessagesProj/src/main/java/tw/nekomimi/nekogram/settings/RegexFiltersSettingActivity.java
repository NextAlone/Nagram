/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package tw.nekomimi.nekogram.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;

import java.util.ArrayList;

import tw.nekomimi.nekogram.helpers.AyuFilter;
import tw.nekomimi.nekogram.ui.RegexFilterEditActivity;
import tw.nekomimi.nekogram.ui.RegexFilterPopup;


public class RegexFiltersSettingActivity extends BaseNekoSettingsActivity {

    private int filtersHeaderRow;
    private int headerDividerRow;
    private int addFilterBtnRow;
    // .. filters

    private long dialogId;

    public RegexFiltersSettingActivity() {
        dialogId = 0L;
    }

    public RegexFiltersSettingActivity(long dialogId) {
        this.dialogId = dialogId;
    }

    @Override
    protected void updateRows() {
        super.updateRows();

        filtersHeaderRow = rowCount++;
        headerDividerRow = rowCount++;

        addFilterBtnRow = rowCount++;

        var filters = AyuFilter.getRegexFilters();
        rowCount += filters.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        updateRows();

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position > addFilterBtnRow) {
            // clicked on filter
            if (dialogId == 0 && LocaleController.isRTL && x > AndroidUtilities.dp(76) || !LocaleController.isRTL && x < (view.getMeasuredWidth() - AndroidUtilities.dp(76))) {
                RegexFilterPopup.show(this, view, x, y, position - addFilterBtnRow - 1);
            } else {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                ArrayList<AyuFilter.FilterModel> filterModels = AyuFilter.getRegexFilters();
                AyuFilter.FilterModel filterModel = filterModels.get(position - addFilterBtnRow - 1);

                boolean enabled = !textCheckCell.isChecked();
                textCheckCell.setChecked(enabled);
                filterModel.setEnabled(enabled, dialogId);
                AyuFilter.saveFilter(filterModels);
            }
        } else if (position == addFilterBtnRow) {
            presentFragment(new RegexFilterEditActivity());
        }
    }

    @Override
    protected boolean onItemLongClick(View view, int position, float x, float y) {
        if (dialogId == 0 && position > addFilterBtnRow) {
            RegexFilterPopup.show(this, view, x, y, position - addFilterBtnRow - 1);
            return true;
        }
        return super.onItemLongClick(view, position, x, y);
    }

    @Override
    public BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    protected String getActionBarTitle() {
        return LocaleController.getString(R.string.RegexFilters);
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean payload) {
            switch (holder.getItemViewType()) {
                case TYPE_SHADOW:
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case TYPE_CHECK:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position > addFilterBtnRow) {
                        AyuFilter.FilterModel filterModel = AyuFilter.getRegexFilters().get(position - addFilterBtnRow - 1);
                        textCheckCell.setTextAndCheck(filterModel.regex, filterModel.isEnabled(dialogId), true);
                    }
                    break;
                case TYPE_TEXT:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == addFilterBtnRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.RegexFiltersAdd), R.drawable.msg_add, false);
                    }
                    break;
                case TYPE_HEADER:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == filtersHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.RegexFiltersHeader));
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (
                    position == headerDividerRow
            ) {
                return TYPE_SHADOW;
            } else if (
                    position == filtersHeaderRow
            ) {
                return TYPE_HEADER;
            } else if (position == addFilterBtnRow) {
                return TYPE_TEXT;
            }
            return TYPE_CHECK;
        }
    }
}

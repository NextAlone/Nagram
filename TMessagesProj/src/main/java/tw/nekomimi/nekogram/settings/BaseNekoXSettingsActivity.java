package tw.nekomimi.nekogram.settings;

import static tw.nekomimi.nekogram.settings.BaseNekoSettingsActivity.PARTIAL;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;
import tw.nekomimi.nekogram.config.cell.*;

public class BaseNekoXSettingsActivity extends BaseFragment {
    protected RecyclerListView.SelectionAdapter listAdapter;
    protected CellGroup cellGroup;
    protected BlurredRecyclerView listView;
    protected LinearLayoutManager layoutManager;
    protected UndoView tooltip;
    protected HashMap<String, Integer> rowMap = new HashMap<>(20);
    protected HashMap<Integer, String> rowMapReverse = new HashMap<>(20);
    protected HashMap<Integer, ConfigItem> rowConfigMapReverse = new HashMap<>(20);

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(getTitle());
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new BlurredRecyclerView(context);
        listView.setSections(true);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        actionBar.setAdaptiveBackground(listView);

        tooltip = new UndoView(context);
        frameLayout.addView(tooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));
        return fragmentView;
    }

    protected void updateRows() {
        if (listAdapter == null) {
            setCanNotChange();
            return;
        }
        ArrayList<AbstractConfigCell> rows = new ArrayList<>(cellGroup.rows);
        setCanNotChange();
        DiffUtil.calculateDiff(new ConfigCellDiffCallback(rows, cellGroup.rows)).dispatchUpdatesTo(listAdapter);
    }

    protected void setCanNotChange() {
    }

    public int getBaseGuid() {
        return 10000;
    }

    public int getDrawable() {
        return 0;
    }

    public String getTitle() {
        return "";
    }

    protected void addRowsToMap() {
        rowMap.clear();
        rowMapReverse.clear();
        rowConfigMapReverse.clear();
        String key;
        ConfigItem config;
        for (int i = 0; i < cellGroup.rows.size(); i++) {
            config = getBindConfig(cellGroup.rows.get(i));
            key = getRowKey(cellGroup.rows.get(i));
            if (key == null) key = String.valueOf(i);
            rowMap.put(key, i);
            rowMapReverse.put(i, key);
            rowConfigMapReverse.put(i, config);
        }
    }

    protected String getRowKey(int position) {
        if (rowMapReverse.containsKey(position)) {
            return rowMapReverse.get(position);
        }
        return String.valueOf(position);
    }

    protected String getRowValue(int position) {
        ConfigItem config = rowConfigMapReverse.get(position);
        if (config != null) return config.String();
        return null;
    }

    protected ConfigItem getBindConfig(AbstractConfigCell row) {
        if (row instanceof ConfigCellTextCheck) {
            return ((ConfigCellTextCheck) row).getBindConfig();
        } else if (row instanceof ConfigCellSelectBox) {
            return ((ConfigCellSelectBox) row).getBindConfig();
        } else if (row instanceof ConfigCellTextDetail) {
            return ((ConfigCellTextDetail) row).getBindConfig();
        } else if (row instanceof ConfigCellTextInput) {
            return ((ConfigCellTextInput) row).getBindConfig();
        } else if (row instanceof ConfigCellAutoTextCheck) {
            return ((ConfigCellAutoTextCheck) row).getBindConfig();
        }
        return null;
    }

    protected String getRowKey(AbstractConfigCell row) {
        if (row instanceof WithKey) {
            return ((WithKey) row).getKey();
        } else if (row instanceof ConfigCellTextCheck) {
            return ((ConfigCellTextCheck) row).getKey();
        } else if (row instanceof ConfigCellSelectBox) {
            return ((ConfigCellSelectBox) row).getKey();
        } else if (row instanceof ConfigCellTextDetail) {
            return ((ConfigCellTextDetail) row).getKey();
        } else if (row instanceof ConfigCellTextInput) {
            return ((ConfigCellTextInput) row).getKey();
        } else if (row instanceof ConfigCellCustom) {
            return ((ConfigCellCustom) row).getKey();
        } else if (row instanceof ConfigCellAutoTextCheck) {
            return ((ConfigCellAutoTextCheck) row).getKey();
        }
        return null;
    }

    protected int getRowPositionByKey(String key) {
        int position = -1;
        Integer temp = rowMap.get(key);
        if (temp != null) position = temp;
        return position;
    }

    protected void createLongClickDialog(Context context, BaseFragment fragment, String prefix,  int position) {
        String key = getRowKey(position);
        String value = getRowValue(position);
        ArrayList<CharSequence> itemsArray = new ArrayList<>();
        itemsArray.add(LocaleController.getString(R.string.CopyLink));
        if (value != null) {
            itemsArray.add(LocaleController.getString(R.string.BackupSettings));
        }
        CharSequence[] items = itemsArray.toArray(new CharSequence[0]);
        showDialog(new AlertDialog.Builder(context)
                .setItems(
                        items,
                        (dialogInterface, i) -> {
                            switch (i) {
                                case 0:
                                    AndroidUtilities.addToClipboard(String.format(Locale.getDefault(), BaseNekoSettingsActivity.settingsPrefix, getMessagesController().linkPrefix, prefix, key));
                                    BulletinFactory.of(fragment).createCopyLinkBulletin().show();
                                    break;
                                case 1:
                                    AndroidUtilities.addToClipboard(String.format(Locale.getDefault(), BaseNekoSettingsActivity.settingsPrefix + "&v=%s", getMessagesController().linkPrefix, prefix, key, value));
                                    BulletinFactory.of(fragment).createCopyLinkBulletin().show();
                                    break;
                            }
                        })
                .create());
    }

    public void importToRow(String key, String value, Runnable unknown) {
        int position = -1;
        try {
            position = Integer.parseInt(key);
        } catch (NumberFormatException exception) {
            position = getRowPositionByKey(key);
        }
        ConfigItem config = rowConfigMapReverse.get(position);
        Context context = getParentActivity();
        if (context != null && config != null) {
            Object new_value = config.checkConfigFromString(value);
            if (new_value == null) {
                scrollToRow(key, unknown);
                return;
            }
            var builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString(R.string.ImportSettings));
            builder.setMessage(LocaleController.getString(R.string.ImportSettingsAlert));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), (dialogInter, i) -> scrollToRow(key, unknown));
            builder.setPositiveButton(LocaleController.getString(R.string.Import), (dialogInter, i) -> {
                config.changed(new_value);
                config.saveConfig();
                updateRows();
                listAdapter.notifyDataSetChanged();
                scrollToRow(key, unknown);
            });
            builder.show();
        } else {
            scrollToRow(key, unknown);
        }
    }

    public void scrollToRow(String key, Runnable unknown) {
        int position = -1;
        try {
            position = Integer.parseInt(key);
        } catch (NumberFormatException exception) {
            Integer temp = rowMap.get(key);
            if (temp != null) position = temp;
        }
        if (position > -1 && listView != null && layoutManager != null) {
            int finalPosition = position;
            listView.highlightRow(() -> {
                layoutManager.scrollToPositionWithOffset(finalPosition, AndroidUtilities.dp(60));
                return finalPosition;
            });
        } else if (unknown != null) {
            unknown.run();
        }
    }

    public HashMap<Integer, String> getRowMapReverse() {
        return rowMapReverse;
    }

    public static class ConfigCellDiffCallback extends DiffUtil.Callback {
        private final List<AbstractConfigCell> oldList;
        private final List<AbstractConfigCell> newList;

        ConfigCellDiffCallback(List<AbstractConfigCell> oldList, List<AbstractConfigCell> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // 使用对象引用比较，因为每个配置行都是唯一的实例
            return oldList.get(oldItemPosition) == newList.get(newItemPosition);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            // 配置行内容不变，只需要比较对象引用
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }
    }

    protected abstract class BaseListAdapter extends RecyclerListView.SelectionAdapter {

        protected final Context mContext;

        public BaseListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return cellGroup.rows.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            AbstractConfigCell a = cellGroup.rows.get(position);
            if (a != null) {
                return a.isEnabled();
            }
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            AbstractConfigCell a = cellGroup.rows.get(position);
            if (a != null) {
                return a.getType();
            }
            return CellGroup.ITEM_TYPE_TEXT_DETAIL;
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean partial, boolean divider) {

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            var partial = PARTIAL.equals(holder.getPayload());
            var top = position > 0;
            var bottom = position < getItemCount() - 1;
            var type = holder.getItemViewType();
            var nextType = position < getItemCount() - 1 ? getItemViewType(position + 1) : -1;
            var divider = nextType != -1 && nextType != CellGroup.ITEM_TYPE_DIVIDER && nextType != CellGroup.ITEM_TYPE_TEXT;
            if (type == CellGroup.ITEM_TYPE_DIVIDER) {
                ShadowSectionCell shadowCell = (ShadowSectionCell) holder.itemView;
                shadowCell.setTopBottom(top, bottom);
                return;
            }
            onBindViewHolder(holder, position, partial, divider);
        }

        public View onCreateViewHolderView(int viewType) {
            View view = null;
            switch (viewType) {
                case CellGroup.ITEM_TYPE_DIVIDER:
                    view = new ShadowSectionCell(mContext);
                    break;
                case CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL:
                    view = new TextSettingsCell(mContext);
                    break;
                case CellGroup.ITEM_TYPE_TEXT_CHECK:
                    view = new TextCheckCell(mContext);
                    break;
                case CellGroup.ITEM_TYPE_HEADER:
                    view = new HeaderCell(mContext);
                    break;
                case CellGroup.ITEM_TYPE_TEXT_DETAIL:
                    view = new TextDetailSettingsCell(mContext);
                    break;
                case CellGroup.ITEM_TYPE_TEXT:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
            }
            return view;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = onCreateViewHolderView(viewType);

            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

            return new RecyclerListView.Holder(view);
        }
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }
    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        listView.setPadding(0, 0, 0, bottom);
        listView.setClipToPadding(false);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tooltip.getLayoutParams();
        layoutParams.setMargins(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), AndroidUtilities.dp(8) + bottom);
        tooltip.setLayoutParams(layoutParams);
    }
}

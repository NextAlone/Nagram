package tw.nekomimi.nkmr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

@SuppressLint("RtlHardcoded")
public class NekomuraSettingsFragment extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private ArrayList<NekomuraTGCell> rows = new ArrayList<>();

    //添加设置行

    private final NekomuraTGCell header1 = addNekomuraTGCell(new NekomuraTGHeader(LocaleController.getString("General")));
    private final NekomuraTGCell largeAvatarInDrawer = addNekomuraTGCell(new NekomuraTGTextSettings(null, NekomuraConfig.largeAvatarInDrawer, LocaleController.getString("valuesLargeAvatarInDrawer"), null));
    private final NekomuraTGCell unreadBadgeOnBackButton = addNekomuraTGCell(new NekomuraTGTextCheck(NekomuraConfig.unreadBadgeOnBackButton));
    private final NekomuraTGCell divider1 = addNekomuraTGCell(new NekomuraTGDivider());

    private static final int ITEM_TYPE_DIVIDER = 1;
    private static final int ITEM_TYPE_TEXT_SETTINGS = 2; //可以在右边设置文字
    private static final int ITEM_TYPE_TEXT_CHECK = 3;
    private static final int ITEM_TYPE_HEADER = 4;
    private static final int ITEM_TYPE_TEXT_DETAIL = 6;
    private static final int ITEM_TYPE_TEXT = 7;

    public NekomuraTGCell addNekomuraTGCell(NekomuraTGCell a) {
        rows.add(a);
        return a;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows();

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("NekomuraSettings", R.string.NekomuraSettings));

        actionBar.setOccupyStatusBar(!AndroidUtilities.isTablet());
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setAdapter(listAdapter);

        // 上面是各种窗口初始化

        // 设置回调事件。。。
        listView.setOnItemClickListener((view, position, x, y) -> {
            NekomuraTGCell a = rows.get(position);
            if (a instanceof NekomuraTGTextCheck) {
                ((NekomuraTGTextCheck) a).onClick((TextCheckCell) view);
            } else if (a instanceof NekomuraTGTextSettings) {
                ((NekomuraTGTextSettings) a).onClick();
            }
        });
        listView.setOnItemLongClickListener((view, position) -> {
            return true;
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateRows() {
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        return themeDescriptions;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // onBindViewHolder 一般设置文字，是否打勾什么的
            NekomuraTGCell a = rows.get(position);
            if (a != null) {
                a.onBindViewHolder(holder);
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            NekomuraTGCell a = rows.get(position);
            if (a != null) {
                return a.isEnabled();
            }
            return true;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case ITEM_TYPE_DIVIDER:
                    view = new ShadowSectionCell(mContext);
                    break;
                case ITEM_TYPE_TEXT_SETTINGS:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case ITEM_TYPE_TEXT_CHECK:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case ITEM_TYPE_HEADER:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case ITEM_TYPE_TEXT_DETAIL:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case ITEM_TYPE_TEXT:
                    view = new TextInfoPrivacyCell(mContext);
                    // view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            NekomuraTGCell a = rows.get(position);
            if (a != null) {
                return a.getType();
            }
            return 6;
        }
    }

    //设置对象

    public interface NekomuraTGCell {
        int getType();

        boolean isEnabled();

        void onBindViewHolder(RecyclerView.ViewHolder holder);
    }

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
            //还有特殊情况哦
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
            HeaderCell headerCell = (HeaderCell) holder.itemView;
            headerCell.setText(title);
        }
    }

    public class NekomuraTGTextCheck implements NekomuraTGCell {
        private final NekomuraConfig.ConfigItem bindConfig;
        private final String title;


        public NekomuraTGTextCheck(NekomuraConfig.ConfigItem bind) {
            this.bindConfig = bind;
            this.title = LocaleController.getString(bindConfig.getKey());
        }

        public NekomuraTGTextCheck(String customTitle, NekomuraConfig.ConfigItem bind) {
            this.bindConfig = bind;
            if (customTitle == null) {
                title = LocaleController.getString(bindConfig.getKey());
            } else {
                title = customTitle;
            }
        }

        public int getType() {
            return ITEM_TYPE_TEXT_CHECK;
        }

        public boolean isEnabled() {
            return true;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
            TextCheckCell cell = (TextCheckCell) holder.itemView;
            cell.setTextAndCheck(title, bindConfig.Bool(), !(rows.get(rows.indexOf(this) + 1) instanceof NekomuraTGDivider));
        }

        public void onClick(TextCheckCell cell) {
            cell.setChecked(bindConfig.toggleConfigBool());
        }
    }

    public class NekomuraTGTextSettings implements NekomuraTGCell {
        private final NekomuraConfig.ConfigItem bindConfig;
        private final String[] selectList;//实际上是int类型的设置
        private final String title;
        private Runnable onClickCustom;


        //不需要的custom就填null
        public NekomuraTGTextSettings(String customTitle, NekomuraConfig.ConfigItem bind, String selectList_s, Runnable customOnClick) {
            this.bindConfig = bind;
            if (selectList_s == null) {
                this.selectList = null;
            } else {
                this.selectList = selectList_s.split("\n");
            }
            if (customTitle == null) {
                title = LocaleController.getString(bindConfig.getKey());
            } else {
                title = customTitle;
            }
            this.onClickCustom = customOnClick;
        }

        public int getType() {
            return ITEM_TYPE_TEXT_SETTINGS;
        }

        public boolean isEnabled() {
            return true;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder) {
            TextSettingsCell cell = (TextSettingsCell) holder.itemView;
            cell.setTextAndValue(title, selectList == null ? "" : selectList[bindConfig.Int()], !(rows.get(rows.indexOf(this) + 1) instanceof NekomuraTGDivider));
        }

        public void onClick() {
            if (onClickCustom != null) {
                try {
                    onClickCustom.run();
                } catch (Exception e) {
                }
                return;
            }

            Context context = getParentActivity();
            if (context == null) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                    parentLayout.rebuildAllFragmentViews(false, false);
                });
            }
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }
}
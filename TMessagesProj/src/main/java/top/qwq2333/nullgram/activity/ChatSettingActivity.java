/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.core.text.HtmlCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ActionBar.BaseFragment;

@SuppressLint("NotifyDataSetChanged")
public class ChatSettingActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows(true);

        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("Chat", R.string.Chat));

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

        listAdapter = new ChatSettingActivity.ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);
        ((DefaultItemAnimator) listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener((view, position, x, y) -> {
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

    private void updateRows(boolean notify) {
        rowCount = 0;

        if (notify && listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(
            new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR,
                new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class,
                    HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class},
                null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(
            new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null,
                null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null,
                null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(
            new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null,
                null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null,
                null, Theme.key_avatar_actionBarIconBlue));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null,
                null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null,
                null, null, Theme.key_avatar_actionBarSelectorBlue));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null,
                null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null,
                null, Theme.key_actionBarDefaultSubmenuItem));

        themeDescriptions.add(
            new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null,
                Theme.key_listSelector));

        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null,
                null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER,
            new Class[]{ShadowSectionCell.class}, null, null, null,
            Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class},
            new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class},
            new String[]{"valueTextView"}, null, null, null,
            Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                new String[]{"textView"}, null, null, null,
                Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                new String[]{"valueTextView"}, null, null, null,
                Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
            new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
            new String[]{"valueTextView"}, null, null, null,
            Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
            new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
            new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class},
            new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class},
                new String[]{"textView"}, null, null, null,
                Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class},
                new String[]{"valueTextView"}, null, null, null,
                Theme.key_windowBackgroundWhiteGrayText2));

        return themeDescriptions;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 2 || type == 3;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    holder.itemView.setBackground(
                        Theme.getThemedDrawable(mContext, R.drawable.greydivider,
                            Theme.key_windowBackgroundGrayShadow));
                    break;
                }
                case 2: {
                    break;
                }
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(
                new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return 2;
        }
    }

}

package com.exteragram.messenger.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import com.exteragram.messenger.preferences.cells.InfoSettingsCell;
import com.exteragram.messenger.ExteraUtils;

public class MainPreferencesEntry extends BaseFragment {
    private int rowCount;
    private ListAdapter listAdapter;

    private int categoryHeaderRow;
    private int generalRow;
    private int appearanceRow;
    private int chatsRow;

    private int categoryDividerRow;
    private int aboutExteraDividerRow;

    private int infoHeaderRow;
    private int aboutExteraRow;
    private int sourceCodeRow;
    private int channelRow;
    private int groupRow;
    private int crowdinRow;
    private int infoDividerRow;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRowsId();
        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("Preferences", R.string.Preferences));
        actionBar.setAllowOverlayTitle(false);

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

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        RecyclerListView listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);

        if (listView.getItemAnimator() != null) {
            ((DefaultItemAnimator) listView.getItemAnimator()).setDelayAnimations(false);
        }

        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == aboutExteraRow) {
                Browser.openUrl(getParentActivity(), "https://extera.codes/");
            } else if (position == sourceCodeRow) {
                Browser.openUrl(getParentActivity(), "https://github.com/exteraSquad/exteraGram");
            } else if (position == channelRow) {
                MessagesController.getInstance(currentAccount).openByUserName(("exteragram"), this, 1);
            } else if (position == groupRow) {
                MessagesController.getInstance(currentAccount).openByUserName(("exterachat"), this, 1);
            } else if (position == crowdinRow) {
                Browser.openUrl(getParentActivity(), "https://crowdin.com/project/exteralocales");
            } else if (position == appearanceRow) {
                presentFragment(new AppearancePreferencesEntry());
            } else if (position == chatsRow) {
                presentFragment(new ChatsPreferencesEntry());
            } else if (position == generalRow) {
                presentFragment(new GeneralPreferencesEntry());
            }
        });
        return fragmentView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRowsId() {
        rowCount = 0;
        
        aboutExteraRow = rowCount++;
        aboutExteraDividerRow = rowCount++;

        categoryHeaderRow = rowCount++;
        generalRow = rowCount++;
        appearanceRow = rowCount++;
        chatsRow = rowCount++;
        categoryDividerRow = rowCount++;

        infoHeaderRow = rowCount++;
        channelRow = rowCount++;
        groupRow = rowCount++;
        crowdinRow = rowCount++;
        sourceCodeRow = rowCount++;
        infoDividerRow = rowCount++;

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    if (position == infoDividerRow) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                case 2:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == generalRow) {
                        textCell.setTextAndIcon(LocaleController.getString("General", R.string.General), R.drawable.msg_media, true);
                    } else if (position == appearanceRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Appearance", R.string.Appearance), R.drawable.msg_theme, true);
                    } else if (position == chatsRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Chats", R.string.Chats), R.drawable.msg_discussion, false);
                    } else if (position == channelRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("Channel", R.string.Channel), "@exteragram", R.drawable.msg_channel, true);
                    } else if (position == groupRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("Chats", R.string.Chats), "@exterachat", R.drawable.msg_markunread, true);
                    } else if (position == crowdinRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("Crowdin", R.string.Crowdin), "Crowdin", R.drawable.msg_translate, true);
                    } else if (position == sourceCodeRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("SourceCode", R.string.SourceCode), "Github", R.drawable.msg_delete, false);
                    }
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    break;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == categoryHeaderRow) {
                        headerCell.setText(LocaleController.getString("Categories", R.string.Categories));
                    } else if (position == infoHeaderRow){
                        headerCell.setText(LocaleController.getString("Links", R.string.Links));
                    }
                    break;
                case 4:
                    InfoSettingsCell textDetailCell = (InfoSettingsCell) holder.itemView;
                    break;
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 2;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 2:
                    view = new TextCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new InfoSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new ShadowSectionCell(mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }
        @Override
        public int getItemViewType(int position) {
            if (position == categoryDividerRow || position == aboutExteraDividerRow || position == infoDividerRow) {
                return 1;
            } else if (position == generalRow || position == appearanceRow || position == chatsRow ||
                      position == channelRow || position == groupRow || position == crowdinRow ||
                      position == sourceCodeRow) {
                return 2;
            } else if (position == infoHeaderRow || position == categoryHeaderRow) {
                return 3;
            } else if (position == aboutExteraRow) {
                return 4;
            }
            return 1;
        }
    }
}

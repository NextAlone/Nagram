package com.exteragram.messenger.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;

import com.exteragram.messenger.ExteraConfig;

public class DrawerPreferencesEntry extends BaseFragment {

    private int rowCount;
    private ListAdapter listAdapter;

    private int drawerHeaderRow;
    private int attentionRow;
    private int newGroupRow;
    private int newSecretChatRow;
    private int newChannelRow;
    private int contactsRow;
    private int callsRow;
    private int peopleNearbyRow;
    private int archivedChatsRow;
    private int savedMessagesRow;
    private int inviteFriendsRow;
    private int telegramFeaturesRow;
    private int drawerDividerRow;

    private UndoView restartTooltip;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRowsId(true);
        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("Drawer", R.string.Drawer));
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
            if (position == attentionRow) {
                Browser.openUrl(getParentActivity(), "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
            } else if (position == newGroupRow) {
                ExteraConfig.toggleNewGroup();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.newGroup);
                }
            } else if (position == newSecretChatRow) {
                ExteraConfig.toggleNewSecretChat();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.newSecretChat);
                }
            } else if (position == newChannelRow) {
                ExteraConfig.toggleNewChannel();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.newChannel);
                }
            } else if (position == contactsRow) {
                ExteraConfig.toggleContacts();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.contacts);
                }
            } else if (position == callsRow) {
                ExteraConfig.toggleCalls();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.calls);
                }
            } else if (position == peopleNearbyRow) {
                ExteraConfig.togglePeopleNearby();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.peopleNearby);
                }
            } else if (position == archivedChatsRow) {
                ExteraConfig.toggleArchivedChats();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.archivedChats);
                }
            } else if (position == savedMessagesRow) {
                ExteraConfig.toggleSavedMessages();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.savedMessages);
                }
            } else if (position == inviteFriendsRow) {
                ExteraConfig.toggleInviteFriends();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.inviteFriends);
                }
            } else if (position == telegramFeaturesRow) {
                ExteraConfig.toggleTelegramFeatures();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.telegramFeatures);
                }
            }
        });
        restartTooltip = new UndoView(context);
        restartTooltip.setInfoText(LocaleController.formatString("RestartRequired", R.string.RestartRequired));
        frameLayout.addView(restartTooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));
        return fragmentView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRowsId(boolean notify) {
        rowCount = 0;

        drawerHeaderRow = rowCount++;
        attentionRow = rowCount++;
        newGroupRow = rowCount++;
        newSecretChatRow = rowCount++;
        newChannelRow = rowCount++;
        contactsRow = rowCount++;
        callsRow = rowCount++;
        peopleNearbyRow = rowCount++;
        archivedChatsRow = rowCount++;
        savedMessagesRow = rowCount++;
        inviteFriendsRow = rowCount++;
        telegramFeaturesRow = rowCount++;
        drawerDividerRow = rowCount++;

        if (listAdapter != null && notify) {
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
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == drawerHeaderRow) {
                        headerCell.setText(LocaleController.getString("Drawer", R.string.Drawer));
                    }
                    break;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == newGroupRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("NewGroup", R.string.NewGroup), ExteraConfig.newGroup, true);
                    } else if (position == newSecretChatRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), ExteraConfig.newSecretChat, true);
                    } else if (position == newChannelRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("NewChannel", R.string.NewChannel), ExteraConfig.newChannel, true);
                    } else if (position == contactsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("Contacts", R.string.Contacts), ExteraConfig.contacts, true);
                    } else if (position == callsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("Calls", R.string.Calls), ExteraConfig.calls, true);
                    } else if (position == peopleNearbyRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("PeopleNearby", R.string.PeopleNearby), LocaleController.getString("PNDescription", R.string.PNDescription), ExteraConfig.peopleNearby, true, true);
                    } else if (position == archivedChatsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ArchivedChats", R.string.ArchivedChats), ExteraConfig.archivedChats, true);
                    } else if (position == savedMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SavedMessages", R.string.SavedMessages), ExteraConfig.savedMessages, true);
                    } else if (position == inviteFriendsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InviteFriends", R.string.InviteFriends), ExteraConfig.inviteFriends, true);
                    } else if (position == telegramFeaturesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("TelegramFeatures", R.string.TelegramFeatures), ExteraConfig.telegramFeatures, true);
                    }
                    break;
                case 4:
                    TextDetailSettingsCell textDetailCell = (TextDetailSettingsCell) holder.itemView;
                    textDetailCell.setMultilineDetail(true);
                    if (position == attentionRow) {
                        textDetailCell.setTextAndValue(LocaleController.getString("Attention", R.string.Attention), LocaleController.getString("AttentionDescription", R.string.AttentionDescription),  true);
                    }
                    break;
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 3 || type == 7;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 2:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new TextDetailSettingsCell(mContext);
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
            if (position == drawerDividerRow) {
                return 1;
            } else if (position == drawerHeaderRow) {
                return 2;
            } else if (position == newGroupRow || position == newSecretChatRow || position == newChannelRow ||
                    position == contactsRow || position == callsRow || position == peopleNearbyRow ||
                    position == archivedChatsRow || position == savedMessagesRow ||
                    position == inviteFriendsRow || position == telegramFeaturesRow) {
                return 3;
            } else if (position == attentionRow) {
                return 4;
            }
            return 1;
        }
    }
}

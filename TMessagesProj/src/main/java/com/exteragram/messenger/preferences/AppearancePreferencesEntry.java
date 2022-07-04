package com.exteragram.messenger.preferences;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.ActionBar.AlertDialog;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.cells.TextCheckWithIconCell;

public class AppearancePreferencesEntry extends BaseFragment {

    private int rowCount;
    private ListAdapter listAdapter;
    private ValueAnimator statusBarColorAnimate;

    private int applicationHeaderRow;
    private int useSystemFontsRow;
    private int useSystemEmojiRow;
    private int transparentStatusBarRow;
    private int blurForAllThemesRow;
    private int centerTitleRow;
    private int newSwitchStyleRow;
    private int transparentNavBarRow;
    private int squareFabRow;
    private int squareFabInfoRow;

    private int iconsHeaderRow;
    private int eventChooserRow;
    private int iconsDividerRow;

    private int drawerHeaderRow;
    private int newGroupRow;
    private int newSecretChatRow;
    private int newChannelRow;
    private int contactsRow;
    private int callsRow;
    private int peopleNearbyRow;
    private int archivedChatsRow;
    private int savedMessagesRow;
    private int scanQrRow;
    private int inviteFriendsRow;
    private int telegramFeaturesRow;
    private int drawerDividerRow;

    private int newGroupIcon;
    private int newSecretIcon;
    private int newChannelIcon;
    private int contactsIcon;
    private int callsIcon;
    private int archiveIcon;
    private int savedIcon;
    private int settingsIcon;
    private int scanQrIcon;
    private int inviteIcon;
    private int helpIcon;
    private int peopleNearbyIcon;

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
        actionBar.setTitle(LocaleController.getString("Appearance", R.string.Appearance));
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
            if (position == useSystemFontsRow) {
                ExteraConfig.toggleUseSystemFonts();
                AndroidUtilities.clearTypefaceCache();
                Parcelable recyclerViewState = null;
                if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                AlertDialog progressDialog = new AlertDialog(context, 3);
                progressDialog.show();
                AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
            } else if (position == useSystemEmojiRow) {
                SharedConfig.toggleUseSystemEmoji();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.useSystemEmoji);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == transparentStatusBarRow) {
                SharedConfig.toggleNoStatusBar();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.noStatusBar);
                }
                int color = Theme.getColor(Theme.key_actionBarDefault, null, true);
                int alpha = ColorUtils.calculateLuminance(color) > 0.7f ? 0x0f : 0x33;
                if (statusBarColorAnimate != null && statusBarColorAnimate.isRunning()) {
                    statusBarColorAnimate.end();
                }
                statusBarColorAnimate = SharedConfig.noStatusBar ? ValueAnimator.ofInt(alpha, 0) : ValueAnimator.ofInt(0, alpha);
                statusBarColorAnimate.setDuration(200);
                statusBarColorAnimate.addUpdateListener(animation -> getParentActivity().getWindow().setStatusBarColor(ColorUtils.setAlphaComponent(0, (int) animation.getAnimatedValue())));
                statusBarColorAnimate.start();
            } else if (position == blurForAllThemesRow) {
                ExteraConfig.toggleBlurForAllThemes();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.blurForAllThemes);
                }
                restartTooltip.showWithAction(0, UndoView.ACTION_CACHE_WAS_CLEARED, null, null);
            } else if (position == centerTitleRow) {
                ExteraConfig.toggleCenterTitle();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.centerTitle);
                }
                Parcelable recyclerViewState = null;
                if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                AlertDialog progressDialog = new AlertDialog(context, 3);
                progressDialog.show();
                AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
                listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            } else if (position == newSwitchStyleRow) {
                ExteraConfig.toggleNewSwitchStyle();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.newSwitchStyle);
                }
                Parcelable recyclerViewState = null;
                if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                AlertDialog progressDialog = new AlertDialog(context, 3);
                progressDialog.show();
                AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
                listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            } else if (position == transparentNavBarRow) {
                ExteraConfig.toggleTransparentNavBar();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.transparentNavBar);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == squareFabRow) {
                ExteraConfig.toggleSquareFab();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.squareFab);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == newGroupRow) {
                ExteraConfig.toggleDrawerElements(1);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.newGroup);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == newSecretChatRow) {
                ExteraConfig.toggleDrawerElements(2);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.newSecretChat);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == newChannelRow) {
                ExteraConfig.toggleDrawerElements(3);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.newChannel);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == contactsRow) {
                ExteraConfig.toggleDrawerElements(4);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.contacts);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == callsRow) {
                ExteraConfig.toggleDrawerElements(5);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.calls);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == peopleNearbyRow) {
                ExteraConfig.toggleDrawerElements(6);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.peopleNearby);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == archivedChatsRow) {
                ExteraConfig.toggleDrawerElements(7);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.archivedChats);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == savedMessagesRow) {
                ExteraConfig.toggleDrawerElements(8);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.savedMessages);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == scanQrRow) {
                ExteraConfig.toggleDrawerElements(9);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.scanQr);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == inviteFriendsRow) {
                ExteraConfig.toggleDrawerElements(10);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.inviteFriends);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == telegramFeaturesRow) {
                ExteraConfig.toggleDrawerElements(11);
                if (view instanceof TextCheckWithIconCell) {
                    ((TextCheckWithIconCell) view).setChecked(ExteraConfig.telegramFeatures);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == eventChooserRow) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("DrawerIconPack", R.string.DrawerIconPack));
                builder.setItems(new CharSequence[]{
                        LocaleController.getString("Default", R.string.Default),
                        LocaleController.getString("NewYear", R.string.NewYear),
                        LocaleController.getString("ValentinesDay", R.string.ValentinesDay),
                        LocaleController.getString("Halloween", R.string.Halloween)
                }, (dialog, which) -> {
                    ExteraConfig.setEventType(which);
                    RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(eventChooserRow);
                    if (holder != null) {
                        listAdapter.onBindViewHolder(holder, eventChooserRow);
                    }
                    Parcelable recyclerViewState = null;
                    if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                    parentLayout.rebuildAllFragmentViews(true, true);
                    AlertDialog progressDialog = new AlertDialog(context, 3);
                    progressDialog.show();
                    AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
                    listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
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

        applicationHeaderRow = rowCount++;
        useSystemFontsRow = rowCount++;
        useSystemEmojiRow = rowCount++;
        transparentStatusBarRow = rowCount++;
        blurForAllThemesRow = rowCount++;
        centerTitleRow = rowCount++;
        newSwitchStyleRow = rowCount++;
        transparentNavBarRow = rowCount++;
        squareFabRow = rowCount++;
        squareFabInfoRow = rowCount++;

        iconsHeaderRow = rowCount++;
        eventChooserRow = rowCount++;
        iconsDividerRow = rowCount++;
    
        drawerHeaderRow = rowCount++;
        newGroupRow = rowCount++;
        newSecretChatRow = rowCount++;
        newChannelRow = rowCount++;
        contactsRow = rowCount++;
        callsRow = rowCount++;
        peopleNearbyRow = rowCount++;
        archivedChatsRow = rowCount++;
        savedMessagesRow = rowCount++;
        scanQrRow = rowCount++;
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
            switch (ExteraConfig.eventType) {
                case 1:
                    newGroupIcon = R.drawable.msg_groups_ny;
                    newSecretIcon = R.drawable.msg_secret_ny;
                    newChannelIcon = R.drawable.msg_channel_ny;
                    contactsIcon = R.drawable.msg_contacts_ny;
                    callsIcon = R.drawable.msg_calls_ny;
                    savedIcon = R.drawable.msg_saved_ny;
                    settingsIcon = R.drawable.msg_settings_ny;
                    inviteIcon = R.drawable.msg_invite_ny;
                    helpIcon = R.drawable.msg_help_ny;
                    peopleNearbyIcon = R.drawable.msg_nearby_ny;
                    break;
                case 2:
                    newGroupIcon = R.drawable.msg_groups_14;
                    newSecretIcon = R.drawable.msg_secret_14;
                    newChannelIcon = R.drawable.msg_channel_14;
                    contactsIcon = R.drawable.msg_contacts_14;
                    callsIcon = R.drawable.msg_calls_14;
                    savedIcon = R.drawable.msg_saved_14;
                    settingsIcon = R.drawable.msg_settings_14;
                    inviteIcon = R.drawable.msg_secret_ny;
                    helpIcon = R.drawable.msg_help;
                    peopleNearbyIcon = R.drawable.msg_secret_14;
                    break;
                case 3:
                    newGroupIcon = R.drawable.msg_groups_hw;
                    newSecretIcon = R.drawable.msg_secret_hw;
                    newChannelIcon = R.drawable.msg_channel_hw;
                    contactsIcon = R.drawable.msg_contacts_hw;
                    callsIcon = R.drawable.msg_calls_hw;
                    savedIcon = R.drawable.msg_saved_hw;
                    settingsIcon = R.drawable.msg_settings_hw;
                    inviteIcon = R.drawable.msg_invite_hw;
                    helpIcon = R.drawable.msg_help_hw;
                    peopleNearbyIcon = R.drawable.msg_secret_hw;
                    break;
                default:
                    newGroupIcon = R.drawable.msg_groups;
                    newSecretIcon = R.drawable.msg_secret;
                    newChannelIcon = R.drawable.msg_channel;
                    contactsIcon = R.drawable.msg_contacts;
                    callsIcon = R.drawable.msg_calls;
                    savedIcon = R.drawable.msg_saved;
                    settingsIcon = R.drawable.msg_settings;
                    inviteIcon = R.drawable.msg_invite;
                    helpIcon = R.drawable.msg_help;
                    peopleNearbyIcon = R.drawable.msg_nearby;
                    break;
            }
            switch (holder.getItemViewType()) {
                case 1:
                    if (position == drawerDividerRow) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == applicationHeaderRow) {
                        headerCell.setText(LocaleController.getString("Appearance", R.string.Appearance));
                    } else if (position == drawerHeaderRow) {
                        headerCell.setText(LocaleController.getString("DrawerElements", R.string.DrawerElements));
                    } else if (position == iconsHeaderRow) {
                        headerCell.setText(LocaleController.getString("DrawerOptions", R.string.DrawerOptions));
                    }
                    break;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == transparentStatusBarRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("TransparentStatusBar", R.string.TransparentStatusBar), SharedConfig.noStatusBar, true);
                    } else if (position == useSystemFontsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("UseSystemFonts", R.string.UseSystemFonts), ExteraConfig.useSystemFonts, true);
                    } else if (position == useSystemEmojiRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("UseSystemEmoji", R.string.UseSystemEmoji), SharedConfig.useSystemEmoji, true);
                    } else if (position == blurForAllThemesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BlurForAllThemes", R.string.BlurForAllThemes), ExteraConfig.blurForAllThemes, true);
                    } else if (position == centerTitleRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("CenterTitle", R.string.CenterTitle), ExteraConfig.centerTitle, true);
                    } else if (position == newSwitchStyleRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("NewSwitchStyle", R.string.NewSwitchStyle), ExteraConfig.newSwitchStyle, true);
                    } else if (position == transparentNavBarRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("TransparentNavBar", R.string.TransparentNavBar), LocaleController.getString("TransparentNavBarValue", R.string.TransparentNavBarValue), ExteraConfig.transparentNavBar, true, true);
                    } else if (position == squareFabRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SquareFab", R.string.SquareFab), ExteraConfig.squareFab, false);
                    }
                    break;
                case 4:
                    TextCheckWithIconCell textCheckWithIconCell = (TextCheckWithIconCell) holder.itemView;
                    textCheckWithIconCell.setEnabled(true, null);
                    if (position == newGroupRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), newGroupIcon, ExteraConfig.newGroup, true);
                    } else if (position == newSecretChatRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), newSecretIcon, ExteraConfig.newSecretChat, true);
                    } else if (position == newChannelRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), newChannelIcon, ExteraConfig.newChannel, true);
                    } else if (position == contactsRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("Contacts", R.string.Contacts), contactsIcon, ExteraConfig.contacts, true);
                    } else if (position == callsRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("Calls", R.string.Calls), callsIcon, ExteraConfig.calls, true);
                    } else if (position == peopleNearbyRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("PeopleNearby", R.string.PeopleNearby), peopleNearbyIcon, ExteraConfig.peopleNearby, true);
                    } else if (position == archivedChatsRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("ArchivedChats", R.string.ArchivedChats), R.drawable.msg_archive, ExteraConfig.archivedChats, true);
                    } else if (position == savedMessagesRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("SavedMessages", R.string.SavedMessages), savedIcon, ExteraConfig.savedMessages, true);
                    } else if (position == scanQrRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("AuthAnotherClient", R.string.AuthAnotherClient), R.drawable.msg_qrcode, ExteraConfig.scanQr, true);
                    } else if (position == inviteFriendsRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("InviteFriends", R.string.InviteFriends), inviteIcon, ExteraConfig.inviteFriends, true);
                    } else if (position == telegramFeaturesRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("TelegramFeatures", R.string.TelegramFeatures), helpIcon, ExteraConfig.telegramFeatures, false);
                    }
                    break;
                case 5:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    if (position == eventChooserRow) {
                        String value;
                        if (ExteraConfig.eventType == 1) {
                            value = LocaleController.getString("NewYear", R.string.NewYear);
                        } else if (ExteraConfig.eventType == 2) {
                            value = LocaleController.getString("ValentinesDay", R.string.ValentinesDay);
                        } else if (ExteraConfig.eventType == 3) {
                            value = LocaleController.getString("Halloween", R.string.Halloween);
                        } else {
                            value = LocaleController.getString("Default", R.string.Default);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("DrawerIconPack", R.string.DrawerIconPack), value, false);
                    }
                    break;
                case 6:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == squareFabInfoRow) {
                        cell.setText(LocaleController.getString("SquareFabInfo", R.string.SquareFabInfo));
                    }
                    break;
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 3 || type == 4 || type == 5;
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
                    view = new TextCheckWithIconCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
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
            if (position == iconsDividerRow || position == drawerDividerRow) {
                return 1;
            } else if (position == applicationHeaderRow || position == drawerHeaderRow || position == iconsHeaderRow) {
                return 2;
            } else if (position == useSystemFontsRow || position == useSystemEmojiRow || position == transparentStatusBarRow || position == transparentNavBarRow ||
                      position == squareFabRow || position == blurForAllThemesRow || position == centerTitleRow || position == newSwitchStyleRow) {
                return 3;
            } else if (position == newGroupRow || position == newSecretChatRow || position == newChannelRow ||
                      position == contactsRow || position == callsRow || position == peopleNearbyRow ||
                      position == archivedChatsRow || position == savedMessagesRow ||
                      position == scanQrRow || position == telegramFeaturesRow || position == inviteFriendsRow) {
                return 4;
            } else if (position == eventChooserRow) {
                return 5;
            } else if (position == squareFabInfoRow) {
                return 6;
            }
            return 1;
        }
    }
}
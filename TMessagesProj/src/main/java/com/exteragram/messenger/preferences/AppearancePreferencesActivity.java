/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

package com.exteragram.messenger.preferences;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ExteraUtils;
import com.exteragram.messenger.components.FabShapeCell;
import com.exteragram.messenger.components.TextCheckWithIconCell;

public class AppearancePreferencesActivity extends BasePreferencesActivity {

    private FabShapeCell fabShapeCell;

    private ValueAnimator statusBarColorAnimate;
    private Parcelable recyclerViewState = null;
    private UserConfig me = UserConfig.getInstance(UserConfig.selectedAccount);

    private int applicationHeaderRow;
    private int fabShapeRow;
    private int useSystemFontsRow;
    private int useSystemEmojiRow;
    private int transparentStatusBarRow;
    private int blurForAllThemesRow;
    private int centerTitleRow;
    private int newSwitchStyleRow;
    private int disableDividersRow;
    private int transparentNavBarRow;
    private int transparentNavBarInfoRow;

    private int iconsHeaderRow;
    private int eventChooserRow;
    private int iconsDividerRow;

    private int drawerHeaderRow;
    private int statusRow;
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

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        applicationHeaderRow = newRow();
        fabShapeRow = newRow();
        useSystemFontsRow = newRow();
        useSystemEmojiRow = newRow();
        transparentStatusBarRow = newRow();
        blurForAllThemesRow = newRow();
        centerTitleRow = newRow();
        newSwitchStyleRow = newRow();
        disableDividersRow = newRow();
        transparentNavBarRow = newRow();
        transparentNavBarInfoRow = newRow();

        iconsHeaderRow = newRow();
        eventChooserRow = newRow();
        iconsDividerRow = newRow();
    
        drawerHeaderRow = newRow();
        
        if (me != null && me.isPremium()) statusRow = newRow();
        newGroupRow = newRow();
        newSecretChatRow = newRow();
        newChannelRow = newRow();
        contactsRow = newRow();
        callsRow = newRow();
        peopleNearbyRow = newRow();
        archivedChatsRow = newRow();
        savedMessagesRow = newRow();
        scanQrRow = newRow();
        inviteFriendsRow = newRow();
        telegramFeaturesRow = newRow();
        drawerDividerRow = newRow();
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == useSystemFontsRow) {
            ExteraConfig.toggleUseSystemFonts();
            AndroidUtilities.clearTypefaceCache();
            if (getListView().getLayoutManager() != null) recyclerViewState = getListView().getLayoutManager().onSaveInstanceState();
            parentLayout.rebuildAllFragmentViews(true, true);
            getListView().getLayoutManager().onRestoreInstanceState(recyclerViewState);
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
            showBulletin();
        } else if (position == centerTitleRow) {
            ExteraConfig.toggleCenterTitle();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ExteraConfig.centerTitle);
            }
            if (getListView().getLayoutManager() != null) recyclerViewState = getListView().getLayoutManager().onSaveInstanceState();
            parentLayout.rebuildAllFragmentViews(true, true);
            getListView().getLayoutManager().onRestoreInstanceState(recyclerViewState);
        } else if (position == newSwitchStyleRow) {
            ExteraConfig.toggleNewSwitchStyle();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ExteraConfig.newSwitchStyle);
            }
            if (getListView().getLayoutManager() != null) recyclerViewState = getListView().getLayoutManager().onSaveInstanceState();
            parentLayout.rebuildAllFragmentViews(true, true);
            getListView().getLayoutManager().onRestoreInstanceState(recyclerViewState);
        } else if (position == disableDividersRow) {
            ExteraConfig.toggleDisableDividers();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ExteraConfig.disableDividers);
            }
            if (getListView().getLayoutManager() != null) recyclerViewState = getListView().getLayoutManager().onSaveInstanceState();
            parentLayout.rebuildAllFragmentViews(true, true);
            getListView().getLayoutManager().onRestoreInstanceState(recyclerViewState);
        } else if (position == transparentNavBarRow) {
            ExteraConfig.toggleTransparentNavBar();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ExteraConfig.transparentNavBar);
            }
            parentLayout.rebuildAllFragmentViews(false, false);
        } else if (position == newGroupRow) {
            ExteraConfig.toggleDrawerElements(1);
            if (view instanceof TextCheckWithIconCell) {
                ((TextCheckWithIconCell) view).setChecked(ExteraConfig.newGroup);
            }
            parentLayout.rebuildAllFragmentViews(false, false);
        } else if (position == statusRow) {
            ExteraConfig.toggleDrawerElements(12);
            if (view instanceof TextCheckWithIconCell) {
                ((TextCheckWithIconCell) view).setChecked(ExteraConfig.changeStatus);
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
            }, new int[] {
                    R.drawable.msg_block,
                    R.drawable.msg_settings_ny,
                    R.drawable.msg_saved_14,
                    R.drawable.msg_contacts_hw
            }, (dialog, which) -> {
                ExteraConfig.setEventType(which);
                RecyclerView.ViewHolder holder = getListView().findViewHolderForAdapterPosition(eventChooserRow);
                if (holder != null) {
                    listAdapter.onBindViewHolder(holder, eventChooserRow);
                }
                if (getListView().getLayoutManager() != null) recyclerViewState = getListView().getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                getListView().getLayoutManager().onRestoreInstanceState(recyclerViewState);
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    @Override
    protected String getTitle() {
        return LocaleController.getString("Appearance", R.string.Appearance);
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            switch (type) {
                case 12:
                    fabShapeCell = new FabShapeCell(mContext) {
                        @Override
                        protected void rebuildFragments() {
                            parentLayout.rebuildAllFragmentViews(false, false);
                        }
                    };
                    fabShapeCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    fabShapeCell.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                    return new RecyclerListView.Holder(fabShapeCell);
                default:
                    return super.onCreateViewHolder(parent, type);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    if (position == drawerDividerRow) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == applicationHeaderRow) {
                        headerCell.setText(LocaleController.getString("Appearance", R.string.Appearance));
                    } else if (position == drawerHeaderRow) {
                        headerCell.setText(LocaleController.getString("DrawerElements", R.string.DrawerElements));
                    } else if (position == iconsHeaderRow) {
                        headerCell.setText(LocaleController.getString("DrawerOptions", R.string.DrawerOptions));
                    }
                    break;
                case 5:
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
                    } else if (position == disableDividersRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableDividers", R.string.DisableDividers), ExteraConfig.disableDividers, true);
                    } else if (position == transparentNavBarRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("TransparentNavBar", R.string.TransparentNavBar), ExteraConfig.transparentNavBar, false);
                    }
                    break;
                case 6:
                    TextCheckWithIconCell textCheckWithIconCell = (TextCheckWithIconCell) holder.itemView;
                    textCheckWithIconCell.setEnabled(true, null);
                    int[] icons = ExteraUtils.getDrawerIconPack();
                    if (position == statusRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("ChangeEmojiStatus", R.string.ChangeEmojiStatus), R.drawable.msg_status_edit, ExteraConfig.changeStatus, true);
                    } else if (position == newGroupRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), icons[0], ExteraConfig.newGroup, true);
                    } else if (position == newSecretChatRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), icons[1], ExteraConfig.newSecretChat, true);
                    } else if (position == newChannelRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), icons[2], ExteraConfig.newChannel, true);
                    } else if (position == contactsRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("Contacts", R.string.Contacts), icons[3], ExteraConfig.contacts, true);
                    } else if (position == callsRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("Calls", R.string.Calls), icons[4], ExteraConfig.calls, true);
                    } else if (position == peopleNearbyRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("PeopleNearby", R.string.PeopleNearby), icons[8], ExteraConfig.peopleNearby, true);
                    } else if (position == archivedChatsRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("ArchivedChats", R.string.ArchivedChats), R.drawable.msg_archive, ExteraConfig.archivedChats, true);
                    } else if (position == savedMessagesRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("SavedMessages", R.string.SavedMessages), icons[5], ExteraConfig.savedMessages, true);
                    } else if (position == scanQrRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("AuthAnotherClient", R.string.AuthAnotherClient), R.drawable.msg_qrcode, ExteraConfig.scanQr, true);
                    } else if (position == inviteFriendsRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("InviteFriends", R.string.InviteFriends), icons[6], ExteraConfig.inviteFriends, true);
                    } else if (position == telegramFeaturesRow) {
                        textCheckWithIconCell.setTextAndCheckAndIcon(LocaleController.getString("TelegramFeatures", R.string.TelegramFeatures), icons[7], ExteraConfig.telegramFeatures, false);
                    }
                    break;
                case 7:
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
                case 8:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == transparentNavBarInfoRow) {
                        cell.setText(LocaleController.getString("TransparentNavBarInfo", R.string.TransparentNavBarInfo));
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == iconsDividerRow || position == drawerDividerRow) {
                return 1;
            } else if (position == applicationHeaderRow || position == drawerHeaderRow || position == iconsHeaderRow) {
                return 3;
            } else if (position == useSystemFontsRow || position == useSystemEmojiRow || position == transparentStatusBarRow || position == transparentNavBarRow ||
                      position == blurForAllThemesRow || position == centerTitleRow || position == newSwitchStyleRow || position == disableDividersRow) {
                return 5;
            } else if (position == eventChooserRow) {
                return 7;
            } else if (position == transparentNavBarInfoRow) {
                return 8;
            } else if (position == fabShapeRow) {
                return 12;
            }
            return 6;
        }
    }
}
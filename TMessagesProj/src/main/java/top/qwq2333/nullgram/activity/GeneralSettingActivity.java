package top.qwq2333.nullgram.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import top.qwq2333.nullgram.config.ConfigManager;
import top.qwq2333.nullgram.ui.DrawerProfilePreviewCell;
import top.qwq2333.nullgram.utils.Defines;

@SuppressLint("NotifyDataSetChanged")
public class GeneralSettingActivity extends BaseActivity {

    private DrawerProfilePreviewCell profilePreviewCell;

    private int generalRow;

    private int drawerRow;
    private int avatarAsDrawerBackgroundRow;
    private int avatarBackgroundBlurRow;
    private int avatarBackgroundDarkenRow;
    private int largeAvatarAsBackgroundRow;
    private int hidePhoneRow;
    private int drawer2Row;


    private int showBotAPIRow;
    private int showExactNumberRow;
    private int disableInstantCameraRow;
    private int disableUndoRow;
    private int skipOpenLinkConfirmRow;
    private int autoProxySwitchRow;
    private int useSystemEmojiRow;
    private int disableVibrationRow;
    private int general2Row;


    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    protected String getActionBarTitle() {
        return LocaleController.getString("General", R.string.General);
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == showBotAPIRow) {
            ConfigManager.toggleBoolean(Defines.showBotAPIID);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.showBotAPIID));
            }
        } else if (position == hidePhoneRow) {
            ConfigManager.toggleBoolean(Defines.hidePhone);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.hidePhone));
            }
            parentLayout.rebuildAllFragmentViews(false, false);
            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            listAdapter.notifyItemChanged(drawerRow, new Object());
        } else if (position == avatarAsDrawerBackgroundRow) {
            ConfigManager.toggleBoolean(Defines.avatarAsDrawerBackground);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.avatarAsDrawerBackground));
            }
            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            TransitionManager.beginDelayedTransition(profilePreviewCell);
            listAdapter.notifyItemChanged(drawerRow, new Object());
            if (ConfigManager.getBooleanOrFalse(Defines.avatarAsDrawerBackground)) {
                updateRows();
                listAdapter.notifyItemRangeInserted(avatarBackgroundBlurRow, 2);
            } else {
                listAdapter.notifyItemRangeRemoved(avatarBackgroundBlurRow, 2);
                updateRows();
            }
        } else if (position == avatarBackgroundBlurRow) {
            ConfigManager.toggleBoolean(Defines.avatarBackgroundBlur);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.avatarBackgroundBlur));
            }
            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            listAdapter.notifyItemChanged(drawerRow, new Object());
        } else if (position == avatarBackgroundDarkenRow) {
            ConfigManager.toggleBoolean(Defines.avatarBackgroundDarken);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.avatarBackgroundDarken));
            }
            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            listAdapter.notifyItemChanged(drawerRow, new Object());
        } else if (position == largeAvatarAsBackgroundRow) {
            ConfigManager.toggleBoolean(Defines.largeAvatarAsBackground);
            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            TransitionManager.beginDelayedTransition(profilePreviewCell);
            listAdapter.notifyItemChanged(drawerRow, new Object());
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.largeAvatarAsBackground));
            }
        } else if (position == showExactNumberRow) {
            ConfigManager.toggleBoolean(Defines.showExactNumber);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.showExactNumber));
            }
        } else if (position == disableInstantCameraRow) {
            ConfigManager.toggleBoolean(Defines.disableInstantCamera);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.disableInstantCamera));
            }
        } else if (position == disableUndoRow) {
            ConfigManager.toggleBoolean(Defines.disableUndo);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.disableUndo));
            }
        } else if (position == skipOpenLinkConfirmRow) {
            ConfigManager.toggleBoolean(Defines.skipOpenLinkConfirm);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.skipOpenLinkConfirm));
            }
        } else if (position == autoProxySwitchRow) {
            ConfigManager.toggleBoolean(Defines.autoSwitchProxy);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.autoSwitchProxy));
            }
        } else if (position == useSystemEmojiRow) {
            ConfigManager.toggleBoolean(Defines.useSystemEmoji);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.useSystemEmoji));
            }
        } else if (position == disableVibrationRow) {
            ConfigManager.toggleBoolean(Defines.disableVibration);
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.disableVibration));
            }
        }

    }

    @Override
    protected boolean onItemLongClick(View view, int position, float x, float y) {
        return false;
    }

    @Override
    protected String getKey() {
        return "g";
    }

    protected void updateRows() {
        super.updateRows();

        drawerRow = rowCount++;
        avatarAsDrawerBackgroundRow = rowCount++;
        if (ConfigManager.getBooleanOrFalse(Defines.avatarAsDrawerBackground)) {
            avatarBackgroundBlurRow = rowCount++;
            avatarBackgroundDarkenRow = rowCount++;
            largeAvatarAsBackgroundRow = rowCount++;
        } else {
            avatarBackgroundBlurRow = -1;
            avatarBackgroundDarkenRow = -1;
            largeAvatarAsBackgroundRow = -1;
        }
        hidePhoneRow = rowCount++;
        drawer2Row = rowCount++;

        generalRow = rowCount++;
        showBotAPIRow = rowCount++;
        showExactNumberRow = rowCount++;
        disableInstantCameraRow = rowCount++;
        disableUndoRow = rowCount++;
        skipOpenLinkConfirmRow = rowCount++;
        autoProxySwitchRow = rowCount++;
        useSystemEmojiRow = rowCount++;
        disableVibrationRow = rowCount++;
        general2Row = rowCount++;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends BaseListAdapter {
        private final DrawerLayoutContainer mDrawerLayoutContainer;

        public ListAdapter(Context context) {
            super(context);
            mDrawerLayoutContainer = new DrawerLayoutContainer(mContext);

        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    if (position == general2Row) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
                case 2: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    break;
                }
                case 3: {
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    textCell.setEnabled(true, null);
                    if (position == showBotAPIRow) {
                        textCell.setTextAndCheck(LocaleController.getString("showBotAPIID", R.string.showBotAPIID), ConfigManager.getBooleanOrFalse(Defines.showBotAPIID), true);
                    } else if (position == hidePhoneRow) {
                        textCell.setTextAndCheck(LocaleController.getString("hidePhone", R.string.hidePhone), ConfigManager.getBooleanOrFalse(Defines.hidePhone), true);
                    } else if (position == showExactNumberRow) {
                        textCell.setTextAndCheck(LocaleController.getString("showExactNumber", R.string.showExactNumber), ConfigManager.getBooleanOrFalse(Defines.showExactNumber), true);
                    } else if (position == disableInstantCameraRow) {
                        textCell.setTextAndCheck(LocaleController.getString("disableInstantCamera", R.string.disableInstantCamera), ConfigManager.getBooleanOrFalse(Defines.disableInstantCamera), true);
                    } else if (position == disableUndoRow) {
                        textCell.setTextAndCheck(LocaleController.getString("disableUndo", R.string.disableUndo), ConfigManager.getBooleanOrFalse(Defines.disableUndo), true);
                    } else if (position == skipOpenLinkConfirmRow) {
                        textCell.setTextAndCheck(LocaleController.getString("skipOpenLinkConfirm", R.string.skipOpenLinkConfirm), ConfigManager.getBooleanOrFalse(Defines.skipOpenLinkConfirm), true);
                    } else if (position == autoProxySwitchRow) {
                        textCell.setTextAndCheck(LocaleController.getString("autoProxySwitch", R.string.autoProxySwitch), ConfigManager.getBooleanOrFalse(Defines.autoSwitchProxy), true);
                    } else if (position == avatarAsDrawerBackgroundRow) {
                        textCell.setTextAndCheck(LocaleController.getString("AvatarAsBackground", R.string.AvatarAsBackground), ConfigManager.getBooleanOrFalse(Defines.avatarAsDrawerBackground), true);
                    } else if (position == avatarBackgroundBlurRow) {
                        textCell.setTextAndCheck(LocaleController.getString("BlurAvatarBackground", R.string.BlurAvatarBackground), ConfigManager.getBooleanOrFalse(Defines.avatarBackgroundBlur), true);
                    } else if (position == avatarBackgroundDarkenRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DarkenAvatarBackground", R.string.DarkenAvatarBackground), ConfigManager.getBooleanOrFalse(Defines.avatarBackgroundDarken), true);
                    } else if (position == largeAvatarAsBackgroundRow) {
                        textCell.setTextAndCheck(LocaleController.getString("LargeAvatarAsBackground", R.string.largeAvatarAsBackground), ConfigManager.getBooleanOrFalse(Defines.largeAvatarAsBackground), true);
                    } else if (position == useSystemEmojiRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UseSystemEmoji", R.string.useSystemEmoji), ConfigManager.getBooleanOrFalse(Defines.useSystemEmoji), true);
                    } else if (position == disableVibrationRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DisableVibration", R.string.disableVibration), ConfigManager.getBooleanOrFalse(Defines.disableVibration), true);
                    }
                    break;
                }
                case 4: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == generalRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                    }
                    break;
                }
                case 5: {
                    NotificationsCheckCell textCell = (NotificationsCheckCell) holder.itemView;
                    break;
                }
                case 8: {
                    DrawerProfilePreviewCell cell = (DrawerProfilePreviewCell) holder.itemView;
                    cell.setUser(getUserConfig().getCurrentUser(), false);
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 2 || type == 3 || type == 6 || type == 5;
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
                case 3:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new NotificationsCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 7:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 8:
                    profilePreviewCell = new DrawerProfilePreviewCell(mContext, mDrawerLayoutContainer);
                    profilePreviewCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    profilePreviewCell.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                    return new RecyclerListView.Holder(profilePreviewCell);
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == general2Row || position == drawer2Row) {
                return 1;
            } else if ((position > generalRow && position < general2Row) || (position > drawerRow && position < drawer2Row)) {
                return 3;
            } else if (position == generalRow) {
                return 4;
            } else if (position == drawerRow) {
                return 8;
            }
            return -1;
        }
    }
}

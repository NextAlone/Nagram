package top.qwq2333.nullgram.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

import top.qwq2333.nullgram.config.ConfigManager;
import top.qwq2333.nullgram.ui.DrawerProfilePreviewCell;
import top.qwq2333.nullgram.utils.Defines;

@SuppressLint("NotifyDataSetChanged")
public class GeneralSettingActivity extends BaseFragment {

    private DrawerProfilePreviewCell profilePreviewCell;
    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;

    private int generalRow;

    private int drawerRow;
    private int avatarAsDrawerBackgroundRow;
    private int avatarBackgroundBlurRow;
    private int avatarBackgroundDarkenRow;
    private int hidePhoneRow;
    private int drawer2Row;


    private int showBotAPIRow;
    private int showExactNumberRow;
    private int disableInstantCameraRow;
    private int disableUndoRow;
    private int skipOpenLinkConfirmRow;
    private int autoProxySwitchRow;
    private int general2Row;


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows();

        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("General", R.string.General));

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

        listView = new RecyclerListView(context);
        listView.setLayoutManager(
            new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);
        ((DefaultItemAnimator) listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(listView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == showBotAPIRow) {
                ConfigManager.toggleBoolean(Defines.showBotAPIID);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(
                        ConfigManager.getBooleanOrFalse(Defines.showBotAPIID));
                }
            } else if (position == hidePhoneRow) {
                ConfigManager.toggleBoolean(Defines.hidePhone);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(
                        ConfigManager.getBooleanOrFalse(Defines.hidePhone));
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
            } else if (position == showExactNumberRow) {
                ConfigManager.toggleBoolean(Defines.showExactNumber);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(
                        ConfigManager.getBooleanOrFalse(Defines.showExactNumber));
                }
            } else if (position == disableInstantCameraRow) {
                ConfigManager.toggleBoolean(Defines.disableInstantCamera);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(
                        ConfigManager.getBooleanOrFalse(Defines.disableInstantCamera));
                }
            } else if (position == disableUndoRow) {
                ConfigManager.toggleBoolean(Defines.disableUndo);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(
                        ConfigManager.getBooleanOrFalse(Defines.disableUndo));
                }
            } else if (position == skipOpenLinkConfirmRow) {
                ConfigManager.toggleBoolean(Defines.skipOpenLinkConfirm);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(
                        ConfigManager.getBooleanOrFalse(Defines.skipOpenLinkConfirm));
                }
            } else if (position == autoProxySwitchRow) {
                ConfigManager.toggleBoolean(Defines.autoSwitchProxy);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(
                        ConfigManager.getBooleanOrFalse(Defines.autoSwitchProxy));
                }
            }
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

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        try (Cursor cursor = getParentActivity().getContentResolver()
            .query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return result;
    }


    private void updateRows() {
        rowCount = 0;

        drawerRow = rowCount++;
        avatarAsDrawerBackgroundRow = rowCount++;
        if (ConfigManager.getBooleanOrFalse(Defines.avatarAsDrawerBackground)) {
            avatarBackgroundBlurRow = rowCount++;
            avatarBackgroundDarkenRow = rowCount++;
        } else {
            avatarBackgroundBlurRow = -1;
            avatarBackgroundDarkenRow = -1;
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
        general2Row = rowCount++;
        if (listAdapter != null) {
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
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    if (position == general2Row) {
                        holder.itemView.setBackground(
                            Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom,
                                Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(
                            Theme.getThemedDrawable(mContext, R.drawable.greydivider,
                                Theme.key_windowBackgroundGrayShadow));
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
                        textCell.setTextAndCheck(LocaleController.getString("showBotAPIID",
                            R.string.showBotAPIID), ConfigManager.getBooleanOrFalse(
                            Defines.showBotAPIID), true);
                    } else if (position == hidePhoneRow) {
                        textCell.setTextAndCheck(LocaleController.getString("hidePhone",
                            R.string.hidePhone), ConfigManager.getBooleanOrFalse(
                            Defines.hidePhone), true);
                    } else if (position == showExactNumberRow) {
                        textCell.setTextAndCheck(LocaleController.getString("showExactNumber",
                            R.string.showExactNumber), ConfigManager.getBooleanOrFalse(
                            Defines.showExactNumber), true);
                    } else if (position == disableInstantCameraRow) {
                        textCell.setTextAndCheck(LocaleController.getString("disableInstantCamera",
                            R.string.disableInstantCamera), ConfigManager.getBooleanOrFalse(
                            Defines.disableInstantCamera), true);
                    } else if (position == disableUndoRow) {
                        textCell.setTextAndCheck(LocaleController.getString("disableUndo",
                            R.string.disableUndo), ConfigManager.getBooleanOrFalse(
                            Defines.disableUndo), true);
                    } else if (position == skipOpenLinkConfirmRow) {
                        textCell.setTextAndCheck(LocaleController.getString("skipOpenLinkConfirm",
                            R.string.skipOpenLinkConfirm), ConfigManager.getBooleanOrFalse(
                            Defines.skipOpenLinkConfirm), true);
                    } else if (position == autoProxySwitchRow) {
                        textCell.setTextAndCheck(LocaleController.getString("autoProxySwitch",
                            R.string.autoProxySwitch), ConfigManager.getBooleanOrFalse(
                            Defines.autoSwitchProxy), true);
                    } else if (position == avatarAsDrawerBackgroundRow) {
                        textCell.setTextAndCheck(LocaleController.getString("AvatarAsBackground", R.string.AvatarAsBackground), ConfigManager.getBooleanOrFalse(Defines.avatarAsDrawerBackground), true);
                    } else if (position == avatarBackgroundBlurRow) {
                        textCell.setTextAndCheck(LocaleController.getString("BlurAvatarBackground", R.string.BlurAvatarBackground), ConfigManager.getBooleanOrFalse(Defines.avatarBackgroundBlur), true);
                    } else if (position == avatarBackgroundDarkenRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DarkenAvatarBackground", R.string.DarkenAvatarBackground), ConfigManager.getBooleanOrFalse(Defines.avatarBackgroundDarken), true);
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
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider,
                        Theme.key_windowBackgroundGrayShadow));
                    break;
                case 8:
                    profilePreviewCell = new DrawerProfilePreviewCell(mContext);
                    profilePreviewCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    profilePreviewCell.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                    return new RecyclerListView.Holder(profilePreviewCell);
            }
            //noinspection ConstantConditions
            view.setLayoutParams(
                new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
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

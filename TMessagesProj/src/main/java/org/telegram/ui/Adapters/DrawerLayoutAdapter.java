/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerActionCheckCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SideMenultItemAnimator;

import java.util.ArrayList;
import java.util.Collections;

import cn.hutool.core.util.StrUtil;
import kotlin.jvm.functions.Function0;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.NekoXConfig;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter implements NotificationCenter.NotificationCenterDelegate {

    private Context mContext;
    public ArrayList<Item> items = new ArrayList<>(11);
    private ArrayList<Integer> accountNumbers = new ArrayList<>();
    private boolean accountsShown;
    private DrawerProfileCell profileCell;
    private SideMenultItemAnimator itemAnimator;
    private boolean hasGps;

    public DrawerLayoutAdapter(Context context, SideMenultItemAnimator animator) {
        mContext = context;
        itemAnimator = animator;
        accountsShown = MessagesController.getGlobalMainSettings().getBoolean("accountsShown", true);
        Theme.createCommonDialogResources(context);
        resetItems();
        try {
            hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        } catch (Throwable e) {
            hasGps = false;
        }
    }

    private int getAccountRowsCount() {
        return accountNumbers.size() + 2;
    }

    @Override
    public int getItemCount() {
        int count = items.size() + 2;
        if (accountsShown) {
            count += getAccountRowsCount();
        }
        return count;
    }

    public void setAccountsShown(boolean value, boolean animated) {
        if (accountsShown == value || itemAnimator.isRunning()) {
            return;
        }
        accountsShown = value;
        MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShown", accountsShown).apply();
        if (profileCell != null) {
            profileCell.setAccountsShown(accountsShown, animated);
        }
        MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShown", accountsShown).apply();
        if (animated) {
            itemAnimator.setShouldClipChildren(false);
            if (accountsShown) {
                notifyItemRangeInserted(2, getAccountRowsCount());
            } else {
                notifyItemRangeRemoved(2, getAccountRowsCount());
            }
        } else {
            notifyDataSetChanged();
        }
    }

    public boolean isAccountsShown() {
        return accountsShown;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateUserStatus) {
            if (args[0] != null) {
                TLRPC.TL_updateUserStatus update = (TLRPC.TL_updateUserStatus) args[0];
                long selectedUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                if (update.user_id != selectedUserId) {
                    return;
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int itemType = holder.getItemViewType();
        return itemType == 3 || itemType == 4 || itemType == 5 || itemType == 6;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = profileCell = new DrawerProfileCell(mContext);
                break;
            case 2:
                view = new DividerCell(mContext);
                break;
            case 3:
                view = new DrawerActionCell(mContext);
                break;
            case 6:
                view = new DrawerActionCheckCell(mContext);
                break;
            case 4:
                view = new DrawerUserCell(mContext);
                break;
            case 5:
                view = new DrawerAddCell(mContext);
                break;
            case 1:
            default:
                view = new EmptyCell(mContext, AndroidUtilities.dp(8));
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                DrawerProfileCell profileCell = (DrawerProfileCell) holder.itemView;
                profileCell.setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()), accountsShown);
                break;
            }
            case 3: {
                DrawerActionCell drawerActionCell = (DrawerActionCell) holder.itemView;
                position -= 2;
                if (accountsShown) {
                    position -= getAccountRowsCount();
                }
                items.get(position).bind(drawerActionCell);
                drawerActionCell.setPadding(0, 0, 0, 0);
                break;
            }
            case 6: {
                DrawerActionCheckCell drawerActionCell = (DrawerActionCheckCell) holder.itemView;
                position -= 2;
                if (accountsShown) {
                    position -= getAccountRowsCount();
                }
                ((CheckItem) items.get(position)).bindCheck(drawerActionCell);
                drawerActionCell.setPadding(0, 0, 0, 0);
                break;
            }
            case 4: {
                DrawerUserCell drawerUserCell = (DrawerUserCell) holder.itemView;
                drawerUserCell.invalidate();
                drawerUserCell.setAccount(accountNumbers.get(position - 2));
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        } else if (i == 1) {
            return 1;
        }
        i -= 2;
        if (accountsShown) {
            if (i < accountNumbers.size()) {
                return 4;
            } else {
                if (i == accountNumbers.size()) {
                    return 5;
                } else if (i == accountNumbers.size() + 1) {
                    return 2;
                }
            }
            i -= getAccountRowsCount();
        }
        if (items.get(i) == null) {
            return 2;
        }
        return items.get(i) instanceof CheckItem ? 6 : 3;
    }

    public void swapElements(int fromIndex, int toIndex) {
        int idx1 = fromIndex - 2;
        int idx2 = toIndex - 2;
        if (idx1 < 0 || idx2 < 0 || idx1 >= accountNumbers.size() || idx2 >= accountNumbers.size()) {
            return;
        }
        final UserConfig userConfig1 = UserConfig.getInstance(accountNumbers.get(idx1));
        final UserConfig userConfig2 = UserConfig.getInstance(accountNumbers.get(idx2));
        final int tempLoginTime = userConfig1.loginTime;
        userConfig1.loginTime = userConfig2.loginTime;
        userConfig2.loginTime = tempLoginTime;
        userConfig1.saveConfig(false);
        userConfig2.saveConfig(false);
        Collections.swap(accountNumbers, idx1, idx2);
        notifyItemMoved(fromIndex, toIndex);
    }

    private void resetItems() {
        accountNumbers.clear();
        for (int a : SharedConfig.activeAccounts) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                accountNumbers.add(a);
            }
        }
        Collections.sort(accountNumbers, (o1, o2) -> {
            long l1 = UserConfig.getInstance(o1).loginTime;
            long l2 = UserConfig.getInstance(o2).loginTime;
            if (l1 > l2) {
                return 1;
            } else if (l1 < l2) {
                return -1;
            }
            return 0;
        });

        items.clear();
        if (!UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            return;
        }
        int contactsIcon = R.drawable.menu_contacts;
        int savedIcon = R.drawable.menu_saved;
        int settingsIcon = R.drawable.menu_settings;
        int callsIcon = R.drawable.menu_calls;
        items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), contactsIcon));
        items.add(new Item(11, LocaleController.getString("SavedMessages", R.string.SavedMessages), savedIcon));
        items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), settingsIcon));
        items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), callsIcon));
        if (NekoConfig.useProxyItem.Bool() && (!NekoConfig.hideProxyByDefault.Bool() || SharedConfig.proxyEnabled)) {
            items.add(new CheckItem(13, LocaleController.getString("Proxy", R.string.Proxy), R.drawable.menu_policy, () -> SharedConfig.proxyEnabled, () -> {
                SharedConfig.setProxyEnable(!SharedConfig.proxyEnabled);
                return true;
            }));
        }
        if (NekoXConfig.disableStatusUpdate && !UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().bot) {
            boolean online = MessagesController.getInstance(UserConfig.selectedAccount).isOnline();
            String message = online ? StrUtil.upperFirst(LocaleController.getString("Online", R.string.Online)) : LocaleController.getString("VoipOfflineTitle", R.string.VoipOfflineTitle);
            if (NekoXConfig.keepOnlineStatus) {
                message += " (" + LocaleController.getString("Locked", R.string.Locked) + ")";
            }
            items.add(new CheckItem(14, message, R.drawable.msg_views, () -> online, () -> {
                MessagesController controller = MessagesController.getInstance(UserConfig.selectedAccount);
                controller.updateStatus(!online);
                return true;
            }));
        }
    }

    public int getId(int position) {
        position -= 2;
        if (accountsShown) {
            position -= getAccountRowsCount();
        }
        if (position < 0 || position >= items.size()) {
            return -1;
        }
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    public int getFirstAccountPosition() {
        if (!accountsShown) {
            return RecyclerView.NO_POSITION;
        }
        return 2;
    }

    public int getLastAccountPosition() {
        if (!accountsShown) {
            return RecyclerView.NO_POSITION;
        }
        return 1 + accountNumbers.size();
    }

    public CheckItem getItem(int position) {
        position -= 2;
        if (accountsShown) {
            position -= getAccountRowsCount();
        }
        if (position < 0 || position >= items.size()) {
            return null;
        }
        Item item = items.get(position);
        return item instanceof CheckItem ? (CheckItem) item : null;
    }

    private static class Item {
        public int icon;
        public String text;
        public int id;

        public Item(int id, String text, int icon) {
            this.icon = icon;
            this.id = id;
            this.text = text;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(id, text, icon);
        }
    }

    public class CheckItem extends Item {

        public Function0<Boolean> isChecked;
        public Function0<Boolean> doSwitch;

        public CheckItem(int id, String text, int icon, Function0<Boolean> isChecked, @Nullable Function0<Boolean> doSwitch) {
            super(id, text, icon);
            this.isChecked = isChecked;
            this.doSwitch = doSwitch;
        }

        public void bindCheck(DrawerActionCheckCell actionCell) {
            actionCell.setTextAndValueAndCheck(text, icon, null, isChecked.invoke(), false, false);
            if (doSwitch != null) {
                actionCell.setOnCheckClickListener((v) -> {
                    if (doSwitch.invoke()) {
                        actionCell.setChecked(isChecked.invoke());
                    }
                });
            }
        }

    }

}

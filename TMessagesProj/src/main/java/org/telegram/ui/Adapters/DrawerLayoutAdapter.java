/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Locale;

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

    public DrawerLayoutAdapter(Context context, SideMenultItemAnimator animator) {
        mContext = context;
        itemAnimator = animator;
        accountsShown = true;
        Theme.createDialogsResources(context);
        resetItems();
    }

    private int getAccountRowsCount() {
        int count = accountNumbers.size() + 1;
        if (accountNumbers.size() < UserConfig.MAX_ACCOUNT_COUNT) {
            count++;
        }
        return count;
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
                int selectedUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
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
                if (accountNumbers.size() < UserConfig.MAX_ACCOUNT_COUNT) {
                    if (i == accountNumbers.size()) {
                        return 5;
                    } else if (i == accountNumbers.size() + 1) {
                        return 2;
                    }
                } else {
                    if (i == accountNumbers.size()) {
                        return 2;
                    }
                }
            }
            i -= getAccountRowsCount();
        }
        if (items.get(i) == null) {
            return 2;
        }
        return items.get(i) instanceof CheckItem ? 6 : 3;
    }

    private void resetItems() {
        accountNumbers.clear();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                accountNumbers.add(a);
            } else {
                editor.remove(String.format(Locale.US, "account_pos_%d", a));
            }
        }
        editor.apply();
        Collections.sort(accountNumbers, (o1, o2) -> {
            long l1 = preferences.getLong(String.format(Locale.US, "account_pos_%d", o1), UserConfig.getInstance(o1).loginTime);
            long l2 = preferences.getLong(String.format(Locale.US, "account_pos_%d", o2), UserConfig.getInstance(o2).loginTime);
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
        int eventType = Theme.getEventType();
        int contactsIcon = R.drawable.baseline_perm_contact_calendar_24;
        int savedIcon = R.drawable.baseline_bookmark_24;
        int settingsIcon = R.drawable.baseline_settings_24;
        int inviteIcon = R.drawable.baseline_person_add_24;
        int helpIcon = R.drawable.baseline_help_24;
        items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), contactsIcon));
        items.add(new Item(11, LocaleController.getString("SavedMessages", R.string.SavedMessages), savedIcon));
        items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), settingsIcon));
        items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), inviteIcon));
        if (NekoConfig.useProxyItem && (!NekoConfig.hideProxyByDefault || SharedConfig.proxyEnabled)) {
            items.add(new CheckItem(13, LocaleController.getString("Proxy", R.string.Proxy), R.drawable.baseline_security_24, () -> SharedConfig.proxyEnabled, () -> {
                SharedConfig.setProxyEnable(!SharedConfig.proxyEnabled);
                return true;
            }));
        }
        if (NekoXConfig.disableStatusUpdate && !UserConfig.getInstance(UserConfig.selectedAccount).isBot) {
            boolean online = MessagesController.getInstance(UserConfig.selectedAccount).isOnline();
            String message = online ? StrUtil.upperFirst(LocaleController.getString("Online", R.string.Online)) : LocaleController.getString("VoipOfflineTitle", R.string.VoipOfflineTitle);
            if (NekoXConfig.keepOnlineStatus) {
                message += " (" + LocaleController.getString("Locked", R.string.Locked) + ")";
            }
            items.add(new CheckItem(14, message, R.drawable.baseline_visibility_24, () -> online, () -> {
                MessagesController controller = MessagesController.getInstance(UserConfig.selectedAccount);
                controller.updateStatus(!online);
                return true;
            }));
        }
        items.add(null); // divider
        items.add(new CheckItem(12, LocaleController.getString("DarkMode", R.string.NightMode), R.drawable.baseline_brightness_2_24, () -> Theme.getActiveTheme().isDark(), null));
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
            actionCell.setTextAndIcon(text, icon);
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

    public int getAccountsCount() {
        return accountNumbers.size();
    }

    public void swapAccountPosition(int currentAdapterPosition, int targetAdapterPosition) {
        int currentIndex = currentAdapterPosition - 2;
        int targetIndex = targetAdapterPosition - 2;
        int currentElement = accountNumbers.get(currentIndex);
        int targetElement = accountNumbers.get(targetIndex);
        accountNumbers.set(targetIndex, currentElement);
        accountNumbers.set(currentIndex, targetElement);
        ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE).edit().
                putLong(String.format(Locale.US, "account_pos_%d", currentElement), targetIndex).
                putLong(String.format(Locale.US, "account_pos_%d", targetElement), currentIndex)
                .apply();
        notifyItemMoved(currentAdapterPosition, targetAdapterPosition);
    }
}
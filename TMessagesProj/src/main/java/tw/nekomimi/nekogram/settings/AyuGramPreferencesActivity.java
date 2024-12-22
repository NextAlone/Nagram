/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package tw.nekomimi.nekogram.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
//import com.radolyn.ayugram.AyuConfig;
import tw.nekomimi.nekogram.NekoConfig;
import com.radolyn.ayugram.AyuConstants;
import com.radolyn.ayugram.utils.AyuState;
import org.jetbrains.annotations.NotNull;
import org.telegram.messenger.*;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.*;
import org.telegram.ui.Components.RecyclerListView;

import java.util.Locale;

public class AyuGramPreferencesActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {

    private static final int TOGGLE_BUTTON_VIEW = 1000;

    private int ghostEssentialsHeaderRow;
    private int ghostModeToggleRow;
    private int sendReadMessagePacketsRow;
    private int sendOnlinePacketsRow;
    private int sendUploadProgressRow;
    private int sendOfflinePacketAfterOnlineRow;
    private int markReadAfterSendRow;
    private int ghostDividerRow;
    private int showGhostToggleInDrawerRow;
    private boolean ghostModeMenuExpanded;

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        ghostEssentialsHeaderRow = newRow();
        ghostModeToggleRow = newRow();
        if (ghostModeMenuExpanded) {
            sendReadMessagePacketsRow = newRow();

            sendOnlinePacketsRow = newRow();
            sendUploadProgressRow = newRow();
            sendOfflinePacketAfterOnlineRow = newRow();
        } else {
            sendReadMessagePacketsRow = -1;
            sendOnlinePacketsRow = -1;
            sendUploadProgressRow = -1;
            sendOfflinePacketAfterOnlineRow = -1;
        }
        markReadAfterSendRow = newRow();
        ghostDividerRow = newRow();


        showGhostToggleInDrawerRow = newRow();


    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        // todo: register `MESSAGES_DELETED_NOTIFICATION` on all notification centers, not only on the current account

        NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, AyuConstants.MESSAGES_DELETED_NOTIFICATION);
        NotificationCenter.getGlobalInstance().addObserver(this, AyuConstants.AYUSYNC_STATE_CHANGED);

        return true;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
       return;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, AyuConstants.MESSAGES_DELETED_NOTIFICATION);
        NotificationCenter.getGlobalInstance().removeObserver(this, AyuConstants.AYUSYNC_STATE_CHANGED);
    }

    private void updateGhostViews() {
        var isActive = NekoConfig.isGhostModeActive();

        listAdapter.notifyItemChanged(ghostModeToggleRow, payload);
        listAdapter.notifyItemChanged(sendReadMessagePacketsRow, !isActive);
        listAdapter.notifyItemChanged(sendOnlinePacketsRow, !isActive);
        listAdapter.notifyItemChanged(sendUploadProgressRow, !isActive);
        listAdapter.notifyItemChanged(sendOfflinePacketAfterOnlineRow, isActive);

        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
    }


    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == ghostModeToggleRow) {
            ghostModeMenuExpanded ^= true;
            updateRowsId();
            listAdapter.notifyItemChanged(ghostModeToggleRow, payload);
            if (ghostModeMenuExpanded) {
                listAdapter.notifyItemRangeInserted(ghostModeToggleRow + 1, 4);
            } else {
                listAdapter.notifyItemRangeRemoved(ghostModeToggleRow + 1, 4);
            }
        } else if (position == sendReadMessagePacketsRow) {
            NekoConfig.editor.putBoolean("sendReadMessagePackets", NekoConfig.sendReadMessagePackets ^= true).apply();
            ((CheckBoxCell) view).setChecked(NekoConfig.sendReadMessagePackets, true);

            AyuState.setAllowReadPacket(false, -1);
            updateGhostViews();
        } else if (position == sendOnlinePacketsRow) {
            NekoConfig.editor.putBoolean("sendOnlinePackets", NekoConfig.sendOnlinePackets ^= true).apply();
            ((CheckBoxCell) view).setChecked(NekoConfig.sendOnlinePackets, true);

            updateGhostViews();
        } else if (position == sendUploadProgressRow) {
            NekoConfig.editor.putBoolean("sendUploadProgress", NekoConfig.sendUploadProgress ^= true).apply();
            ((CheckBoxCell) view).setChecked(NekoConfig.sendUploadProgress, true);

            updateGhostViews();
        } else if (position == sendOfflinePacketAfterOnlineRow) {
            NekoConfig.editor.putBoolean("sendOfflinePacketAfterOnline", NekoConfig.sendOfflinePacketAfterOnline ^= true).apply();
            ((CheckBoxCell) view).setChecked(NekoConfig.sendOfflinePacketAfterOnline, true);

            updateGhostViews();
        } else if (position == markReadAfterSendRow) {
            NekoConfig.editor.putBoolean("markReadAfterSend", NekoConfig.markReadAfterSend ^= true).apply();
            ((TextCheckCell) view).setChecked(NekoConfig.markReadAfterSend);

            AyuState.setAllowReadPacket(false, -1);

        } else if (position == showGhostToggleInDrawerRow) {
            NekoConfig.editor.putBoolean("showGhostToggleInDrawer", NekoConfig.showGhostToggleInDrawer ^= true).apply();
            ((TextCheckCell) view).setChecked(NekoConfig.showGhostToggleInDrawer);

            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
        }
    }

    @Override
    protected String getTitle() {
        return LocaleController.getString(R.string.AyuPreferences);
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    private int getGhostModeSelectedCount() {
        int count = 0;
        if (!NekoConfig.sendReadMessagePackets) count++;
        if (!NekoConfig.sendOnlinePackets) count++;
        if (!NekoConfig.sendUploadProgress) count++;
        if (NekoConfig.sendOfflinePacketAfterOnline) count++;

        return count;
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean payload) {
            switch (holder.getItemViewType()) {
                case 1:
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ghostEssentialsHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.GhostEssentialsHeader));
                    }
                    break;
                case 5:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == markReadAfterSendRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.MarkReadAfterSend), NekoConfig.markReadAfterSend, true);
                    } else if (position == showGhostToggleInDrawerRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.ShowGhostToggleInDrawer), NekoConfig.showGhostToggleInDrawer, true);
                    }
                    break;
                case 18:
                    TextCheckCell2 checkCell = (TextCheckCell2) holder.itemView;
                    if (position == ghostModeToggleRow) {
                        int selectedCount = getGhostModeSelectedCount();
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.GhostModeToggle), NekoConfig.isGhostModeActive(), true, true);
                        checkCell.setCollapseArrow(String.format(Locale.US, "%d/4", selectedCount), !ghostModeMenuExpanded, () -> {
                            NekoConfig.toggleGhostMode();
                            updateGhostViews();
                        });
                    }
                    checkCell.getCheckBox().setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
                    checkCell.getCheckBox().setDrawIconType(0);
                    break;
                case 19:
                    CheckBoxCell checkBoxCell = (CheckBoxCell) holder.itemView;
                    if (position == sendReadMessagePacketsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontSendReadPackets), "", !NekoConfig.sendReadMessagePackets, true, true);
                    } else if (position == sendOnlinePacketsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontSendOnlinePackets), "", !NekoConfig.sendOnlinePackets, true, true);
                    } else if (position == sendUploadProgressRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontSendUploadProgress), "", !NekoConfig.sendUploadProgress, true, true);
                    } else if (position == sendOfflinePacketAfterOnlineRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendOfflinePacketAfterOnline), "", NekoConfig.sendOfflinePacketAfterOnline, true, true);
                    }
                    checkBoxCell.setPad(1);
                    break;
            }
        }

        @NonNull
        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            if (viewType == TOGGLE_BUTTON_VIEW) {
                var view = new NotificationsCheckCell(mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                return new RecyclerListView.Holder(view);
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public int getItemViewType(int position) {
            if (
                    position == ghostDividerRow
            ) {
                return 1;
            } else if (
                    position == ghostEssentialsHeaderRow
            ) {
                return 3;
            } else if (
                    position == ghostModeToggleRow
            ) {
                return 18;
            } else if (
                    position >= sendReadMessagePacketsRow && position <= sendOfflinePacketAfterOnlineRow
            ) {
                return 19;
            }
            return 5;
        }
    }
}

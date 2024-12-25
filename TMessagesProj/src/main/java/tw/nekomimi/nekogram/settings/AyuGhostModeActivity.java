package tw.nekomimi.nekogram.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.radolyn.ayugram.AyuConfig;
import com.radolyn.ayugram.AyuConstants;
import com.radolyn.ayugram.utils.AyuState;
import org.jetbrains.annotations.NotNull;
import org.telegram.messenger.*;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.*;
import org.telegram.ui.Components.RecyclerListView;

import java.util.Locale;

public class AyuGhostModeActivity extends BaseNekoSettingsActivity {

    // Row constants
    private static final int ROW_COUNT = 8;

    // Row indices
    private int ghostEssentialsHeaderRow;
    private int ghostModeToggleRow;
    private int sendReadMessagePacketsRow;
    private int sendOnlinePacketsRow;
    private int sendUploadProgressRow;
    private int sendReadStoryPacketsRow;
    private int sendOfflinePacketAfterOnlineRow;
    private int markReadAfterSendRow;
    private int ghostDividerRow;
    private int showGhostToggleInDrawerRow;

    private boolean ghostModeMenuExpanded;

    @Override
    protected void updateRows() {
        super.updateRows();
        ghostEssentialsHeaderRow = addRow();
        ghostModeToggleRow = addRow();

        // Handle expanded menu rows
        if (ghostModeMenuExpanded) {
            sendReadMessagePacketsRow = addRow();
            sendOnlinePacketsRow = addRow();
            sendUploadProgressRow = addRow();
            sendReadStoryPacketsRow = addRow();
            sendOfflinePacketAfterOnlineRow = addRow();
        } else {
            resetExpandedRows();
        }

        markReadAfterSendRow = addRow();
        ghostDividerRow = addRow();
        showGhostToggleInDrawerRow = addRow();
    }

    private void resetExpandedRows() {
        sendReadMessagePacketsRow = -1;
        sendOnlinePacketsRow = -1;
        sendUploadProgressRow = -1;
        sendReadStoryPacketsRow = -1;
        sendOfflinePacketAfterOnlineRow = -1;
    }

    @Override
    public boolean onFragmentCreate() {
        // TODO: Register `MESSAGES_DELETED_NOTIFICATION` for all notification centers, not just the current account
        return super.onFragmentCreate();
    }

    private void updateGhostViews() {
        boolean isActive = AyuConfig.isGhostModeActive();
        listAdapter.notifyItemChanged(ghostModeToggleRow, PARTIAL);
        toggleRowState(sendReadMessagePacketsRow, !isActive);
        toggleRowState(sendOnlinePacketsRow, !isActive);
        toggleRowState(sendUploadProgressRow, !isActive);
        toggleRowState(sendReadStoryPacketsRow, !isActive);
        toggleRowState(sendOfflinePacketAfterOnlineRow, isActive);

        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
    }

    private void toggleRowState(int row, boolean enabled) {
        if (row != -1) {
            listAdapter.notifyItemChanged(row, enabled);
        }
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == ghostModeToggleRow) {
            toggleGhostMenu(view);
        } else if (position == sendReadMessagePacketsRow) {
            toggleConfig("sendReadMessagePackets", view);
        } else if (position == sendOnlinePacketsRow) {
            toggleConfig("sendOnlinePackets", view);
        } else if (position == sendUploadProgressRow) {
            toggleConfig("sendUploadProgress", view);
        } else if (position == sendReadStoryPacketsRow) {
            toggleConfig("sendReadStoryPackets", view);
        } else if (position == sendOfflinePacketAfterOnlineRow) {
            toggleConfig("sendOfflinePacketAfterOnline", view);
        } else if (position == markReadAfterSendRow) {
            toggleTextConfig("markReadAfterSend", view);
        } else if (position == showGhostToggleInDrawerRow) {
            toggleTextConfig("showGhostToggleInDrawer", view);
        }
    }

    private void toggleGhostMenu(View view) {
        ghostModeMenuExpanded ^= true;
        updateRows();
        listAdapter.notifyItemChanged(ghostModeToggleRow, PARTIAL);
        if (ghostModeMenuExpanded) {
            listAdapter.notifyItemRangeInserted(ghostModeToggleRow + 1, ROW_COUNT);
        } else {
            listAdapter.notifyItemRangeRemoved(ghostModeToggleRow + 1, ROW_COUNT);
        }
    }

    private void toggleConfig(String key, View view) {
        boolean newValue = !AyuConfig.getBoolean(key);
        AyuConfig.putBoolean(key, newValue);
        ((CheckBoxCell) view).setChecked(newValue, true);
        updateGhostViews();
    }

    private void toggleTextConfig(String key, View view) {
        boolean newValue = !AyuConfig.getBoolean(key);
        AyuConfig.putBoolean(key, newValue);
        ((TextCheckCell) view).setChecked(newValue);
    }

    @Override
    protected String getKey() {
        return "ghost";
    }

    @Override
    protected String getActionBarTitle() {
        return LocaleController.getString(R.string.AyuPreferences);
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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean payload) {
            // Handle row binding based on row type
            if (holder.getItemViewType() == TYPE_CHECKBOX2) {
                bindCheckBoxCell(holder, position);
            }
            // Handle other cases
        }

        private void bindCheckBoxCell(@NonNull RecyclerView.ViewHolder holder, int position) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) holder.itemView;
            if (position == sendReadMessagePacketsRow) {
                bindCheckBox(checkBoxCell, "sendReadMessagePackets", R.string.DontReadMessages);
            } else if (position == sendOnlinePacketsRow) {
                bindCheckBox(checkBoxCell, "sendOnlinePackets", R.string.DontSendOnlinePackets);
            } else if (position == sendUploadProgressRow) {
                bindCheckBox(checkBoxCell, "sendUploadProgress", R.string.DontSendUploadProgress);
            }
        }

        private void bindCheckBox(CheckBoxCell cell, String key, int textRes) {
            boolean value = AyuConfig.getBoolean(key);
            cell.setText(LocaleController.getString(textRes), "", value, true, true);
        }
    }
}

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
import com.radolyn.ayugram.AyuConfig;
import com.radolyn.ayugram.utils.AyuState;
import org.jetbrains.annotations.NotNull;
import org.telegram.messenger.*;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.*;
import org.telegram.ui.Components.RecyclerListView;

import java.util.Locale;

public class AyuGhostModeActivity extends BaseNekoSettingsActivity {

    private int GhostModeHeaderRow;
    private int GhostModeTitleRow;
    private int sendReadMessagePacketsRow;
    private int sendOnlinePacketsRow;
    private int sendUploadProgressRow;
    private int sendReadStotyPacketsRow;
    private int sendOfflinePacketAfterOnlineRow;
    private int markReadAfterSendRow;
    private int ghostDividerRow;
    private int showGhostToggleInDrawerRow;
    private boolean ghostModeMenuExpanded;

    @Override
    protected void updateRows() {
        super.updateRows();

        GhostModeHeaderRow = addRow();
        GhostModeTitleRow = addRow();
        if (ghostModeMenuExpanded) {
            sendReadMessagePacketsRow = addRow();
            sendOnlinePacketsRow = addRow();
            sendUploadProgressRow = addRow();
            sendReadStotyPacketsRow = addRow();
            sendOfflinePacketAfterOnlineRow = addRow();
        } else {
            sendReadMessagePacketsRow = -1;
            sendOnlinePacketsRow = -1;
            sendUploadProgressRow = -1;
            sendReadStotyPacketsRow = -1;
            sendOfflinePacketAfterOnlineRow = -1;
        }
        markReadAfterSendRow = addRow();
        ghostDividerRow = addRow();
        showGhostToggleInDrawerRow = addRow();
    }

    @Override
    public boolean onFragmentCreate() {
        // todo: register `MESSAGES_DELETED_NOTIFICATION` on all notification centers, not only on the current account
        super.onFragmentCreate();
        return true;
    }


    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    private void updateGhostViews() {
        var isActive = AyuConfig.isGhostModeActive();

        listAdapter.notifyItemChanged(GhostModeTitleRow, PARTIAL);
        listAdapter.notifyItemChanged(sendReadMessagePacketsRow, !isActive);
        listAdapter.notifyItemChanged(sendOnlinePacketsRow, !isActive);
        listAdapter.notifyItemChanged(sendUploadProgressRow, !isActive);
        listAdapter.notifyItemChanged(sendReadStotyPacketsRow,!isActive);
        listAdapter.notifyItemChanged(sendOfflinePacketAfterOnlineRow, isActive);

        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
    }


    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == GhostModeTitleRow) {
            ghostModeMenuExpanded ^= true;
            updateRows();
            listAdapter.notifyItemChanged(GhostModeTitleRow, PARTIAL);
            if (ghostModeMenuExpanded) {
                listAdapter.notifyItemRangeInserted(GhostModeTitleRow + 1, 5);
            } else {
                listAdapter.notifyItemRangeRemoved(GhostModeTitleRow + 1, 5);
            }
        } else if (position == sendReadMessagePacketsRow) {
            AyuConfig.editor.putBoolean("sendReadMessagePackets", AyuConfig.sendReadMessagePackets ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendReadMessagePackets, true);
            AyuState.setAllowReadPacket(false, -1);
            updateGhostViews();
        } else if (position == sendOnlinePacketsRow) {
            AyuConfig.editor.putBoolean("sendOnlinePackets", AyuConfig.sendOnlinePackets ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendOnlinePackets, true);
            updateGhostViews();
        } else if (position == sendUploadProgressRow) {
            AyuConfig.editor.putBoolean("sendUploadProgress", AyuConfig.sendUploadProgress ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendUploadProgress, true);
            updateGhostViews();
        } else if (position == sendReadStotyPacketsRow) {
            AyuConfig.editor.putBoolean("sendReadStotyPackets", AyuConfig.sendReadStotyPackets ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendReadStotyPackets, true);
            updateGhostViews();
        } else if (position == sendOfflinePacketAfterOnlineRow) {
            AyuConfig.editor.putBoolean("sendOfflinePacketAfterOnline", AyuConfig.sendOfflinePacketAfterOnline ^= true).apply();
            ((CheckBoxCell) view).setChecked(AyuConfig.sendOfflinePacketAfterOnline, true);
            updateGhostViews();
        } else if (position == markReadAfterSendRow) {
            AyuConfig.editor.putBoolean("markReadAfterSend", AyuConfig.markReadAfterSend ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.markReadAfterSend);
            AyuState.setAllowReadPacket(false, -1);
        } else if (position == showGhostToggleInDrawerRow) {
            AyuConfig.editor.putBoolean("showGhostToggleInDrawer", AyuConfig.showGhostToggleInDrawer ^= true).apply();
            ((TextCheckCell) view).setChecked(AyuConfig.showGhostToggleInDrawer);

            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
        }
    }

    @Override
    protected String getActionBarTitle() {
        return LocaleController.getString(R.string.GhostModeTitle);
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    private int getGhostModeSelectedCount() {
        int count = 0;
        if (!AyuConfig.sendReadMessagePackets) count++;
        if (!AyuConfig.sendOnlinePackets) count++;
        if (!AyuConfig.sendUploadProgress) count++;
        if (!AyuConfig.sendReadStotyPackets) count++;
        if (AyuConfig.sendOfflinePacketAfterOnline) count++;
        return count;
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean payload) {
            switch (holder.getItemViewType()) {
                case TYPE_SHADOW:
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case TYPE_HEADER:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == GhostModeHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.GhostModeHeader));
                    }
                    break;
                case TYPE_CHECK:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == markReadAfterSendRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.MarkReadAfterAction), AyuConfig.markReadAfterSend, true);
                    } else if (position == showGhostToggleInDrawerRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.ShowGhostToggleInDrawer), AyuConfig.showGhostToggleInDrawer, true);
                    }
                    break;
                case TYPE_CHECK2:
                    TextCheckCell2 textCheckCell2 = (TextCheckCell2) holder.itemView;
                    if (position == GhostModeTitleRow) {
                        int selectedCount = getGhostModeSelectedCount();
                        textCheckCell2.setTextAndCheck(LocaleController.getString(R.string.GhostModeTitle), AyuConfig.isGhostModeActive(), true, true);
                        textCheckCell2.setCollapseArrow(String.format(Locale.US, "%d/5", selectedCount), !ghostModeMenuExpanded, () -> {
                            AyuConfig.toggleGhostMode();
                            updateGhostViews();
                        });
                    }
                    textCheckCell2.getCheckBox().setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
                    textCheckCell2.getCheckBox().setDrawIconType(0);
                    break;
                case TYPE_CHECKBOX:
                    CheckBoxCell checkBoxCell = (CheckBoxCell) holder.itemView;
                    if (position == sendReadMessagePacketsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontReadMessages), "", !AyuConfig.sendReadMessagePackets, true, true);
                    } else if (position == sendOnlinePacketsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontSendOnlinePackets), "", !AyuConfig.sendOnlinePackets, true, true);
                    } else if (position == sendUploadProgressRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontSendUploadProgress), "", !AyuConfig.sendUploadProgress, true, true);
                    } else if (position == sendReadStotyPacketsRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.DontReadStories), "", !AyuConfig.sendReadStotyPackets, true, true);
                    } else if (position == sendOfflinePacketAfterOnlineRow) {
                        checkBoxCell.setText(LocaleController.getString(R.string.SendOfflinePacketAfterOnline), "", AyuConfig.sendOfflinePacketAfterOnline, true, true);
                    }
                    checkBoxCell.setPad(1);
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == ghostDividerRow) {
                return TYPE_SHADOW;
            }
            if (position == GhostModeHeaderRow) {
                return TYPE_HEADER;
            }
            if (position == GhostModeTitleRow) {
                return TYPE_CHECK2;
            }
            if (position >= sendReadMessagePacketsRow && position <= sendOfflinePacketAfterOnlineRow) {
                return TYPE_CHECKBOX;
            }
            if (position == markReadAfterSendRow || position == showGhostToggleInDrawerRow) {
                return TYPE_CHECK;
            }
            return super.getItemViewType(position);
        }
    }
}

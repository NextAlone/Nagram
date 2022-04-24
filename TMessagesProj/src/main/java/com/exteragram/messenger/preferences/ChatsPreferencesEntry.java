package com.exteragram.messenger.preferences;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.UndoView;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.cells.StickerSizePreviewCell;

public class ChatsPreferencesEntry extends BaseFragment {

    private int rowCount;
    private ListAdapter listAdapter;

    private ActionBarMenuItem resetItem;
    private StickerSizeCell stickerSizeCell;

    private int stickerSizeHeaderRow;
    private int stickerSizeRow;
    private int stickerSizeDividerRow;

    private int stickersHeaderRow;
    private int hideStickerTimeRow;
    private int unlimitedRecentStickersRow;
    private int stickersDividerRow;

    private int chatHeaderRow;
    private int hideSendAsChannelRow;
    private int hideKeyboardOnScrollRow;
    private int disableReactionsRow;
    private int disableGreetingStickerRow;
    private int disableJumpToNextChannelRow;
    private int archiveOnPullRow;
    private int dateOfForwardedMsgRow;
    private int chatDividerRow;

    private int mediaHeaderRow;
    private int rearVideoMessagesRow;
    private int disableCameraRow;
    private int disableProximityEventsRow;
    private int pauseOnMinimizeRow;
    private int disablePlaybackRow;
    private int mediaDividerRow;

    private UndoView restartTooltip;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRowsId(true);
        return true;
    }

    private class StickerSizeCell extends FrameLayout {

        private final StickerSizePreviewCell messagesCell;
        private final SeekBarView sizeBar;
        private final int startStickerSize = 8;
        private final int endStickerSize = 20;

        private final TextPaint textPaint;
        private int lastWidth;

        public StickerSizeCell (Context context) {
            super(context);

            setWillNotDraw(false);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(AndroidUtilities.dp(16));

            sizeBar = new SeekBarView(context);
            sizeBar.setReportChanges(true);
            sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public void onSeekBarDrag(boolean stop, float progress) {
                    sizeBar.getSeekBarAccessibilityDelegate().postAccessibilityEventRunnable(StickerSizeCell.this);
                    ExteraConfig.setStickerSize(startStickerSize + (endStickerSize - startStickerSize) * progress);
                    StickerSizeCell.this.invalidate();
                    if (resetItem.getVisibility() != VISIBLE) {
                        AndroidUtilities.updateViewVisibilityAnimated(resetItem, true, 0.5f, true);
                    }
                }

                @Override
                public void onSeekBarPressed(boolean pressed) {

                }
            });
            sizeBar.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
            addView(sizeBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.LEFT | Gravity.TOP, 9, 5, 43, 11));

            messagesCell = new StickerSizePreviewCell(context, parentLayout);
            messagesCell.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            addView(messagesCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 53, 0, 0));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText(String.valueOf(Math.round(ExteraConfig.stickerSize)), getMeasuredWidth() - AndroidUtilities.dp(39), AndroidUtilities.dp(28), textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (lastWidth != width) {
                sizeBar.setProgress((ExteraConfig.stickerSize - startStickerSize) / (float) (endStickerSize - startStickerSize));
                lastWidth = width;
            }
        }

        @Override
        public void invalidate() {
            super.invalidate();
            lastWidth = -1;
            messagesCell.invalidate();
            sizeBar.invalidate();
        }

        @Override
        public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(event);
            sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityEvent(this, event);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, info);
        }

        @Override
        public boolean performAccessibilityAction(int action, Bundle arguments) {
            return super.performAccessibilityAction(action, arguments) || sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, action, arguments);
        }
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("Chats", R.string.Chats));
        actionBar.setAllowOverlayTitle(false);

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }

        ActionBarMenu menu = actionBar.createMenu();
        resetItem = menu.addItem(0, R.drawable.msg_reset);
        resetItem.setContentDescription(LocaleController.getString("Reset", R.string.Reset));
        resetItem.setVisibility(ExteraConfig.stickerSize != 14.0f ? View.VISIBLE : View.GONE);
        resetItem.setTag(null);
        resetItem.setOnClickListener(v -> {
            AndroidUtilities.updateViewVisibilityAnimated(resetItem, false, 0.5f, true);
            ValueAnimator animator = ValueAnimator.ofFloat(ExteraConfig.stickerSize, 14.0f);
            animator.setDuration(200);
            animator.addUpdateListener(valueAnimator -> {
                ExteraConfig.setStickerSize((Float) valueAnimator.getAnimatedValue());
                stickerSizeCell.invalidate();
            });
            animator.start();
        });


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
            if (position == hideStickerTimeRow) {
                ExteraConfig.toggleHideStickerTime();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.hideStickerTime);
                }
            } else if (position == unlimitedRecentStickersRow) {
                ExteraConfig.toggleUnlimitedRecentStickers();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.unlimitedRecentStickers);
                }
            } else if (position == hideSendAsChannelRow) {
                ExteraConfig.toggleHideSendAsChannel();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.hideSendAsChannel);
                }
            } else if (position == hideKeyboardOnScrollRow) {
                ExteraConfig.toggleHideKeyboardOnScroll();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.hideKeyboardOnScroll);
                }
            } else if (position == archiveOnPullRow) {
                ExteraConfig.toggleArchiveOnPull();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.archiveOnPull);
                }
            } else if (position == dateOfForwardedMsgRow) {
                ExteraConfig.toggleDateOfForwardedMsg();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.dateOfForwardedMsg);
                }
            } else if (position == disableReactionsRow) {
                ExteraConfig.toggleDisableReactions();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableReactions);
                }

                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == disableGreetingStickerRow) {
                ExteraConfig.toggleDisableGreetingSticker();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableGreetingSticker);
                }

                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == disableJumpToNextChannelRow) {
                ExteraConfig.toggleDisableJumpToNextChannel();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableJumpToNextChannel);
                }

                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == rearVideoMessagesRow) {
                ExteraConfig.toggleRearVideoMessages();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.rearVideoMessages);
                }
            } else if (position == disableCameraRow) {
                ExteraConfig.toggleDisableCamera();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableCamera);
                }
            } else if (position == disableProximityEventsRow) {
                ExteraConfig.toggleDisableProximityEvents();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableProximityEvents);
                }
            } else if (position == pauseOnMinimizeRow) {
                ExteraConfig.togglePauseOnMinimize();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.pauseOnMinimize);
                }
            } else if (position == disablePlaybackRow) {
                ExteraConfig.toggleDisablePlayback();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disablePlayback);
                }
                restartTooltip.showWithAction(0, UndoView.ACTION_CACHE_WAS_CLEARED, null, null);
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

        stickerSizeHeaderRow = rowCount++;
        stickerSizeRow = rowCount++;
        stickerSizeDividerRow = rowCount++;

        stickersHeaderRow = rowCount++;
        hideStickerTimeRow = rowCount++;
        unlimitedRecentStickersRow = rowCount++;
        stickersDividerRow = rowCount++;

        chatHeaderRow = rowCount++;
        hideSendAsChannelRow = rowCount++;
        hideKeyboardOnScrollRow = rowCount++;
        disableReactionsRow = rowCount++;
        disableGreetingStickerRow = rowCount++;
        disableJumpToNextChannelRow = rowCount++;
        archiveOnPullRow = rowCount++;
        dateOfForwardedMsgRow = rowCount++;
        chatDividerRow = rowCount++;

        mediaHeaderRow = rowCount++;
        rearVideoMessagesRow = rowCount++;
        disableCameraRow = rowCount++;
        disableProximityEventsRow = rowCount++;
        pauseOnMinimizeRow = rowCount++;
        disablePlaybackRow = rowCount++;
        mediaDividerRow = rowCount++;

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
                    if (position == stickerSizeHeaderRow) {
                        headerCell.setText(LocaleController.getString("StickerSize", R.string.StickerSize));
                    } else if (position == stickersHeaderRow) {
                        headerCell.setText(LocaleController.getString("AccDescrStickers", R.string.AccDescrStickers));
                    } else if (position == chatHeaderRow) {
                        headerCell.setText(LocaleController.getString("Chats", R.string.Chats));
                    } else if (position == mediaHeaderRow) {
                        headerCell.setText(LocaleController.getString("Media", R.string.Media));
                    }
                    break;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == hideStickerTimeRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("StickerTime", R.string.StickerTime), ExteraConfig.hideStickerTime, true);
                    } else if (position == unlimitedRecentStickersRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("UnlimitedRecentStickers", R.string.UnlimitedRecentStickers), ExteraConfig.unlimitedRecentStickers, false);
                    } else if (position == hideSendAsChannelRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("HideSendAsChannel", R.string.HideSendAsChannel), ExteraConfig.hideSendAsChannel, true);
                    } else if (position == hideKeyboardOnScrollRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("HideKeyboardOnScroll", R.string.HideKeyboardOnScroll), ExteraConfig.hideKeyboardOnScroll, true);
                    } else if (position == archiveOnPullRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ArchiveOnPull", R.string.ArchiveOnPull), ExteraConfig.archiveOnPull, true);
                    } else if (position == dateOfForwardedMsgRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DateOfForwardedMsg", R.string.DateOfForwardedMsg), ExteraConfig.dateOfForwardedMsg, false);
                    } else if (position == disableReactionsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableReactions", R.string.DisableReactions), ExteraConfig.disableReactions, true);
                    } else if (position == disableGreetingStickerRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableGreetingSticker", R.string.DisableGreetingSticker), ExteraConfig.disableGreetingSticker, true);
                    } else if (position == disableJumpToNextChannelRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableJumpToNextChannel", R.string.DisableJumpToNextChannel), ExteraConfig.disableJumpToNextChannel, true);
                    } else if (position == rearVideoMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("RearVideoMessages", R.string.RearVideoMessages), ExteraConfig.rearVideoMessages, true);
                    } else if (position == disableCameraRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableCamera", R.string.DisableCamera), ExteraConfig.disableCamera, true);
                    } else if (position == disableProximityEventsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableProximityEvents", R.string.DisableProximityEvents), ExteraConfig.disableProximityEvents, true);
                    } else if (position == pauseOnMinimizeRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("PauseOnMinimize", R.string.PauseOnMinimize), LocaleController.getString("POMDescription", R.string.POMDescription), ExteraConfig.pauseOnMinimize, true, true);
                    } else if (position == disablePlaybackRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DisablePlayback", R.string.DisablePlayback), LocaleController.getString("DPDescription", R.string.DPDescription), ExteraConfig.disablePlayback, true, false);
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
                    view = stickerSizeCell = new StickerSizeCell(mContext);
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
            if (position == stickerSizeDividerRow || position == stickersDividerRow || position == chatDividerRow || position == mediaDividerRow) {
                return 1;
            } else if (position == stickerSizeHeaderRow || position == stickersHeaderRow || position == chatHeaderRow || position == mediaHeaderRow) {
                return 2;
            } else if (position == hideStickerTimeRow || position == unlimitedRecentStickersRow || position == hideSendAsChannelRow ||
                       position == hideKeyboardOnScrollRow || position == archiveOnPullRow || position == dateOfForwardedMsgRow ||
                       position == disableReactionsRow || position == disableGreetingStickerRow || position == disableJumpToNextChannelRow ||
                       position == rearVideoMessagesRow || position == disableCameraRow || position == disableProximityEventsRow ||
                       position == pauseOnMinimizeRow || position == disablePlaybackRow) {
                return 3;
            } else if (position == stickerSizeRow) {
                return 4;
            }
            return 1;
        }
    }
}

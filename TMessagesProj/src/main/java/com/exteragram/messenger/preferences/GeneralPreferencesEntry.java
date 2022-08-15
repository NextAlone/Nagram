package com.exteragram.messenger.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.text.TextPaint;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.UndoView;

import com.exteragram.messenger.ExteraConfig;

public class GeneralPreferencesEntry extends BaseFragment {

    private int rowCount;
    private ListAdapter listAdapter;

    private AvatarCornersCell avatarCornersCell;
    
    private int avatarCornersHeaderRow;
    private int avatarCornersRow;
    private int avatarCornersDividerRow;

    private int generalHeaderRow;
    private int formatTimeWithSecondsRow;
    private int disableNumberRoundingRow;
    private int chatsOnTitleRow;
    private int disableVibrationRow;
    private int forceTabletModeRow;
    private int disableAnimatedAvatarsRow;
    private int generalDividerRow;

    private int profileHeaderRow;
    private int showIDRow;
    private int showDCRow;
    private int hidePhoneNumberRow;
    private int profileDividerRow;

    private int archiveHeaderRow;
    private int archiveOnPullRow;
    private int disableUnarchiveSwipeRow;
    private int forcePacmanAnimationRow;
    private int forcePacmanAnimationInfoRow;

    private UndoView restartTooltip;

    private class AvatarCornersCell extends FrameLayout {

        private final SeekBarView sizeBar;
        private final FrameLayout preview;
        private final int startCornersSize = 0;
        private final int endCornersSize = 30;

        private final TextPaint textPaint;
        private int lastWidth;

        public AvatarCornersCell(Context context) {
            super(context);

            setWillNotDraw(false);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

            sizeBar = new SeekBarView(context);
            sizeBar.setReportChanges(true);
            sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public void onSeekBarDrag(boolean stop, float progress) {
                    sizeBar.getSeekBarAccessibilityDelegate().postAccessibilityEventRunnable(AvatarCornersCell.this);
                    ExteraConfig.setAvatarCorners(startCornersSize + (endCornersSize - startCornersSize) * progress);
                    AvatarCornersCell.this.invalidate();
                    preview.invalidate();
                    parentLayout.rebuildAllFragmentViews(false, false);
                }

                @Override
                public void onSeekBarPressed(boolean pressed) {

                }
            });
            sizeBar.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
            addView(sizeBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.LEFT | Gravity.TOP, 9, 5, 43, 11));

            preview = new FrameLayout(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    int color = Theme.getColor(Theme.key_switchTrack);
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);

                    int w = getMeasuredWidth();
                    int h = getMeasuredHeight();

                    Theme.dialogs_onlineCirclePaint.setColor(Color.argb(41, r, g, b));
                    canvas.drawRoundRect(0, 0, w, h, AndroidUtilities.dp(6), AndroidUtilities.dp(6), Theme.dialogs_onlineCirclePaint);
                    canvas.drawRoundRect(AndroidUtilities.dp(92), h / 2.0f + AndroidUtilities.dp(8), AndroidUtilities.dp(230), h / 2.0f + AndroidUtilities.dp(18), w / 2.0f, w / 2.0f, Theme.dialogs_onlineCirclePaint);

                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_chats_onlineCircle));
                    canvas.drawCircle(AndroidUtilities.dp(69), h / 2.0f + AndroidUtilities.dp(21), AndroidUtilities.dp(7), Theme.dialogs_onlineCirclePaint);

                    Path online = new Path();
                    online.addCircle(AndroidUtilities.dp(69), h / 2.0f + AndroidUtilities.dp(21), AndroidUtilities.dp(12), Path.Direction.CCW);
                    canvas.clipPath(online, Region.Op.DIFFERENCE);

                    Theme.dialogs_onlineCirclePaint.setColor(Color.argb(90, r, g, b));
                    canvas.drawRoundRect(AndroidUtilities.dp(21), h / 2.0f - AndroidUtilities.dp(28), AndroidUtilities.dp(77), h / 2.0f + AndroidUtilities.dp(28), ExteraConfig.getAvatarCorners(56), ExteraConfig.getAvatarCorners(56), Theme.dialogs_onlineCirclePaint);
                    canvas.drawCircle(AndroidUtilities.dp(70), h / 2.0f + AndroidUtilities.dp(22), AndroidUtilities.dp(8), Theme.dialogs_onlineCirclePaint);
                    canvas.drawRoundRect(AndroidUtilities.dp(92), h / 2.0f - AndroidUtilities.dp(8), AndroidUtilities.dp(170), h / 2.0f - AndroidUtilities.dp(18), w / 2.0f, w / 2.0f, Theme.dialogs_onlineCirclePaint);

                    textPaint.setTextSize(AndroidUtilities.dp(14));
                    textPaint.setColor(Color.argb(91, r, g, b));
                    textPaint.setTextAlign(Paint.Align.RIGHT);
                    textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    canvas.drawText(LocaleController.getInstance().formatterDay.format(System.currentTimeMillis()), w - AndroidUtilities.dp(20), h / 2.0f - AndroidUtilities.dp(8), textPaint);
                }
            };
            preview.setWillNotDraw(false);
            addView(preview, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 90, Gravity.TOP | Gravity.CENTER, 21, 50, 21, 10));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            textPaint.setTextSize(AndroidUtilities.dp(16));
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rregular.ttf"));
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(String.valueOf(Math.round(ExteraConfig.avatarCorners)), getMeasuredWidth() - AndroidUtilities.dp(39), AndroidUtilities.dp(28), textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (lastWidth != width) {
                sizeBar.setProgress((ExteraConfig.avatarCorners - startCornersSize) / (float) (endCornersSize - startCornersSize));
                lastWidth = width;
            }
        }

        @Override
        public void invalidate() {
            super.invalidate();
            lastWidth = -1;
            sizeBar.invalidate();
            preview.invalidate();
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
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRowsId(true);
        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("General", R.string.General));
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
            if (position == disableNumberRoundingRow) {
                ExteraConfig.toggleDisableNumberRounding();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableNumberRounding);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == formatTimeWithSecondsRow) {
                ExteraConfig.toggleFormatTimeWithSeconds();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.formatTimeWithSeconds);
                }
                LocaleController.getInstance().recreateFormatters();
                parentLayout.rebuildAllFragmentViews(false, false);
                avatarCornersCell.invalidate();
            } else if (position == chatsOnTitleRow) {
                ExteraConfig.toggleChatsOnTitle();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.chatsOnTitle);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == disableVibrationRow) {
                ExteraConfig.toggleDisableVibration();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableVibration);
                }
                restartTooltip.showWithAction(0, UndoView.ACTION_CACHE_WAS_CLEARED, null, null);
            } else if (position == forceTabletModeRow) {
                ExteraConfig.toggleForceTabletMode();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.forceTabletMode);
                }
                restartTooltip.showWithAction(0, UndoView.ACTION_CACHE_WAS_CLEARED, null, null);
            } else if (position == disableAnimatedAvatarsRow) {
                ExteraConfig.toggleDisableAnimatedAvatars();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableAnimatedAvatars);
                }
            } else if (position == archiveOnPullRow) {
                ExteraConfig.toggleArchiveOnPull();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.archiveOnPull);
                }
            } else if (position == disableUnarchiveSwipeRow) {
                ExteraConfig.toggleDisableUnarchiveSwipe();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.disableUnarchiveSwipe);
                }
            } else if (position == forcePacmanAnimationRow) {
                ExteraConfig.toggleForcePacmanAnimation();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.forcePacmanAnimation);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == hidePhoneNumberRow) {
                ExteraConfig.toggleHidePhoneNumber();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.hidePhoneNumber);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            } else if (position == showIDRow) {
                ExteraConfig.toggleShowID();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.showID);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
            } else if (position == showDCRow) {
                ExteraConfig.toggleShowDC();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.showDC);
                }
                parentLayout.rebuildAllFragmentViews(false, false);
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

        avatarCornersHeaderRow = rowCount++;
        avatarCornersRow = rowCount++;
        avatarCornersDividerRow = rowCount++;
        
        generalHeaderRow = rowCount++;
        disableNumberRoundingRow = rowCount++;
        formatTimeWithSecondsRow = rowCount++;
        chatsOnTitleRow = rowCount++;
        disableVibrationRow = rowCount++;
        disableAnimatedAvatarsRow = rowCount++;
        forceTabletModeRow = rowCount++;
        generalDividerRow = rowCount++;

        profileHeaderRow = rowCount++;
        hidePhoneNumberRow = rowCount++;
        showIDRow = rowCount++;
        showDCRow = rowCount++;
        profileDividerRow = rowCount++;

        archiveHeaderRow = rowCount++;
        archiveOnPullRow = rowCount++;
        disableUnarchiveSwipeRow = rowCount++;
        forcePacmanAnimationRow = rowCount++;
        forcePacmanAnimationInfoRow = rowCount++;

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
                    if (position == generalHeaderRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                    } else if (position == archiveHeaderRow) {
                        headerCell.setText(LocaleController.getString("ArchivedChats", R.string.ArchivedChats));
                    } else if (position == profileHeaderRow) {
                        headerCell.setText(LocaleController.getString("Profile", R.string.Profile));
                    } else if (position == avatarCornersHeaderRow) {
                        headerCell.setText(LocaleController.getString("AvatarCorners", R.string.AvatarCorners));
                    }
                    break;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setEnabled(true, null);
                    if (position == disableNumberRoundingRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DisableNumberRounding", R.string.DisableNumberRounding), "1.23K -> 1,234", ExteraConfig.disableNumberRounding, true, true);
                    } else if (position == formatTimeWithSecondsRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("FormatTimeWithSeconds", R.string.FormatTimeWithSeconds), "12:34 -> 12:34:56", ExteraConfig.formatTimeWithSeconds, true, true);
                    } else if (position == chatsOnTitleRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ChatsOnTitle", R.string.ChatsOnTitle), ExteraConfig.chatsOnTitle, true);
                    } else if (position == disableVibrationRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableVibration", R.string.DisableVibration), ExteraConfig.disableVibration, true);
                    } else if (position == disableAnimatedAvatarsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableAnimatedAvatars", R.string.DisableAnimatedAvatars), ExteraConfig.disableAnimatedAvatars, true);
                    } else if (position == forceTabletModeRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ForceTabletMode", R.string.ForceTabletMode), ExteraConfig.forceTabletMode, false);
                    } else if (position == disableUnarchiveSwipeRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableUnarchiveSwipe", R.string.DisableUnarchiveSwipe), ExteraConfig.disableUnarchiveSwipe, true);
                    } else if (position == archiveOnPullRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ArchiveOnPull", R.string.ArchiveOnPull), ExteraConfig.archiveOnPull, true);
                    } else if (position == forcePacmanAnimationRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ForcePacmanAnimation", R.string.ForcePacmanAnimation), ExteraConfig.forcePacmanAnimation, false);
                    } else if (position == hidePhoneNumberRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("HidePhoneNumber", R.string.HidePhoneNumber), ExteraConfig.hidePhoneNumber, true);
                    } else if (position == showIDRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ShowID", R.string.ShowID), ExteraConfig.showID, true);
                    } else if (position == showDCRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ShowDC", R.string.ShowDC), ExteraConfig.showDC, false);
                    }
                    break;
                case 4:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == forcePacmanAnimationInfoRow) {
                        cell.setText(LocaleController.getString("ForcePacmanAnimationInfo", R.string.ForcePacmanAnimationInfo));
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 3;
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
                    view = new TextInfoPrivacyCell(mContext);
                    break;
                case 5:
                    view = avatarCornersCell = new AvatarCornersCell(mContext);
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
            if (position == generalDividerRow || position == profileDividerRow || position == avatarCornersDividerRow) {
                return 1;
            } else if (position == generalHeaderRow || position == archiveHeaderRow || position == profileHeaderRow || position == avatarCornersHeaderRow) {
                return 2;
            } else if (position == disableNumberRoundingRow || position == formatTimeWithSecondsRow || position == hidePhoneNumberRow || position == showIDRow || position == showDCRow || position == chatsOnTitleRow || position == archiveOnPullRow ||
                      position == disableVibrationRow || position == forceTabletModeRow || position == disableUnarchiveSwipeRow || position == forcePacmanAnimationRow || position == disableAnimatedAvatarsRow) {
                return 3;
            } else if (position == forcePacmanAnimationInfoRow) {
                return 4;
            } else if (position == avatarCornersRow) {
                return 5;
            }
            return 1;
        }
    }
}

package com.exteragram.messenger.preferences;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;

import com.exteragram.messenger.ExteraConfig;

public class AppearancePreferencesEntry extends BaseFragment {

    private int rowCount;
    private ListAdapter listAdapter;
    private ValueAnimator statusBarColorAnimate;

    private int applicationHeaderRow;
    private int useSystemFontsRow;
    private int useSystemEmojiRow;
    private int transparentStatusBarRow;
    private int blurForAllThemesRow;
    private int centerTitleRow;
    private int newSwitchStyleRow;
    private int transparentNavBarRow;
    private int squareFabRow;
    private int applicationDividerRow;

    private int generalHeaderRow;
    private int hideAllChatsRow;
    private int hidePhoneNumberRow;
    private int showIDRow;
    private int chatsOnTitleRow;
    private int disableVibrationRow;
    private int forceTabletModeRow;

    private UndoView restartTooltip;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRowsId(true);
        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("Appearance", R.string.Appearance));
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
            if (position == useSystemFontsRow) {
                ExteraConfig.toggleUseSystemFonts();
                AndroidUtilities.clearTypefaceCache();
                Parcelable recyclerViewState = null;
                if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                AlertDialog progressDialog = new AlertDialog(context, 3);
                progressDialog.show();
                AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
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
                restartTooltip.showWithAction(0, UndoView.ACTION_CACHE_WAS_CLEARED, null, null);
            } else if (position == hideAllChatsRow) {
                ExteraConfig.toggleHideAllChats();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.hideAllChats);
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
            } else if (position == centerTitleRow) {
                ExteraConfig.toggleCenterTitle();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.centerTitle);
                }
                Parcelable recyclerViewState = null;
                if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                AlertDialog progressDialog = new AlertDialog(context, 3);
                progressDialog.show();
                AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
                listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            } else if (position == newSwitchStyleRow) {
                ExteraConfig.toggleNewSwitchStyle();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.newSwitchStyle);
                }
                Parcelable recyclerViewState = null;
                if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                AlertDialog progressDialog = new AlertDialog(context, 3);
                progressDialog.show();
                AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
                listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            } else if (position == transparentNavBarRow) {
                ExteraConfig.toggleTransparentNavBar();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.newSwitchStyle);
                }
                Parcelable recyclerViewState = null;
                if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                AlertDialog progressDialog = new AlertDialog(context, 3);
                progressDialog.show();
                AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
                listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            } else if (position == squareFabRow) {
                ExteraConfig.toggleSquareFab();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ExteraConfig.squareFab);
                }
                Parcelable recyclerViewState = null;
                if (listView.getLayoutManager() != null) recyclerViewState = listView.getLayoutManager().onSaveInstanceState();
                parentLayout.rebuildAllFragmentViews(true, true);
                AlertDialog progressDialog = new AlertDialog(context, 3);
                progressDialog.show();
                AndroidUtilities.runOnUIThread(progressDialog::dismiss, 400);
                listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
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

        applicationHeaderRow = rowCount++;
        useSystemFontsRow = rowCount++;
        useSystemEmojiRow = rowCount++;
        transparentStatusBarRow = rowCount++;
        blurForAllThemesRow = rowCount++;
        centerTitleRow = rowCount++;
        newSwitchStyleRow = rowCount++;
        transparentNavBarRow = rowCount++;
        squareFabRow = rowCount++;
        applicationDividerRow = rowCount++;

        generalHeaderRow = rowCount++;
        hideAllChatsRow = rowCount++;
        hidePhoneNumberRow = rowCount++;
        showIDRow = rowCount++;
        chatsOnTitleRow = rowCount++;
        disableVibrationRow = rowCount++;
        forceTabletModeRow = rowCount++;

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
                    if (position == applicationHeaderRow) {
                        headerCell.setText(LocaleController.getString("Appearance", R.string.Appearance));
                    } else if (position == generalHeaderRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                    }
                    break;
                case 3:
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
                    } else if (position == transparentNavBarRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("TransparentNavBar", R.string.TransparentNavBar), ExteraConfig.transparentNavBar, true);
                    } else if (position == squareFabRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SquareFab", R.string.SquareFab), ExteraConfig.squareFab, false);
                    } else if (position == hideAllChatsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("HideAllChats", R.string.HideAllChats), ExteraConfig.hideAllChats, true);
                    } else if (position == hidePhoneNumberRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("HidePhoneNumber", R.string.HidePhoneNumber), ExteraConfig.hidePhoneNumber, true);
                    } else if (position == showIDRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ShowID", R.string.ShowID), ExteraConfig.showID, true);
                    } else if (position == chatsOnTitleRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ChatsOnTitle", R.string.ChatsOnTitle), ExteraConfig.chatsOnTitle, true);
                    } else if (position == disableVibrationRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("DisableVibration", R.string.DisableVibration), ExteraConfig.disableVibration, true);
                    } else if (position == forceTabletModeRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ForceTabletMode", R.string.ForceTabletMode), ExteraConfig.forceTabletMode, false);
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
                default:
                    view = new ShadowSectionCell(mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == applicationDividerRow) {
                return 1;
            } else if (position == applicationHeaderRow || position == generalHeaderRow) {
                return 2;
            } else if (position == useSystemFontsRow || position == useSystemEmojiRow || position == transparentStatusBarRow || position == transparentNavBarRow ||
                       position == squareFabRow || position == blurForAllThemesRow || position == centerTitleRow || position == newSwitchStyleRow || position == hideAllChatsRow ||
                       position == hidePhoneNumberRow || position == showIDRow || position == chatsOnTitleRow || position == disableVibrationRow || position == forceTabletModeRow) {
                return 3;
            }
            return 1;
        }
    }
}

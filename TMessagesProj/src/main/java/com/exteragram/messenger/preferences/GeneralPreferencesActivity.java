/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

package com.exteragram.messenger.preferences;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exteragram.messenger.ExteraConfig;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;

public class GeneralPreferencesActivity extends BasePreferencesActivity {

    private int speedBoostersHeaderRow;
    private int downloadSpeedChooserRow;
    private int uploadSpeedBoostRow;
    private int speedBoostersDividerRow;

    private int generalHeaderRow;
    private int formatTimeWithSecondsRow;
    private int disableNumberRoundingRow;
    private int chatsOnTitleRow;
    private int disableVibrationRow;
    private int forceTabletModeRow;
    private int generalDividerRow;

    private int profileHeaderRow;
    private int showIDRow;
    private int showDCRow;
    private int hidePhoneNumberRow;
    private int profileDividerRow;

    private int premiumHeaderRow;
    private int disableAnimatedAvatarsRow;
    private int premiumAutoPlaybackRow;
    private int hidePremiumStickersTabRow;
    private int hideFeaturedEmojisTabsRow;
    private int premiumDividerRow;

    private int archiveHeaderRow;
    private int archiveOnPullRow;
    private int disableUnarchiveSwipeRow;
    private int forcePacmanAnimationRow;
    private int forcePacmanAnimationInfoRow;

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        speedBoostersHeaderRow = newRow();
        downloadSpeedChooserRow = newRow();
        uploadSpeedBoostRow = newRow();
        speedBoostersDividerRow = newRow();

        generalHeaderRow = newRow();
        disableNumberRoundingRow = newRow();
        formatTimeWithSecondsRow = newRow();
        chatsOnTitleRow = newRow();
        disableVibrationRow = newRow();
        forceTabletModeRow = newRow();
        generalDividerRow = newRow();

        profileHeaderRow = newRow();
        hidePhoneNumberRow = newRow();
        showIDRow = newRow();
        showDCRow = newRow();
        profileDividerRow = newRow();

        premiumHeaderRow = newRow();
        disableAnimatedAvatarsRow = newRow();
        premiumAutoPlaybackRow = newRow();
        hidePremiumStickersTabRow = getUserConfig().isPremium() ? newRow() : -1;
        hideFeaturedEmojisTabsRow = newRow();
        premiumDividerRow = newRow();

        archiveHeaderRow = newRow();
        archiveOnPullRow = newRow();
        disableUnarchiveSwipeRow = newRow();
        forcePacmanAnimationRow = newRow();
        forcePacmanAnimationInfoRow = newRow();
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == disableNumberRoundingRow) {
            ExteraConfig.editor.putBoolean("disableNumberRounding", ExteraConfig.disableNumberRounding ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.disableNumberRounding);
            parentLayout.rebuildAllFragmentViews(false, false);
        } else if (position == formatTimeWithSecondsRow) {
            ExteraConfig.editor.putBoolean("formatTimeWithSeconds", ExteraConfig.formatTimeWithSeconds ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.formatTimeWithSeconds);
            LocaleController.getInstance().recreateFormatters();
            parentLayout.rebuildAllFragmentViews(false, false);
        } else if (position == chatsOnTitleRow) {
            ExteraConfig.editor.putBoolean("chatsOnTitle", ExteraConfig.chatsOnTitle ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.chatsOnTitle);
            parentLayout.rebuildAllFragmentViews(false, false);
        } else if (position == disableVibrationRow) {
            ExteraConfig.editor.putBoolean("disableVibration", ExteraConfig.disableVibration ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.disableVibration);
            showBulletin();
        } else if (position == forceTabletModeRow) {
            ExteraConfig.editor.putBoolean("forceTabletMode", ExteraConfig.forceTabletMode ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.forceTabletMode);
            showBulletin();
        } else if (position == disableAnimatedAvatarsRow) {
            ExteraConfig.editor.putBoolean("disableAnimatedAvatars", ExteraConfig.disableAnimatedAvatars ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.disableAnimatedAvatars);
        } else if (position == archiveOnPullRow) {
            ExteraConfig.editor.putBoolean("archiveOnPull", ExteraConfig.archiveOnPull ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.archiveOnPull);
        } else if (position == disableUnarchiveSwipeRow) {
            ExteraConfig.editor.putBoolean("disableUnarchiveSwipe", ExteraConfig.disableUnarchiveSwipe ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.disableUnarchiveSwipe);
        } else if (position == forcePacmanAnimationRow) {
            ExteraConfig.editor.putBoolean("forcePacmanAnimation", ExteraConfig.forcePacmanAnimation ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.forcePacmanAnimation);
            parentLayout.rebuildAllFragmentViews(false, false);
        } else if (position == hidePhoneNumberRow) {
            ExteraConfig.editor.putBoolean("hidePhoneNumber", ExteraConfig.hidePhoneNumber ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.hidePhoneNumber);
            parentLayout.rebuildAllFragmentViews(false, false);
            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
        } else if (position == showIDRow) {
            ExteraConfig.editor.putBoolean("showID", ExteraConfig.showID ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.showID);
            parentLayout.rebuildAllFragmentViews(false, false);
        } else if (position == showDCRow) {
            ExteraConfig.editor.putBoolean("showDC", ExteraConfig.showDC ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.showDC);
            parentLayout.rebuildAllFragmentViews(false, false);
        } else if (position == premiumAutoPlaybackRow) {
            ExteraConfig.editor.putBoolean("premiumAutoPlayback", ExteraConfig.premiumAutoPlayback ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.premiumAutoPlayback);
        } else if (position == hidePremiumStickersTabRow) {
            ExteraConfig.editor.putBoolean("hidePremiumStickersTab", ExteraConfig.hidePremiumStickersTab ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.hidePremiumStickersTab);
        } else if (position == hideFeaturedEmojisTabsRow) {
            ExteraConfig.editor.putBoolean("hideFeaturedEmojisTabs", ExteraConfig.hideFeaturedEmojisTabs ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.hideFeaturedEmojisTabs);
        } else if (position == uploadSpeedBoostRow) {
            ExteraConfig.editor.putBoolean("uploadSpeedBoost", ExteraConfig.uploadSpeedBoost ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.uploadSpeedBoost);
        }
    }

    @Override
    protected String getTitle() {
        return LocaleController.getString("General", R.string.General);
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
        public int getItemCount() {
            return rowCount;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            if (type == 13) {
                SlideChooseView slideChooseView = new SlideChooseView(mContext);
                slideChooseView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                return new RecyclerListView.Holder(slideChooseView);
            }
            return super.onCreateViewHolder(parent, type);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == generalHeaderRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                    } else if (position == archiveHeaderRow) {
                        headerCell.setText(LocaleController.getString("ArchivedChats", R.string.ArchivedChats));
                    } else if (position == profileHeaderRow) {
                        headerCell.setText(LocaleController.getString("Profile", R.string.Profile));
                    } else if (position == premiumHeaderRow) {
                        headerCell.setText(LocaleController.getString("TelegramPremium", R.string.TelegramPremium));
                    } else if (position == speedBoostersHeaderRow) {
                        headerCell.setText(LocaleController.getString("DownloadSpeedBoost", R.string.DownloadSpeedBoost));
                    }
                    break;
                case 5:
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
                    } else if (position == premiumAutoPlaybackRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("PremiumAutoPlayback", R.string.PremiumAutoPlayback), ExteraConfig.premiumAutoPlayback, true);
                    } else if (position == hidePremiumStickersTabRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("HidePremiumStickersTab", R.string.HidePremiumStickersTab), ExteraConfig.hidePremiumStickersTab, true);
                    } else if (position == hideFeaturedEmojisTabsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("HideFeaturedEmojisTabs", R.string.HideFeaturedEmojisTabs), ExteraConfig.hideFeaturedEmojisTabs, false);
                    } else if (position == uploadSpeedBoostRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("UploadSpeedBoost", R.string.UploadSpeedBoost), ExteraConfig.uploadSpeedBoost, false);
                    }
                    break;
                case 8:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == forcePacmanAnimationInfoRow) {
                        cell.setText(LocaleController.getString("ForcePacmanAnimationInfo", R.string.ForcePacmanAnimationInfo));
                        cell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        cell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                case 13:
                    SlideChooseView slide = (SlideChooseView) holder.itemView;
                    if (position == downloadSpeedChooserRow) {
                        slide.setNeedDivider(true);
                        slide.setCallback(index -> ExteraConfig.editor.putInt("downloadSpeedBoost", ExteraConfig.downloadSpeedBoost = index).apply());
                        slide.setOptions(ExteraConfig.downloadSpeedBoost, LocaleController.getString("BlurOff", R.string.BlurOff), LocaleController.getString("SpeedFast", R.string.SpeedFast), LocaleController.getString("Ultra", R.string.Ultra));
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == generalDividerRow || position == profileDividerRow ||
                position == premiumDividerRow || position == speedBoostersDividerRow) {
                return 1;
            } else if (position == generalHeaderRow || position == archiveHeaderRow || position == profileHeaderRow ||
                       position == premiumHeaderRow || position == speedBoostersHeaderRow) {
                return 3;
            } else if (position == forcePacmanAnimationInfoRow) {
                return 8;
            } else if (position == downloadSpeedChooserRow) {
                return 13;
            }
            return 5;
        }
    }
}
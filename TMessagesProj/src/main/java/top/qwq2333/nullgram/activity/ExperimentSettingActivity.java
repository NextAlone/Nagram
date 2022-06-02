package top.qwq2333.nullgram.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
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
import top.qwq2333.nullgram.utils.Defines;

@SuppressLint("NotifyDataSetChanged")
public class ExperimentSettingActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;

    private int experimentRow;
    private int blockSponsorAdsRow;
    private int syntaxHighlightRow;
    private int aliasChannelRow;
    private int keepFormattingRow;
    private int enchantAudioRow;
    private int linkedUserRow;
    private int overrideChannelAliasRow;
    private int experiment2Row;


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows();

        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("Experiment", R.string.Experiment));

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
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);
        ((DefaultItemAnimator) listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == blockSponsorAdsRow) {
                ConfigManager.toggleBoolean(Defines.blockSponsorAds);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.blockSponsorAds));
                }
            } else if (position == syntaxHighlightRow) {
                ConfigManager.putBoolean(Defines.codeSyntaxHighlight, ConfigManager.getBooleanOrDefault(Defines.codeSyntaxHighlight, true));
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrDefault(Defines.codeSyntaxHighlight, true));
                }
            } else if (position == aliasChannelRow) {
                boolean currentStatus = ConfigManager.getBooleanOrFalse(Defines.channelAlias);
                if (!currentStatus && !ConfigManager.getBooleanOrFalse(Defines.labelChannelUser)) {
                    ConfigManager.putBoolean(Defines.labelChannelUser, true);
                }
                ConfigManager.toggleBoolean(Defines.channelAlias);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.channelAlias));
                }
            } else if (position == keepFormattingRow) {
                ConfigManager.toggleBoolean(Defines.keepCopyFormatting);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.keepCopyFormatting));
                }
            } else if (position == enchantAudioRow) {
                ConfigManager.toggleBoolean(Defines.enchantAudio);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.enchantAudio));
                }
            } else if (position == linkedUserRow) {
                ConfigManager.toggleBoolean(Defines.linkedUser);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.linkedUser));
                }
            } else if (position == overrideChannelAliasRow) {
                ConfigManager.toggleBoolean(Defines.overrideChannelAlias);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ConfigManager.getBooleanOrFalse(Defines.overrideChannelAlias));
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


    private void updateRows() {
        rowCount = 0;

        experimentRow = rowCount++;
        if (ConfigManager.getBooleanOrFalse(Defines.showHiddenSettings)) {
            blockSponsorAdsRow = rowCount++;
        }
        syntaxHighlightRow = rowCount++;
        aliasChannelRow = rowCount++;
        keepFormattingRow = rowCount++;
        enchantAudioRow = rowCount++;
        linkedUserRow = rowCount++;
        if (ConfigManager.getBooleanOrFalse(Defines.linkedUser) && ConfigManager.getBooleanOrFalse(Defines.labelChannelUser)) {
            overrideChannelAliasRow = rowCount++;
        } else {
            overrideChannelAliasRow = -1;
        }
        experiment2Row = rowCount++;

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

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
                    if (position == experiment2Row) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
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
                    if (position == blockSponsorAdsRow) {
                        textCell.setTextAndCheck(LocaleController.getString("blockSponsorAds", R.string.blockSponsorAds), ConfigManager.getBooleanOrFalse(Defines.blockSponsorAds), true);
                    } else if (position == syntaxHighlightRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("codeSyntaxHighlight", R.string.codeSyntaxHighlight), LocaleController.getString("codeSyntaxHighlightDetails", R.string.codeSyntaxHighlightDetails), ConfigManager.getBooleanOrFalse(Defines.codeSyntaxHighlight), true, true);
                    } else if (position == aliasChannelRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("channelAlias", R.string.channelAlias), LocaleController.getString("channelAliasDetails", R.string.channelAliasDetails), ConfigManager.getBooleanOrFalse(Defines.channelAlias), true, true);
                    } else if (position == keepFormattingRow) {
                        textCell.setTextAndCheck(LocaleController.getString("keepFormatting", R.string.keepFormatting), ConfigManager.getBooleanOrFalse(Defines.keepCopyFormatting), true);
                    } else if (position == enchantAudioRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("enchantAudioRow", R.string.enchantAudio), LocaleController.getString("enchantAudioDetails", R.string.enchantAudioDetails), ConfigManager.getBooleanOrFalse(Defines.enchantAudio), true, true);
                    } else if (position == linkedUserRow) {
                        textCell.setTextAndValueAndCheck(LocaleController.getString("linkUser", R.string.linkUser), LocaleController.getString("linkUserDetails", R.string.linkUserDetails), ConfigManager.getBooleanOrFalse(Defines.linkedUser), true, true);
                    } else if (position == overrideChannelAliasRow) {
                        textCell.setTextAndValueAndCheck(
                            ConfigManager.getBooleanOrFalse(Defines.channelAlias) ?
                                LocaleController.getString("overrideChannelAlias", R.string.overrideChannelAlias) :
                                LocaleController.getString("labelChannelInChat", R.string.labelChannelInChat),
                            LocaleController.getString("overrideChannelAliasDetails", R.string.overrideChannelAliasDetails),
                            ConfigManager.getBooleanOrFalse(Defines.overrideChannelAlias), true, true);
                    }
                    break;
                }
                case 4: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == experimentRow) {
                        headerCell.setText(LocaleController.getString("Experiment", R.string.Experiment));
                    }
                    break;
                }
                case 5: {
                    NotificationsCheckCell textCell = (NotificationsCheckCell) holder.itemView;
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
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == experiment2Row) {
                return 1;
            } else if (position == experimentRow) {
                return 4;
            }
            return 3;
        }
    }
}

/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

package com.exteragram.messenger.preferences;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;

import com.exteragram.messenger.components.InfoSettingsCell;
import com.exteragram.messenger.ExteraUtils;
import com.exteragram.messenger.updater.UpdaterBottomSheet;

public class MainPreferencesActivity extends BasePreferencesActivity {

    private int categoryHeaderRow;
    private int generalRow;
    private int appearanceRow;
    private int chatsRow;

    private int categoryDividerRow;
    private int aboutExteraDividerRow;

    private int infoHeaderRow;
    private int aboutExteraRow;
    private int sourceCodeRow;
    private int channelRow;
    private int groupRow;
    private int crowdinRow;
    private int infoDividerRow;

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        aboutExteraRow = newRow();
        aboutExteraDividerRow = newRow();

        categoryHeaderRow = newRow();
        generalRow = newRow();
        appearanceRow = newRow();
        chatsRow = newRow();
        categoryDividerRow = newRow();

        infoHeaderRow = newRow();
        channelRow = newRow();
        groupRow = newRow();
        crowdinRow = newRow();
        sourceCodeRow = newRow();
        infoDividerRow = newRow();
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == aboutExteraRow) {
            (new UpdaterBottomSheet(getParentActivity(), false)).show();
        } else if (position == sourceCodeRow) {
            Browser.openUrl(getParentActivity(), "https://github.com/exteraSquad/exteraGram");
        } else if (position == channelRow) {
            MessagesController.getInstance(currentAccount).openByUserName(("exteragram"), this, 1);
        } else if (position == groupRow) {
            MessagesController.getInstance(currentAccount).openByUserName(("exterachat"), this, 1);
        } else if (position == crowdinRow) {
            Browser.openUrl(getParentActivity(), "https://crowdin.com/project/exteralocales");
        } else if (position == appearanceRow) {
            presentFragment(new AppearancePreferencesActivity());
        } else if (position == chatsRow) {
            presentFragment(new ChatsPreferencesActivity());
        } else if (position == generalRow) {
            presentFragment(new GeneralPreferencesActivity());
        }
    }

    @Override
    protected String getTitle() {
        return LocaleController.getString("Preferences", R.string.Preferences);
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

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    if (position == infoDividerRow) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                case 2:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == generalRow) {
                        textCell.setTextAndIcon(LocaleController.getString("General", R.string.General), R.drawable.msg_media, true);
                    } else if (position == appearanceRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Appearance", R.string.Appearance), R.drawable.msg_theme, true);
                    } else if (position == chatsRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Chats", R.string.Chats), R.drawable.msg_discussion, true);
                    } else if (position == channelRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("Channel", R.string.Channel), "@exteragram", R.drawable.msg_channel, true);
                    } else if (position == groupRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("Chats", R.string.Chats), "@exterachat", R.drawable.msg_markunread, true);
                    } else if (position == crowdinRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("Crowdin", R.string.Crowdin), "Crowdin", R.drawable.msg_translate, true);
                    } else if (position == sourceCodeRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("SourceCode", R.string.SourceCode), "Github", R.drawable.msg_delete, false);
                    }
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    break;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == categoryHeaderRow) {
                        headerCell.setText(LocaleController.getString("Categories", R.string.Categories));
                    } else if (position == infoHeaderRow){
                        headerCell.setText(LocaleController.getString("Links", R.string.Links));
                    }
                    break;
                case 4:
                    InfoSettingsCell textDetailCell = (InfoSettingsCell) holder.itemView;
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == categoryDividerRow || position == aboutExteraDividerRow || position == infoDividerRow) {
                return 1;
            } else if (position == infoHeaderRow || position == categoryHeaderRow) {
                return 3;
            } else if (position == aboutExteraRow) {
                return 4;
            }
            return 2;
        }
    }
}

/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.GnuGeneralPublicLicense20;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
import io.noties.markwon.Markwon;
import java.util.ArrayList;
import java.util.List;
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
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;

public class LicenseActivity  extends BaseFragment  {
    private final Notices notices = new Notices();
    private ListView listView;

    @Override
    public View createView(Context context) {
        super.onFragmentCreate();
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("OpenSource", R.string.OpenSource));

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

        Notice markwon = new Notice("Markwon", "https://github.com/noties/Markwon",
            "Copyright 2017 Dimitry Ivanov (mail@dimitryivanov.ru)", new ApacheSoftwareLicense20());
        Notice nekogram = new Notice("Nekogram", "https://gitlab.com/Nekogram/Nekogram",
            "Author: NekoInverter", new GnuGeneralPublicLicense20());
        Notice nekox = new Notice("NekoX",
            "https://github.com/NekoX-dev/NekoX",
            "Author: NekoX-Dev",
            new GnuGeneralPublicLicense20());
        Notice genuine = new Notice("Genuine",
            "https://github.com/brevent/genuine",
        "Copyright brevent",
        new CCBYNCSA40());
        notices.addNotice(markwon);
        notices.addNotice(nekogram);
        notices.addNotice(nekox);
        notices.addNotice(genuine);
        notices.addNotice(LicensesDialog.LICENSES_DIALOG_NOTICE);


        BaseAdapter mAdapter = new BaseAdapter() {
            private final List<Notice> mNotices = notices.getNotices();
            private final LayoutInflater inflater = getParentActivity().getLayoutInflater();
            private final Markwon markwon = Markwon.create(context);

            @Override
            public int getCount() {
                return mNotices.size();
            }

            @Override
            public Object getItem(int position) {
                return mNotices.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Notice notice = mNotices.get(position);
                TextView title;
                TextView licenseView;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.simple_license_item, parent, false);
                    title = convertView.findViewById(R.id.sLicenseItem_title);
                    licenseView = convertView.findViewById(R.id.sLicenseItem_licensePrev);
                    licenseView.setTypeface(Typeface.MONOSPACE);
                } else {
                    title = convertView.findViewById(R.id.sLicenseItem_title);
                    licenseView = convertView.findViewById(R.id.sLicenseItem_licensePrev);
                }
                markwon.setMarkdown(title,
                    "- " + notice.getName() + "  \n(<" + notice.getUrl() + ">)");
                licenseView.setText(notice.getCopyright() + "\n\n" + notice.getLicense()
                    .getSummaryText(context));
                return convertView;
            }
        };


        listView = new ListView(context, null);
        listView.setId(R.id.list_view);
        listView.setDivider(null);
        listView.setAdapter(mAdapter);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.addView(listView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return fragmentView;

    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        return true;
    }


    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(
            new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR,
                new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class,
                    HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class},
                null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(
            new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null,
                null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null,
                null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(
            new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null,
                null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null,
                null, Theme.key_avatar_actionBarIconBlue));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null,
                null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null,
                null, null, Theme.key_avatar_actionBarSelectorBlue));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null,
                null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(
            new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null,
                null, Theme.key_actionBarDefaultSubmenuItem));

        themeDescriptions.add(
            new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null,
                Theme.key_listSelector));

        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null,
                null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER,
            new Class[]{ShadowSectionCell.class}, null, null, null,
            Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class},
            new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class},
            new String[]{"valueTextView"}, null, null, null,
            Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                new String[]{"textView"}, null, null, null,
                Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                new String[]{"valueTextView"}, null, null, null,
                Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class},
                new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
            new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
            new String[]{"valueTextView"}, null, null, null,
            Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
            new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class},
            new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class},
            new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class},
                new String[]{"textView"}, null, null, null,
                Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(
            new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class},
                new String[]{"valueTextView"}, null, null, null,
                Theme.key_windowBackgroundWhiteGrayText2));

        return themeDescriptions;
    }

    private class CCBYNCSA40 extends License {

            @Override
            public String getName() {
                return "Cretive Common Attribution-NonCommercial-ShareAlike 4.0 International";
            }

            @Override
            public String readSummaryTextFromResources(final Context context) {
                return getContent(context, R.raw.cc_by_nc_sa_40_summary);
            }

            @Override
            public String readFullTextFromResources(final Context context) {
                return getContent(context, R.raw.cc_by_nc_sa_40_full);
            }

            @Override
            public String getVersion() {
                return "4.0";
            }

            @Override
            public String getUrl() {
                return "https://creativecommons.org/licenses/by-nc-sa/4.0/";
            }
    }


}

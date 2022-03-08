/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Timer;

import kotlin.Unit;
import tw.nekomimi.nekogram.ui.BottomBuilder;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.ShareUtil;

public class LanguageSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ListAdapter searchListViewAdapter;
    private EmptyTextProgressView emptyView;

    private boolean searchWas;
    private boolean searching;

    private Timer searchTimer;
    private ArrayList<LocaleController.LocaleInfo> searchResult;
    private ArrayList<LocaleController.LocaleInfo> sortedLanguages;
    private ArrayList<LocaleController.LocaleInfo> unofficialLanguages;

    @Override
    public boolean onFragmentCreate() {
        fillLanguages();
        LocaleController.getInstance().loadRemoteLanguages(currentAccount);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    }

    @Override
    public View createView(Context context) {
        searching = false;
        searchWas = false;

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Language", R.string.Language));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                searching = true;
            }

            @Override
            public void onSearchCollapse() {
                search(null);
                searching = false;
                searchWas = false;
                if (listView != null) {
                    emptyView.setVisibility(View.GONE);
                    listView.setAdapter(listAdapter);
                }
            }

            @Override
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                search(text);
                if (text.length() != 0) {
                    searchWas = true;
                    if (listView != null) {
                        listView.setAdapter(searchListViewAdapter);
                    }
                } else {
                    searching = false;
                    searchWas = false;
                    if (listView != null) {
                        emptyView.setVisibility(View.GONE);
                        listView.setAdapter(listAdapter);
                    }
                }
            }
        });
        item.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));

        listAdapter = new ListAdapter(context, false);
        searchListViewAdapter = new ListAdapter(context, true);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        emptyView = new EmptyTextProgressView(context);
        emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        emptyView.showTextView();
        emptyView.setShowAtCenter(true);
        frameLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new RecyclerListView(context);
        listView.setEmptyView(emptyView);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (getParentActivity() == null || parentLayout == null || !(view instanceof LanguageCell)) {
                return;
            }
            LanguageCell cell = (LanguageCell) view;
            LocaleController.LocaleInfo localeInfo = cell.getCurrentLocale();
            if (localeInfo != null) {
                if (localeInfo.toInstall) {
                    AlertsCreator.createLanguageAlert((LaunchActivity) getParentActivity(),localeInfo.pack,() -> {
                        finishFragment();
                    }).show();
                    return;
                }
                LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, currentAccount);
                parentLayout.rebuildAllFragmentViews(false, false);
            }
            finishFragment();
        });

        // NekoX: Merge 8.4.1, Remove offcial changes
        listView.setOnItemLongClickListener((view, position) -> {
            if (getParentActivity() == null || parentLayout == null || !(view instanceof LanguageCell)) {
                return false;
            }
            LanguageCell cell = (LanguageCell) view;
            LocaleController.LocaleInfo localeInfo = cell.getCurrentLocale();

            if (localeInfo == null || localeInfo.pathToFile == null || localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE) {
                return false;
            }

            BottomBuilder builder = new BottomBuilder(getParentActivity());

            builder.addItem(LocaleController.getString("BotShare",R.string.BotShare),R.drawable.msg_share,false,(__) -> {

                ShareUtil.shareText(getParentActivity(),"https://t.me/setlanguage/" + localeInfo.shortName.replace('_','-'));

                return Unit.INSTANCE;

            });

            if (!localeInfo.isBuiltIn()) {

                builder.addItem(LocaleController.getString("DeleteLocalizationTitle", R.string.DeleteLocalizationTitle), R.drawable.msg_delete, true, (__) -> {

                    AlertUtil.showConfirm(getParentActivity(),
                            LocaleController.getString("DeleteLocalizationTitle", R.string.DeleteLocalizationTitle),
                            R.drawable.msg_delete,
                            LocaleController.getString("Delete", R.string.Delete), true, () -> {

                                if (LocaleController.getInstance().deleteLanguage(localeInfo, currentAccount)) {
                                    fillLanguages();
                                    if (searchResult != null) {
                                        searchResult.remove(localeInfo);
                                    }
                                    if (listAdapter != null) {
                                        listAdapter.notifyDataSetChanged();
                                    }
                                    if (searchListViewAdapter != null) {
                                        searchListViewAdapter.notifyDataSetChanged();
                                    }
                                }

                            });

                    return Unit.INSTANCE;

                });

            }

            builder.show();

            return true;
        });

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack) {
            if (listAdapter != null) {
                fillLanguages();
                AndroidUtilities.runOnUIThread(() -> { listAdapter.notifyDataSetChanged(); });
            }
        }
    }

    private void fillLanguages() {
        final LocaleController.LocaleInfo currentLocale = LocaleController.getInstance().getCurrentLocaleInfo();
        Comparator<LocaleController.LocaleInfo> comparator = (o, o2) -> {
            if (o == currentLocale) {
                return -1;
            } else if (o2 == currentLocale) {
                return 1;
            } else if (o.serverIndex == o2.serverIndex) {
                return o.name.compareTo(o2.name);
            }
            if (o.serverIndex > o2.serverIndex) {
                return 1;
            } else if (o.serverIndex < o2.serverIndex) {
                return -1;
            }
            return 0;
        };

        sortedLanguages = new ArrayList<>();
        unofficialLanguages = new ArrayList<>(LocaleController.getInstance().unofficialLanguages);

        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        for (int a = 0, size = arrayList.size(); a < size; a++) {
            LocaleController.LocaleInfo info = arrayList.get(a);
            if (info.serverIndex != Integer.MAX_VALUE) {
                sortedLanguages.add(info);
            } else {
                unofficialLanguages.add(info);
            }
        }
        Collections.sort(sortedLanguages, comparator);
        Collections.sort(unofficialLanguages, comparator);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void search(final String query) {
        if (query == null) {
            searching = false;
            searchResult = null;
            if (listView != null) {
                emptyView.setVisibility(View.GONE);
                listView.setAdapter(listAdapter);
            }
        } else {
//            try {
//                if (searchTimer != null) {
//                    searchTimer.cancel();
//                }
//            } catch (Exception e) {
//                FileLog.e(e);
//            }
//            searchTimer = new Timer();
//            searchTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                try {
//                    searchTimer.cancel();
//                    searchTimer = null;
//                } catch (Exception e) {
//                    FileLog.e(e);
//                }
                processSearch(query);
//                }
//            }, 100, 300);
        }
    }

    private void processSearch(final String query) {
        Utilities.searchQueue.postRunnable(() -> {

            String q = query.trim().toLowerCase();
            if (q.length() == 0) {
                updateSearchResults(new ArrayList<>());
                return;
            }
            long time = System.currentTimeMillis();
            ArrayList<LocaleController.LocaleInfo> resultArray = new ArrayList<>();

            boolean noSearch = false;

            for (int a = 0, N = unofficialLanguages.size(); a < N; a++) {
                LocaleController.LocaleInfo c = unofficialLanguages.get(a);
                if (c.name.toLowerCase().contains(query) || c.nameEnglish.toLowerCase().contains(query)) {
                    resultArray.add(c);
                } else if (c.shortName.contains(query)) {
                    resultArray.add(c);
                    if (c.shortName.equals(query)) {
                        noSearch = true;
                    }
                }
            }

            for (int a = 0, N = sortedLanguages.size(); a < N; a++) {
                LocaleController.LocaleInfo c = sortedLanguages.get(a);
                if (c.name.toLowerCase().contains(query) || c.nameEnglish.toLowerCase().contains(query)) {
                    resultArray.add(c);
                } else if (c.shortName.contains(query)) {
                    resultArray.add(c);
                    if (c.shortName.equals(query)) {
                        noSearch = true;
                    }
                }
            }

            updateSearchResults(resultArray);

            if (!noSearch) {

                TLRPC.TL_langpack_getLanguage req = new TLRPC.TL_langpack_getLanguage();
                req.lang_code = query.replace('_', '-');
                req.lang_pack = "android";
                ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                    if (response instanceof TLRPC.TL_langPackLanguage) {
                        TLRPC.TL_langPackLanguage res = (TLRPC.TL_langPackLanguage) response;
                        if (res.strings_count == 0) return;
                        resultArray.add(new LocaleController.LocaleInfo() {{
                            name = res.native_name;
                            nameEnglish = res.name;
                            shortName = res.lang_code;
                            pluralLangCode = res.plural_code;
                            baseLangCode = res.base_lang_code;
                            isRtl = res.rtl;
                            if (res.official) {
                                pathToFile = "remote";
                            } else {
                                pathToFile = "unofficial";
                            }
                            toInstall = true;
                            pack = res;
                        }});
                        updateSearchResults(resultArray);
                    }
                }));

            }

        });
    }

    private void updateSearchResults(final ArrayList<LocaleController.LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(() -> {
            searchResult = arrCounties;
            searchListViewAdapter.notifyDataSetChanged();
        });
    }

    // NekoX: Merge 8.4.1, remove TranslateSettings

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean isSearch) {
            mContext = context;
            search = isSearch;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override
        public int getItemCount() {
            if (search) {
                if (searchResult == null) {
                    return 0;
                }
                return searchResult.size();
            } else {
                int count = sortedLanguages.size();
                if (count != 0) {
                    count++;
                }
                if (!unofficialLanguages.isEmpty()) {
                    count += unofficialLanguages.size() + 1;
                }
                return count;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0: {
                    view = new LanguageCell(mContext, false);
//                    view = new TextRadioCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                }
//                case 2:
//                    TranslateSettings translateSettings = new TranslateSettings(mContext);
//                    view = translateSettings;
//                    break;
//                case 3:
//                    HeaderCell header = new HeaderCell(mContext);
//                    header.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    header.setText(LocaleController.getString("Language", R.string.Language));
//                    view = header;
//                    break;
                case 1:
                default: {
                    view = new ShadowSectionCell(mContext);
                    break;
                }
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    LanguageCell textSettingsCell = (LanguageCell) holder.itemView;
//                    TextRadioCell textSettingsCell = (TextRadioCell) holder.itemView;
                    LocaleController.LocaleInfo localeInfo;
                    boolean last;
                    if (search) {
                        localeInfo = searchResult.get(position);
                        last = position == searchResult.size() - 1;
                    } else if (!unofficialLanguages.isEmpty() && position >= 0 && position < unofficialLanguages.size()) {
                        localeInfo = unofficialLanguages.get(position);
                        last = position == unofficialLanguages.size() - 1;
                    } else {
                        if (!unofficialLanguages.isEmpty()) {
                            position -= unofficialLanguages.size() + 1;
                        }
                        localeInfo = sortedLanguages.get(position);
                        last = position == sortedLanguages.size() - 1;
                    }
                    if (localeInfo.isLocal()) {
                        textSettingsCell.setLanguage(LanguageSelectActivity.this, localeInfo, String.format("%1$s (%2$s)", localeInfo.name, LocaleController.getString("LanguageCustom", R.string.LanguageCustom)), !last);
                    } else {
                        textSettingsCell.setLanguage(LanguageSelectActivity.this, localeInfo, null, !last);
                    }
                    textSettingsCell.setLanguageSelected(localeInfo == LocaleController.getInstance().getCurrentLocaleInfo());
                    break;
                }
                case 1: {
                    ShadowSectionCell sectionCell = (ShadowSectionCell) holder.itemView;
                    if (!unofficialLanguages.isEmpty() && position == unofficialLanguages.size()) {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (search) {
                return 0;
            }
            if (!unofficialLanguages.isEmpty() && (i == unofficialLanguages.size() || i == unofficialLanguages.size() + sortedLanguages.size() + 1) || unofficialLanguages.isEmpty() && i == sortedLanguages.size()) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon));

        return themeDescriptions;
    }
}

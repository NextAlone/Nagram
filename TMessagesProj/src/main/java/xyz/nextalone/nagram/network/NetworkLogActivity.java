package xyz.nextalone.nagram.network;

import android.content.Context;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleActionBar;
import org.telegram.ui.ActionBar.SimpleActionBarMenu;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeBind;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

import tw.nekomimi.nekogram.database.NetworkLogItem;

public class NetworkLogActivity extends BaseFragment {

    private RecyclerListView listView;
    private NetworkLogAdapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView emptyView;
    private ProgressBar progressBar;
    private ActionBarMenuItem searchItem;

    private List<NetworkLogItem> allLogs = new ArrayList<>();
    private List<NetworkLogItem> filteredLogs = new ArrayList<>();
    private String currentSearchQuery = "";

    private boolean isSearchMode = false;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NetworkLogDb.init();
        return true;
    }

    @Override
    protected boolean useSimpleActionBar() {
        return true;
    }

    @Override
    public SimpleActionBar createSimpleActionBar(Context context) {
        simpleActionBar = super.createSimpleActionBar(context);
        simpleActionBar.setTitle(LocaleController.getString(R.string.NetworkLog));
        simpleActionBar.setBackButtonImage(R.drawable.ic_ab_back);
        simpleActionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (isSearchMode) {
                        exitSearchMode();
                    } else {
                        finishFragment();
                    }
                } else if (id == 1) {
                    showClearConfirmDialog();
                }
            }
        });
        return simpleActionBar;
    }

    @Override
    public View createView(Context context) {
        setHasOwnBackground(true);

        SimpleActionBarMenu menu = simpleActionBar.createMenu();
        menu.clearItems();

        searchItem = menu.addItem(2, R.drawable.msg_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                isSearchMode = true;
                currentSearchQuery = "";
                performSearch("");
            }

            @Override
            public void onSearchCollapse() {
                exitSearchMode();
            }

            @Override
            public void onTextChanged(EditText editText) {
                currentSearchQuery = editText.getText().toString();
                performSearch(currentSearchQuery);
            }
        });
        searchItem.setSearchFieldHint(LocaleController.getString(R.string.SearchLogs));

        ActionBarMenuItem clearMenuItem = menu.addItem(1, R.drawable.menu_delete_old);
        clearMenuItem.setContentDescription(LocaleController.getString(R.string.ClearHistory));

        fragmentView = new FrameLayout(context) {
            @Override
            protected void dispatchDraw(@NonNull Canvas canvas) {
                super.dispatchDraw(canvas);
                glassEngine.invalidate();
            }
        };
        ThemeBind.backgroundColor(fragmentView, Theme.key_windowBackgroundGray, getResourceProvider());

        listView = new RecyclerListView(context);
        listView.setSections();
        listView.setSectionsDrawBackground(true);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(true);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setHasFixedSize(false);
        listView.setNestedScrollingEnabled(true);

        adapter = new NetworkLogAdapter(context, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((view, position) -> adapter.onItemClick(position));

        ((FrameLayout) fragmentView).addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP));

        emptyView = new TextView(context);
        emptyView.setText(LocaleController.getString(R.string.NoNetworkLogs));
        emptyView.setTextSize(16);
        ThemeBind.textColor(emptyView, Theme.key_windowBackgroundWhiteGrayText2, getResourceProvider());
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        ((FrameLayout) fragmentView).addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        progressBar.setVisibility(View.GONE);
        ((FrameLayout) fragmentView).addView(progressBar, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        loadLogs();
        return fragmentView;
    }

    @Override
    protected RecyclerListView getListViewForSimpleGlass() {
        return listView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadLogs() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            allLogs = NetworkLogDb.getAll();
            filteredLogs = new ArrayList<>(allLogs);
            AndroidUtilities.runOnUIThread(() -> {
                progressBar.setVisibility(View.GONE);
                updateEmptyState();
                adapter.setLogs(filteredLogs);
            });
        }).start();
    }

    private void performSearch(String query) {
        if (query == null) {
            query = "";
        }

        if (query.isEmpty()) {
            filteredLogs = new ArrayList<>(allLogs);
        } else {
            filteredLogs = NetworkLogDb.search(query);
        }

        updateEmptyState();
        adapter.setLogs(filteredLogs);
        layoutManager.scrollToPositionWithOffset(0, 0);
    }

    private void exitSearchMode() {
        isSearchMode = false;
        currentSearchQuery = "";
        filteredLogs = new ArrayList<>(allLogs);
        updateEmptyState();
        adapter.setLogs(filteredLogs);
    }

    private void updateEmptyState() {
        if (filteredLogs == null || filteredLogs.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    public void showClearConfirmDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString(R.string.ClearNetworkLogs));
        builder.setMessage(LocaleController.getString(R.string.ClearNetworkLogsConfirm));
        builder.setPositiveButton(LocaleController.getString(R.string.Clear), (dialog, which) -> clearLogs());
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void clearLogs() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            NetworkLogDb.clearAll();
            allLogs.clear();
            filteredLogs.clear();
            AndroidUtilities.runOnUIThread(() -> {
                progressBar.setVisibility(View.GONE);
                updateEmptyState();
                adapter.setLogs(new ArrayList<>());
            });
        }).start();
    }

    public void openLogDetail(NetworkLogItem item) {
        presentFragment(new NetworkLogDetailActivity(item));
    }

    @Override
    public boolean onBackPressed(boolean invoked) {
        if (isSearchMode) {
            if (invoked) {
                try {
                    if (simpleActionBar != null && simpleActionBar.isSearchFieldVisible()) {
                        simpleActionBar.closeSearchField(false);
                    }
                } catch (Exception ignore) {
                }
                exitSearchMode();
            }
            return false;
        }
        return super.onBackPressed(invoked);
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }
}

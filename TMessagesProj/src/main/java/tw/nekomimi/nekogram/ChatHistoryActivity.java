package tw.nekomimi.nekogram;

import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleActionBarMenuItem;
import org.telegram.ui.ActionBar.SimpleActionBarMenu;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeBind;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;
import org.telegram.ui.ChatActivity;
import org.telegram.utils.glass.BaseFragmentSimpleGlass;

import java.util.ArrayList;
import java.util.LinkedList;


public class ChatHistoryActivity extends BaseFragment {

    // Chat categories
    public enum ChatCategory {
        ALL(0, R.string.AllChats),
        CHANNELS(1, R.string.FilterNameChannels),
        GROUPS(2, R.string.FilterNameGroups),
        USERS(3, R.string.FilterNameUsers),
        BOTS(4, R.string.FilterNameBots);

        public final int id;
        public final int title;

        ChatCategory(int id, int title) {
            this.id = id;
            this.title = title;
        }
    }

    // UI Components
    private ViewPagerFixed viewPager;
    private ViewPagerFixed.TabsView tabsView;
    private FrameLayout tabsContainer;
    private View listTopOverlay;
    private int insetTop;
    private int insetBottom;

    // Data
    private final ArrayList<HistoryItem> allHistoryItems = new ArrayList<>();
    private final ArrayList<HistoryItem> filteredHistoryItems = new ArrayList<>();

    // Search
    private boolean isSearchMode = false;
    private String searchQuery = "";
    private ActionBarMenuItem searchItem;

    // State preservation - only save search state
    private boolean savedSearchMode = false;
    private String savedSearchQuery = "";
    private boolean isOpeningChat = false; // Flag indicating whether opening chat
    private boolean hasBeenInitialized = false; // Flag indicating whether initialized

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        loadHistoryItems();
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        saveState();
    }

    /**
     * Save current state (search state only)
     */
    private void saveState() {
        // Only save search state
        // Do not preserve search state if query is empty to avoid inconsistent UI on return
        if (isSearchMode && !android.text.TextUtils.isEmpty(searchQuery)) {
            savedSearchMode = true;
            savedSearchQuery = searchQuery;
        } else {
            savedSearchMode = false;
            savedSearchQuery = "";
        }
        FileLog.d("Save search state: mode=" + savedSearchMode + ", query=" + savedSearchQuery);
    }

    /**
     * Restore previously saved state (search state only)
     */
    private void restoreState() {
        FileLog.d("Start restoring search state: mode=" + savedSearchMode + ", query=" + savedSearchQuery);

        // Restore search state
        if (savedSearchMode && !android.text.TextUtils.isEmpty(savedSearchQuery)) {
            isSearchMode = true;
            searchQuery = savedSearchQuery;

            // Update title
            updateTitle();

            // Restore search field state
            if (searchItem != null) {
                searchItem.postDelayed(() -> {
                    searchItem.openSearch(false); // No animation
                    if (searchItem.getSearchField() != null) {
                        searchItem.getSearchField().setText(savedSearchQuery);
                    }
                }, 50);
            }

            // Execute search
            performSearch(savedSearchQuery);
            FileLog.d("Search state restored");
        }
    }


    @Override
    protected boolean useSimpleActionBar() {
        return true;
    }

    @Override
    public View createView(Context context) {
        // Setup SimpleActionBar
        simpleActionBar.setBackButtonImage(R.drawable.ic_ab_back);
        updateTitle();

        simpleActionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == 1) {
                    showClearHistoryDialog();
                }
            }
        });

        // Create menu (clear existing menu first)
        SimpleActionBarMenu menu = simpleActionBar.createMenu();
        menu.clearItems();

        // Add search button
        searchItem = menu.addItem(3, R.drawable.msg_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                isSearchMode = true;
                searchQuery = "";
                updateTitle();
                performSearch("");
            }

            @Override
            public void onSearchCollapse() {
                exitSearchMode();
            }

            @Override
            public void onTextChanged(EditText editText) {
                searchQuery = editText.getText().toString();
                performSearch(searchQuery);
            }
        });

        // Add options button (settings icon for menu)
        SimpleActionBarMenuItem optionsItem = menu.addItem(2, R.drawable.msg_settings);
        optionsItem.addSubItem(1, R.drawable.msg_delete, getString(R.string.ClearRecentChats));

        // Create main layout
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void dispatchDraw(@NonNull Canvas canvas) {
                super.dispatchDraw(canvas);
                glassEngine.invalidate();
            }
        };
        ThemeBind.backgroundColor(frameLayout, Theme.key_windowBackgroundGray, getResourceProvider());

        viewPager = new ViewPagerFixed(context);
        viewPager.setAdapter(new CategoryPagerAdapter());
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));

        tabsContainer = new FrameLayout(context);
        tabsView = viewPager.createTabsView(true, ViewPagerFixed.SELECTOR_TYPE_BUBBLE_STYLE);

        listTopOverlay = new View(context);
        frameLayout.addView(listTopOverlay, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 0, Gravity.TOP));
        listTopOverlay.getLayoutParams().height = insetTop + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(44);

        tabsContainer.setTranslationY(BaseFragmentSimpleGlass.getPillGlassTop(insetTop) - AndroidUtilities.dp(9.66f));
        tabsContainer.addView(tabsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 36 + 6 + 6));
        tabsContainer.setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(4), AndroidUtilities.dp(4), AndroidUtilities.dp(4));
        tabsContainer.setBackground(getBaseSimpleGlass().factory
                .create(tabsContainer)
                .setColorProvider(BlurredBackgroundProviderImpl.simpleActionBar(getResourceProvider()))
                .setPadding(AndroidUtilities.dp(9.66f))
                .setRadius(AndroidUtilities.dp(18))
        );

        getBaseSimpleGlass().setViews(frameLayout, viewPager, simpleActionBar, getResourceProvider());
        getBaseSimpleGlass().setCapture((canvas, position) -> {
            for (int a = 0, N = viewPager.getChildCount(); a < N; a++) {
                final View v = viewPager.getChildAt(a);
                if (v instanceof RecyclerListView) {
                    final RecyclerListView listView = (RecyclerListView) v;
                    Blur3Utils.captureRelativeParent(listView, canvas, position, listView, frameLayout);
                }
            }
        });
        getBaseSimpleGlass().setClipView(null);
        frameLayout.addView(tabsContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP, -4, 0, -4, 0));
        listTopOverlay.setBackground(getBaseSimpleGlass().createActionBarFadeDrawable(listTopOverlay));
        simpleActionBar.setBackground(null);

        fragmentView = frameLayout;

        // Mark as initialized
        hasBeenInitialized = true;

        // Restore saved state (only when returning from chat)
        if (isOpeningChat) {
            fragmentView.post(this::restoreState);
        }

        return fragmentView;
    }

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        insetTop = top;
        insetBottom = bottom;

        if (tabsContainer == null || listTopOverlay == null || viewPager == null) {
            return;
        }

        tabsContainer.setTranslationY(BaseFragmentSimpleGlass.getPillGlassTop(insetTop) - AndroidUtilities.dp(9.66f));
        ViewGroup.LayoutParams layoutParams = listTopOverlay.getLayoutParams();
        layoutParams.height = insetTop + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(44);

        for (int a = 0, N = viewPager.getChildCount(); a < N; a++) {
            final View v = viewPager.getChildAt(a);
            if (v instanceof RecyclerListView) {
                BaseFragmentSimpleGlass.setListViewParams((RecyclerListView) v, insetTop, insetBottom, ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(42), 0);
            }
        }
    }

    private String getTabTitle(ChatCategory category) {
        int count = getCategoryCount(category);
        String baseTitle = getString(category.title);
        return count > 0 ? baseTitle + " (" + count + ")" : baseTitle;
    }

    private int getCategoryCount(ChatCategory category) {
        int count = 0;
        for (HistoryItem item : allHistoryItems) {
            if (shouldIncludeItem(item, category)) {
                count++;
            }
        }
        return count;
    }

    private boolean shouldIncludeItem(HistoryItem item, ChatCategory category) {
        if (category == ChatCategory.ALL) {
            return true;
        }

        if (item.user != null) {
            // User dialog
            if (item.user.bot) {
                return category == ChatCategory.BOTS;
            } else {
                return category == ChatCategory.USERS;
            }
        } else if (item.chat != null) {
            // Chat dialog
            if (item.chat.broadcast) {
                return category == ChatCategory.CHANNELS;
            } else {
                return category == ChatCategory.GROUPS;
            }
        }
        return false;
    }

    private void loadHistoryItems() {
        allHistoryItems.clear();

        try {
            // Get recent dialogs from BackButtonMenuRecent
            LinkedList<Long> recentDialogIds = BackButtonMenuRecent.getRecentDialogs(currentAccount);
            for (Long dialogId : recentDialogIds) {
                HistoryItem item = new HistoryItem();
                item.dialogId = dialogId;

                if (dialogId > 0) {
                    // User dialog
                    // First try to get from memory cache
                    item.user = MessagesController.getInstance(currentAccount).getUser(dialogId);

                    // Fallback: load user from database if not in memory (after app restart)
                    if (item.user == null) {
                        try {
                            java.util.ArrayList<Long> userIds = new java.util.ArrayList<>();
                            userIds.add(dialogId);
                            java.util.ArrayList<TLRPC.User> users = MessagesStorage.getInstance(currentAccount).getUsers(userIds);
                            if (!users.isEmpty()) {
                                item.user = users.get(0);
                                // Put it in memory cache for future use
                                MessagesController.getInstance(currentAccount).putUsers(users, true);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    // Skip if user is still null (couldn't load from memory/database)
                    if (item.user == null) {
                        continue;
                    }


                } else {
                    // Chat dialog
                    long chatId = -dialogId;
                    item.chat = MessagesController.getInstance(currentAccount).getChat(chatId);
                    // If chat is null, try to load it from database
                    if (item.chat == null) {
                        try {
                            java.util.ArrayList<Long> chatIds = new java.util.ArrayList<>();
                            chatIds.add(chatId);
                            java.util.ArrayList<TLRPC.Chat> chats = MessagesStorage.getInstance(currentAccount).getChats(chatIds);
                            if (!chats.isEmpty()) {
                                item.chat = chats.get(0);
                                // Put it in memory cache for future use
                                MessagesController.getInstance(currentAccount).putChat(item.chat, true);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    // Skip if chat is still null (couldn't load from database either)
                    if (item.chat == null) {
                        continue;
                    }
                }

                allHistoryItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize filtered data
        if (isSearchMode) {
            performSearch(searchQuery);
        } else {
            filteredHistoryItems.clear();
            filteredHistoryItems.addAll(allHistoryItems);
        }
    }

    private void updateTitle() {
        if (simpleActionBar == null) {
            return;
        }
        if (isSearchMode) {
            simpleActionBar.setTitle(getString(R.string.Search));
        } else {
            simpleActionBar.setTitle(getString(R.string.RecentChats));
        }
    }

    private void exitSearchMode() {
        isSearchMode = false;
        searchQuery = "";

        // Clear saved search state (user actively exits search)
        savedSearchMode = false;
        savedSearchQuery = "";

        updateTitle();
        refreshAllPages();
    }

    private void performSearch(String query) {
        filteredHistoryItems.clear();

        if (TextUtils.isEmpty(query)) {
            // In empty search, show all items (browse mode)
            filteredHistoryItems.addAll(allHistoryItems);
        } else {
            String lowerQuery = query.toLowerCase();
            for (HistoryItem item : allHistoryItems) {
                if (matchesSearchQuery(item, lowerQuery)) {
                    filteredHistoryItems.add(item);
                }
            }
        }

        refreshAllPages();
    }

    private boolean matchesSearchQuery(HistoryItem item, String query) {
        // Search in user/chat name
        String name = "";
        if (item.user != null) {
            name = ContactsController.formatName(item.user.first_name, item.user.last_name);
        } else if (item.chat != null) {
            name = item.chat.title;
        }

        if (name.toLowerCase().contains(query)) {
            return true;
        }

        // Search in username (prefer public username, fallback to local fields)
        String username = "";
        if (item.user != null) {
            username = getBestLocalUsername(item.user);
        } else if (item.chat != null) {
            username = getBestLocalUsername(item.chat);
        }

        return !TextUtils.isEmpty(username) && username.toLowerCase().contains(query);
    }

    private void showClearHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(getString(R.string.ClearRecentChats));
        builder.setMessage(getString(R.string.ClearRecentChatAlert));

        builder.setPositiveButton(getString(R.string.Clear), (dialog, which) -> clearHistory());

        builder.setNegativeButton(getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void clearHistory() {
        BackButtonMenuRecent.clearRecentDialogs(currentAccount);

        // Clear saved state
        clearSavedState();

        // Immediately refresh the interface
        loadHistoryItems();
        refreshAllPages();
        BulletinFactory.of(this).createSimpleBulletin(R.raw.ic_delete, getString(R.string.ClearRecentChats)).show();
    }

    /**
     * Clear saved search state
     */
    private void clearSavedState() {
        savedSearchMode = false;
        savedSearchQuery = "";
        isOpeningChat = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        FileLog.d("onResume: isOpeningChat=" + isOpeningChat + ", hasBeenInitialized=" + hasBeenInitialized + ", savedSearchMode=" + savedSearchMode);

        // If returning from chat page
        if (isOpeningChat && hasBeenInitialized) {
            isOpeningChat = false; // Reset flag

            // If user was in search mode, restore search state without refreshing
            if (savedSearchMode && !android.text.TextUtils.isEmpty(savedSearchQuery)) {
                restoreState();
                return; // Don't refresh list, keep search results
            }

            // If search mode was opened with empty query, exit search mode on return and close search UI
            if (isSearchMode && android.text.TextUtils.isEmpty(searchQuery)) {
                try {
                    if (simpleActionBar != null && simpleActionBar.isSearchFieldVisible()) {
                        simpleActionBar.closeSearchField(false);
                    }
                } catch (Exception ignore) {
                }
                exitSearchMode();
            }
        }

        // Reset flag
        isOpeningChat = false;

        // Reload data to get latest chat order
        if (hasBeenInitialized) {
            loadHistoryItems();
            refreshAllPages();
        }
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

    private void refreshAllPages() {
        if (viewPager != null) {
            // Clear all cached views to prevent old content from showing
            clearViewPagerCache();

            // Force refresh all pages by recreating the adapter
            viewPager.setAdapter(new CategoryPagerAdapter());
        }
    }

    private void clearViewPagerCache() {
        if (viewPager != null) {
            try {
                // Force ViewPager to clear its view cache
                viewPager.removeAllViews();

                // Request layout to ensure proper refresh
                viewPager.requestLayout();

                // Small delay to ensure views are properly cleared
                viewPager.post(() -> {
                    if (viewPager != null) {
                        viewPager.invalidate();
                    }
                });
            } catch (Exception e) {
                // Ignore any exceptions during cache clearing
            }
        }
    }

    // ViewPager Adapter
    private class CategoryPagerAdapter extends ViewPagerFixed.Adapter {
        @Override
        public int getItemCount() {
            return ChatCategory.values().length;
        }

        @Override
        public String getItemTitle(int position) {
            return getTabTitle(ChatCategory.values()[position]);
        }

        @Override
        public View createView(int viewType) {
            Context context = getContext();
            if (context == null) return new View(getParentActivity());

            // Create RecyclerView for this category
            RecyclerListView listView = new RecyclerListView(context);
            listView.setClipToPadding(false);
            listView.setSections();
            listView.setSectionsDrawBackground(true);
            listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            listView.setVerticalScrollBarEnabled(false);
            glassEngine.addScrolledView(listView);
            BaseFragmentSimpleGlass.setListViewParams(listView, insetTop, insetBottom, ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(42), 0);

            return listView;
        }

        @Override
        public void bindView(View view, int position, int viewType) {
            if (view instanceof RecyclerListView) {
                RecyclerListView listView = (RecyclerListView) view;

                // Clear any existing adapter to prevent data mixing
                listView.setAdapter(null);

                // Create fresh adapter with current data
                CategoryListAdapter adapter = new CategoryListAdapter(position);
                listView.setAdapter(adapter);

                // Set click listener
                listView.setOnItemClickListener(adapter::onItemClick);

                // Scroll to top
                listView.scrollToPosition(0);
            }
        }
    }

    // Category List Adapter
    private class CategoryListAdapter extends RecyclerListView.SelectionAdapter {
        private final ChatCategory category;
        private final ArrayList<HistoryItem> categoryItems = new ArrayList<>();

        public CategoryListAdapter(int categoryIndex) {
            category = ChatCategory.values()[categoryIndex];
            updateCategoryData();
        }

        private void updateCategoryData() {
            categoryItems.clear();

            // Use filtered data in search mode, otherwise use all data
            ArrayList<HistoryItem> sourceItems = isSearchMode ? filteredHistoryItems : allHistoryItems;

            // Ensure we're working with the latest data
            if (sourceItems.isEmpty()) {
                FileLog.d("No data available for " + category.name() + " category");
                return;
            }

            for (HistoryItem item : sourceItems) {
                if (shouldIncludeItem(item, category)) {
                    categoryItems.add(item);
                }
            }

            FileLog.d("Updated " + category.name() + " category: " + categoryItems.size() + " items from " + sourceItems.size() + " total" + (isSearchMode ? " (search mode)" : ""));
        }

        public void onItemClick(View view, int position) {
            if (position >= 0 && position < categoryItems.size()) {
                HistoryItem item = categoryItems.get(position);
                openChat(item);
            }
        }

        @Override
        public int getItemCount() {
            return categoryItems.isEmpty() ? 1 : categoryItems.size(); // Show empty state if no items
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);

            if (viewType == 1) { // Empty state
                if (holder.itemView instanceof EmptyStateCell) {
                    EmptyStateCell emptyStateCell = (EmptyStateCell) holder.itemView;

                    if (isSearchMode) {
                        // In search mode, show search results
                        if (TextUtils.isEmpty(searchQuery)) {
                            emptyStateCell.setText("", getString(R.string.Search));
                        } else {
                            emptyStateCell.setText("", getString(R.string.NoResult));
                        }
                    } else {
                        // For specific categories, show "No xx found" (no title)
                        emptyStateCell.setText("", getString(R.string.FilterNoChatsToDisplay));
                    }
                }
            } else { // History item
                if (holder.itemView instanceof HistoryCell && position >= 0 && position < categoryItems.size()) {
                    HistoryCell historyCell = (HistoryCell) holder.itemView;
                    HistoryItem item = categoryItems.get(position);
                    historyCell.setDialog(item);
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return !categoryItems.isEmpty();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1) {
                view = new EmptyStateCell(parent.getContext());
            } else {
                view = new HistoryCell(parent.getContext());
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return categoryItems.isEmpty() ? 1 : 0; // 0 = history item, 1 = empty state
        }
    }

    private void openChat(HistoryItem item) {
        if (item == null || (item.user == null && item.chat == null)) {
            return;
        }

        // If user is in search mode with empty query, close search UI and exit search before navigating
        if (isSearchMode && TextUtils.isEmpty(searchQuery)) {
            try {
                if (simpleActionBar != null && simpleActionBar.isSearchFieldVisible()) {
                    simpleActionBar.closeSearchField(false);
                }
            } catch (Exception ignore) {
            }
            exitSearchMode();
        }

        // Mark as opening chat
        isOpeningChat = true;

        // Save current state before opening chat
        saveState();

        // Open chat activity
        Bundle args = new Bundle();
        if (item.dialogId < 0) {
            args.putLong("chat_id", -item.dialogId);
        } else {
            args.putLong("user_id", item.dialogId);
        }
        presentFragment(new ChatActivity(args));
    }

    private boolean chatExistsInCurrentAccount(HistoryItem item) {
        if (item.dialogId > 0) {
            // User dialog - check if user exists in current account
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(item.dialogId);
            return user != null;
        } else {
            // Chat dialog - check if chat exists in current account
            long chatId = -item.dialogId;
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(chatId);
            return chat != null;
        }
    }

    private void showChatOptionsMenu(HistoryItem item, View anchorView) {
        // Determine available options
        boolean hasUsername = false;
        String username = null;
        if (item.user != null) {
            username = getBestLocalUsername(item.user);
            hasUsername = !TextUtils.isEmpty(username);
        } else if (item.chat != null) {
            username = getBestLocalUsername(item.chat);
            hasUsername = !TextUtils.isEmpty(username);
        }

        boolean canOpen = hasUsername || chatExistsInCurrentAccount(item);

        // Create final copies for lambda
        final String finalUsername = username;
        final boolean finalHasUsername = hasUsername;
        final boolean finalCanOpen = canOpen;

        ItemOptions options = ItemOptions.makeOptions(this, anchorView);
        options.addIf(finalCanOpen, R.drawable.msg_openin, getString(R.string.Open), () -> openChat(item));
        options.addIf(finalHasUsername, R.drawable.msg_share, getString(R.string.ShareFile), () -> shareChat(finalUsername));
        options.addIf(finalHasUsername, R.drawable.msg_copy, getString(R.string.Copy), () -> copyUsername(finalUsername));
        options.addGap();
        options.add(R.drawable.msg_delete, getString(R.string.Delete), true, () -> showDeleteChatDialog(item));
        options.show();
    }

    private void shareChat(String username) {
        try {
            String shareText = "@" + username;
            ShareAlert shareAlert = ShareAlert.createShareAlert(getContext(), null, shareText, false, shareText, false);
            showDialog(shareAlert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyUsername(String username) {
        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    getParentActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("username", "@" + username);
            clipboard.setPrimaryClip(clip);
            BulletinFactory.of(this).createSimpleBulletin(R.raw.copy,
                    getString(R.string.TextCopied)).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteChatDialog(HistoryItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(getString(R.string.Delete));

        String chatName = "";
        if (item.user != null) {
            chatName = ContactsController.formatName(item.user);
        } else if (item.chat != null) {
            chatName = ContactsController.formatName(item.chat);
        }

        builder.setMessage(LocaleController.formatString(R.string.DeleteRecentChatMessage, chatName));

        builder.setPositiveButton(getString(R.string.Delete), (dialog, which) -> deleteChatFromHistory(item));

        builder.setNegativeButton(getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void deleteChatFromHistory(HistoryItem item) {
        BackButtonMenuRecent.deleteFromRecentDialogs(currentAccount, item.dialogId);

        // Refresh the interface
        loadHistoryItems();
        refreshAllPages();

        BulletinFactory.of(this).createSimpleBulletin(R.raw.ic_delete,
                getString(R.string.DeleteRecentChatTooltip)).show();
    }


    // Data classes
    private static class HistoryItem {
        long dialogId;
        TLRPC.Chat chat;
        TLRPC.User user;
    }

    // Custom cells
    private class HistoryCell extends FrameLayout {
        private final BackupImageView avatarImageView;
        private final TextView nameTextView;
        private final TextView usernameTextView;
        private final AvatarDrawable avatarDrawable;
        private HistoryItem currentItem;

        public HistoryCell(Context context) {
            super(context);

            avatarDrawable = new AvatarDrawable();
            avatarImageView = new BackupImageView(context);
            avatarImageView.setRoundRadius(AndroidUtilities.dp(25));
            addView(avatarImageView, LayoutHelper.createFrame(50, 50, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));

            nameTextView = new TextView(context);
            ThemeBind.textColor(nameTextView, Theme.key_windowBackgroundWhiteBlackText);
            nameTextView.setTextSize(16);
            nameTextView.setLines(1);
            nameTextView.setMaxLines(1);
            nameTextView.setSingleLine(true);
            nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            nameTextView.setGravity(Gravity.LEFT);
            nameTextView.setTypeface(AndroidUtilities.bold());
            addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 82, 10, 64, 0));

            usernameTextView = new TextView(context);
            ThemeBind.textColor(usernameTextView, Theme.key_windowBackgroundWhiteGrayText3);
            usernameTextView.setTextSize(14);
            usernameTextView.setLines(1);
            usernameTextView.setMaxLines(1);
            usernameTextView.setSingleLine(true);
            usernameTextView.setEllipsize(TextUtils.TruncateAt.END);
            usernameTextView.setGravity(Gravity.LEFT);
            addView(usernameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 82, 36, 64, 0));

            // Add options button (three dots)
            ImageView optionsButton = new ImageView(context);
            optionsButton.setFocusable(false);
            ThemeBind.selectorBackground(optionsButton, Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)), null, ThemeBind.of(Theme.key_listSelector));
            optionsButton.setImageResource(R.drawable.ic_ab_other);
            ThemeBind.colorFilter(optionsButton, Theme.key_stickers_menu, PorterDuff.Mode.MULTIPLY);
            optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            optionsButton.setOnClickListener(v -> {
                if (currentItem != null) {
                    showChatOptionsMenu(currentItem, v);
                }
            });
            addView(optionsButton, LayoutHelper.createFrame(48, 48, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));

            ThemeBind.backgroundColor(this, Theme.key_windowBackgroundWhite);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72), MeasureSpec.EXACTLY));
        }

        public void setDialog(HistoryItem item) {
            this.currentItem = item;

            if (item.user != null) {
                // User dialog
                String name;
                if (UserObject.isDeleted(item.user)) {
                    name = getString(R.string.HiddenName);
                } else {
                    name = UserObject.getUserName(item.user);
                    // If getUserName returns empty or "HiddenName", try to use username as fallback
                    if (TextUtils.isEmpty(name) || name.equals(getString(R.string.HiddenName))) {
                        if (!TextUtils.isEmpty(item.user.username)) {
                            name = "@" + item.user.username;
                        } else {
                            name = "User " + item.user.id; // Last resort fallback
                        }
                    }
                }

                avatarDrawable.setInfo(item.user);
                avatarImageView.setForUserOrChat(item.user, avatarDrawable);
                nameTextView.setText(name);

                // Show username or special status
                String usernameText = getUsernameText(item.user);
                if (!TextUtils.isEmpty(usernameText) && !usernameText.trim().isEmpty()) {
                    usernameTextView.setText(usernameText);
                    usernameTextView.setVisibility(VISIBLE);
                } else {
                    usernameTextView.setVisibility(GONE);
                }
            } else if (item.chat != null) {
                // Chat dialog
                avatarDrawable.setInfo(item.chat);
                avatarImageView.setForUserOrChat(item.chat, avatarDrawable);
                nameTextView.setText(item.chat.title);

                // Show username or private status
                String usernameText = getChatUsernameText(item.chat);
                if (!TextUtils.isEmpty(usernameText)) {
                    usernameTextView.setText(usernameText);
                    usernameTextView.setVisibility(VISIBLE);
                } else {
                    usernameTextView.setVisibility(GONE);
                }
            }
        }

        private String getUsernameText(TLRPC.User user) {
            // Use UserObject.getPublicUsername to get the primary username (including collectible usernames)
            String username = UserObject.getPublicUsername(user);
            if (!TextUtils.isEmpty(username) && !username.trim().isEmpty()) {
                return "@" + username;
            }
            // fallback to best local username without network
            String fallback = getBestLocalUsername(user);
            if (!TextUtils.isEmpty(fallback)) {
                return "@" + fallback;
            }
            // show user id when no username locally
            return "ID: " + user.id;
        }

        private String getChatUsernameText(TLRPC.Chat chat) {
            // Use ChatObject.getPublicUsername to get the primary username (including collectible usernames)
            String username = ChatObject.getPublicUsername(chat);
            if (!TextUtils.isEmpty(username)) {
                return "@" + username;
            }
            String fallback = getBestLocalUsername(chat);
            if (!TextUtils.isEmpty(fallback)) {
                return "@" + fallback;
            }

            // Show private status for private channels/groups
            if (chat.broadcast) {
                return getString(R.string.ChannelPrivate);
            } else {
                return getString(R.string.MegaPrivate);
            }
        }
    }

    // Removed passive refresh per request

    // Prefer real-time public username; fallback to locally available username fields without network
    private String getBestLocalUsername(TLRPC.User user) {
        if (user == null) return "";
        String username = UserObject.getPublicUsername(user);
        if (!TextUtils.isEmpty(username)) return username;
        if (!TextUtils.isEmpty(user.username)) return user.username; // legacy primary username
        if (user.usernames != null) {
            // pick the first active collectible username if present locally
            for (int i = 0; i < user.usernames.size(); i++) {
                TLRPC.TL_username u = user.usernames.get(i);
                if (u != null && u.active && !TextUtils.isEmpty(u.username)) {
                    return u.username;
                }
            }
        }
        return "";
    }

    private String getBestLocalUsername(TLRPC.Chat chat) {
        if (chat == null) return "";
        String username = ChatObject.getPublicUsername(chat);
        if (!TextUtils.isEmpty(username)) return username;
        if (!TextUtils.isEmpty(chat.username)) return chat.username;
        if (chat.usernames != null) {
            for (int i = 0; i < chat.usernames.size(); i++) {
                TLRPC.TL_username u = chat.usernames.get(i);
                if (u != null && u.active && !TextUtils.isEmpty(u.username)) {
                    return u.username;
                }
            }
        }
        return "";
    }

    private static class EmptyStateCell extends FrameLayout {
        private final TextView titleTextView;
        private final TextView descriptionTextView;

        public EmptyStateCell(Context context) {
            super(context);

            titleTextView = new TextView(context);
            ThemeBind.textColor(titleTextView, Theme.key_windowBackgroundWhiteGrayText3);
            titleTextView.setTextSize(17);
            titleTextView.setGravity(Gravity.CENTER);
            addView(titleTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 32, 48, 32, 0));

            descriptionTextView = new TextView(context);
            ThemeBind.textColor(descriptionTextView, Theme.key_windowBackgroundWhiteGrayText3);
            descriptionTextView.setTextSize(15);
            descriptionTextView.setGravity(Gravity.CENTER);
            addView(descriptionTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 32, 80, 32, 48));

            ThemeBind.backgroundColor(this, Theme.key_windowBackgroundWhite);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // Use a reasonable height for empty state, container will handle the full coverage
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(200), MeasureSpec.EXACTLY)
            );
        }

        public void setText(String title, String description) {
            if (TextUtils.isEmpty(title)) {
                titleTextView.setVisibility(GONE);
            } else {
                titleTextView.setText(title);
                titleTextView.setVisibility(VISIBLE);
            }

            if (TextUtils.isEmpty(description)) {
                descriptionTextView.setVisibility(GONE);
            } else {
                descriptionTextView.setText(description);
                descriptionTextView.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }
}

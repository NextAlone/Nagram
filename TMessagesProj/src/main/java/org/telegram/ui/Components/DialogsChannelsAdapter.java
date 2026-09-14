package org.telegram.ui.Components;

import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.A;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.UserInfoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DialogsChannelsAdapter extends UniversalAdapter {

    private final Context context;
    private final int currentAccount;
    private final int folderId;
    private final Theme.ResourcesProvider resourcesProvider;

    public final ArrayList<MessageObject> messages = new ArrayList<>();
    public final ArrayList<TLRPC.Chat> searchMyChannels = new ArrayList<>();
    public final ArrayList<TLRPC.Chat> searchRecommendedChannels = new ArrayList<>();
    public final ArrayList<TLRPC.Chat> searchChannels = new ArrayList<>();
    public boolean expandedSearchChannels;

    public boolean expandedMyChannels;
    public final ArrayList<TLRPC.Chat> myChannels = new ArrayList<>();

    public DialogsChannelsAdapter(RecyclerListView listView, Context context, int currentAccount, int folderId, Theme.ResourcesProvider resourcesProvider) {
        super(listView, context, currentAccount, 0, null, resourcesProvider);
        super.fillItems = this::fillItems;
        this.context = context;
        this.currentAccount = currentAccount;
        this.folderId = folderId;
        this.resourcesProvider = resourcesProvider;
        update(false);
    }

    // Privacy patch: myChannels (below) is a curated shortlist used for the
    // "your channels" quick-browse row when there's no query — it's
    // deliberately limited to public channels and capped at 100 for that
    // display. That makes it the wrong source for actual search: a joined
    // private channel, or a public channel beyond the 100th, would silently
    // never be findable. This method instead returns every broadcast channel
    // the account is a member of, public or private, with no cap, purely
    // from local dialog data (no network request).
    private ArrayList<TLRPC.Chat> getAllJoinedChannelsForSearch() {
        ArrayList<TLRPC.Chat> channels = new ArrayList<>();
        ArrayList<TLRPC.Dialog> dialogs = MessagesController.getInstance(currentAccount).getAllDialogs();
        for (TLRPC.Dialog d : dialogs) {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-d.id);
            if (chat == null || !ChatObject.isChannelAndNotMegaGroup(chat) || ChatObject.isNotInChat(chat)) continue;
            channels.add(chat);
        }
        return channels;
    }

    public void updateMyChannels() {
        ArrayList<TLRPC.Chat> channels = new ArrayList<>();
        ArrayList<TLRPC.Dialog> dialogs = MessagesController.getInstance(currentAccount).getAllDialogs();
        for (TLRPC.Dialog d : dialogs) {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-d.id);
            if (chat == null || !ChatObject.isChannelAndNotMegaGroup(chat) || !ChatObject.isPublic(chat) || ChatObject.isNotInChat(chat)) continue;
            channels.add(chat);
            if (channels.size() >= 100)
                break;
        }
        myChannels.clear();
        myChannels.addAll(channels);
    }

    public void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        if (TextUtils.isEmpty(query)) {
            if (myChannels != null && !myChannels.isEmpty()) {
                if (myChannels.size() > 5) {
                    items.add(UItem.asGraySection(getString(R.string.SearchMyChannels), getString(expandedMyChannels ? R.string.ShowLess : R.string.ShowMore), this::toggleExpandedMyChannels));
                } else {
                    items.add(UItem.asGraySection(getString(R.string.SearchMyChannels)));
                }
                int count = myChannels.size();
                if (!expandedMyChannels)
                    count = Math.min(5, count);
                for (int i = 0; i < count; ++i) {
                    items.add(UItem.asProfileCell(myChannels.get(i)).withUsername(true));
                }
            }
            MessagesController.ChannelRecommendations recommendations = MessagesController.getInstance(currentAccount).getCachedChannelRecommendations(0);
            if (recommendations != null) {
                ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                for (TLObject obj : recommendations.chats) {
                    if (obj instanceof TLRPC.Chat) {
                        final TLRPC.Chat chat = (TLRPC.Chat) obj;
                        TLRPC.Chat localChat = MessagesController.getInstance(currentAccount).getChat(chat.id);
                        if (ChatObject.isNotInChat(chat) && (localChat == null || ChatObject.isNotInChat(localChat)))
                            chats.add(chat);
                    }
                }
                if (!chats.isEmpty()) {
                    items.add(UItem.asGraySection(getString(R.string.SearchRecommendedChannels)));
                }
                for (TLRPC.Chat chat : chats) {
                    items.add(UItem.asProfileCell(chat));
                }
            } else {
                items.add(UItem.asFlicker(FlickerLoadingView.GRAY_SECTION));
                items.add(UItem.asFlicker(FlickerLoadingView.PROFILE_SEARCH_CELL));
                items.add(UItem.asFlicker(FlickerLoadingView.PROFILE_SEARCH_CELL));
                items.add(UItem.asFlicker(FlickerLoadingView.PROFILE_SEARCH_CELL));
                items.add(UItem.asFlicker(FlickerLoadingView.PROFILE_SEARCH_CELL));
            }
        } else {
            ArrayList<TLRPC.Chat> foundChannels = new ArrayList<>();
            for (TLRPC.Chat chat : searchMyChannels) {
                TLRPC.Chat localChat = MessagesController.getInstance(currentAccount).getChat(chat.id);
                if (ChatObject.isNotInChat(chat) && (localChat == null || ChatObject.isNotInChat(localChat)))
                    foundChannels.add(chat);
            }
            for (TLRPC.Chat chat : searchRecommendedChannels) {
                TLRPC.Chat localChat = MessagesController.getInstance(currentAccount).getChat(chat.id);
                if (ChatObject.isNotInChat(chat) && (localChat == null || ChatObject.isNotInChat(localChat)))
                    foundChannels.add(chat);
            }
            for (TLRPC.Chat chat : searchChannels) {
                TLRPC.Chat localChat = MessagesController.getInstance(currentAccount).getChat(chat.id);
                if (ChatObject.isNotInChat(chat) && (localChat == null || ChatObject.isNotInChat(localChat)))
                    foundChannels.add(chat);
            }
            if (!foundChannels.isEmpty()) {
                if (foundChannels.size() > 5 && !messages.isEmpty()) {
                    items.add(UItem.asGraySection(getString(R.string.SearchChannels), getString(expandedSearchChannels ? R.string.ShowLess : R.string.ShowMore), this::toggleExpandedSearchChannels));
                } else {
                    items.add(UItem.asGraySection(getString(R.string.SearchChannels)));
                }
                int count = foundChannels.size();
                if (!expandedSearchChannels && !messages.isEmpty())
                    count = Math.min(5, count);
                for (int i = 0; i < count; ++i) {
                    items.add(UItem.asProfileCell(foundChannels.get(i)));
                }
            }
            if (!messages.isEmpty()) {
                items.add(UItem.asGraySection(getString(R.string.SearchMessages)));
                for (MessageObject message : messages) {
                    items.add(UItem.asSearchMessage(message));
                }
                if (hasMore) {
                    items.add(UItem.asFlicker(FlickerLoadingView.DIALOG_TYPE));
                }
            }
        }
    }

    public void toggleExpandedSearchChannels(View view) {
        expandedSearchChannels = !expandedSearchChannels;
        update(true);
        if (expandedSearchChannels) {
            hideKeyboard();
        }
    }

    public void toggleExpandedMyChannels(View view) {
        expandedMyChannels = !expandedMyChannels;
        update(true);
        if (expandedMyChannels) {
            hideKeyboard();
        }
    }

    protected void hideKeyboard() {

    }

    public TLRPC.Chat getChat(int position) {
        UItem item = getItem(position);
        return item != null && item.object instanceof TLRPC.Chat ? (TLRPC.Chat) item.object : null;
    }

    public Object getObject(int position) {
        UItem item = getItem(position);
        return item != null ? item.object : null;
    }

    public boolean loadingMessages;
    public boolean loadingChannels;

    private boolean hasMore;
    private int allCount;
    private int nextRate;
    private int searchChannelsId;
    public String query;
    // Privacy patch: this used to (1) run TL_messages_searchGlobal with
    // broadcasts_only=true, which searches message text across every public
    // channel on Telegram, and (2) run TL_contacts_search with broadcasts=true,
    // which queries Telegram's public channel directory for both channels you
    // are not in (response.results) and Telegram's server-side channel
    // recommendations (a discovery/suggestion feed of public channels).
    // Both of those are "public" search against Telegram's servers.
    //
    // Replaced with a purely local filter over channels already joined
    // (myChannels, populated by updateMyChannels() from this account's own
    // dialog list) — no network request is sent, and only channels you are
    // already a member of can ever appear.
    private void searchMessages(boolean next) {
        loadingMessages = false;
        loadingChannels = false;
        final int searchId = ++searchChannelsId;

        messages.clear();
        hasMore = false;
        allCount = 0;
        nextRate = 0;

        searchRecommendedChannels.clear();
        searchChannels.clear();

        searchMyChannels.clear();
        if (!TextUtils.isEmpty(this.query)) {
            String q = this.query.toLowerCase(), qT = AndroidUtilities.translitSafe(q);
            for (TLRPC.Chat channel : getAllJoinedChannelsForSearch()) {
                if (channel == null || channel.title == null) continue;
                String t = channel.title.toLowerCase(), tT = AndroidUtilities.translitSafe(t);
                if (t.startsWith(q) || t.contains(" " + q) || tT.startsWith(qT) || tT.contains(" " + qT)) {
                    searchMyChannels.add(channel);
                }
            }
        }

        if (searchId == searchChannelsId) {
            update(true);
        }
    }

    private final Runnable searchMessagesRunnable = () -> searchMessages(false);
    public void search(String query) {
        updateMyChannels();
        if (TextUtils.equals(query, this.query)) return;
        this.query = query;
        AndroidUtilities.cancelRunOnUIThread(searchMessagesRunnable);
        if (TextUtils.isEmpty(this.query)) {
            messages.clear();
            searchChannels.clear();
            searchRecommendedChannels.clear();
            searchMyChannels.clear();
            update(true);
            searchChannelsId++;
            loadingMessages = false;
            loadingChannels = false;
            hasMore = false;
            nextRate = 0;
            if (listView != null) {
                listView.scrollToPosition(0);
            }
            return;
        }

        messages.clear();
        searchChannels.clear();
        searchRecommendedChannels.clear();
        searchMyChannels.clear();

        AndroidUtilities.runOnUIThread(searchMessagesRunnable, 1000);
        loadingMessages = true;
        loadingChannels = true;

        update(true);

        if (listView != null) {
            listView.scrollToPosition(0);
        }
    }

    public void searchMore() {
        if (!hasMore || loadingMessages || TextUtils.isEmpty(this.query)) {
            return;
        }
        searchMessages(true);
    }

    public ArrayList<TLRPC.Chat> getNextChannels(int position) {
        ArrayList<TLRPC.Chat> channels = new ArrayList<>();
        for (int pos = position + 1; pos < getItemCount(); ++pos) {
            TLRPC.Chat chat = getChat(pos);
            if (chat == null) continue;
            channels.add(chat);
        }
        return channels;
    }

    public void checkBottom() {
        if (!hasMore || loadingMessages || TextUtils.isEmpty(this.query) || listView == null)
            return;
        if (seesLoading()) {
            searchMore();
        }
    }

    public boolean seesLoading() {
        if (listView == null) return false;
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View child = listView.getChildAt(i);
            if (child instanceof FlickerLoadingView) {
                return true;
            }
        }
        return false;
    }

    public boolean atTop() {
        if (listView == null) return false;
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View child = listView.getChildAt(i);
            if (listView.getChildAdapterPosition(child) == 0)
                return true;
        }
        return false;
    }
}

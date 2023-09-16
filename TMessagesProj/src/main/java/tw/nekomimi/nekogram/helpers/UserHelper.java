package tw.nekomimi.nekogram.helpers;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.TopicsFragment;

import java.util.HashMap;
import java.util.Locale;

public class UserHelper extends BaseController {

    private static final SparseArray<UserHelper> Instance = new SparseArray<>();

    public UserHelper(int num) {
        super(num);
    }

    public static UserHelper getInstance(int num) {
        UserHelper localInstance = Instance.get(num);
        if (localInstance == null) {
            synchronized (UserHelper.class) {
                localInstance = Instance.get(num);
                if (localInstance == null) {
                    Instance.put(num, localInstance = new UserHelper(num));
                }
            }
        }
        return localInstance;
    }

    void resolveUser(String userName, long userId, Utilities.Callback<TLRPC.User> callback) {
        resolvePeer(userName, peer -> {
            if (peer instanceof TLRPC.TL_peerUser) {
                callback.run(peer.user_id == userId ? getMessagesController().getUser(userId) : null);
            } else {
                callback.run(null);
            }
        });
    }

    private void resolvePeer(String userName, Utilities.Callback<TLRPC.Peer> callback) {
        TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
        req.username = userName;
        getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (response != null) {
                TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                getMessagesController().putUsers(res.users, false);
                getMessagesController().putChats(res.chats, false);
                getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                callback.run(res.peer);
            } else {
                callback.run(null);
            }
        }));
    }

}

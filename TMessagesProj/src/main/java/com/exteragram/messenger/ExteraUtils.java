package com.exteragram.messenger;

import android.os.Build;
import android.graphics.drawable.Drawable;

import org.telegram.ui.ActionBar.Theme;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLObject;

import com.exteragram.messenger.ExteraConfig;

public class ExteraUtils {
    public static Drawable drawFab() {
        return drawFab(false);
    }

    public static Drawable drawFab(boolean altColor) {
        int r = AndroidUtilities.dp(ExteraConfig.squareFab ? 16 : 100);
        int c = Theme.getColor(altColor ? Theme.key_dialogFloatingButton : Theme.key_chats_actionBackground);
        int pc = Theme.getColor(altColor ? (Build.VERSION.SDK_INT >= 21 ? Theme.key_dialogFloatingButtonPressed : Theme.key_dialogFloatingButton) : Theme.key_chats_actionPressedBackground);
        Drawable fab = Theme.createSimpleSelectorRoundRectDrawable(r, c, pc);
        return fab;
    }

    public static String getDC(TLRPC.User user) {
        return getDC(user, null);
    }

    public static String getDC(TLRPC.Chat chat) {
        return getDC(null, chat);
    }

    // thx to @Owlgram for idea
    public static String getDC(TLRPC.User user, TLRPC.Chat chat) {
        int DC = 0;
        int myDC = AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().getCurrentDatacenterId();
        if (user != null) {
            if (UserObject.isUserSelf(user) && myDC != -1) {
                DC = myDC;
            } else {
                DC = user.photo != null ? user.photo.dc_id : -1;
            }
        } else if (chat != null) {
            DC = chat.photo != null ? chat.photo.dc_id : -1;
        }
        if (DC == -1 || DC == 0) {
            return getDCName(DC);
        } else {
            return "DC" + DC + ", " + getDCName(DC);
        }
    }
    
    public static String getDCName(int dc) {
        switch (dc) {
            case 1:
            case 3:
                return "MIA, Miami FL, USA";
            case 2:
            case 4:
                return "AMS, Amsterdam, NL";
            case 5:
                return "SIN, Singapore, SG";
            default:
                return LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
        }
    }

    public static String getAppName() {
        String beta = BuildVars.isBetaApp() ? " β" : "";
        return LocaleController.getString("exteraAppName", R.string.exteraAppName) + beta;
    }
    
    public static String zalgoFilter(CharSequence text) {
        if (text == null) {
            return "";
        } else {
            return zalgoFilter(text.toString());
        }
    }

    public static String zalgoFilter(String text) {
        if (text == null) {
            return "";
        } else if (ExteraConfig.zalgoFilter && text.matches(".*\\p{Mn}{4}.*")) {
		    return text.replaceAll("(?i)([aeiouy]̈)|[̀-ͯ҉]", "").replaceAll("[\\p{Mn}]", "");
		} else {
		    return text;
		}
    }

    public static boolean checkSubFor(long id) {
        TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(id);
        if (chat != null && !chat.left && !chat.kicked) {
            return true;
        }
        return false;
    }
}
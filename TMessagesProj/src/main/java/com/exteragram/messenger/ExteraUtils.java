/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

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
            return String.format("DC%d, %s", DC, getDCName(DC));
        }
    }
    
    public static String getDCName(int dc) {
        switch (dc) {
            case 1:
            case 3:
                return "Miami FL, USA";
            case 2:
            case 4:
                return "Amsterdam, NL";
            case 5:
                return "Singapore, SG";
            default:
                return LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
        }
    }

    public static String getAppName() {
        String beta = BuildVars.isBetaApp() ? " β" : "";
        return LocaleController.getString("exteraAppName", R.string.exteraAppName) + beta;
    }

    public static String zalgoFilter(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        } else if (ExteraConfig.zalgoFilter && text.matches(".*\\p{Mn}{3}.*")) {
            String finalString = text.replaceAll("(?i)([aeiouy]̈)|[̀-ͯ҉]|[\\p{Mn}]", "");
            if (finalString == null || finalString.isEmpty()) {
                return LocaleController.getString("EventLogOriginalCaptionEmpty", R.string.EventLogOriginalCaptionEmpty);
            } else {
                return finalString;
            }
        } else {
            return text;
        }
    }

    public static boolean checkSubFor(long id) {
        TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(id);
        return chat != null && !chat.left && !chat.kicked;
    }

    public static int[] getDrawerIconPack() {
        switch (ExteraConfig.eventType) {
            case 1:
                return new int[] {
                    R.drawable.msg_groups_ny,
                    R.drawable.msg_secret_ny,
                    R.drawable.msg_channel_ny,
                    R.drawable.msg_contacts_ny,
                    R.drawable.msg_calls_ny,
                    R.drawable.msg_saved_ny,
                    R.drawable.msg_invite_ny,
                    R.drawable.msg_help_ny,
                    R.drawable.msg_nearby_ny
                };
            case 2:
                return new int[] {
                    R.drawable.msg_groups_14,
                    R.drawable.msg_secret_14,
                    R.drawable.msg_channel_14,
                    R.drawable.msg_contacts_14,
                    R.drawable.msg_calls_14,
                    R.drawable.msg_saved_14,
                    R.drawable.msg_secret_ny,
                    R.drawable.msg_help,
                    R.drawable.msg_secret_14
                };
            case 3:
                return new int[] {
                    R.drawable.msg_groups_hw,
                    R.drawable.msg_secret_hw,
                    R.drawable.msg_channel_hw,
                    R.drawable.msg_contacts_hw,
                    R.drawable.msg_calls_hw,
                    R.drawable.msg_saved_hw,
                    R.drawable.msg_invite_hw,
                    R.drawable.msg_help_hw,
                    R.drawable.msg_secret_hw
                };
            default:
                return new int[] {
                    R.drawable.msg_groups,
                    R.drawable.msg_secret,
                    R.drawable.msg_channel,
                    R.drawable.msg_contacts,
                    R.drawable.msg_calls,
                    R.drawable.msg_saved,
                    R.drawable.msg_invite,
                    R.drawable.msg_help,
                    R.drawable.msg_nearby
                };
        }
    }
}
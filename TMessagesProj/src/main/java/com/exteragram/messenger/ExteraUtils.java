package com.exteragram.messenger;

import android.os.Build;
import android.graphics.drawable.Drawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.messenger.AndroidUtilities;
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
}
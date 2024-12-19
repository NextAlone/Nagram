/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram;

import org.telegram.messenger.BuildVars;

public class AyuConstants {
    public static final long[] OFFICIAL_CHANNELS = {
            1905581924, // @ayugramchat
            1794457129, // @ayugram1338
            1434550607, // @radolyn
    };
    public static final long[] DEVS = {
            139303278, // @alexeyzavar
            778327202, // @sharapagorg
            963494570, // @Zanko_no_tachi
            238292700, // @MaxPlays
            1795176335, // @radolyn_services
    };

    public static final int MESSAGES_DELETED_NOTIFICATION = 6969;
    public static final int AYUSYNC_STATE_CHANGED = 6970;
    public static final int AYUSYNC_LAST_SENT_CHANGED = 6971;
    public static final int AYUSYNC_LAST_RECEIVED_CHANGED = 6972;
    public static final int AYUSYNC_REGISTER_STATUS_CODE_CHANGED = 6973;
}

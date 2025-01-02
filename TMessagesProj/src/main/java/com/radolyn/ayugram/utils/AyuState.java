/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.utils;
import com.radolyn.ayugram.AyuConfig;


public class AyuState {
    private static final AyuStateVariable allowReadPacket = new AyuStateVariable();

    public static void setAllowReadPacket(boolean val, int resetAfter) {
        allowReadPacket.val = val;
        allowReadPacket.resetAfter = resetAfter;
    }

    public static boolean getAllowReadPacket() {
        return AyuConfig.sendReadMessagePackets || allowReadPacket.process();
    }

}

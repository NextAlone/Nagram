/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.utils;

public class AyuStateVariable {
    private final Object sync = new Object();

    public boolean val;
    public int resetAfter;

    public boolean process() {
        synchronized (sync) {
            if (resetAfter == -1) {
                return val;
            }

            resetAfter -= 1;
            var currentVal = val;

            if (resetAfter == 0) {
                val = false;
            }

            return currentVal;
        }
    }
}

/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.messenger;

import android.util.Log;

import org.telegram.messenger.time.FastDateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import cn.hutool.core.lang.caller.CallerUtil;
import cn.hutool.core.lang.caller.StackTraceCaller;

public class FileLog {

    private final static StackTraceCaller caller = new StackTraceCaller();

    public static String getNetworkLogPath() {
        return "";
    }

    private static String mkTag() {
        return caller.getCaller(3).getSimpleName();
    }

    private static String mkMessage(Throwable e) {
        String message = e.getMessage();
        if (message != null) return message;
        return e.getClass().getSimpleName();
    }

    public static void e(final String message, final Throwable exception) {
        Log.e(mkTag(), message, exception);
    }

    public static void e(final String message) {
        Log.e(mkTag(), message);
    }

    public static void e(final Throwable e) {
        Log.e(mkTag(),mkMessage(e),e);
    }

    public static void d(final String message) {
        if (!BuildVars.LOGS_ENABLED) return;
        Log.d(mkTag(), message);
    }

    public static void w(final String message) {
        if (!BuildVars.LOGS_ENABLED) return;
        Log.w(mkTag(), message);
    }

}

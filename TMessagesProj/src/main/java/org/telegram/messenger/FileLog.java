/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.messenger;

import android.util.Log;

import cn.hutool.core.lang.caller.StackTraceCaller;
import cn.hutool.core.util.StrUtil;

public class FileLog {

    private final static StackTraceCaller caller = new StackTraceCaller();

    public static String getNetworkLogPath() {
        if (BuildVars.DEBUG_VERSION) return "/dev/null";
        return "";
    }

    private static String mkTag() {

        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        return StrUtil.subAfter(stackTrace[2].getClassName(), ".", true);

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
        Log.e(mkTag(), mkMessage(e), e);
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

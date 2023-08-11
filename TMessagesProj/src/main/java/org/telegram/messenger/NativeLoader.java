/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.io.File;

import tw.nekomimi.nekogram.utils.FileUtil;

public class NativeLoader {

    private final static int LIB_VERSION = 45;
    private final static String LIB_NAME = "tmessages." + LIB_VERSION;
    private final static String LIB_SO_NAME = "lib" + LIB_NAME + ".so";
    private final static String LOCALE_LIB_SO_NAME = "lib" + LIB_NAME + "loc.so";

    private static volatile boolean nativeLoaded = false;
    public static StringBuilder log = new StringBuilder();

    private static File getNativeLibraryDir(Context context) {
        File f = null;
        if (context != null) {
            try {
                f = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (f == null) {
            f = new File(context.getApplicationInfo().dataDir, "lib");
        }
        if (f.isDirectory()) {
            return f;
        }
        return null;
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static synchronized void initNativeLibs(Context context) {
        if (nativeLoaded) {
            return;
        }

        try {
            System.loadLibrary(LIB_NAME);
            nativeLoaded = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded normal lib");
            }
            return;
        } catch (Error e) {
            FileLog.e(e);
        }

        try {
            System.loadLibrary(FileUtil.extLib(LIB_NAME).getPath());
            FileLog.d("loaded extracted lib");
            nativeLoaded = true;
        } catch (Error e) {
            FileLog.e(e);
            log.append("185: ").append(e).append("\n");
        }
    }

    public static String getAbiFolder() {
        String folder;
        try {
            String str = Build.CPU_ABI;
            if (Build.CPU_ABI.equalsIgnoreCase("x86_64")) {
                folder = "x86_64";
            } else if (Build.CPU_ABI.equalsIgnoreCase("arm64-v8a")) {
                folder = "arm64-v8a";
            } else if (Build.CPU_ABI.equalsIgnoreCase("armeabi-v7a")) {
                folder = "armeabi-v7a";
            } else if (Build.CPU_ABI.equalsIgnoreCase("armeabi")) {
                folder = "armeabi";
            } else if (Build.CPU_ABI.equalsIgnoreCase("x86")) {
                folder = "x86";
            } else if (Build.CPU_ABI.equalsIgnoreCase("mips")) {
                folder = "mips";
            } else {
                folder = "armeabi";
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Unsupported arch: " + Build.CPU_ABI);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            folder = "armeabi";
        }

        String javaArch = System.getProperty("os.arch");
        if (javaArch != null && javaArch.contains("686")) {
            folder = "x86";
        }
        return folder;
    }

    private static native void init(String path, boolean enable);

    public static boolean loaded() {
        return nativeLoaded;
    }
    //public static native void crash();
}

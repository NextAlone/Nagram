package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.content.Context;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import tw.nekomimi.nekogram.utils.FileUtil;

public final class TgVoipNativeLoader {

    private final static int LIB_REVISION = 1;
    private final static String LIB_NAME = "tgvoip";

    private static volatile boolean nativeLoaded = false;

    private TgVoipNativeLoader() {
    }

    public static synchronized void initNativeLib(Context context, int version) {
        if (!nativeLoaded) {
            final String libName = String.format(Locale.ROOT, "%s%d.%d", LIB_NAME, version, LIB_REVISION);
            if (!loadNativeLib(context, libName)) {
                throw new IllegalStateException("unable to load native tgvoip library: " + libName);
            }
            nativeLoaded = true;
        }
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static boolean loadNativeLib(Context context, String libName) {

        try {
            System.loadLibrary(libName);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded normal lib: " + libName);
            }
            return true;
        } catch (Error e) {
            FileLog.e(e);
        }

        try {
            System.load(FileUtil.extLib(libName).getPath());
            FileLog.d("loaded extracted lib");
            nativeLoaded = true;
        } catch (Error e) {
            FileLog.e(e);
        }

        return false;
    }
}

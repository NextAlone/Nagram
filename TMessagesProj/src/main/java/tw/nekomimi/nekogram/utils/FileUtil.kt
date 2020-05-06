package tw.nekomimi.nekogram.utils

import android.os.Build
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.resource.ResourceUtil
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.BuildVars
import org.telegram.messenger.FileLog
import java.io.File
import java.util.zip.ZipFile

object FileUtil {

    @JvmStatic
    fun initDir(dir: File) {

        var parentDir: File? = dir

        while (parentDir != null) {

            if (parentDir.isFile) parentDir.deleteRecursively()

            parentDir = parentDir.parentFile

            break

        }

        dir.mkdirs()

        // ignored

    }

    @JvmStatic
    fun delete(file: File?) {

        runCatching {

            file?.deleteRecursively()

        }

    }

    @JvmStatic
    fun initFile(file: File) {

        file.parentFile?.also { initDir(it) }

        if (!file.isFile) {

            if (file.isDirectory) file.deleteRecursively()

            if (!file.isFile) {

                if (!file.createNewFile() && !file.isFile) {

                    error("unable to create file ${file.path}")

                }

            }

        }

    }

    @JvmStatic
    fun readUtf8String(file: File) = file.readText()

    @JvmStatic
    fun writeUtf8String(text: String, save: File) {

        initFile(save)

        save.writeText(text)

    }

    @JvmStatic
    fun saveAsset(path: String, saveTo: File) {

        val assets = ApplicationLoader.applicationContext.assets

        saveTo.outputStream().use {

            IoUtil.copy(assets.open(path), it)

        }

    }


    @Suppress("DEPRECATION")
    private fun getAbi() = try {
        if (Build.CPU_ABI.equals("x86_64", ignoreCase = true)) {
            "x86_64"
        } else if (Build.CPU_ABI.equals("arm64-v8a", ignoreCase = true)) {
            "arm64-v8a"
        } else if (Build.CPU_ABI.equals("armeabi-v7a", ignoreCase = true)) {
            "armeabi-v7a"
        } else if (Build.CPU_ABI.equals("armeabi", ignoreCase = true)) {
            "armeabi"
        } else if (Build.CPU_ABI.equals("x86", ignoreCase = true)) {
            "x86"
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Unsupported arch: " + Build.CPU_ABI)
            }
            "armeabi"
        }
    } catch (e: Exception) {
        FileLog.e(e)
        "armeabi"
    }

    @JvmStatic
    fun extLib(name: String): File {

        var execFile = File(ApplicationLoader.applicationContext.applicationInfo.nativeLibraryDir, "lib$name.so")

        if (!execFile.isFile) {

            System.loadLibrary(name)

            if (!execFile.isFile) {

                FileLog.d("Native library $execFile not found")

                execFile = File(ApplicationLoader.getDataDirFixed(), "cache/lib/${execFile.name}")

                if (!execFile.isFile) {

                    saveNonAsset("lib/${getAbi()}/${execFile.name}", execFile);

                }

            }

        }

        if (!execFile.canExecute()) {

            execFile.setExecutable(true)

        }

        return execFile

    }

    @JvmStatic
    fun saveNonAsset(path: String, saveTo: File) {

        ZipFile(ApplicationLoader.applicationContext.applicationInfo.sourceDir).use {

            it.getInputStream(it.getEntry(path) ?: return@use).use { ins ->

                IoUtil.copy(ins, saveTo)

                return

            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ApplicationLoader.applicationContext.applicationInfo.splitSourceDirs.forEach { split ->

                ZipFile(split).use {

                    it.getInputStream(it.getEntry(path) ?: return@use).use { ins ->

                        IoUtil.copy(ins, saveTo)

                        return

                    }

                }

            }

        }

        ResourceUtil.getStream(path)?.use {

            IoUtil.copy(it, saveTo)

            return

        }

        error("res not found: $path")

    }

}
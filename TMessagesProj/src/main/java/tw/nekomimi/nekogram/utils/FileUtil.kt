package tw.nekomimi.nekogram.utils

import android.os.Build
import cn.hutool.core.io.resource.ResourceUtil
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import java.io.File
import java.util.*
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

    @JvmStatic
    @Suppress("DEPRECATION") val abi by lazy {

        val libDirs = mutableListOf<String>()

        ZipFile(ApplicationLoader.applicationContext.applicationInfo.sourceDir).use {

            it.getEntry("lib/") ?: return@use

            for (entry in it.entries()) {

                if (entry.isDirectory && it.name.length > 4 && it.name.startsWith("lib/")) {

                    libDirs.add(entry.name.substringAfter("lib/").substringBefore("/"))

                }

            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ApplicationLoader.applicationContext.applicationInfo.splitSourceDirs?.forEach { split ->

                ZipFile(split).use {

                    it.getEntry("lib/") ?: return@use

                    for (entry in it.entries()) {

                        if (entry.isDirectory && it.name.length > 4 && it.name.startsWith("lib/")) {

                            libDirs.add(entry.name.substringAfter("lib/").substringBefore("/"))

                        }

                    }

                }

            }

        }

        if (libDirs.size == 1) libDirs[0] else {

            Build.CPU_ABI.toLowerCase()

        }.also {

            FileLog.d("current abi: $it")

        }

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

                    saveNonAsset("lib/$abi/${execFile.name}", execFile);

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

            ApplicationLoader.applicationContext.applicationInfo.splitSourceDirs?.forEach { split ->

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
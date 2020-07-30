package tw.nekomimi.nekogram.utils

import android.os.Build
import cn.hutool.core.io.resource.ResourceUtil
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import java.io.File
import java.util.zip.ZipFile

object FileUtil {

    @JvmStatic
    fun initDir(dir: File) {

        var parentDir: File? = dir

        while (parentDir != null) {

            if (parentDir.isDirectory) break

            if (parentDir.isFile) parentDir.deleteRecursively()

            parentDir = parentDir.parentFile

        }

        dir.mkdirs()

        // ignored

    }

    @JvmStatic
    @JvmOverloads
    fun delete(file: File?, filter: (File) -> Boolean = { true }) {

        runCatching {

            file?.takeIf { filter(it) }?.deleteRecursively()

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Build.SUPPORTED_ABIS[0]
            } else {
                Build.CPU_ABI
            }.toLowerCase()

        }.also {

            FileLog.d("current abi: $it")

        }

    }

    @JvmStatic
    fun extLib(name: String): File {

        val execFile = File(ApplicationLoader.applicationContext.applicationInfo.nativeLibraryDir, "lib$name.so")

        if (execFile.isFile && execFile.canExecute()) return execFile

        val newFile = File(ApplicationLoader.getDataDirFixed(), "cache/lib/${execFile.name}")

        if (newFile.isFile) {

            FileLog.d("lib already extracted: $newFile")

            if (!newFile.canExecute()) {

                newFile.setExecutable(true)

            }

            return newFile

        }

        if (execFile.isFile) {

            FileLog.w("$execFile not executable")

            if (!newFile.isFile) {

                execFile.copyTo(newFile)

            }

            if (!newFile.canExecute()) {

                newFile.setExecutable(true)

            }

            return newFile

        }

        if (!newFile.isFile) {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                Build.SUPPORTED_ABIS.forEachIndexed { index, abi ->

                    runCatching {

                        saveNonAsset("lib/$abi/${execFile.name}", execFile)

                        FileLog.d("lib extracted with default abi$index ($abi): $execFile")

                    }.onFailure {

                        FileLog.d("$abi: ${it.message ?: it.javaClass.simpleName}")

                    }

                }

                error("library not found ${execFile.name}")

            }

        } else {

            runCatching {

                saveNonAsset("lib/${Build.CPU_ABI}/${execFile.name}", execFile)

                FileLog.d("lib extracted with default abi (${Build.CPU_ABI}): $execFile")

            }.recover {

                saveNonAsset("lib/${Build.CPU_ABI2}/${execFile.name}", execFile)

                FileLog.d("lib extracted with abi2 (${Build.CPU_ABI2}): $execFile")

            }

        }

        if (!execFile.canExecute()) {

            execFile.setExecutable(true)

        }

        return execFile

    }


    @JvmStatic
    fun saveNonAsset(path: String, saveTo: File) {

        runCatching {

            ResourceUtil.getStream(path).use {

                FileLog.d("found nonAsset in resources: $path")

                IoUtil.copy(it, saveTo)

                return

            }

        }

        ZipFile(ApplicationLoader.applicationContext.applicationInfo.sourceDir).use {

            it.getInputStream(it.getEntry(path) ?: return@use).use { ins ->

                FileLog.d("found nonAsset in main apk: $path")

                IoUtil.copy(ins, saveTo)

                return

            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ApplicationLoader.applicationContext.applicationInfo.splitSourceDirs?.forEach { split ->

                ZipFile(split).use {

                    it.getInputStream(it.getEntry(path) ?: return@use).use { ins ->

                        FileLog.d("found nonAsset in split apk: $path, $split")

                        IoUtil.copy(ins, saveTo)

                        return

                    }

                }

            }

        }

        error("res not found: $path")

    }

}
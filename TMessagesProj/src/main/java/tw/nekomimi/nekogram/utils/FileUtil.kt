package tw.nekomimi.nekogram.utils

import org.telegram.messenger.ApplicationLoader
import java.io.File
import java.io.FileInputStream

object FileUtil {

    @JvmStatic
    fun initDir(dir: File) {

        dir.mkdirs()

        // ignored

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
    fun extLib(name: String): File {

        val execFile = File(ApplicationLoader.applicationContext.applicationInfo.nativeLibraryDir, "lib$name.so")

        if (!execFile.isFile) {

            val abi = when (execFile.parentFile!!.name) {

                "arm64", "aarch64" -> "arm64-v8a"
                "x86", "i386", "i486", "i586", "i686" -> "x86"
                "x86_64", "amd64" -> "x86_64"
                else -> "armeabi-v7a"

            }

            saveNonAsset("lib/$abi/${execFile.name}", execFile);

        }

        if (!execFile.canExecute()) {

            execFile.setExecutable(true)

        }

        return execFile

    }

    @JvmStatic
    fun saveNonAsset(path: String, saveTo: File) {

        val assets = ApplicationLoader.applicationContext.assets

        saveTo.outputStream().use {

            IoUtil.copy(FileInputStream(assets.openNonAssetFd(path).fileDescriptor), it)

        }

    }

}
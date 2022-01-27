package top.qwq2333.nullgram.utils

import cn.hutool.core.io.IoUtil
import org.telegram.messenger.ApplicationLoader
import java.io.File

object FileUtils {

    @JvmStatic
    fun deleteDirectory(directoryToBeDeleted: File): Boolean {
        val allContents = directoryToBeDeleted.listFiles()
        if (allContents != null) {
            for (file in allContents) {
                deleteDirectory(file)
            }
        }
        return directoryToBeDeleted.delete()
    }

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

}

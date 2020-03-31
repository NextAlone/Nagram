package tw.nekomimi.nekogram.utils

import org.telegram.messenger.ApplicationLoader
import java.io.File

object FileUtil {

    @JvmStatic
    fun initDir(dir: File) {

        dir.parentFile?.also { initDir(it) }

        if (!dir.isDirectory) {

            if (dir.isFile) dir.deleteRecursively()

            runCatching {

                if (!dir.mkdir()) {
                    error("unable to create dir ${dir.path}")

                }

            }.getOrThrow()

        }

    }

    @JvmStatic
    fun initFile(file: File) {

        file.parentFile?.also { initDir(it) }

        if (!file.isFile) {

            if (file.isDirectory) file.deleteRecursively()

            if (!file.isFile) {

                runCatching {

                    if (!file.createNewFile()) {

                        error("unable to create file ${file.path}")

                    }

                }.getOrThrow()

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
    fun saveAsset(path: String,saveTo: File) {

        val assets = ApplicationLoader.applicationContext.assets

        saveTo.outputStream().use {

            IoUtil.copy(assets.open(path), it)

        }

    }

}
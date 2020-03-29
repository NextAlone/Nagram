package tw.nekomimi.nekogram.utils

import java.io.File

object FileUtil {

    @JvmStatic
    fun readUtf8String(file: File) = file.readText()

    @JvmStatic
    fun writeUtf8String(text: String, save: File) {

        if (save.isDirectory) save.deleteRecursively()

        if (!save.isFile) save.createNewFile()

        save.writeText(text)

    }

}
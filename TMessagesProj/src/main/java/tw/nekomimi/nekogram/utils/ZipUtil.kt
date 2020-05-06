package tw.nekomimi.nekogram.utils

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

object ZipUtil {

    @JvmStatic
    fun makeZip(zipFile: File, vararg contents: File) {

        val params = ZipParameters().apply {

            compressionLevel = CompressionLevel.ULTRA

        }

        ZipFile(zipFile).apply {

            contents.forEach {

                if (it.isFile) addFile(it, params) else addFolder(it, params)

            }

        }

    }

    fun read(input: InputStream, path: String): ByteArray {

        ZipInputStream(input).use { zip ->

            while (true) {

                val entry = zip.nextEntry ?: break

                if (entry.name == path) return zip.readBytes()

            }

        }

        error("path not found")

    }

    @JvmStatic
    fun unzip(input: InputStream, output: File) {

        ZipInputStream(input).use { zip ->

            while (true) {

                val entry = zip.nextEntry ?: break

                val entryFile = File(output, entry.name)

                if (entry.isDirectory) {

                    entryFile.mkdirs()

                } else {

                    entryFile.outputStream().use {

                        zip.copyTo(it)

                    }

                }

            }

        }

    }

}
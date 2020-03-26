package tw.nekomimi.nekogram.utils

import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

object ZipUtil {

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
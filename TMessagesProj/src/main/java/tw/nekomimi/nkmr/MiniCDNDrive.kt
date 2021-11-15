package tw.nekomimi.nkmr

import android.graphics.BitmapFactory
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import android.graphics.Color
import org.telegram.messenger.FileLog
import java.nio.charset.Charset


object MiniCDNDrive {

    data class metaJSON(
            var time: Long = 0,
            var filename: String,
            var size: Long = 0,
            var sha1: String,
            var block: Array<metaJSON_Block>,
    )

    data class metaJSON_Block(
            var size: Long = 0,
            var sha1: String,
            var url: String
    )

    interface CallbackPercent {
        fun callbackPercent(percent: Int);
    }

    fun Download(f: File, metaURL: String, callbackPercent: CallbackPercent, callbackFinished: Runnable) {
        thread {
            var output = FileOutputStream(f, false)
            val client = OkHttpClient();

            val request = Request.Builder()
                    .url(meta2Real(metaURL))
                    .build()
            val response = client.newCall(request).execute()
            val data = readPhotoBytes(response.body!!.bytes())

            val meta = Gson().fromJson(String(data, Charset.forName("UTF-8")), metaJSON::class.java)

            var counter = 0
            for (block in meta.block) {
                lateinit var data2: ByteArray
                val try_max = 3

                for (i in 0 until try_max) {
                    try {
                        val url = block.url.replace("http://", "https://")

                        val request2 = Request.Builder()
                                .url(url)
                                .build()
                        val response2 = client.newCall(request2).execute()
                        data2 = readPhotoBytes(response2.body!!.bytes())
                    } catch (e: Exception) {
                        if (i == try_max - 1) {
                            throw e
                        }
                    }
                }
                output.write(data2)

                //TODO progress
                counter++
                callbackPercent.callbackPercent((counter * 100.0 / meta.block.size).roundToInt())
            }

            output.close()
            callbackFinished.run()
        }
    }

    fun meta2Real(meta: String): String {
        val regexp = Regex("bdex://([a-fA-F0-9]{40})")
        val matches = regexp.find(meta)!!

        return "https://i0.hdslb.com/bfs/album/${matches.groupValues[1]}.png"
    }

    fun readPhotoBytes(input: ByteArray): ByteArray {
        val img = BitmapFactory.decodeByteArray(input, 0, input.size)

        val buf = ArrayList<Byte>()

        for (y in 0 until img.width) {
            for (x in 0 until img.height) {
                val colour: Int = img.getPixel(x, y)
                buf.add(Color.red(colour).toByte())
                buf.add(Color.green(colour).toByte())
                buf.add(Color.blue(colour).toByte())
            }
        }

        val buf1 = buf.toByteArray()
        val length = buf1.readUInt32LE(0).toInt()

        return buf1.copyOfRange(4, 4 + length)
    }

    private fun ByteArray.readUInt32LE(offset: Int = 0): Long {
        return (((this[offset + 3].toInt() and 0xFF).toLong() shl 24) or
                ((this[offset + 2].toInt() and 0xFF).toLong() shl 16) or
                ((this[offset + 1].toInt() and 0xFF).toLong() shl 8) or
                (this[offset].toInt() and 0xFF).toLong())
    }

}
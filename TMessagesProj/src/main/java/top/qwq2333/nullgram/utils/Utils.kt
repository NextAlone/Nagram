package top.qwq2333.nullgram.utils

import android.graphics.Typeface
import android.util.Base64
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessageObject
import top.qwq2333.nullgram.config.ConfigManager
import java.io.BufferedReader
import java.io.FileReader
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils {

    @JvmStatic
    fun showForwardDate(obj: MessageObject, orig: CharSequence): String = if (ConfigManager.getBooleanOrFalse(Defines.dateOfForwardedMsg)) {
        "$orig • ${LocaleController.formatDate(obj.messageOwner.fwd_from.date.toLong())}"
    } else {
        orig.toString()
    }

    @JvmStatic
    fun getBotIDFromUserID(originalID: Long, isChannel: Boolean): Long = if (isChannel) {
        -1000000000000L - originalID
    } else {
        -originalID
    }

    @JvmStatic
    fun getUserIDFromBotID(botID: Long, isChannel: Boolean) = if (isChannel) {
        -(botID + 1000000000000L)
    } else {
        -botID
    }

    @JvmStatic
    fun getSecurePassword(password: String, salt: String): String {
        lateinit var generatedPassword: String
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(Base64.decode(salt, Base64.DEFAULT))
            val bytes = md.digest(password.toByteArray())
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(((bytes[i].toInt() and 0xff) + 0x100).toString(16).substring(1))
            }
            generatedPassword = sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return generatedPassword
    }

    @JvmStatic
    public fun getSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(32)
        random.nextBytes(salt)
        return salt
    }

    @JvmStatic
    var loadSystemEmojiFailed = false

    @JvmStatic
    private var systemEmojiTypeface: Typeface? = null

    @JvmStatic
    fun getSystemEmojiTypeface(): Typeface? {
        if (!loadSystemEmojiFailed && systemEmojiTypeface == null) {
            try {
                val p: Pattern = Pattern.compile(">(.*emoji.*)</font>", Pattern.CASE_INSENSITIVE)
                val br = BufferedReader(FileReader("/system/etc/fonts.xml"))
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    val m: Matcher = p.matcher(line)
                    if (m.find()) {
                        systemEmojiTypeface = Typeface.createFromFile("/system/fonts/" + m.group(1))
                        Log.d("emoji font file fonts.xml = " + m.group(1))
                        break
                    }
                }
                br.close()
            } catch (e: Exception) {
                Log.e(e)
            }
            if (systemEmojiTypeface == null) {
                try {
                    systemEmojiTypeface = Typeface.createFromFile("/system/fonts/${Defines.aospEmojiFont}")
                    Log.d("emoji font file = ${Defines.aospEmojiFont}")
                } catch (e: Exception) {
                    Log.e(e)
                    loadSystemEmojiFailed = true
                }
            }
        }
        return systemEmojiTypeface
    }

}

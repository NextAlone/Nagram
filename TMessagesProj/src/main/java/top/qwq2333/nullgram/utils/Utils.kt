package top.qwq2333.nullgram.utils

import android.util.Base64
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessageObject
import top.qwq2333.nullgram.config.ConfigManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

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

}

package tw.nekomimi.nekogram.transtale

import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import org.telegram.messenger.LocaleController
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.database.mkDatabase
import tw.nekomimi.nekogram.utils.UIUtil
import java.util.*
import kotlin.collections.HashMap

class TranslateDb(val code: String) {

    var conn: ObjectRepository<TransItem> = db.getRepository(code, TransItem::class.java)

    companion object {

        val db = mkDatabase("translate_caches")

        val repo = HashMap<Locale, TranslateDb>()
        val chat = db.getRepository("chat", ChatLanguage::class.java)
        val ccTarget = db.getRepository("opencc", ChatCCTarget::class.java)

        @JvmStatic fun getChatLanguage(chatId: Long, default: Locale): Locale {

            return chat.find(ObjectFilters.eq("chatId", chatId)).firstOrDefault()?.language?.code2Locale
                    ?: default

        }

        @JvmStatic
        fun saveChatLanguage(chatId: Long, locale: Locale) = UIUtil.runOnIoDispatcher {

            chat.update(ChatLanguage(chatId, locale.locale2code), true)

        }

        @JvmStatic
        fun getChatCCTarget(chatId: Long, default: String?): String? {

            return ccTarget.find(ObjectFilters.eq("chatId", chatId)).firstOrDefault()?.ccTarget
                    ?: default

        }

        @JvmStatic
        fun saveChatCCTarget(chatId: Long, target: String) = UIUtil.runOnIoDispatcher {

            ccTarget.update(ChatCCTarget(chatId, target), true)

        }

        @JvmStatic
        fun currentTarget() = NekoConfig.translateToLang.String()?.transDbByCode
                ?: LocaleController.getInstance().currentLocale.transDb

        @JvmStatic
        fun forLocale(locale: Locale) = locale.transDb

        @JvmStatic
        fun currentInputTarget() = NekoConfig.translateInputLang.String().transDbByCode

        @JvmStatic
        fun clearAll() {

            db.listRepositories()
                    .filter { it  != "chat" }
                    .map { db.getCollection(it) }
                    .forEach { it.drop() }

            repo.clear()

        }

    }

    fun clear() = synchronized(this) {

        conn.drop()

    }

    fun contains(text: String) = synchronized(this) { conn.find(ObjectFilters.eq("text", text)).count() > 0 }

    fun save(text: String, trans: String) = synchronized<Unit>(this) {

        conn.update(TransItem(text, trans), true)

    }

    fun query(text: String) = synchronized(this) {

        conn.find(ObjectFilters.eq("text", text)).firstOrDefault()?.trans

    }

}
package tw.nekomimi.nekogram.transtale

import org.dizitart.no2.common.mapper.SimpleNitriteMapper
import org.dizitart.no2.common.module.NitriteModule
import org.dizitart.no2.filters.FluentFilter
import org.dizitart.no2.repository.ObjectRepository
import org.telegram.messenger.LocaleController
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.database.mkDatabase
import tw.nekomimi.nekogram.transtale.entity.ChatCCTarget
import tw.nekomimi.nekogram.transtale.entity.ChatLanguage
import tw.nekomimi.nekogram.transtale.entity.TransItem
import tw.nekomimi.nekogram.transtale.mapper.ChatCCTargetConverter
import tw.nekomimi.nekogram.transtale.mapper.ChatLanguageConverter
import tw.nekomimi.nekogram.transtale.mapper.TransItemConverter
import tw.nekomimi.nekogram.utils.UIUtil
import java.util.Locale

class TranslateDb(val code: String) {

    var conn: ObjectRepository<TransItem> = db.getRepository(TransItem::class.java, code)

    companion object {

        val db = mkDatabase("translate_caches", module = NitriteModule.module(getNitriteMapper()))

        val repo = HashMap<Locale, TranslateDb>()
        val chat: ObjectRepository<ChatLanguage> = db.getRepository(ChatLanguage::class.java, "chat")
        val ccTarget: ObjectRepository<ChatCCTarget> = db.getRepository(ChatCCTarget::class.java, "opencc")

        @JvmStatic fun getNitriteMapper(): SimpleNitriteMapper {
            val nitriteMapper = SimpleNitriteMapper()
            nitriteMapper.registerEntityConverter(ChatCCTargetConverter())
            nitriteMapper.registerEntityConverter(ChatLanguageConverter())
            nitriteMapper.registerEntityConverter(TransItemConverter())
            return nitriteMapper
        }

        @JvmStatic fun getChatLanguage(chatId: Long, default: Locale): Locale {

            return chat.find(FluentFilter.where("chatId").eq(chatId)).firstOrNull()?.language?.code2Locale
                    ?: default

        }

        @JvmStatic
        fun saveChatLanguage(chatId: Long, locale: Locale) = UIUtil.runOnIoDispatcher {

            chat.update(ChatLanguage(chatId, locale.locale2code), true)

        }

        @JvmStatic
        fun getChatCCTarget(chatId: Long, default: String?): String? {

            return ccTarget.find(FluentFilter.where("chatId").eq(chatId)).firstOrNull()?.ccTarget
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

    fun contains(text: String) = synchronized(this) { conn.find(FluentFilter.where("text").eq(text)).count() > 0 }

    fun save(text: String, trans: String) = synchronized<Unit>(this) {

        conn.update(TransItem(text, trans), true)

    }

    fun query(text: String) = synchronized(this) {

        conn.find(FluentFilter.where("text").eq(text)).firstOrNull()?.trans

    }

}

package tw.nekomimi.nekogram.transtale

import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import org.telegram.messenger.LocaleController
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.database.mkDatabase
import java.util.*
import kotlin.collections.HashMap

class TranslateDb(val code: String) {

    var conn: ObjectRepository<TransItem> = db.getRepository(code, TransItem::class.java)

    companion object {

        val db = mkDatabase("translate_caches")

        val repo = HashMap<Locale, TranslateDb>()

        @JvmStatic fun currentTarget() = NekoConfig.translateToLang?.transDbByCode ?: LocaleController.getInstance().currentLocale.transDb

        @JvmStatic fun forLocale(locale: Locale) = locale.transDb

        @JvmStatic fun currentInputTarget() = NekoConfig.translateInputLang.transDbByCode

        @JvmStatic fun clearAll() {

            db.listRepositories().map { it.transDbByCode }.forEach { it.clear() }

            repo.clear()

        }

    }

    fun clear() = synchronized<Unit>(this) {

        conn.drop()
        conn = db.getRepository(code, TransItem::class.java)

    }

    fun contains(text: String) = synchronized(this) { conn.find(ObjectFilters.eq("text", text)).count() > 0 }

    fun save(text: String, trans: String) = synchronized<Unit>(this) {

        conn.update(TransItem(text, trans), true)

    }

    fun query(text: String) = synchronized<String?>(this) {

        return conn.find(ObjectFilters.eq("text", text)).firstOrDefault().trans

    }

}
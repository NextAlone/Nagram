package tw.nekomimi.nekogram.translator

import org.dizitart.no2.objects.filters.ObjectFilters
import tw.nekomimi.nekogram.database.mkCacheDatabase

object TranslateDb {

    val db by lazy { mkCacheDatabase("trans") }
    val conn by lazy { db.getRepository("trans", TransItem::class.java) }

    @JvmStatic
    fun contains(text: String) = conn.find(ObjectFilters.eq("text", text)).count() > 0

    @JvmStatic
    fun save(text: String, trans: String) {

        conn.update(TransItem(text, trans), true)

    }

    @JvmStatic
    fun query(text: String): String? {

        val result = conn.find(ObjectFilters.eq("text", text));

        if (result.hasMore()) {

            return result.first().trans

        }

        return null

    }

}
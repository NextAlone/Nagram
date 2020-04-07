package tw.nekomimi.nekogram.database

import android.content.SharedPreferences
import org.dizitart.no2.Nitrite
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.translator.TranslateDb

fun mkDatabase(name: String) = Nitrite.builder().compressed()
        .filePath("${ApplicationLoader.applicationContext.filesDir.parentFile!!.apply { 
            mkdirs()
        }}/databases/$name.db")
        .openOrCreate(name, "nya")

fun mkCacheDatabase(name: String) = Nitrite.builder().compressed()
        .filePath("${ApplicationLoader.applicationContext.cacheDir}/$name.db")
        .openOrCreate(name, "nya")

fun Nitrite.openSharedPreference(name: String) = DbPref(getCollection(name))
fun openMainSharedPreference(name: String) = ApplicationLoader.databaseMain.openSharedPreference(name)
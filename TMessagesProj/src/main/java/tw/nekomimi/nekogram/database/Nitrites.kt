package tw.nekomimi.nekogram.database

import org.dizitart.no2.Nitrite
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.translator.TranslateDb

fun mkCacheDatabase(name: String) = Nitrite.builder().compressed()
        .filePath("${ApplicationLoader.applicationContext.cacheDir}/$name.db")
        .openOrCreate(name, "nya")
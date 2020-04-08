package tw.nekomimi.nekogram.database

import org.dizitart.no2.Nitrite
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File

fun mkDatabase(name: String): Nitrite {

    val dir = File("${ApplicationLoader.getDataDirFixed()}/files")

    FileUtil.initDir(dir)

    return Nitrite.builder().compressed()
            .filePath("$dir/$name.db")
            .openOrCreate(name, "nya")!!

}

fun mkCacheDatabase(name: String) : Nitrite {

    val dir = File("${ApplicationLoader.getDataDirFixed()}/cache")

    FileUtil.initDir(dir)

    return Nitrite.builder().compressed()
            .filePath("$dir/$name.db")
            .openOrCreate(name, "nya")!!

}

fun Nitrite.openSharedPreference(name: String) = DbPref(getCollection(name))

private lateinit var mainSharedPreferencesDatabase: Nitrite

fun openMainSharedPreference(name: String): DbPref {

    if (!::mainSharedPreferencesDatabase.isInitialized) {

        mainSharedPreferencesDatabase = mkDatabase("shared_preferences")

    }

    return mainSharedPreferencesDatabase.openSharedPreference(name)

}
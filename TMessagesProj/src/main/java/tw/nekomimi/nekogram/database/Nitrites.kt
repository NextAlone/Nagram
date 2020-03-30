package tw.nekomimi.nekogram.database

import org.dizitart.no2.Nitrite
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File

@JvmOverloads
fun mkDatabase(name: String): Nitrite {

    val dir = File("${ApplicationLoader.getDataDirFixed()}/databases")

    FileUtil.initDir(dir)

    return Nitrite.builder().compressed()
            .filePath("$dir/$name.db")
            .openOrCreate(name, "nya")!!

}

@JvmOverloads
fun mkCacheDatabase(name: String) : Nitrite {

    val dir = File("${ApplicationLoader.getDataDirFixed()}/cache")

    FileUtil.initDir(dir)

    return Nitrite.builder().compressed()
            .filePath("$dir/$name.db")
            .openOrCreate(name, "nya")!!

}

fun Nitrite.openSharedPreference(name: String) = DbPref(getCollection(name))

val mainSharedPreferencesDatabase = mkDatabase("shared_preferences")

fun openMainSharedPreference(name: String) = mainSharedPreferencesDatabase.openSharedPreference(name)
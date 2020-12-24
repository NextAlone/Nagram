package tw.nekomimi.nekogram.database

import org.dizitart.no2.Nitrite
import org.dizitart.no2.mvstore.MVStoreModule
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File

fun mkDatabase(name: String): Nitrite {

    val file = File("${ApplicationLoader.getDataDirFixed()}/databases/$name.db")

    FileUtil.initDir(file.parentFile!!)

    fun create() = Nitrite.builder()
            .loadModule(MVStoreModule.withConfig()
                    .filePath(file)
                    .build())
            .openOrCreate()!!

    runCatching {
        return create()
    }.onFailure {
        FileLog.e(it)
        file.deleteRecursively()

    }

    return create()

}

fun Nitrite.openSharedPreference(name: String) = DbPref(getCollection(name))

private lateinit var mainSharedPreferencesDatabase: Nitrite

fun openMainSharedPreference(name: String): DbPref {

    if (!::mainSharedPreferencesDatabase.isInitialized) {
        mainSharedPreferencesDatabase = mkDatabase("shared_preferences")
    }

    return mainSharedPreferencesDatabase.openSharedPreference(name)

}
package tw.nekomimi.nekogram.database

import org.dizitart.no2.Nitrite
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File

fun mkDatabase(name: String): Nitrite {

    File("${ApplicationLoader.getDataDirFixed()}/database").apply {

        if (exists()) deleteRecursively()

    }

    val file = File("${ApplicationLoader.getDataDirFixed()}/databases/$name.db")

    FileUtil.initDir(file.parentFile!!)

    runCatching {

        return Nitrite.builder().compressed()
                .filePath(file.path)
                .openOrCreate()!!

    }.onFailure {

        file.deleteRecursively()

    }

    return Nitrite.builder().compressed()
            .filePath(file.path)
            .openOrCreate()!!

}

fun mkCacheDatabase(name: String): Nitrite {

    val file = File("${ApplicationLoader.getDataDirFixed()}/cache/$name.db")

    FileUtil.initDir(file.parentFile!!)

    runCatching {

        return Nitrite.builder().compressed()
                .filePath(file.path)
                .openOrCreate()!!

    }

    file.deleteRecursively()

    return Nitrite.builder().compressed()
            .filePath(file.path)
            .openOrCreate()!!

}

fun Nitrite.openSharedPreference(name: String) = DbPref(getCollection(name))

private lateinit var mainSharedPreferencesDatabase: Nitrite

fun openMainSharedPreference(name: String): DbPref {

    if (!::mainSharedPreferencesDatabase.isInitialized) {

        mainSharedPreferencesDatabase = mkDatabase("shared_preferences")

    }

    return mainSharedPreferencesDatabase.openSharedPreference(name)

}
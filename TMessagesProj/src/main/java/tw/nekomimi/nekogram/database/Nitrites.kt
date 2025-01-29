package tw.nekomimi.nekogram.database

import org.dizitart.no2.Nitrite
import org.dizitart.no2.common.module.NitriteModule
import org.dizitart.no2.mvstore.MVStoreModule
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File

@JvmOverloads
fun mkDatabase(name: String, delete: Boolean = false, module: NitriteModule? = null): Nitrite {

    val file = File("${ApplicationLoader.getDataDirFixed()}/databases/$name.db")
    FileUtil.initDir(file.parentFile!!)
    if (delete) {
        file.deleteRecursively()
    }

    fun create(): Nitrite {
        val storeModule = MVStoreModule.withConfig()
            .filePath(file)
            .build()
        var nitriteBuilder = Nitrite.builder()
            .loadModule(storeModule)
        if (module != null) nitriteBuilder = nitriteBuilder.loadModule(module)
        val nitrite = nitriteBuilder.openOrCreate()

        val test = nitrite.openSharedPreference("shared_preferences")
        test.collection.close()

        return nitrite
    }

    runCatching {
        return create()
    }.onFailure {
        file.deleteRecursively()
    }

    return create()

}

fun Nitrite.openSharedPreference(name: String) = DbPref(getCollection(name))

private lateinit var mainSharedPreferencesDatabase: Nitrite

@JvmOverloads
fun openMainSharedPreference(name: String, delete: Boolean = false): DbPref {

    if (!::mainSharedPreferencesDatabase.isInitialized || delete) {

        mainSharedPreferencesDatabase = mkDatabase("shared_preferences", delete)

    }

    return try {

        mainSharedPreferencesDatabase.openSharedPreference(name)

    } catch (_: IllegalStateException) {

        openMainSharedPreference(name, true)

    }

}

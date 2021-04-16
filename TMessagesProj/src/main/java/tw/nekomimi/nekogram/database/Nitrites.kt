package tw.nekomimi.nekogram.database

import org.dizitart.no2.Nitrite
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File

@JvmOverloads
fun mkDatabase(name: String, delete: Boolean = false): Nitrite {

    val file = File("${ApplicationLoader.getDataDirFixed()}/databases/$name.db")
    FileUtil.initDir(file.parentFile!!)
    if (delete) {
        file.deleteRecursively()
    }

    fun create(): Nitrite {
        val nitrite = Nitrite.builder()
                .filePath(file)
                .openOrCreate()!!

        val test = nitrite.openSharedPreference("shared_preferences")
        test.connection.close()

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

    } catch (e: IllegalStateException) {

        openMainSharedPreference(name, true)

    }

}
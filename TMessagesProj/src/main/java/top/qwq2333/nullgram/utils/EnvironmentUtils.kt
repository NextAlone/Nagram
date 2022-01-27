/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import top.qwq2333.nullgram.config.ConfigManager
import java.io.File
import java.util.*

object EnvironmentUtils {
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    val rootDirectories: List<File> by lazy {

        try {
            val mStorageManager =
                ApplicationLoader.applicationContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            (mStorageManager.javaClass.getMethod("getVolumePaths")
                .invoke(mStorageManager) as Array<String>).map { File(it) }
        } catch (e: Throwable) {
            AndroidUtilities.getRootDirs()
        }

    }

    @JvmStatic
    val availableDirectories
        get() = LinkedList<File>().apply {

            add(File(ApplicationLoader.getDataDirFixed(), "files/media"))
            add(File(ApplicationLoader.getDataDirFixed(), "cache/media"))

            rootDirectories.forEach {

                add(
                    File(
                        it,
                        "Android/data/" + ApplicationLoader.applicationContext.packageName + "/files"
                    )
                )
                add(
                    File(
                        it,
                        "Android/data/" + ApplicationLoader.applicationContext.packageName + "/cache"
                    )
                )

            }

            if (Build.VERSION.SDK_INT < 30) {
                add(Environment.getExternalStoragePublicDirectory("NekoX"))
            }

        }.map { it.path }.toTypedArray()

    // This is the only media path of NekoX, don't use other!
    @JvmStatic
    fun getTelegramPath(): File {

        if (ConfigManager.getStringOrDefault(Defines.cachePath, "") == "") {
            // https://github.com/NekoX-Dev/NekoX/issues/284
            ConfigManager.putString(Defines.cachePath, availableDirectories[2])
        }

        var telegramPath = File(ConfigManager.getStringOrDefault(Defines.cachePath, ""))

        if (telegramPath.isDirectory || telegramPath.mkdirs()) {

            return telegramPath

        }

        // fallback

        telegramPath = ApplicationLoader.applicationContext.getExternalFilesDir(null) ?: File(
            ApplicationLoader.getDataDirFixed(),
            "cache/files"
        )

        if (telegramPath.isDirectory || telegramPath.mkdirs()) {

            return telegramPath

        }

        telegramPath = File(ApplicationLoader.getDataDirFixed(), "cache/files")

        if (!telegramPath.isDirectory) telegramPath.mkdirs()

        return telegramPath

    }

    @JvmStatic
    fun doTest() {

        d("rootDirectories: ${rootDirectories.size}")

        rootDirectories.forEach { d(it.path) }

    }
}

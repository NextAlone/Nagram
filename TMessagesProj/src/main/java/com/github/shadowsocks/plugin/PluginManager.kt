/*******************************************************************************
 *                                                                             *
 *  Copyright (C) 2017 by Max Lv <max.c.lv@gmail.com>                          *
 *  Copyright (C) 2017 by Mygod Studio <contact-shadowsocks-android@mygod.be>  *
 *                                                                             *
 *  This program is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by       *
 *  the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                        *
 *                                                                             *
 *  This program is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 *  GNU General Public License for more details.                               *
 *                                                                             *
 *  You should have received a copy of the GNU General Public License          *
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                             *
 *******************************************************************************/

package com.github.shadowsocks.plugin

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.system.Os
import androidx.annotation.RequiresApi
import com.github.shadowsocks.utils.listenForPackageChanges
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import java.io.File
import java.io.FileNotFoundException

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object PluginManager {

    val app get() = ApplicationLoader.applicationContext

    fun bundleOf(pair: Pair<String, String>) = Bundle().apply { putString(pair.first, pair.second) }

    class PluginNotFoundException(plugin: String) : FileNotFoundException(plugin) {
    }

    private var receiver: BroadcastReceiver? = null
    private var cachedPlugins: PluginList? = null

    @JvmStatic
    fun fetchPlugins() = synchronized(this) {
        if (receiver == null) receiver = ApplicationLoader.applicationContext.listenForPackageChanges {
            synchronized(this) {
                receiver = null
                cachedPlugins = null
            }
        }
        if (cachedPlugins == null) cachedPlugins = PluginList()
        cachedPlugins!!
    }

    private fun buildUri(id: String) = Uri.Builder()
            .scheme(PluginContract.SCHEME)
            .authority(PluginContract.AUTHORITY)
            .path("/$id")
            .build()

    @JvmStatic
    fun buildIntent(id: String, action: String): Intent = Intent(action, buildUri(id))

    // the following parts are meant to be used by :bg
    @Throws(Throwable::class)
    fun init(configuration: PluginConfiguration): Pair<String, PluginOptions>? {
        if (configuration.selected.isEmpty()) return null
        var throwable: Throwable? = null

        try {
            val result = initNative(configuration)
            if (result != null) return result
        } catch (t: Throwable) {
            if (throwable == null) throwable = t else FileLog.e(t)
        }

        // add other plugin types here

        throw throwable ?: PluginNotFoundException(configuration.selected)
    }

    private fun initNative(configuration: PluginConfiguration): Pair<String, PluginOptions>? {
        var flags = PackageManager.GET_META_DATA
        if (Build.VERSION.SDK_INT >= 24) {
            flags = flags or PackageManager.MATCH_DIRECT_BOOT_UNAWARE or PackageManager.MATCH_DIRECT_BOOT_AWARE
        }
        val providers = app.packageManager.queryIntentContentProviders(
                Intent(PluginContract.ACTION_NATIVE_PLUGIN, buildUri(configuration.selected)), flags)
        if (providers.isEmpty()) return null
        val provider = providers.single().providerInfo
        val options = configuration.getOptions { provider.loadString(PluginContract.METADATA_KEY_DEFAULT_CONFIG) }
        var failure: Throwable? = null
        try {
            initNativeFaster(provider)?.also { return it to options }
        } catch (t: Throwable) {
            FileLog.w("Initializing native plugin faster mode failed")
            failure = t
        }

        val uri = Uri.Builder().apply {
            scheme(ContentResolver.SCHEME_CONTENT)
            authority(provider.authority)
        }.build()
        try {
            return initNativeFast(app.contentResolver, options, uri)?.let { it to options }
        } catch (t: Throwable) {
            FileLog.w("Initializing native plugin fast mode failed")
            failure?.also { t.addSuppressed(it) }
            failure = t
        }

        try {
            return initNativeSlow(app.contentResolver, options, uri)?.let { it to options }
        } catch (t: Throwable) {
            failure?.also { t.addSuppressed(it) }
            throw t
        }
    }

    private fun initNativeFaster(provider: ProviderInfo): String? {
        return provider.loadString(PluginContract.METADATA_KEY_EXECUTABLE_PATH)?.let { relativePath ->
            File(provider.applicationInfo.nativeLibraryDir).resolve(relativePath).apply {
                check(canExecute())
            }.absolutePath
        }
    }

    private fun initNativeFast(cr: ContentResolver, options: PluginOptions, uri: Uri): String? {
        return cr.call(uri, PluginContract.METHOD_GET_EXECUTABLE, null,
                bundleOf(PluginContract.EXTRA_OPTIONS to options.id))?.getString(PluginContract.EXTRA_ENTRY)?.also {
            check(File(it).canExecute())
        }
    }

    @SuppressLint("Recycle")
    private fun initNativeSlow(cr: ContentResolver, options: PluginOptions, uri: Uri): String? {
        var initialized = false
        fun entryNotFound(): Nothing = throw IndexOutOfBoundsException("Plugin entry binary not found")
        val pluginDir = File(app.noBackupFilesDir, "plugin")
        (cr.query(uri, arrayOf(PluginContract.COLUMN_PATH, PluginContract.COLUMN_MODE), null, null, null)
            ?: return null).use { cursor ->
            if (!cursor.moveToFirst()) entryNotFound()
            pluginDir.deleteRecursively()
            if (!pluginDir.mkdirs()) throw FileNotFoundException("Unable to create plugin directory")
            val pluginDirPath = pluginDir.absolutePath + '/'
            do {
                val path = cursor.getString(0)
                val file = File(pluginDir, path)
                check(file.absolutePath.startsWith(pluginDirPath))
                cr.openInputStream(uri.buildUpon().path(path).build())!!.use { inStream ->
                    file.outputStream().use { outStream -> inStream.copyTo(outStream) }
                }
                Os.chmod(file.absolutePath, when (cursor.getType(1)) {
                    Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(1)
                    Cursor.FIELD_TYPE_STRING -> cursor.getString(1).toInt(8)
                    else -> throw IllegalArgumentException("File mode should be of type int")
                })
                if (path == options.id) initialized = true
            } while (cursor.moveToNext())
        }
        if (!initialized) entryNotFound()
        return File(pluginDir, options.id).absolutePath
    }

    fun ComponentInfo.loadString(key: String) = when (val value = metaData.get(key)) {
        is String -> value
        is Int -> app.packageManager.getResourcesForApplication(applicationInfo).getString(value)
        null -> null
        else -> error("meta-data $key has invalid type ${value.javaClass}")
    }
}

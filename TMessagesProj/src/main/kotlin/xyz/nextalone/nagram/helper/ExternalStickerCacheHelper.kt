package xyz.nextalone.nagram.helper

import android.content.Context
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.MediaDataController
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet
import tw.nekomimi.nekogram.config.cell.ConfigCellAutoTextCheck
import xyz.nextalone.nagram.NaConfig
import java.io.File

object ExternalStickerCacheHelper {
    const val TAG = "ExternalStickerCache"

    private val cachePath = ApplicationLoader.applicationContext.getExternalFilesDir(null)!!.resolve("caches")

    @JvmStatic
    fun checkUri(configCell: ConfigCellAutoTextCheck, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            NaConfig.externalStickerCacheUri?.let { uri ->
                AndroidUtilities.runOnUIThread { configCell.setSubtitle("Loading...") }
                val dir = DocumentFile.fromTreeUri(context, uri)
                var subtitle: String
                if (dir == null) {
                    subtitle = "Error: failed to access document"
                } else {
                    if (dir.isDirectory) {
                        val testFile = dir.findFile("test") ?: dir.createFile("text/plain", "test")
                        if (testFile == null) {
                            subtitle = "Error: cannot create file"
                        } else {
                            if (testFile.canRead() && testFile.canWrite()) {
                                subtitle = "Currently using: ${dir.name}"
                            } else {
                                subtitle = "Error: read/write is not supported"
                            }
                            if (!testFile.delete()) subtitle = "Error: cannot delete file"
                        }
                    } else {
                        subtitle = "Error: not a directory"
                    }
                }
                AndroidUtilities.runOnUIThread { configCell.setSubtitle(subtitle) }
            }
        }
    }

    private var caching = false
    private var cacheAgain = false

    @JvmStatic
    fun cacheStickers() {
        if (caching) {
            cacheAgain = true
            return
        }
        if (NaConfig.externalStickerCache.String().isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch {
            caching = true
            val stickerSets = MediaDataController.getInstance(UserConfig.selectedAccount).getStickerSets(MediaDataController.TYPE_IMAGE)
            val context = ApplicationLoader.applicationContext
            try {
                val uri = NaConfig.externalStickerCacheUri ?: return@launch
                val resolver = context.contentResolver
                DocumentFile.fromTreeUri(context, uri)?.let { dir ->
                    logD("Caching ${stickerSets.size} sticker set(s)...")
                    if (dir.isDirectory) {
                        val stickerSetDirMap = dir.listFiles().run {
                            val map = mutableMapOf<String, DocumentFile>()
                            forEach { it.name?.let { name -> map[name] = it } }
                            map
                        }
                        stickerSets.forEach { stickerSetObject ->
                            val stickerSet = stickerSetObject.set
                            val stickers = stickerSetObject.documents
                            val idString = stickerSet.id.toString()
                            (stickerSetDirMap[idString] ?: dir.createDirectory(idString))?.let { stickerSetDir ->
                                if (stickerSetDir.isDirectory) {
                                    val stickerFileMap = stickerSetDir.listFiles().run {
                                        val map = mutableMapOf<String, DocumentFile>()
                                        forEach { it.name?.let { name -> map[name] = it } }
                                        map
                                    }
                                    for (sticker in stickers) {
                                        val webp = "image/webp"
                                        val webpExt = ".webp"
                                        val localPath = "${sticker.dc_id}_${sticker.id}.webp"
                                        val localPathLowQuality = "-${sticker.id}_1109.webp"
                                        val stickerFile = File(cachePath, localPath)
                                        val stickerFileLowQuality = File(cachePath, localPathLowQuality)
                                        val destName = "${sticker.id}_high"
                                        val destNameExt = destName + webpExt
                                        val destNameNotFound = stickerFileMap[destNameExt] == null
                                        val destNameLowQuality = "${sticker.id}_low"
                                        val destNameLowQualityExt = destNameLowQuality + webpExt

                                        if (stickerFile.exists()) {
                                            if (destNameNotFound) {
                                                stickerSetDir.createFile(webp, destName)?.let { destFile ->
                                                    resolver.openOutputStream(destFile.uri)?.let { stickerFile.inputStream().copyTo(it) }
                                                    logV("Created file ${destFile.name}")
                                                    stickerFileMap[destNameLowQualityExt]?.let {
                                                        it.delete()
                                                        logV("Deleted low quality file")
                                                    }
                                                }
                                            }
                                        } else if (stickerFileLowQuality.exists()) {
                                            if (stickerFileMap[destNameLowQualityExt] == null && destNameNotFound) {
                                                stickerSetDir.createFile(webp, destNameLowQuality)?.let { destFileLowQuality ->
                                                    resolver.openOutputStream(destFileLowQuality.uri)?.let { stickerFileLowQuality.inputStream().copyTo(it) }
                                                    logV("Created low quality file ${destFileLowQuality.name}")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logException(e, "caching stickers")
            }
            if (cacheAgain) {
                delay(30000)
                cacheAgain = false
                cacheStickers()
            } else {
                caching = false
            }
        }
    }

    @JvmStatic
    fun refreshCacheFiles(set: TL_messages_stickerSet) {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = NaConfig.externalStickerCacheUri ?: return@launch
            val context = ApplicationLoader.applicationContext
            try {
                DocumentFile.fromTreeUri(context, uri)?.let { dir ->
                    val stickerSet = set.set
                    val idString = stickerSet.id.toString()
                    logD("Refreshing cache $idString...")
                    dir.findFile(idString)?.let {
                        it.delete()
                        logD("Deleting exist files...")
                        while (true) {
                            delay(500)
                            if (dir.findFile(idString) == null) break
                        }
                    }
                    dir.createDirectory(idString)?.let { stickerSetDir ->
                        val stickers = set.documents
                        val resolver = context.contentResolver
                        for (sticker in stickers) {
                            val localPath = "${sticker.dc_id}_${sticker.id}.webp"
                            val localPathLowQuality = "-${sticker.id}_1109.webp"
                            val stickerFile = File(cachePath, localPath)
                            val stickerFileLowQuality = File(cachePath, localPathLowQuality)
                            fun create(name: String, type: String) = stickerSetDir.createFile(type, name)
                            val webp = "image/webp"
                            val destName = "${sticker.id}_high"
                            val destNameLowQuality = "${sticker.id}_low"

                            if (stickerFile.exists()) {
                                create(destName, webp)?.let { destFile ->
                                    resolver.openOutputStream(destFile.uri)?.let { stickerFile.inputStream().copyTo(it) }
                                    logV("Created file ${destFile.name}")
                                }
                            } else if (stickerFileLowQuality.exists()) {
                                create(destNameLowQuality, webp)?.let { destFileLowQuality ->
                                    resolver.openOutputStream(destFileLowQuality.uri)?.let { stickerFileLowQuality.inputStream().copyTo(it) }
                                    logV("Created low quality file ${destFileLowQuality.name}")
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logException(e, "refreshing specific cache")
            }
        }
    }

    @JvmStatic
    fun deleteCacheFiles(set: TL_messages_stickerSet) {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = NaConfig.externalStickerCacheUri ?: return@launch
            val context = ApplicationLoader.applicationContext
            try {
                DocumentFile.fromTreeUri(context, uri)?.let { dir ->
                    val stickerSet = set.set
                    val idString = stickerSet.id.toString()
                    dir.findFile(idString)?.delete()
                }
            } catch (e: Exception) {
                logException(e, "deleting specific cache")
            }
        }
    }

    private fun logException(e: Exception, s: String) {
        val exception = e.javaClass.canonicalName
        val message = e.message
        Log.e(TAG, "Exception while $s: $exception: $message")
    }
    private fun logV(message: String) = Log.v(TAG, message)
    private fun logD(message: String) = Log.d(TAG, message)
}

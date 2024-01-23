package xyz.nextalone.nagram.helper

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.BuildVars
import org.telegram.messenger.MediaDataController
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet
import tw.nekomimi.nekogram.config.cell.ConfigCellButton
import xyz.nextalone.nagram.NaConfig
import java.io.File

object ExternalStickerCacheHelper {
    @JvmStatic
    fun checkUri(configCell: ConfigCellButton, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = NaConfig.externalStickerCacheUri
            val dir = DocumentFile.fromTreeUri(context, uri)
            if (dir == null) {
                configCell.setSubtitle("Error: failed to access document")
            } else {
                if (dir.isDirectory) {
                    val testFile = dir.createFile("text/plain", "test file")
                    if (testFile == null) {
                        configCell.setSubtitle("Error: cannot create file")
                    } else {
                        if (testFile.canRead() && testFile.canWrite()) {
                            configCell.setSubtitle("Currently using: ${dir.name}")
                        } else {
                            configCell.setSubtitle("Error: read/write is not supported")
                        }
                        if (!testFile.delete()) configCell.setSubtitle("Error: cannot delete file")
                    }
                } else {
                    configCell.setSubtitle("Error: not a directory")
                }
            }
        }
    }

    private val SYNC get() = ""

    @JvmStatic
    fun onCacheStickers(type: Int, stickerSets: ArrayList<TL_messages_stickerSet>, context: Context) {
        if (NaConfig.externalStickerCache.String().isEmpty()) return
        when (type) {
            MediaDataController.TYPE_EMOJI,
            MediaDataController.TYPE_EMOJIPACKS,
            MediaDataController.TYPE_FEATURED_EMOJIPACKS,
            -> return
            else -> CoroutineScope(Dispatchers.IO).launch {
                synchronized(SYNC) {
                    try {
                        val uri = NaConfig.externalStickerCacheUri
                        val resolver = context.contentResolver
                        DocumentFile.fromTreeUri(context, uri)?.let { dir ->
                            val cachePath = getCachePath(context)
                            if (dir.isDirectory) {
                                val stickerSetDirMap = dir.listFiles().run {
                                    val map = mutableMapOf<String, DocumentFile>()
                                    forEach { it.name?.let { name -> map[name] = it } }
                                    map
                                }
                                stickerSets.forEach { stickerSetObject ->
                                    val stickerSet = stickerSetObject.set
                                    val stickers = stickerSetObject.documents
                                    val avatarId = stickerSet.thumb_document_id
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
                                                val localPath = "${sticker.dc_id}_${sticker.id}.webp"
                                                val localPathLowQuality = "-${sticker.id}_1109.webp"
                                                val stickerFile = File(cachePath, localPath)
                                                val stickerFileLowQuality = File(cachePath, localPathLowQuality)
                                                val destName = "${sticker.id}_high"
                                                val destNameLowQuality = "${sticker.id}_low"

                                                if (stickerFile.exists()) {
                                                    if (stickerFileMap[destName] == null) {
                                                        stickerSetDir.createFile(webp, destName)?.let { destFile ->
                                                            resolver.openOutputStream(destFile.uri)?.let { stickerFile.inputStream().copyTo(it) }
                                                            stickerFileMap[destNameLowQuality]?.delete()
                                                        }
                                                    }
                                                } else if (stickerFileLowQuality.exists()) {
                                                    if (stickerFileMap[destNameLowQuality] == null) {
                                                        stickerSetDir.createFile(webp, destNameLowQuality)?.let { destFileLowQuality ->
                                                            resolver.openOutputStream(destFileLowQuality.uri)?.let { stickerFile.inputStream().copyTo(it) }
                                                            stickerFileMap[destName]?.delete()
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
                        if (BuildVars.DEBUG_VERSION) throw RuntimeException(e)
                    }
                }
            }
        }
    }

    private fun getCachePath(context: Context) = context.getExternalFilesDir(null)!!.resolve("caches")
}

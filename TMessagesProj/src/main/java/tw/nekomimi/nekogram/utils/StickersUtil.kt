package tw.nekomimi.nekogram.utils

import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.MediaDataController
import org.telegram.messenger.NotificationCenter
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLRPC.*
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ActionBar.BaseFragment
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object StickersUtil {

    private fun AlertDialog.updateStatus(message: CharSequence) = AndroidUtilities.runOnUIThread { setMessage(message); }

    @JvmStatic
    fun importStickers(stickerObj: JsonObject, f: BaseFragment, progress: AlertDialog) = runBlocking {

        val cancel = AtomicBoolean()

        progress.setOnCancelListener {

            cancel.set(true)

        }

        val installed = f.mediaDataController.getStickerSets(MediaDataController.TYPE_IMAGE)

        val finishLoad = AtomicBoolean()
        val archivedSets = LinkedList<StickerSetCovered>()

        fun loadStickers() {

            val req = TL_messages_getArchivedStickers()
            req.offset_id = if (archivedSets.isEmpty()) 0 else archivedSets[archivedSets.size - 1].set.id
            req.limit = 100
            req.masks = false

            f.connectionsManager.sendRequest(req) { response, _ ->

                if (response is TL_messages_archivedStickers) {

                    archivedSets.addAll(response.sets)

                    if (response.sets.size < 100) {

                        finishLoad.set(true)

                    } else {

                        loadStickers()

                    }

                }

            }

        }

        loadStickers()

        stickerObj.getAsJsonObject("stickerSets")?.also {

            val stickerSets = LinkedList(it.entrySet().map {

                object : Map.Entry<String, String> {

                    override val key: String get() = it.key
                    override val value: String get() = it.value.asString

                }

            }).apply { reverse() }

            val waitLock = AtomicBoolean()

            install@ for (stickerSetObj in stickerSets) {

                if (cancel.get()) return@runBlocking

                for (s in installed) if (s.set.short_name == stickerSetObj.value) continue@install
                for (s in archivedSets) if (s.set.short_name == stickerSetObj.value) continue@install

                waitLock.set(false)

                f.connectionsManager.sendRequest(TL_messages_installStickerSet().apply {

                    stickerset = TL_inputStickerSetShortName().apply {

                        short_name = stickerSetObj.value

                    }

                }) { response, error ->

                    if (response is TL_messages_stickerSetInstallResultSuccess) {

                        f.mediaDataController.loadStickers(MediaDataController.TYPE_IMAGE, false, true)

                        progress.updateStatus("Installed: ${stickerSetObj.key}")

                    } else if (response is TL_messages_stickerSetInstallResultArchive) {

                        f.mediaDataController.loadStickers(MediaDataController.TYPE_IMAGE, false, true)

                        AndroidUtilities.runOnUIThread {

                            f.notificationCenter.postNotificationName(NotificationCenter.needAddArchivedStickers, response.sets)

                        }

                        progress.updateStatus("Archived: ${stickerSetObj.key}")

                    } else if (error != null) {

                        progress.updateStatus("Error ${error.code}: ${error.text}")

                    }

                    waitLock.set(true)

                }

                while (!waitLock.get() && !cancel.get()) delay(100L)

            }

        }

        stickerObj.getAsJsonObject("archivedStickers")?.also {

            val stickerSets = LinkedList(it.entrySet().map {

                object : Map.Entry<String, String> {

                    override val key: String get() = it.key
                    override val value: String get() = it.value.asString

                }

            }).apply { reverse() }

            val waitLock = AtomicBoolean()

            install@ for (stickerSetObj in stickerSets) {

                if (cancel.get()) return@runBlocking

                waitLock.set(false)

                for (s in installed) if (s.set.short_name == stickerSetObj.value) continue@install
                for (s in archivedSets) if (s.set.short_name == stickerSetObj.value) continue@install

                f.connectionsManager.sendRequest(TL_messages_installStickerSet().apply {

                    stickerset = TL_inputStickerSetShortName().apply {

                        short_name = stickerSetObj.value
                        archived = true

                    }

                }) { response, error ->

                    if (response is TL_messages_stickerSetInstallResultArchive) {

                        f.mediaDataController.loadStickers(MediaDataController.TYPE_IMAGE, false, true)

                        AndroidUtilities.runOnUIThread {

                            f.notificationCenter.postNotificationName(NotificationCenter.needAddArchivedStickers, response.sets)

                        }

                        progress.updateStatus("Archived: ${stickerSetObj.key}")

                    } else if (error != null) {

                        progress.updateStatus("Error ${error.code}: ${error.text}")

                    }

                    waitLock.set(true)

                }

                while (!waitLock.get() && !cancel.get()) delay(100L)

            }

        }

        return@runBlocking

    }

    @JvmStatic
    fun exportStickers(account: Int, exportSets: Boolean, exportArchived: Boolean) = runBlocking {

        val exportObj = JsonObject()

        if (exportSets) {

            exportObj.add("stickerSets", JsonObject().apply {

                MediaDataController.getInstance(account).getStickerSets(MediaDataController.TYPE_IMAGE).forEach {

                    addProperty(it.set.title, it.set.short_name)

                }

            })

        }

        if (exportArchived) {

            val finishLoad = AtomicBoolean()
            val archivedSets = LinkedList<StickerSetCovered>()

            fun loadStickers() {

                val req = TL_messages_getArchivedStickers()
                req.offset_id = if (archivedSets.isEmpty()) 0 else archivedSets[archivedSets.size - 1].set.id
                req.limit = 100
                req.masks = false

                ConnectionsManager.getInstance(account).sendRequest(req) { response, _ ->

                    if (response is TL_messages_archivedStickers) {

                        archivedSets.addAll(response.sets)

                        if (response.sets.size < 100) {

                            finishLoad.set(true)

                        } else {

                            loadStickers()

                        }

                    }

                }

            }

            loadStickers()

            while (!finishLoad.get()) delay(100L)

            exportObj.add("archivedStickers", JsonObject().apply {

                archivedSets.forEach {

                    addProperty(it.set.title, it.set.short_name)

                }

            })

        }

        return@runBlocking exportObj

    }

    @JvmStatic
    fun exportStickers(exportSets: Collection<StickerSet>) = runBlocking {

        val exportObj = JsonObject()

        exportObj.add("stickerSets", JsonObject().apply {

            exportSets.forEach {

                addProperty(it.title, it.short_name)

            }

        })

        return@runBlocking exportObj

    }

}
package tw.nekomimi.nekogram.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.telegram.messenger.MediaDataController
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLRPC.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object StickersUtil {

    @JvmStatic
    fun exportStickers(
            account: Int, exportSets: Boolean,
            exportArchived: Boolean, exportFavourite: Boolean,
            exportRecent: Boolean, exportGifs: Boolean
    ) = runBlocking {


        val gson = Gson()

        val exportObj = JsonObject()

        if (exportSets) {

            exportObj.add("stickerSets", JsonArray().apply {

                MediaDataController.getInstance(account).getStickerSets(MediaDataController.TYPE_IMAGE).forEach {

                    add(JsonObject().apply {

                        addProperty(it.set.title,it.set.short_name)

                    })

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

            exportObj.add("archivedStickers", JsonArray().apply {

                archivedSets.forEach {

                    add(JsonObject().apply {

                        addProperty(it.set.title,it.set.short_name)

                    })

                }

            })

        }

        if (exportRecent) {

            MediaDataController.getInstance(account).loadRecents(MediaDataController.TYPE_IMAGE, false, true, false)

            while (!MediaDataController.getInstance(account).recentStickersLoaded[MediaDataController.TYPE_IMAGE]) delay(100L)

            exportObj.add("recentStickers", JsonArray().apply {

                MediaDataController.getInstance(account).getRecentStickers(MediaDataController.TYPE_IMAGE).forEach {

                    add(gson.toJsonTree(it))

                }

            })

        }

        if (exportFavourite) {

            MediaDataController.getInstance(account).loadRecents(MediaDataController.TYPE_FAVE, false, true, false)

            while (!MediaDataController.getInstance(account).recentStickersLoaded[MediaDataController.TYPE_FAVE]) delay(100L)

            exportObj.add("favouriteStickers", JsonArray().apply {

                MediaDataController.getInstance(account).getRecentStickers(MediaDataController.TYPE_FAVE).forEach {

                    add(gson.toJsonTree(it))

                }

            })

        }

        if (exportGifs) {

            MediaDataController.getInstance(account).loadRecents(MediaDataController.TYPE_IMAGE, true, true, false)

            while (!MediaDataController.getInstance(account).recentGifsLoaded) delay(100L)

            exportObj.add("recentGifs", JsonArray().apply {

                MediaDataController.getInstance(account).recentGifs.forEach {

                    add(gson.toJsonTree(it))

                }

            })

        }

        return@runBlocking exportObj

    }

}
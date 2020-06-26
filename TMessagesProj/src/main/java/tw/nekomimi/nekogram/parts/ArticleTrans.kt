package tw.nekomimi.nekogram.parts

import kotlinx.coroutines.*
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ArticleViewer
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.TranslateDb
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.utils.AlertUtil
import tw.nekomimi.nekogram.utils.UIUtil
import java.lang.Runnable
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap
import kotlin.collections.HashSet

fun HashSet<Any>.filterBaseTexts(): HashSet<Any> {

    var hasNext: Boolean

    do {

        hasNext = false

        HashSet(this).forEach { item ->

            when (item) {

                is TLRPC.TL_textConcat -> {

                    remove(item)
                    addAll(item.texts)

                    hasNext = true

                }

            }

        }

    } while (hasNext)

    return this

}

fun ArticleViewer.doTransLATE() {

    val dialog = AlertUtil.showProgress(parentActivity)

    dialog.show()

    fun update(message: String) {

        UIUtil.runOnUIThread(Runnable { dialog.setMessage(message) })

    }

    val transPool = newFixedThreadPoolContext(5, "Article Trans Pool")

    val cancel = AtomicBoolean(false)

    dialog.setOnCancelListener {

        adapter[0].trans = false
        transMenu.setTextAndIcon(LocaleController.getString("Translate", R.string.Translate), R.drawable.ic_translate)
        cancel.set(true)
        transPool.close()

    }

    GlobalScope.launch(Dispatchers.IO) {

        val copy = HashMap(adapter[0].textToBlocks)
        val array = HashSet(adapter[0].textBlocks).filterBaseTexts()

        val errorCount = AtomicInteger()

        val deferreds = LinkedList<Deferred<Unit>>()

        val all = array.size
        val taskCount = AtomicInteger(array.size)

        update("0 / $all")

        array.forEach { item ->

            when (item) {

                is TLRPC.RichText -> getText(adapter[0], null, item, item, copy[item] ?: copy[item.parentRichText], 1000, true).takeIf { it.isNotBlank() }?.toString()
                is String -> item
                else -> null

            }?.also { str ->

                deferreds.add(async(transPool) {

                    if (TranslateDb.currentTarget().contains(str)) {

                        update("${all - taskCount.get()} / $all")

                        if (taskCount.decrementAndGet() % 10 == 0) UIUtil.runOnUIThread(Runnable {

                            updatePaintSize()

                        })

                        return@async

                    }

                    runCatching {

                        if (cancel.get()) return@async

                        Translator.translate(str)

                        update((all - taskCount.get()).toString() + " / " + all)

                        if (taskCount.decrementAndGet() % 10 == 0) UIUtil.runOnUIThread(Runnable {

                            updatePaintSize()

                        })

                    }.onFailure {

                        if (cancel.get()) return@async

                        if (errorCount.incrementAndGet() > 3) {

                            cancel.set(true)

                            UIUtil.runOnUIThread(Runnable {

                                cancel.set(true)
                                dialog.dismiss()
                                updatePaintSize()
                                adapter[0].trans = false
                                transMenu.setTextAndIcon(LocaleController.getString("Translate", R.string.Translate), R.drawable.ic_translate)

                                AlertUtil.showTransFailedDialog(parentActivity, it is UnsupportedOperationException,it.message ?: it.javaClass.simpleName, Runnable {
                                    doTransLATE()
                                })

                            })

                        }

                    }

                })

            }.also {

                if (it == null) taskCount.decrementAndGet()

            }

        }

        deferreds.awaitAll()

        UIUtil.runOnUIThread(Runnable {

            transPool.cancel()

            if (!cancel.get()) {

                updatePaintSize()
                dialog.dismiss()

            }

        })

    }


}
package tw.nekomimi.nekogram.parts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.telegram.tgnet.tl.TL_iv
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ArticleViewer
import tw.nekomimi.nekogram.transtale.TranslateDb
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.utils.AlertUtil
import tw.nekomimi.nekogram.utils.UIUtil
import tw.nekomimi.nekogram.utils.uUpdate
import java.util.WeakHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private const val ARTICLE_PARALLELISM = 5
private const val MAX_ERRORS = 3
private const val PAINT_REFRESH_INTERVAL = 10

fun HashSet<Any>.filterBaseTexts(): HashSet<Any> {

    var hasNext: Boolean

    do {

        hasNext = false

        HashSet(this).forEach { item ->

            when (item) {

                is TL_iv.textConcat -> {

                    remove(item)
                    addAll(item.texts)

                    hasNext = true

                }

            }

        }

    } while (hasNext)

    return this

}

/**
 * NekoX: scope of the article translation job per viewer, canceled through
 * [cancelArticleTranslations] so that no work outlives the page.
 */
private val articleScopes = WeakHashMap<ArticleViewer, CoroutineScope>()

/** Cancels an in-flight article translation, if any. */
fun ArticleViewer.cancelArticleTranslations() {
    synchronized(articleScopes) {
        articleScopes.remove(this)?.cancel()
    }
}

/** Progress label of the article translation, ticked from several coroutines at once. */
private class ArticleProgress(private val status: AlertDialog) {
    private val total = AtomicInteger(0)
    private val done = AtomicInteger(0)

    fun start(all: Int) {
        total.set(all)
        status.uUpdate("0 / $all")
    }

    /** Marks one block as done and refreshes the paint every [PAINT_REFRESH_INTERVAL] blocks. */
    fun tick(refreshPaint: () -> Unit) {
        val current = done.incrementAndGet()
        val all = total.get()
        if (current % PAINT_REFRESH_INTERVAL == 0) {
            UIUtil.runOnUIThread(Runnable { refreshPaint() })
        }
        status.uUpdate("${all - current} / $all")
    }
}

fun ArticleViewer.doTransLATE() {

    val status = AlertUtil.showProgress(parentActivity)
    status.show()

    val cancel = AtomicBoolean(false)

    status.setOnCancelListener {
        updateTranslateButton(false)
        cancel.set(true)
        cancelArticleTranslations()
    }

    // Blocking network calls: Dispatchers.IO with a bounded parallelism. Scoped to this viewer
    // instead of GlobalScope, so leaving the page aborts the work that is still pending.
    val dispatcher = Dispatchers.IO.limitedParallelism(ARTICLE_PARALLELISM, "ArticleTrans")
    val scope = CoroutineScope(SupervisorJob() + dispatcher)
    synchronized(articleScopes) {
        articleScopes[this]?.cancel()
        articleScopes[this] = scope
    }

    scope.launch {
        val progress = ArticleProgress(status)
        val failures = AtomicInteger()
        try {
            val texts = collectTexts()
            progress.start(texts.size)

            texts.map { text ->
                async { translateArticleText(text, cancel, failures, progress) }
            }.awaitAll()

            if (failures.get() > MAX_ERRORS) {
                UIUtil.runOnUIThread { showArticleTransFailed(status) }
            } else {
                UIUtil.runOnUIThread { finishArticleTrans(status, cancel) }
            }
        } catch (e: Exception) {
            if (!cancel.get()) {
                UIUtil.runOnUIThread { showArticleTransFailed(status, e) }
            }
        } finally {
            UIUtil.runOnUIThread(Runnable { updatePaintSize() })
        }
    }
}

/** Flattens the article into the list of texts that need to be translated. */
private fun ArticleViewer.collectTexts(): List<String> {
    val blocks = HashMap(pages[0].adapter.textToBlocks)
    return HashSet<Any>(pages[0].adapter.textBlocks).filterBaseTexts().mapNotNull { item ->
        when (item) {
            is TL_iv.RichText -> getText(
                pages[0].adapter, null, item, item, blocks[item] ?: blocks[item.parentRichText], 1000, true
            )?.takeIf { it.isNotBlank() }?.toString()

            is String -> item

            else -> null
        }
    }
}

/**
 * Translates a single article block.
 *
 * @return false when the block was skipped or failed, so the caller can count failures.
 */
private suspend fun ArticleViewer.translateArticleText(
    text: String,
    cancel: AtomicBoolean,
    failures: AtomicInteger,
    progress: ArticleProgress
): Boolean {
    if (TranslateDb.currentTarget().contains(text)) {
        progress.tick { updatePaintSize() }
        return true
    }
    if (cancel.get()) return false

    val translated = runCatching { Translator.translate(text) }.getOrNull()
    progress.tick { updatePaintSize() }
    if (translated == null) {
        if (!cancel.get()) {
            failures.incrementAndGet()
            cancel.set(failures.get() > MAX_ERRORS)
        }
        return false
    }
    return true
}

private fun ArticleViewer.finishArticleTrans(status: AlertDialog, cancel: AtomicBoolean) {
    if (cancel.get()) return
    updatePaintSize()
    status.dismiss()
}

private fun ArticleViewer.showArticleTransFailed(status: AlertDialog, error: Throwable? = null) {
    updatePaintSize()
    status.dismiss()
    updateTranslateButton(false)
    AlertUtil.showTransFailedDialog(
        parentActivity,
        error is UnsupportedOperationException,
        error?.message ?: error?.javaClass?.simpleName ?: ""
    ) { doTransLATE() }
}
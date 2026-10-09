package tw.nekomimi.nekogram.parts

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.telegram.messenger.MessageObject
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ChatActivity
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.TranslateDb
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.transtale.code2Locale
import tw.nekomimi.nekogram.utils.AlertUtil
import tw.nekomimi.nekogram.utils.UIUtil
import tw.nekomimi.nekogram.utils.uDismiss
import tw.nekomimi.nekogram.utils.uUpdate
import xyz.nextalone.nagram.NaConfig
import xyz.nextalone.nagram.helper.MessageHelper
import java.util.Locale
import java.util.WeakHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private const val MAX_CONTEXT_MESSAGES = 5
private const val TRANSLATION_PARALLELISM = 5

/**
 * Collects the few messages following [message] so that an LLM translator can resolve the
 * pronouns and the context of the sentence being translated.
 */
private fun translationContext(message: MessageObject, timeline: List<MessageObject>): List<String> {
    if (NekoConfig.translationProvider.Int() != Translator.providerLLM || !NaConfig.llmUseContext.Bool()) {
        return emptyList()
    }
    val index = timeline.indexOf(message)
    if (index < 0) return emptyList()

    return timeline.asSequence()
        .drop(index + 1)
        .filter {
            it.messageOwner.id != 0 && !it.isDateObject && !it.isSponsored &&
                MessageHelper.isMessageObjectAutoTranslatable(it)
        }
        .take(MAX_CONTEXT_MESSAGES)
        .mapNotNull { MessageHelper.getMessagePlainText(it)?.takeIf { text -> text.isNotBlank() } }
        .toList()
        .asReversed()
}

@JvmName("translateMessages")
fun ChatActivity.translateMessages1() = translateMessages()

@JvmName("translateMessages")
fun ChatActivity.translateMessages2(target: Locale) = translateMessages(target)

@JvmName("translateMessages")
fun ChatActivity.translateMessages3(messages: List<MessageObject>) = translateMessages(messages = messages)

@JvmName("translateMessages")
fun ChatActivity.translateMessages4(messages: List<MessageObject>, autoTranslate: Boolean) =
    translateMessages(messages = messages, autoTranslate = autoTranslate)

fun ChatActivity.translateMessages(
    target: Locale = NekoConfig.translateToLang.String().code2Locale,
    messages: List<MessageObject> = messageForTranslate?.let { listOf(it) }
        ?: selectedObjectGroup?.messages
        ?: emptyList(),
    autoTranslate: Boolean = false
) {
    if (messages.isEmpty() || messages.any { it.translating }) return

    if (messages.all { it.translated }) {
        messages.forEach { clearTranslation(it) }
        return
    }

    messages.forEach { it.translating = true }

    // Auto translation starts one job per incoming message, so the cancel flag, the progress
    // label and the bounded dispatcher are per chat and shared by every job of this dialog.
    val cancel = cancellationFlag()
    val status = progressDialog(autoTranslate)
    val timeline = this.messages.toList()
    val scope = translationScope()

    scope.launch {
        val progress = ProgressCounter(messages.size, status)
        try {
            messages.map { message ->
                async { translateOne(message, target, timeline, cancel, status, progress) }
            }.awaitAll()
        } finally {
            messages.forEach { it.translating = false }
            UIUtil.runOnUIThread { if (!cancel.get()) status?.uDismiss() }
        }
    }
}

/** Per chat translation state, shared by every job because auto translation runs one per message. */
private class TransState {
    val cancel = AtomicBoolean()
    val dispatcher = Dispatchers.IO.limitedParallelism(TRANSLATION_PARALLELISM, "MessageTrans")
    var scope: CoroutineScope? = null
    var status: AlertDialog? = null
}

private val transStates = WeakHashMap<ChatActivity, TransState>()

/** Returns (and lazily creates) the translation state of this chat. */
private fun ChatActivity.translationState(): TransState {
    synchronized(transStates) {
        transStates[this]?.let { return it }
        val state = TransState()
        transStates[this] = state
        return state
    }
}

/** Cancellation flag of this chat; setting it aborts every running translation of it. */
private fun ChatActivity.cancellationFlag(): AtomicBoolean = translationState().cancel

/** Progress dialog of a manual translation. Auto translation shows none, so [autoTranslate] skips it. */
private fun ChatActivity.progressDialog(autoTranslate: Boolean): AlertDialog? {
    if (autoTranslate) return null
    val state = translationState()
    synchronized(state) {
        if (state.status?.isShowing != true) {
            state.status = AlertUtil.showProgress(parentActivity).apply {
                setOnCancelListener { cancelTranslations() }
                show()
            }
        }
        return state.status
    }
}

/**
 * Scope shared by every translation job of this chat.
 *
 * It is created once and reused on purpose: auto translation calls [translateMessages] once per
 * incoming message, so a fresh scope per call would cancel the jobs of the previous messages.
 */
private fun ChatActivity.translationScope(): CoroutineScope {
    val state = translationState()
    synchronized(transStates) {
        state.scope?.takeIf { it.isActive }?.let { return it }
        val scope = CoroutineScope(SupervisorJob() + state.dispatcher)
        state.scope = scope
        return scope
    }
}

/** Cancels the in-flight translation jobs of this chat, if any. */
fun ChatActivity.cancelTranslations() {
    synchronized(transStates) {
        transStates.remove(this)?.let { state ->
            state.cancel.set(true)
            state.scope?.cancel()
        }
    }
}

/** Keeps the "done / total" label of the progress dialog up to date. */
private class ProgressCounter(private val total: Int, private val status: AlertDialog?) {
    private val remaining = AtomicInteger(total)

    fun tick() {
        val left = remaining.decrementAndGet()
        if (status != null && total > 1) {
            status.uUpdate("${total - left} / $total")
        }
    }
}

private suspend fun ChatActivity.translateOne(
    message: MessageObject,
    target: Locale,
    timeline: List<MessageObject>,
    cancel: AtomicBoolean,
    status: AlertDialog?,
    progress: ProgressCounter
) {
    try {
        val context = translationContext(message, timeline)
        val db = TranslateDb.forLocale(target)
        if (message.isPoll) {
            translatePoll(message, target, context, db, cancel, status)
        } else {
            translateFromCache(message, db) || translateText(message, target, context, cancel, status)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        reportError(target, listOf(message), e, cancel, status)
    } finally {
        progress.tick()
        // The chat may be gone by now: a canceled job must not touch the message list.
        if (currentCoroutineContext().isActive && !cancel.get()) {
            withContext(Dispatchers.Main) { messageHelper.resetMessageContent(dialogId, message) }
        }
    }
}

/**
 * Applies a cached translation of a plain message without any network access.
 *
 * @return true when the message was fully translated from the cache.
 */
private fun translateFromCache(message: MessageObject, db: TranslateDb): Boolean {
    val source = message.textSource() ?: return false
    val separator = MessageTransText.separatorFor(false)
    val cached = MessageTransText.mergeCached(source, db, separator) ?: return false
    markTranslatedLocally(message) { it.translatedText = cached }
    return true
}

private suspend fun ChatActivity.translateText(
    message: MessageObject,
    target: Locale,
    context: List<String>,
    cancel: AtomicBoolean,
    status: AlertDialog?
): Boolean {
    val source = message.textSource() ?: return false
    if (cancel.get()) return false

    val separator = MessageTransText.separatorFor(false)
    val translated = runCatching {
        Translator.translate(target, MessageTransText.queryTextOf(source), context)
    }.getOrElse {
        // Scope cancellation is not a provider failure: let it propagate so no dialog is shown.
        if (it is CancellationException) throw it
        reportError(target, listOf(message), it, cancel, status)
        return false
    }
    if (translated.isBlank() || cancel.get()) return false

    markTranslatedLocally(message) { it.translatedText = MessageTransText.merge(source, translated, separator) }
    return true
}

private suspend fun ChatActivity.translatePoll(
    message: MessageObject,
    target: Locale,
    context: List<String>,
    db: TranslateDb,
    cancel: AtomicBoolean,
    status: AlertDialog?
): Boolean {
    MessageTransPoll.pollTextOf(message)?.let { skeleton ->
        MessageTransPoll.translateCached(skeleton, db)?.let { cached ->
            markTranslatedLocally(message) { it.translatedPoll = cached }
            return true
        }
    }
    if (cancel.get()) return false

    // A fresh skeleton is required here: translateCached mutates the poll it is given, so the one
    // above may already be partially merged.
    val pollText = MessageTransPoll.pollTextOf(message) ?: return false
    val translated = runCatching {
        MessageTransPoll.translate(pollText, target, context, db)
    }.getOrElse {
        // Scope cancellation is not a provider failure: let it propagate so no dialog is shown.
        if (it is CancellationException) throw it
        reportError(target, listOf(message), it, cancel, status)
        return false
    }

    markTranslatedLocally(message) { it.translatedPoll = translated }
    return true
}

/**
 * Stores the translation into the official upstream fields and flags the message as locally
 * translated, so that `MessageObject.updateTranslation` keeps showing it regardless of the
 * dialog wide setting.
 */
private fun markTranslatedLocally(message: MessageObject, block: (TLRPC.Message) -> Unit) {
    block(message.messageOwner)
    message.translatedLocally = true
}

private fun ChatActivity.clearTranslation(message: MessageObject) {
    val owner = message.messageOwner
    owner.translatedText = null
    owner.translatedPoll = null
    message.translatedLocally = false
    message.updateTranslation(true)
    messageHelper.resetMessageContent(dialogId, message)
}

private fun ChatActivity.reportError(
    target: Locale,
    messages: List<MessageObject>,
    error: Throwable,
    cancel: AtomicBoolean,
    status: AlertDialog?
) {
    if (cancel.get()) return
    status?.uDismiss()
    val activity = parentActivity ?: return
    AlertUtil.showTransFailedDialog(
        activity,
        error is UnsupportedOperationException,
        error.message ?: error.javaClass.simpleName
    ) {
        translateMessages(target, messages)
    }
}

/** The rich text of a plain (non poll) message, or null when there is nothing to translate. */
private fun MessageObject.textSource(): TLRPC.TL_textWithEntities? {
    val text = messageOwner.message?.takeIf { it.isNotBlank() } ?: return null
    return TLRPC.TL_textWithEntities().also {
        it.text = text
        it.entities = messageOwner.entities
    }
}
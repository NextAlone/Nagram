package tw.nekomimi.nekogram.parts

import kotlinx.coroutines.*
import org.telegram.messenger.MessageObject
import org.telegram.messenger.TranslateController
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ChatActivity
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.TranslateDb
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.transtale.code2Locale
import tw.nekomimi.nekogram.transtale.html.HTMLKeeper
import tw.nekomimi.nekogram.utils.AlertUtil
import tw.nekomimi.nekogram.utils.UIUtil
import tw.nekomimi.nekogram.utils.uDismiss
import tw.nekomimi.nekogram.utils.uUpdate
import xyz.nextalone.nagram.NaConfig
import xyz.nextalone.nagram.helper.MessageHelper
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

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
        .take(5)
        .mapNotNull { MessageHelper.getMessagePlainText(it)?.takeIf { text -> text.isNotBlank() } }
        .toList()
        .asReversed()
}

private fun getCachedOrNull(
    db: TranslateDb,
    source: TLRPC.TL_textWithEntities?,
    separator: String
): TLRPC.TL_textWithEntities? {
    if (source == null || source.text.isNullOrBlank()) return null
    val originalText = source.text
    val useKeepFormatting = NaConfig.keepFormatting.Bool() && !source.entities.isNullOrEmpty()
    val queryText = if (useKeepFormatting) {
        HTMLKeeper.entitiesToHtml(originalText, source.entities, false)
    } else {
        originalText
    }
    val cached = db.query(queryText)?.takeIf { it.isNotBlank() } ?: return null

    val result = TLRPC.TL_textWithEntities()
    if (useKeepFormatting) {
        val parsed = HTMLKeeper.htmlToEntities(cached, source.entities, false)
        if (NaConfig.hideOriginAfterTranslation.Bool()) {
            result.text = parsed.text
            result.entities = parsed.entities ?: ArrayList()
        } else {
            val combined = ArrayList<TLRPC.MessageEntity>()
            if (source.entities != null) {
                combined.addAll(source.entities)
            }
            val shift = originalText.length + separator.length
            if (parsed.entities != null) {
                for (entity in parsed.entities) {
                    entity.offset += shift
                    combined.add(entity)
                }
            }
            result.text = "$originalText$separator${parsed.text}"
            result.entities = combined
        }
    } else {
        if (NaConfig.hideOriginAfterTranslation.Bool()) {
            result.text = cached
            result.entities = ArrayList()
        } else {
            result.text = "$originalText$separator$cached"
            result.entities = if (source.entities != null) ArrayList(source.entities) else ArrayList()
        }
    }
    return result
}

fun MessageObject.translateFinished(locale: Locale): Int {

    val db = TranslateDb.forLocale(locale)

    if (isPoll) {
        val media = messageOwner.media as? TLRPC.TL_messageMediaPoll ?: return 1
        val pool = media.poll ?: return 1
        val translatedPoll = TranslateController.PollText.fromMessage(this) ?: return 0

        val questionResult = getCachedOrNull(db, pool.question, " | ") ?: return 0
        translatedPoll.question = questionResult

        for (answer in translatedPoll.answers) {
            val answerResult = getCachedOrNull(db, answer.text, " | ") ?: return 0
            answer.text = answerResult
        }

        if (translatedPoll.solution != null) {
            val solutionResult = getCachedOrNull(db, translatedPoll.solution, " | ") ?: return 0
            translatedPoll.solution = solutionResult
        }
        messageOwner.translatedPoll = translatedPoll
        return 2
    } else {
        val originalMessage = messageOwner.message.takeIf { !it.isNullOrBlank() } ?: return 1
        val source = TLRPC.TL_textWithEntities().apply {
            text = originalMessage
            entities = messageOwner.entities
        }
        val cached = getCachedOrNull(db, source, "\n\n--------\n\n") ?: return 0
        messageOwner.translatedMessage = cached.text
        messageOwner.translatedEntities = if (cached.entities.isNullOrEmpty()) null else cached.entities
        return 2
    }
}

@JvmName("translateMessages")
fun ChatActivity.translateMessages1() = translateMessages()

@JvmName("translateMessages")
fun ChatActivity.translateMessages2(target: Locale) = translateMessages(target)

@JvmName("translateMessages")
fun ChatActivity.translateMessages3(messages: List<MessageObject>) = translateMessages(messages = messages)

@JvmName("translateMessages")
fun ChatActivity.translateMessages4(messages: List<MessageObject>, autoTranslate: Boolean) = translateMessages(messages = messages, autoTranslate = autoTranslate)

fun ChatActivity.translateMessages(
    target: Locale = NekoConfig.translateToLang.String().code2Locale,
    messages: List<MessageObject> = messageForTranslate?.let { listOf(it) }
        ?: selectedObjectGroup?.messages
        ?: emptyList(),
    autoTranslate: Boolean = false
) {
    if (messages.any { it.translating }) return

    if (messages.all { it.messageOwner.translated }) {
        messages.forEach {
            it.messageOwner.translated = false
            it.messageOwner.translatedMessage = null
            it.messageOwner.translatedEntities = null
            it.messageOwner.translatedPoll = null
            messageHelper.resetMessageContent(dialogId, it)
            it.translating = false
        }
        return
    } else {
        messages.forEach { it.translating = true }
    }

    var status: AlertDialog? = null
    val cancel = AtomicBoolean()
    if (!autoTranslate) {
        status = AlertUtil.showProgress(parentActivity).apply {
            setOnCancelListener { cancel.set(true) }
            show()
        }
    }

    val deferreds = LinkedList<Deferred<Unit>>()
    val taskCount = AtomicInteger(messages.size)
    val transPool = newFixedThreadPoolContext(5, "Message Trans Pool")

    fun next() {
        val index = taskCount.decrementAndGet()
        if (index == 0) {
            status?.uDismiss()
        } else if (messages.size > 1) {
            status?.uUpdate("${messages.size - index} / ${messages.size}")
        }
    }

    val timeline = this.messages.toList()
    GlobalScope.launch(Dispatchers.IO) {
        try {
            messages.forEach { selectedObject ->
                val context = translationContext(selectedObject, timeline)
                when (if (context.isEmpty()) selectedObject.translateFinished(target) else 0) {
                    1 -> {
                        selectedObject.translating = false
                        next()
                    }
                    2 -> {
                        selectedObject.translating = false
                        next()
                        withContext(Dispatchers.Main) {
                            selectedObject.messageOwner.translated = true
                            messageHelper.resetMessageContent(dialogId, selectedObject)
                        }
                    }
                    else -> deferreds.add(async(transPool) {
                        try {
                            val success = translateMessage(selectedObject, target, context, cancel, status)
                            if (!cancel.get()) {
                                if (success) {
                                    selectedObject.messageOwner.translated = true
                                } else {
                                    selectedObject.messageOwner.translated = false
                                    selectedObject.messageOwner.translatedMessage = selectedObject.messageOwner.message
                                    selectedObject.messageOwner.translatedEntities = null
                                    selectedObject.messageOwner.translatedPoll = null
                                }
                                next()
                                withContext(Dispatchers.Main) {
                                    messageHelper.resetMessageContent(dialogId, selectedObject)
                                }
                            }
                        } finally {
                            selectedObject.translating = false
                        }
                    })
                }
            }

            deferreds.awaitAll()
        } finally {
            messages.forEach { it.translating = false }
            transPool.cancel()
            UIUtil.runOnUIThread { if (!cancel.get()) status?.uDismiss() }
        }
    }
}

private suspend fun ChatActivity.translateMessage(
    message: MessageObject,
    target: Locale,
    context: List<String>,
    cancel: AtomicBoolean,
    status: AlertDialog?
): Boolean {
    val db = TranslateDb.forLocale(target)
    return if (message.isPoll) {
        translatePoll(message, target, context, db, cancel, status)
    } else {
        translateText(message, target, context, db, cancel, status)
    }
}

private suspend fun ChatActivity.translateTextWithEntities(
    target: Locale,
    source: TLRPC.TL_textWithEntities?,
    context: List<String>,
    db: TranslateDb,
    cancel: AtomicBoolean,
    status: AlertDialog?,
    separator: String
): TLRPC.TL_textWithEntities? {
    if (source == null || source.text.isNullOrBlank()) return null
    if (cancel.get()) return null

    val originalText = source.text
    val useKeepFormatting = NaConfig.keepFormatting.Bool() && !source.entities.isNullOrEmpty()
    val queryText = if (useKeepFormatting) {
        HTMLKeeper.entitiesToHtml(originalText, source.entities, false)
    } else {
        originalText
    }

    var text = if (context.isEmpty()) db.query(queryText)?.takeIf { it.isNotBlank() } else null
    if (text == null) {
        if (cancel.get()) return null
        text = runCatching {
            Translator.translate(target, queryText, context)
        }.getOrElse {
            handleError(target, it, cancel, status)
            return null
        }
    }

    if (text.isBlank() || cancel.get()) return null

    val result = TLRPC.TL_textWithEntities()
    if (useKeepFormatting) {
        val parsed = HTMLKeeper.htmlToEntities(text, source.entities, false)
        if (NaConfig.hideOriginAfterTranslation.Bool()) {
            result.text = parsed.text
            result.entities = parsed.entities ?: ArrayList()
        } else {
            val combined = ArrayList<TLRPC.MessageEntity>()
            if (source.entities != null) {
                combined.addAll(source.entities)
            }
            val shift = originalText.length + separator.length
            if (parsed.entities != null) {
                for (entity in parsed.entities) {
                    entity.offset += shift
                    combined.add(entity)
                }
            }
            result.text = "$originalText$separator${parsed.text}"
            result.entities = combined
        }
    } else {
        if (NaConfig.hideOriginAfterTranslation.Bool()) {
            result.text = text
            result.entities = ArrayList()
        } else {
            result.text = "$originalText$separator$text"
            result.entities = if (source.entities != null) ArrayList(source.entities) else ArrayList()
        }
    }
    return result
}

private suspend fun ChatActivity.translatePoll(
    message: MessageObject,
    target: Locale,
    context: List<String>,
    db: TranslateDb,
    cancel: AtomicBoolean,
    status: AlertDialog?
): Boolean {
    val media = message.messageOwner.media as? TLRPC.TL_messageMediaPoll ?: return false
    val pool = media.poll ?: return false
    val translatedPoll = TranslateController.PollText.fromMessage(message) ?: return false

    val questionResult = translateTextWithEntities(
        target = target,
        source = pool.question,
        context = context,
        db = db,
        cancel = cancel,
        status = status,
        separator = " | "
    ) ?: return false
    translatedPoll.question = questionResult

    for (answer in translatedPoll.answers) {
        val answerResult = translateTextWithEntities(
            target = target,
            source = answer.text,
            context = context,
            db = db,
            cancel = cancel,
            status = status,
            separator = " | "
        ) ?: return false
        answer.text = answerResult
    }

    translatedPoll.solution?.let { solution ->
        val solutionResult = translateTextWithEntities(
            target = target,
            source = solution,
            context = context,
            db = db,
            cancel = cancel,
            status = status,
            separator = " | "
        ) ?: return false
        translatedPoll.solution = solutionResult
    }

    message.messageOwner.translatedPoll = translatedPoll
    return true
}

private suspend fun ChatActivity.translateText(
    message: MessageObject,
    target: Locale,
    context: List<String>,
    db: TranslateDb,
    cancel: AtomicBoolean,
    status: AlertDialog?
): Boolean {
    val originalMessage = message.messageOwner?.message
    if (originalMessage.isNullOrBlank()) {
        return false
    }

    val source = TLRPC.TL_textWithEntities().apply {
        text = originalMessage
        entities = message.messageOwner.entities
    }

    val result = translateTextWithEntities(
        target = target,
        source = source,
        context = context,
        db = db,
        cancel = cancel,
        status = status,
        separator = "\n\n--------\n\n"
    )

    if (result == null) {
        message.messageOwner.translatedMessage = originalMessage
        message.messageOwner.translatedEntities = null
        return false
    }

    message.messageOwner.translatedMessage = result.text
    message.messageOwner.translatedEntities = if (result.entities.isNullOrEmpty()) null else result.entities
    return true
}

private fun ChatActivity.handleError(
    target: Locale,
    error: Throwable,
    cancel: AtomicBoolean,
    status: AlertDialog?
) {
    status?.uDismiss()
    if (parentActivity != null && !cancel.get()) {
        AlertUtil.showTransFailedDialog(
            parentActivity,
            error is UnsupportedOperationException,
            error.message ?: error.javaClass.simpleName
        ) {
            translateMessages(target, messages)
        }
    }
}

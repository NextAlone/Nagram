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

fun MessageObject.translateFinished(locale: Locale): Int {

    val db = TranslateDb.forLocale(locale)

    translating = false

    if (isPoll) {
        val pool = (messageOwner.media as TLRPC.TL_messageMediaPoll).poll
        val question = db.query(pool.question.text) ?: return 0
        val translatedPoll = TranslateController.PollText.fromMessage(this)

        translatedPoll.question.text =
            "${if (!NaConfig.hideOriginAfterTranslation.Bool()) translatedPoll.question.text + " | " else ""}$question"
        translatedPoll.answers.forEach {
            val answer = db.query(it.text.text) ?: return 0
            it.text.text += " | $answer"
        }
        if (translatedPoll.solution != null) {
            val solution = db.query(translatedPoll.solution.text) ?: return 0
            translatedPoll.solution.text =
                "${if (!NaConfig.hideOriginAfterTranslation.Bool()) translatedPoll.solution.text + " | " else ""}$solution"
        }
        messageOwner.translatedPoll = translatedPoll
    } else {

        val text = db.query(messageOwner.message.takeIf { !it.isNullOrBlank() } ?: return 1)
                ?: return 0

        messageOwner.translatedMessage =
            "${if (!NaConfig.hideOriginAfterTranslation.Bool()) messageOwner.message + "\n\n--------\n\n" else ""}$text"

    }

    return 2

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
        messages.forEach { selectedObject ->
            val context = translationContext(selectedObject, timeline)
            when (if (context.isEmpty()) selectedObject.translateFinished(target) else 0) {
                1 -> next()
                2 -> {
                    next()
                    withContext(Dispatchers.Main) {
                        selectedObject.messageOwner.translated = true
                        messageHelper.resetMessageContent(dialogId, selectedObject)
                    }
                }
                else -> deferreds.add(async(transPool) {
                    translateMessage(selectedObject, target, context, cancel, status)
                    if (!cancel.get()) {
                        selectedObject.messageOwner.translated = true
                        next()
                        withContext(Dispatchers.Main) {
                            messageHelper.resetMessageContent(dialogId, selectedObject)
                        }
                    }
                })
            }
        }

        deferreds.awaitAll()
        transPool.cancel()
        UIUtil.runOnUIThread { if (!cancel.get()) status?.uDismiss() }
    }
    messages.forEach { it.translating = false }
}

private suspend fun ChatActivity.translateMessage(
    message: MessageObject,
    target: Locale,
    context: List<String>,
    cancel: AtomicBoolean,
    status: AlertDialog?
) {
    val db = TranslateDb.forLocale(target)
    if (message.isPoll) {
        translatePoll(message, target, context, db, cancel, status)
    } else {
        translateText(message, target, context, db, cancel, status)
    }
}

private suspend fun ChatActivity.translatePoll(
    message: MessageObject,
    target: Locale,
    context: List<String>,
    db: TranslateDb,
    cancel: AtomicBoolean,
    status: AlertDialog?
) {
    val pool = (message.messageOwner.media as TLRPC.TL_messageMediaPoll).poll
    var question = if (context.isEmpty()) db.query(pool.question.text) else null
    if (question == null) {
        if (cancel.get()) return
        question = runCatching {
            Translator.translate(target, pool.question.text, context)
        }.getOrElse {
            handleError(target, it, cancel, status)
            return
        }
    }

    val translatedPoll = TranslateController.PollText.fromMessage(message)
    translatedPoll.question.text = buildString {
        if (!NaConfig.hideOriginAfterTranslation.Bool()) append(pool.question.text + " | ")
        append(question)
    }

    translatedPoll.answers.forEach {
        var answer = if (context.isEmpty()) db.query(it.text.text) else null
        if (answer == null) {
            if (cancel.get()) return@forEach
            answer = runCatching {
                Translator.translate(target, it.text.text, context)
            }.getOrElse { e ->
                handleError(target, e, cancel, status)
                return@forEach
            }
        }
        it.text.text = buildString {
            if (!NaConfig.hideOriginAfterTranslation.Bool()) append(it.text.text + " | ")
            append(answer)
        }
    }

    translatedPoll.solution?.let { solution ->
        var translatedSolution = if (context.isEmpty()) db.query(solution.text) else null
        if (translatedSolution == null) {
            if (cancel.get()) return
            translatedSolution = runCatching {
                Translator.translate(target, solution.text, context)
            }.getOrElse {
                handleError(target, it, cancel, status)
                return
            }
        }
        solution.text = buildString {
            if (!NaConfig.hideOriginAfterTranslation.Bool()) append(solution.text + " | ")
            append(translatedSolution)
        }
    }

    message.messageOwner.translatedPoll = translatedPoll
}

private suspend fun ChatActivity.translateText(
    message: MessageObject,
    target: Locale,
    context: List<String>,
    db: TranslateDb,
    cancel: AtomicBoolean,
    status: AlertDialog?
) {
    var text = if (context.isEmpty()) db.query(message.messageOwner.message) else null
    if (text == null) {
        text = runCatching {
            Translator.translate(target, message.messageOwner.message, context)
        }.getOrElse {
            handleError(target, it, cancel, status)
            return
        }
    }

    message.messageOwner.translatedMessage = buildString {
        if (!NaConfig.hideOriginAfterTranslation.Bool()) append(message.messageOwner.message + "\n\n--------\n\n")
        append(text)
    }
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

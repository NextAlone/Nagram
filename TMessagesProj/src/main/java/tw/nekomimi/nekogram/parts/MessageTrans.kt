package tw.nekomimi.nekogram.parts

import kotlinx.coroutines.*
import org.telegram.messenger.MessageObject
import org.telegram.tgnet.TLRPC
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
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

fun MessageObject.toRawString(): String {

    var content: String

    if (messageOwner.media is TLRPC.TL_messageMediaPoll) {

        val poll = (messageOwner.media as TLRPC.TL_messageMediaPoll).poll
        content = poll.question
        content += "\n"

        for (answer in poll.answers) {

            content += "\n- "
            content += answer.text

        }

    } else {

        content = messageOwner.message ?: ""

    }

    return content

}

fun MessageObject.translateFinished(locale: Locale): Int {

    val db = TranslateDb.forLocale(locale)

    translating = false

    if (isPoll) {

        val pool = (messageOwner.media as TLRPC.TL_messageMediaPoll).poll

        val question = db.query(pool.question) ?: return 0

        pool.translatedQuestion =
            "${if (!NaConfig.hideOriginAfterTranslation.Bool()) pool.question + "\n\n--------\n\n" else ""}$question"

        pool.answers.forEach {

            val answer = db.query(it.text) ?: return 0

            it.translatedText = it.text + " | " + answer

        }

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

fun ChatActivity.translateMessages(target: Locale = NekoConfig.translateToLang.String().code2Locale
                                   , messages: List<MessageObject> = messageForTranslate?.let { listOf(it) }
        ?: selectedObjectGroup?.messages
        ?: emptyList()) {

    if (messages.any { it.translating }) {
        return
    }

    // TODO: Fix file group

    if (messages.all { it.messageOwner.translated }) {

        messages.forEach { messageObject ->

            messageObject.messageOwner.translated = false
            messageHelper.resetMessageContent(dialogId, messageObject)
            messageObject.translating = false

        }

        return

    } else {
        messages.forEach { messageObject ->
            messageObject.translating = true
        }
    }

    val status = AlertUtil.showProgress(parentActivity)

    val cancel = AtomicBoolean()

    status.setOnCancelListener {
        cancel.set(true)
    }

    status.show()

    val deferreds = LinkedList<Deferred<Unit>>()
    val taskCount = AtomicInteger(messages.size)
    val transPool = newFixedThreadPoolContext(5, "Message Trans Pool")

    suspend fun next() {
        val index = taskCount.decrementAndGet()
        if (index == 0) {
            status.uDismiss()
        } else if (messages.size > 1) {
            status.uUpdate("${messages.size - index} / ${messages.size}")
        }
    }

    GlobalScope.launch(Dispatchers.IO) {

        messages.forEachIndexed { _, selectedObject ->

            val state = selectedObject.translateFinished(target)

            if (state == 1) {
                next()
                return@forEachIndexed
            } else if (state == 2) {
                next()

                withContext(Dispatchers.Main) {
                    selectedObject.messageOwner.translated = true
                    messageHelper.resetMessageContent(dialogId, selectedObject)
                }

                return@forEachIndexed

            }

            deferreds.add(async(transPool) trans@{

                val db = TranslateDb.forLocale(target)

                if (selectedObject.isPoll) {

                    val pool = (selectedObject.messageOwner.media as TLRPC.TL_messageMediaPoll).poll

                    var question = db.query(pool.question)

                    if (question == null) {

                        if (cancel.get()) return@trans

                        runCatching {

                            question = Translator.translate(target, pool.question)

                        }.onFailure {

                            status.uDismiss()

                            val parentActivity = parentActivity

                            if (parentActivity != null && !cancel.get()) {

                                AlertUtil.showTransFailedDialog(parentActivity, it is UnsupportedOperationException, it.message
                                        ?: it.javaClass.simpleName) {

                                    translateMessages(target, messages)

                                }

                            }

                            return@trans

                        }

                    }

                    pool.translatedQuestion =
                        "${if (!NaConfig.hideOriginAfterTranslation.Bool()) pool.question + "\n\n--------\n\n" else ""}$question"

                    pool.answers.forEach {

                        var answer = db.query(it.text)

                        if (answer == null) {

                            if (cancel.get()) return@trans

                            runCatching {

                                answer = Translator.translate(target, it.text)

                            }.onFailure { e ->

                                status.uDismiss()

                                val parentActivity = parentActivity

                                if (parentActivity != null && !cancel.get()) {

                                    AlertUtil.showTransFailedDialog(parentActivity, e is UnsupportedOperationException, e.message
                                            ?: e.javaClass.simpleName) {

                                        translateMessages(target, messages)

                                    }

                                }

                                return@trans

                            }

                        }

                        it.translatedText = answer + " | " + it.text

                    }

                } else {

                    var text = db.query(selectedObject.messageOwner.message)

                    if (text == null) {

                        runCatching {

                            text = Translator.translate(target, selectedObject.messageOwner.message)

                        }.onFailure {

                            status.uDismiss()

                            val parentActivity = parentActivity

                            if (parentActivity != null && !cancel.get()) {

                                AlertUtil.showTransFailedDialog(parentActivity, it is UnsupportedOperationException, it.message
                                        ?: it.javaClass.simpleName) {

                                    translateMessages(target, messages)

                                }

                            }

                            return@trans

                        }


                    }

                    selectedObject.messageOwner.translatedMessage =
                        "${if (!NaConfig.hideOriginAfterTranslation.Bool()) selectedObject.messageOwner.message + "\n\n--------\n\n" else ""}$text"

                }

                if (!cancel.get()) {

                    selectedObject.messageOwner.translated = true

                    next()

                    withContext(Dispatchers.Main) {

                        messageHelper.resetMessageContent(dialogId, selectedObject)

                    }

                } else return@trans

            })

        }

        deferreds.awaitAll()
        transPool.cancel()

        UIUtil.runOnUIThread {

            if (!cancel.get()) status.uDismiss()

        }

    }
    messages.forEach { messageObject ->
        messageObject.translating = false
    }

}

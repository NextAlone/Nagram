package tw.nekomimi.nekogram.parts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessageObject
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ChatActivity
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.TranslateDb
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.transtale.code2Locale
import tw.nekomimi.nekogram.utils.AlertUtil
import tw.nekomimi.nekogram.utils.uDismiss
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

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

fun MessageObject.translateFinished(locale: Locale): Boolean {

    val db = TranslateDb.forLocale(locale)

    if (isPoll) {

        val pool = (messageOwner.media as TLRPC.TL_messageMediaPoll).poll

        val question = db.query(pool.question) ?: return false

        pool.translatedQuestion = pool.question + "\n\n--------\n\n" + question

        pool.answers.forEach {

            val answer = db.query(it.text) ?: return false

            it.translatedText = it.text + " | " + answer

        }

    } else {

        val text = db.query(messageOwner.message) ?: return false

        messageOwner.translatedMessage = messageOwner.message + "\n\n--------\n\n" + text

    }

    return true

}

@JvmOverloads
fun ChatActivity.translateMessages(messages: Array<MessageObject>, target: Locale = NekoConfig.translateToLang?.code2Locale
        ?: LocaleController.getInstance().currentLocale) {

    if (messages.all { it.messageOwner.translated }) {

        messages.forEach { messageObject ->

            messageObject.messageOwner.translated = false

            messageHelper.resetMessageContent(dialogId, messageObject)

        }

        return

    }

    val status = AlertUtil.showProgress(parentActivity)

    val canceled = AtomicBoolean()

    status.setOnCancelListener {

        canceled.set(true)

    }

    status.show()

    GlobalScope.launch(Dispatchers.IO) {

        messages.forEachIndexed { i, selectedObject ->

            val isEnd = i == messages.size - 1

            if (selectedObject.translateFinished(target)) {

                withContext(Dispatchers.Main) {

                    selectedObject.messageOwner.translated = true

                    messageHelper.resetMessageContent(dialogId, selectedObject)

                    if (isEnd) status.dismiss()

                }

                return@forEachIndexed

            }

            withContext(Dispatchers.IO) trans@{

                val db = TranslateDb.forLocale(target)

                if (selectedObject.isPoll) {

                    val pool = (selectedObject.messageOwner.media as TLRPC.TL_messageMediaPoll).poll

                    var question = db.query(pool.question)

                    if (question == null) {

                        if (canceled.get()) return@trans

                        runCatching {

                            question = Translator.translate(target, pool.question)

                        }.onFailure {

                            status.uDismiss()

                            val parentActivity = parentActivity

                            if (parentActivity != null && !canceled.get()) {

                                AlertUtil.showTransFailedDialog(parentActivity, it is UnsupportedOperationException, it.message
                                        ?: it.javaClass.simpleName, Runnable {

                                    translateMessages(messages, target)

                                })

                            }

                            return@trans

                        }

                    }

                    pool.translatedQuestion = pool.question + "\n\n--------\n\n" + question

                    pool.answers.forEach {

                        var answer = db.query(it.text)

                        if (answer == null) {

                            if (canceled.get()) return@trans

                            runCatching {

                                answer = Translator.translate(target, it.text)

                            }.onFailure { e ->

                                status.uDismiss()

                                val parentActivity = parentActivity

                                if (parentActivity != null && !canceled.get()) {

                                    AlertUtil.showTransFailedDialog(parentActivity, e is UnsupportedOperationException, e.message
                                            ?: e.javaClass.simpleName, Runnable {

                                        translateMessages(messages, target)

                                    })

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

                            if (parentActivity != null && !canceled.get()) {

                                AlertUtil.showTransFailedDialog(parentActivity, it is UnsupportedOperationException, it.message
                                        ?: it.javaClass.simpleName, Runnable {

                                    translateMessages(messages, target)

                                })

                            }

                            return@trans

                        }


                    }

                    selectedObject.messageOwner.translatedMessage = selectedObject.messageOwner.message + "\n\n--------\n\n" + text

                }

                if (!canceled.get()) {

                    selectedObject.messageOwner.translated = true

                    withContext(Dispatchers.Main) {

                        messageHelper.resetMessageContent(dialogId, selectedObject)

                        if (isEnd) status.dismiss()

                    }

                } else return@trans

            }

        }

    }

}
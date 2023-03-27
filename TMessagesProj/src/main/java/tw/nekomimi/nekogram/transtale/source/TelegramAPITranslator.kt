package tw.nekomimi.nekogram.transtale.source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.telegram.messenger.FileLog
import org.telegram.messenger.SharedConfig
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLObject
import org.telegram.tgnet.TLRPC
import org.telegram.tgnet.TLRPC.TL_error
import org.telegram.tgnet.TLRPC.TL_messages_translateResult
import org.telegram.tgnet.TLRPC.TL_messages_translateText
import tw.nekomimi.nekogram.transtale.Translator
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object TelegramAPITranslator : Translator {

//    val targetLanguages = listOf("DE", "EN", "ES", "FR", "IT", "JA", "NL", "PL", "PT", "RU", "ZH")

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun doTranslate(from: String, to: String, query: String): String {

        return suspendCoroutine {
            val req = TL_messages_translateText()
            req.peer = null
            req.flags = req.flags or 2
            req.text.add(TLRPC.TL_textWithEntities().apply {
                text = query
            })
            req.to_lang = to

            try {
                ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req) { res: TLObject?, err: TL_error? ->
                    if (res is TL_messages_translateResult && res.result.isNotEmpty()) {
                        it.resume(res.result[0].text)
                    } else {
                        FileLog.e(err?.text)
                        it.resumeWithException(RuntimeException("Failed to translate by Telegram API"))
                    }
                }
            } catch (e: Exception) {
                FileLog.e(e)
                it.resumeWithException(e)
            }
        }
    }
}

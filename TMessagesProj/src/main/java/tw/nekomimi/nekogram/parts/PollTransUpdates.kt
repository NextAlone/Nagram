package tw.nekomimi.nekogram.parts

import org.telegram.tgnet.TLRPC

fun postPollTrans(media: TLRPC.TL_messageMediaPoll, poll: TLRPC.TL_poll) {
    poll.translatedQuestion = media.poll.translatedQuestion
    poll.answers.forEach { answer ->
        answer.translatedText = media.poll.answers.find { it.text == answer.text }!!.translatedText
    }
}
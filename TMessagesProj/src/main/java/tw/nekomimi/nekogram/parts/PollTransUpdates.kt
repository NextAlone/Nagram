package tw.nekomimi.nekogram.parts

import org.telegram.tgnet.TLRPC

fun postPollTrans(media: TLRPC.TL_messageMediaPoll, poll: TLRPC.TL_poll) {
    poll.translatedQuestion = media.poll.translatedQuestion
    poll.answers.forEach { answer ->
        val answerF = media.poll.answers.find { it.text.text == answer.text.text }
        if (answerF != null) {
            answer.translatedText = answerF.translatedText
        }
    }
}
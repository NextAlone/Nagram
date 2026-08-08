package xyz.nextalone.nagram.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.Bitmaps.createBitmap
import org.telegram.messenger.BuildConfig
import org.telegram.messenger.ChatObject
import org.telegram.messenger.DialogObject
import org.telegram.messenger.Emoji
import org.telegram.messenger.FileLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MediaController
import org.telegram.messenger.MediaDataController
import org.telegram.messenger.MessageObject
import org.telegram.messenger.R
import org.telegram.messenger.UserConfig
import org.telegram.messenger.Utilities
import org.telegram.tgnet.TLRPC.Chat
import org.telegram.tgnet.TLRPC.TL_messageEntityBankCard
import org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand
import org.telegram.tgnet.TLRPC.TL_messageEntityCashtag
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail
import org.telegram.tgnet.TLRPC.TL_messageEntityHashtag
import org.telegram.tgnet.TLRPC.TL_messageEntityMention
import org.telegram.tgnet.TLRPC.TL_messageEntityPhone
import org.telegram.tgnet.TLRPC.TL_messageEntitySpoiler
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.ChatActivity
import org.telegram.ui.Components.ColoredImageSpan

import tw.nekomimi.nekogram.utils.UIUtil.runOnIoDispatcher
import xyz.nextalone.nagram.NaConfig

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date


object MessageHelper {

    private val spannedStrings = arrayOfNulls<SpannableStringBuilder>(5)

    fun getPathToMessage(messageObject: MessageObject): File? {
        var path = messageObject.messageOwner.attachPath
        if (!TextUtils.isEmpty(path)) {
            val file = File(path)
            if (file.exists()) {
                return file
            } else {
                path = null
            }
        }
        if (TextUtils.isEmpty(path)) {
            val file = FileLoader.getInstance(messageObject.currentAccount)
                .getPathToMessage(messageObject.messageOwner)
            if (file != null && file.exists()) {
                return file
            } else {
                path = null
            }
        }
        if (TextUtils.isEmpty(path)) {
            val file = FileLoader.getInstance(messageObject.currentAccount)
                .getPathToAttach(messageObject.document, true)
            return if (file != null && file.exists()) {
                file
            } else {
                null
            }
        }
        return null
    }

    fun getUriToMessage(messageObject: MessageObject): Uri? {
        val f = getPathToMessage(messageObject) ?: return null
        val context = ApplicationLoader.applicationContext
        return FileProvider.getUriForFile(context, ApplicationLoader.getApplicationId() + ".provider", f)
    }


    fun addMessageToClipboard(selectedObject: MessageObject, callback: Runnable) {
        val file = getPathToMessage(selectedObject)
        if (file != null) {
            if (file.exists()) {
                addFileToClipboard(file, callback)
            }
        }
    }

    fun addMessageToClipboardAsSticker(selectedObject: MessageObject, callback: Runnable) {
        val file = getPathToMessage(selectedObject)
        try {
            if (file != null) {
                val path = file.path
                val image = BitmapFactory.decodeFile(path)
                if (image != null && !TextUtils.isEmpty(path)) {
                    val file2 = File(
                        if (path.endsWith(".jpg")) path.replace(
                            ".jpg",
                            ".webp"
                        ) else "$path.webp"
                    )
                    val stream = FileOutputStream(file2)
                    if (Build.VERSION.SDK_INT >= 30) {
                        image.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, stream)
                    } else {
                        image.compress(Bitmap.CompressFormat.WEBP, 100, stream)
                    }
                    stream.close()
                    addFileToClipboard(file2, callback)
                }
            }
        } catch (ignored: java.lang.Exception) {
        }
    }

    fun addFileToClipboard(file: File?, callback: Runnable) {
        try {
            val context = ApplicationLoader.applicationContext
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val uri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
            val clip = ClipData.newUri(context.contentResolver, "label", uri)
            clipboard.setPrimaryClip(clip)
            callback.run()
        } catch (e: Exception) {
            FileLog.e(e)
        }
    }

    @JvmStatic
    fun showForwardDate(obj: MessageObject, orig: String): String {
        val date: Long = obj.messageOwner.fwd_from.date.toLong()
        val day: String = LocaleController.formatDate(date)
        val time: String = LocaleController.getInstance().formatterDay.format(date * 1000)
        return if (!NaConfig.dateOfForwardedMsg.Bool() || date == 0L) {
            orig
        } else {
            if (day == time) {
                "$orig · $day"
            } else "$orig · $day $time"
        }
    }

    fun zalgoFilter(
        text: String
    ): String {
        return zalgoFilter(text as CharSequence).toString()
    }

    fun zalgoFilter(
        text: CharSequence?
    ): CharSequence {
        return if (text == null) {
            ""
        } else if (NaConfig.zalgoFilter.Bool() && text.matches(
                ".*\\p{Mn}{4}.*".toRegex()
            )
        ) {
            text.replace(
                "(?i)([aeiouy]̈)|[̀-ͯ҉]".toRegex(),
                ""
            )
                .replace(
                    "[\\p{Mn}]".toRegex(),
                    ""
                )
        } else {
            text
        }
    }

    @JvmStatic
    fun getDCLocation(dc: Int): String {
        return when (dc) {
            1, 3 -> "Miami"
            2, 4 -> "Amsterdam"
            5 -> "Singapore"
            else -> "Unknown"
        }
    }

    @JvmStatic
    fun getDCName(dc: Int): String {
        return when (dc) {
            1 -> "Pluto"
            2 -> "Venus"
            3 -> "Aurora"
            4 -> "Vesta"
            5 -> "Flora"
            else -> "Unknown"
        }
    }

    @JvmStatic
    fun containsMarkdown(text: CharSequence?): Boolean {
        val newText = AndroidUtilities.getTrimmedString(text)
        val message = arrayOf(AndroidUtilities.getTrimmedString(newText))
        return MediaDataController.getInstance(UserConfig.selectedAccount)
            .getEntities(message, true).size > 0
    }

    @JvmStatic
    fun canSendAsDice(text: String, parentFragment: ChatActivity, dialog_id: Long): Boolean {
        var canSendGames = true
        if (DialogObject.isChatDialog(dialog_id)) {
            val chat: Chat = parentFragment.messagesController.getChat(-dialog_id)
            canSendGames = ChatObject.canSendStickers(chat)
        }
        return canSendGames && parentFragment.messagesController.diceEmojies.contains(
            text.replace(
                "\ufe0f",
                ""
            )
        )
    }

    @JvmStatic
    fun isLinkOrEmojiOnlyMessage(messageObject: MessageObject): Boolean {
        val entities = messageObject.messageOwner.entities
        if (entities != null) {
            for (entity in entities) {
                if (entity is TL_messageEntityBotCommand ||
                    entity is TL_messageEntityEmail ||
                    entity is TL_messageEntityUrl ||
                    entity is TL_messageEntityMention ||
                    entity is TL_messageEntityCashtag ||
                    entity is TL_messageEntityHashtag ||
                    entity is TL_messageEntityBankCard ||
                    entity is TL_messageEntityPhone
                ) {
                    if (entity.offset == 0 && entity.length == messageObject.messageOwner.message.length) {
                        return true
                    }
                }
            }
        }
        return Emoji.fullyConsistsOfEmojis(messageObject.messageOwner.message)
    }

    @JvmStatic
    fun isMessageObjectAutoTranslatable(messageObject: MessageObject): Boolean {
        if (messageObject.messageOwner.translated || messageObject.translating || messageObject.isOutOwner) {
            return false
        }
        return if (messageObject.isPoll) {
            true
        } else !TextUtils.isEmpty(messageObject.messageOwner.message) && !isLinkOrEmojiOnlyMessage(
            messageObject
        )
    }

    @JvmStatic
    fun getMessagePlainText(messageObject: MessageObject): String? {
        return if (messageObject.isPoll) {
            val media = messageObject.messageOwner.media as? TL_messageMediaPoll
            val poll = media?.poll
            if (poll == null) {
                null
            } else {
                val pollText = StringBuilder(poll.question?.text ?: "").append("\n")
                for (answer in poll.answers) {
                    pollText.append("\n\uD83D\uDD18 ").append(answer.text?.text ?: continue)
                }
                pollText.toString()
            }
        } else if (messageObject.isVoiceTranscriptionOpen) {
            messageObject.messageOwner.voiceTranscription
        } else {
            messageObject.messageOwner.message
        }
    }

    private fun formatTime(timestamp: Int): String {
        return LocaleController.formatString(R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(Date(timestamp * 1000L)), LocaleController.getInstance().formatterDay.format(Date(timestamp * 1000L)))
    }

    fun getTimeHintText(messageObject: MessageObject): CharSequence {
        val text = SpannableStringBuilder()
        if (spannedStrings[3] == null) {
            spannedStrings[3] = SpannableStringBuilder("\u200B")
            spannedStrings[3]?.setSpan(ColoredImageSpan(Theme.chat_timeHintSentDrawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        text.append(spannedStrings[3])
        text.append(' ')
        text.append(formatTime(messageObject.messageOwner.date))
        if (messageObject.messageOwner.edit_date != 0) {
            text.append("\n")
            if (spannedStrings[1] == null) {
                spannedStrings[1] = SpannableStringBuilder("\u200B")
                spannedStrings[1]?.setSpan(ColoredImageSpan(Theme.chat_editDrawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            text.append(spannedStrings[1])
            text.append(' ')
            text.append(formatTime(messageObject.messageOwner.edit_date))
        }
        if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.date != 0) {
            text.append("\n")
            if (spannedStrings[4] == null) {
                spannedStrings[4] = SpannableStringBuilder("\u200B")
                val span = ColoredImageSpan(Theme.chat_timeHintForwardDrawable)
                span.setSize(AndroidUtilities.dp(12f))
                spannedStrings[4]?.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            text.append(spannedStrings[4])
            text.append(' ')
            text.append(formatTime(messageObject.messageOwner.fwd_from.date))
        }
        return text
    }

    private var spoilerChars: CharArray = charArrayOf(
        '⠌', '⡢', '⢑', '⠨', '⠥', '⠮', '⡑'
    )

    fun blurify(text: CharSequence): CharSequence {
        val stringBuilder = StringBuilder(text)
        for (i in text.indices) {
            stringBuilder.setCharAt(i, spoilerChars[i % spoilerChars.size])
        }
        return stringBuilder
    }

    fun blurify(messageObject: MessageObject) {
        if (messageObject.messageOwner == null) {
            return
        }

        if (!TextUtils.isEmpty(messageObject.messageText)) {
            messageObject.messageText = blurify(messageObject.messageText)
        }

        if (!TextUtils.isEmpty(messageObject.messageOwner.message)) {
            messageObject.messageOwner.message = blurify(messageObject.messageOwner.message).toString()
        }

        if (!TextUtils.isEmpty(messageObject.caption)) {
            messageObject.caption = blurify(messageObject.caption)
        }

        if (messageObject.messageOwner.media != null) {
            messageObject.messageOwner.media.spoiler = true
        }
    }

    fun saveStickerToGalleryAsGif(activity: Context, path: String?, video: Boolean, animated: Boolean, callback: Utilities.Callback<Uri>?) {
        Utilities.globalQueue.postRunnable {
            runCatching {
                if (video) {
                    val outputPath = path!!.replace(".webm", ".gif")
                    if (File(outputPath).exists()) {
                        File(outputPath).delete()
                    }

                    runOnIoDispatcher {
                        val success = convertVideoToGif(path, outputPath)
                        AndroidUtilities.runOnUIThread {
                            if (success) {
                                MediaController.saveFile(outputPath, activity, 0, null, null, callback)
                            } else {
                                Log.e("VideoToGif", "Failed to convert to GIF")
                                Toast.makeText(activity, "Failed to convert video to GIF", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else if (animated) {
                    runOnIoDispatcher {
                        val outputPath = path!!.replace(".tgs", ".gif")
                        if (File(outputPath).exists()) {
                            File(outputPath).delete()
                        }

                        val result: LottieResult<LottieComposition> = LottieCompositionFactory.fromJsonInputStreamSync(
                            FileInputStream(File(path)), path)
                        val composition: LottieComposition? = result.value

                        composition?.let { comp ->
                            val success = convertLottieToGif(comp, outputPath)
                            AndroidUtilities.runOnUIThread {
                                if (success) {
                                    MediaController.saveFile(outputPath, activity, 0, null, null, callback)
                                } else {
                                    Log.e("LottieToGif", "Failed to convert animation to GIF")
                                    Toast.makeText(activity, "Failed to convert animation to GIF", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    // Convert static sticker to PNG (since it's already a static image)
                    val image = BitmapFactory.decodeFile(path)
                    if (image != null) {
                        val file = File(path!!.replace(".webp", ".png"))
                        val stream = FileOutputStream(file)
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.close()
                        MediaController.saveFile(file.toString(), activity, 0, null, null, callback)
                    } else {
                        Toast.makeText(activity, "Failed to process image", Toast.LENGTH_SHORT).show()
                    }
                }
            }.onFailure {
                Log.e("VideoToGif", "")
            }
        }
    }

    /**
     * Convert video to GIF
     */
    private fun convertVideoToGif(inputPath: String, outputPath: String): Boolean {
        return try {
            Log.d("VideoToGif", "Converting using FFmpeg + GifEncoder: $inputPath -> $outputPath")

            // Get original frame rate
            val originalFps = getWebmFrameRateNative(inputPath)
            Log.d("VideoToGif", "Original frame rate: $originalFps fps")

            // Use FFmpeg to extract frames as Bitmaps
            val frames = extractFramesFromWebmNative(
                inputPath,
                60   // maxFrames (for quality)
            )

            if (frames != null && frames.isNotEmpty()) {
                Log.d("VideoToGif", "FFmpeg extracted ${frames.size} frames, creating GIF with GifEncoder")

                // Convert Array<Bitmap> to List<Bitmap>
                val frameList = frames.toList()

                val frameDelay = (1000.0 / originalFps).toInt().coerceAtLeast(10)

                Log.d("VideoToGif", "Using frame delay: ${frameDelay}ms for $originalFps fps")

                // Use existing GifEncoder to create GIF
                val success = createGifFromBitmaps(frameList, outputPath, frameDelay)

                if (success) {
                    Log.d("VideoToGif", "GIF creation successful using FFmpeg + GifEncoder")
                } else {
                    Log.e("VideoToGif", "GIF creation failed")
                }

                success
            } else {
                Log.e("VideoToGif", "FFmpeg frame extraction failed")
                false
            }
        } catch (e: Exception) {
            Log.e("VideoToGif", "Error with FFmpeg frame extraction: ${e.message}")
            false
        }
    }

    /**
     * Native method for WebM frame extraction using FFmpeg
     */
    @JvmStatic
    private external fun extractFramesFromWebmNative(
        inputPath: String,
        maxFrames: Int
    ): Array<Bitmap>?

    /**
     * Native method to get WebM frame rate
     */
    @JvmStatic
    private external fun getWebmFrameRateNative(inputPath: String): Double

    /**
     * Convert Lottie to GIF
     */
    private fun convertLottieToGif(composition: LottieComposition, outputPath: String): Boolean {
        return try {
            val lottieDrawable = LottieDrawable().apply {
                this.composition = composition
            }

            val width = minOf(composition.bounds.width(), 320)
            val height = minOf(composition.bounds.height(), 320)
            lottieDrawable.setBounds(0, 0, width, height)

            val frames = mutableListOf<Bitmap>()
            val totalFrames = (composition.endFrame - composition.startFrame).toInt()
            val duration = composition.duration
            val originalFrameRate = composition.frameRate
            val actualFrameCount = totalFrames
            val targetFrameRate = originalFrameRate

            Log.d("LottieToGif", "Using all frames: $actualFrameCount, original fps: $targetFrameRate")

            for (i in 0 until actualFrameCount) {
                val frameIndex = composition.startFrame.toInt() + i
                val clampedFrameIndex = frameIndex.coerceIn(
                    composition.startFrame.toInt(),
                    composition.endFrame.toInt()
                )
                lottieDrawable.frame = clampedFrameIndex
                val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                lottieDrawable.draw(canvas)

                frames.add(bitmap)
                Log.d("LottieToGif", "Frame ${i + 1}/$actualFrameCount, lottie frame: $clampedFrameIndex")
            }

            if (frames.isEmpty()) return false
            val frameDuration = if (originalFrameRate > 0) {
                (1000f / originalFrameRate).toInt()
            } else {
                (duration / totalFrames).toInt()
            }

            Log.d("LottieToGif", "Using frame duration: ${frameDuration}ms (target fps: ${1000f/frameDuration})")

            createGifFromBitmaps(frames, outputPath, frameDuration)
        } catch (e: Exception) {
            Log.e("LottieToGif", "Error converting Lottie to GIF: ${e.message}")
            false
        }
    }

    /**
     * Create GIF from bitmaps
     */
    private fun createGifFromBitmaps(bitmaps: List<Bitmap>, outputPath: String, delayMs: Int): Boolean {
        return try {
            if (bitmaps.isEmpty()) {
                Log.e("GifEncoder", "No bitmaps to encode")
                return false
            }

            Log.d("GifEncoder", "Creating GIF with ${bitmaps.size} frames, delay: ${delayMs}ms")
            Log.d("GifEncoder", "Output path: $outputPath")

            // Ensure output directory exists
            val outputFile = File(outputPath)
            outputFile.parentFile?.mkdirs()

            val output = FileOutputStream(outputPath)
            val gifEncoder = GifEncoder()

            // Configure encoder
            if (!gifEncoder.start(output)) {
                Log.e("GifEncoder", "Failed to start encoder - output stream may be invalid")
                try {
                    output.close()
                } catch (e: Exception) {
                    Log.w("GifEncoder", "Failed to close output stream: ${e.message}")
                }
                return false
            }

            // Set GIF properties
            gifEncoder.setDelay(delayMs)
            gifEncoder.setRepeat(0) // Loop infinitely
            gifEncoder.setQuality(10) // Medium quality

            // Add frames
            var successCount = 0
            for ((index, bitmap) in bitmaps.withIndex()) {
                if (bitmap.isRecycled) {
                    Log.w("GifEncoder", "Skipping recycled bitmap at index $index")
                    continue
                }

                if (gifEncoder.addFrame(bitmap)) {
                    successCount++
                    Log.d("GifEncoder", "Successfully added frame $index")
                } else {
                    Log.e("GifEncoder", "Failed to add frame $index")
                }
            }

            Log.d("GifEncoder", "Successfully added $successCount out of ${bitmaps.size} frames")

            val success = gifEncoder.finish()
            output.close()

            if (success && successCount > 0) {
                val file = File(outputPath)
                Log.d("GifEncoder", "GIF created successfully: ${file.length()} bytes")
            } else {
                Log.e("GifEncoder", "Failed to finish GIF encoding or no frames added")
                return false
            }
            true
        } catch (e: Exception) {
            Log.e("GifEncoder", "Error creating GIF: $e")
            false
        }
    }
}

package tw.nekomimi.nekogram.utils

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.URLSpan
import android.view.View
import kotlinx.coroutines.runBlocking
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.LocaleController
import org.telegram.ui.Components.URLSpanNoUnderline
import tw.nekomimi.nekogram.utils.FileUtil.delete
import tw.nekomimi.nekogram.utils.FileUtil.initDir
import java.io.File


object LocaleUtil {

    @JvmField
    val cacheDir = File(ApplicationLoader.applicationContext.cacheDir, "builtIn_lang_export")

    @JvmStatic
    fun fetchAndExportLang() = runBlocking {

        delete(cacheDir)
        initDir(cacheDir)

        for (localeInfo in LocaleController.getInstance().languages) {

            if (!localeInfo.builtIn || localeInfo.pathToFile != "unofficial") continue

            if (localeInfo.hasBaseLang()) {

                localeInfo.pathToBaseFile.takeIf { it.isFile }?.copyTo(File(cacheDir, localeInfo.pathToBaseFile.name))

            }

            localeInfo.getPathToFile()?.takeIf { it.isFile }?.copyTo(File(cacheDir, localeInfo.getPathToFile().name))

        }


    }

    fun formatWithURLs(charSequence: CharSequence): CharSequence {
        val spannable: Spannable = SpannableString(charSequence)
        val spans = spannable.getSpans(0, charSequence.length, URLSpan::class.java)
        for (urlSpan in spans) {
            var span = urlSpan
            val start = spannable.getSpanStart(span)
            val end = spannable.getSpanEnd(span)
            spannable.removeSpan(span)
            span = object : URLSpanNoUnderline(span.url) {
                override fun onClick(widget: View) {
                    super.onClick(widget)
                }
            }
            spannable.setSpan(span, start, end, 0)
        }
        return spannable
    }

    fun htmlToString(text: String?): CharSequence {
        val htmlParsed: Spannable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY))
        } else {
            SpannableString(Html.fromHtml(text))
        }

        return formatWithURLs(htmlParsed)
    }
}

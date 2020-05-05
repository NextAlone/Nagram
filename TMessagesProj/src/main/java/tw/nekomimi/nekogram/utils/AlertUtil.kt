package tw.nekomimi.nekogram.utils

import android.content.Context
import android.widget.LinearLayout
import android.widget.Toast
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.Components.EditTextBoldCursor
import org.telegram.ui.Components.NumberPicker
import tw.nekomimi.nekogram.BottomBuilder
import tw.nekomimi.nekogram.NekoConfig
import java.util.*
import java.util.concurrent.atomic.AtomicReference

object AlertUtil {

    @JvmStatic
    fun showToast(text: String) = UIUtil.runOnUIThread(Runnable {
        Toast.makeText(
                ApplicationLoader.applicationContext,
                text.takeIf { it.isNotBlank() }
                    ?: "喵 !",
                Toast.LENGTH_LONG
        ).show()
    })

    @JvmStatic
    @JvmOverloads
    fun showSimpleAlert(ctx: Context, text: String, listener: ((AlertDialog.Builder) -> Unit)? = null) {


        showSimpleAlert(ctx,null, text, listener)

    }

    @JvmStatic
    fun showSimpleAlert(ctx: Context,title: String?, text: String, listener: ((AlertDialog.Builder) -> Unit)? = null) = UIUtil.runOnUIThread(Runnable {

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(title ?: LocaleController.getString("NekoX", R.string.NekoX))
        builder.setMessage(text)

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK)) { _, _ ->

            listener?.invoke(builder)

            builder.dismissRunnable.run()

        }

        builder.show()

    })

    @JvmStatic
    fun showCopyAlert(ctx: Context, text: String) = UIUtil.runOnUIThread(Runnable {

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(LocaleController.getString("Translate", R.string.Translate))
        builder.setMessage(text)

        builder.setNegativeButton(LocaleController.getString("Copy", R.string.Copy)) { _, _ ->

            AndroidUtilities.addToClipboard(text)

            AlertUtil.showToast(LocaleController.getString("TextCopied", R.string.TextCopied))

            builder.dismissRunnable.run()

        }

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK)) { _, _ ->

            builder.dismissRunnable.run()

        }

        builder.show()

    })

    @JvmOverloads
    @JvmStatic
    fun showProgress(ctx: Context, text: String = LocaleController.getString("Loading", R.string.Loading)): AlertDialog {

        return AlertDialog.Builder(ctx, 1).apply {

            setMessage(text)

        }.create()

    }

    fun showInput(ctx: Context, title: String, hint: String, onInput: (AlertDialog.Builder, String) -> String) = UIUtil.runOnUIThread(Runnable {

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(title)

        builder.setView(EditTextBoldCursor(ctx).apply {

            setHintText(hint)

        })

    })

    @JvmStatic
    @JvmOverloads
    fun showConfirm(ctx: Context, title: String, text: String? = null, icon: Int, button: String, red: Boolean, listener: Runnable) = UIUtil.runOnUIThread(Runnable {

        /*

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(title)

        builder.setMessage(AndroidUtilities.replaceTags(text))
        builder.setPositiveButton(button, listener)

        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null)
        val alertDialog = builder.show()

        if (red) {

            (alertDialog.getButton(DialogInterface.BUTTON_POSITIVE) as TextView?)?.setTextColor(Theme.getColor(Theme.key_dialogTextRed2))

        }

         */

        val builder = BottomBuilder(ctx)

        if (text != null) {

            builder.addTitle(title, text)

        } else {

            builder.addTitle(title)

        }

        builder.addItem(button, icon, red) {

            builder.dismiss()

            listener.run()

        }

        builder.addCancelItem()

        builder.show()

    })

    @JvmStatic
    @JvmOverloads
    fun showTransFailedDialog(ctx: Context, noRetry: Boolean, noWeb: Boolean = false, message: String, retryRunnable: Runnable) = UIUtil.runOnUIThread(Runnable {

        ctx.setTheme(R.style.Theme_TMessages)

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(LocaleController.getString("TranslateFailed", R.string.TranslateFailed))

        builder.setMessage(message)

        val reference = AtomicReference<AlertDialog>()

        builder.setNeutralButton(LocaleController.getString("ChangeTranslateProvider", R.string.ChangeTranslateProvider)) {

            _, _ ->

            val view = reference.get().getButton(AlertDialog.BUTTON_NEUTRAL)

            val popup = PopupBuilder(view, true)

            val items = LinkedList<String>()

            items.addAll(arrayOf(
                    LocaleController.getString("ProviderGoogleTranslate", R.string.ProviderGoogleTranslate),
                    LocaleController.getString("ProviderGoogleTranslateCN", R.string.ProviderGoogleTranslateCN),
                    LocaleController.getString("ProviderYandex", R.string.ProviderYandex),
                    LocaleController.getString("ProviderLingocloud", R.string.ProviderLingocloud)
            ))

            if (!noWeb) {

                items.addAll(arrayOf(
                        LocaleController.getString("ProviderGoogleTranslateWeb", R.string.ProviderGoogleTranslateWeb),
                        LocaleController.getString("ProviderGoogleTranslateCNWeb", R.string.ProviderGoogleTranslateCNWeb),
                        LocaleController.getString("ProviderBaiduFanyiWeb", R.string.ProviderBaiduFanyiWeb),
                        LocaleController.getString("ProviderDeepLWeb", R.string.ProviderDeepLWeb)
                ))

            }

            popup.setItems(items.toTypedArray()) { item, _ ->

                reference.get().dismiss()

                NekoConfig.setTranslationProvider(if (item < 4) item + 1 else -item + 3)

                retryRunnable.run()

            }

            popup.show()

        }

        if (noRetry) {

            builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel)) { _, _ ->

                reference.get().dismiss()

            }

        } else {

            builder.setPositiveButton(LocaleController.getString("Retry", R.string.Retry)) { _, _ ->

                reference.get().dismiss()

                retryRunnable.run()

            }

            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel)) { _, _ ->

                reference.get().dismiss()

            }

        }

        reference.set(builder.create().apply {

            setDismissDialogByButtons(false)
            show()

        })

    })

    fun showTimePicker(ctx: Context, title: String, callback: (Long) -> Unit) {

        ctx.setTheme(R.style.Theme_TMessages)

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(title)

        builder.setView(LinearLayout(ctx).apply {

            orientation = LinearLayout.HORIZONTAL

            addView(NumberPicker(ctx).apply {

                minValue = 0
                maxValue = 60

            }, LinearLayout.LayoutParams(-2, -2).apply {

                weight = 1F

            })

            addView(NumberPicker(ctx).apply {

                minValue = 0
                maxValue = 60

            }, LinearLayout.LayoutParams(-2, -2).apply {

                weight = 1F

            })

        })

    }

}
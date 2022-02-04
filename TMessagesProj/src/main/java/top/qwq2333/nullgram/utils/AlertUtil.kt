/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.utils

import android.content.Context
import android.widget.Toast
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.AlertDialog
import top.qwq2333.nullgram.ui.BottomBuilder


object AlertUtil {

    @JvmStatic
    fun showToast(e: Throwable) = showToast(e.message ?: e.javaClass.simpleName)

    @JvmStatic
    fun showToast(e: TLRPC.TL_error?) {
        if (e == null) return
        showToast("${e.code}: ${e.text}")
    }

    @JvmStatic
    fun showToast(text: String) = UIUtil.runOnUIThread(Runnable {
        Toast.makeText(
            ApplicationLoader.applicationContext,
            text.takeIf { it.isNotBlank() }
                ?: "Rua !",
            Toast.LENGTH_LONG
        ).show()
    })

    @JvmStatic
    fun showSimpleAlert(ctx: Context?, error: Throwable) {

        showSimpleAlert(ctx, null, error.message ?: error.javaClass.simpleName)

    }

    @JvmStatic
    @JvmOverloads
    fun showSimpleAlert(
        ctx: Context?,
        text: String,
        listener: ((AlertDialog.Builder) -> Unit)? = null
    ) {

        showSimpleAlert(ctx, null, text, listener)

    }

    @JvmStatic
    @JvmOverloads
    fun showSimpleAlert(
        ctx: Context?,
        title: String?,
        text: String,
        listener: ((AlertDialog.Builder) -> Unit)? = null
    ) = UIUtil.runOnUIThread(Runnable {

        if (ctx == null) return@Runnable

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(title ?: LocaleController.getString("AppName", R.string.AppName))
        builder.setMessage(text)

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK)) { _, _ ->

            builder.dismissRunnable?.run()
            listener?.invoke(builder)

        }

        builder.show()

    })

    @JvmStatic
    @JvmOverloads
    fun showConfirm(
        ctx: Context,
        title: String,
        text: String? = null,
        icon: Int,
        button: String,
        red: Boolean,
        listener: Runnable
    ) = UIUtil.runOnUIThread(Runnable {
        val builder = BottomBuilder(ctx)

        if (text != null) {
            builder.addTitle(title, text)
        } else {
            builder.addTitle(title)
        }

        builder.addItem(button, icon, red) {
            listener.run()
        }
        builder.addCancelItem()
        builder.show()

    })


}

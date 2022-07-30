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
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import org.telegram.messenger.BuildConfig
import org.telegram.ui.LaunchActivity
import java.io.File

object ShareUtil {

    @JvmStatic
    @JvmOverloads
    fun shareText(ctx: Context, text: String, choose: Boolean = false) {

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        if (!choose) {

            intent.setClass(ctx, LaunchActivity::class.java)
            ctx.startActivity(intent)

        } else {

            ctx.startActivity(Intent.createChooser(intent, text))

        }

    }

    @JvmOverloads
    @JvmStatic
    fun shareFile(ctx: Context, fileToShare: File, caption: String = "") {

        val uri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", fileToShare)
        } else {
            Uri.fromFile(fileToShare)
        }

        val i = Intent(Intent.ACTION_SEND)

        if (Build.VERSION.SDK_INT >= 24) {

            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }

        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, "")

        if (caption.isNotBlank()) i.putExtra(Intent.EXTRA_SUBJECT, caption)

        i.putExtra(Intent.EXTRA_STREAM, uri)
        i.setClass(ctx, LaunchActivity::class.java)

        ctx.startActivity(i)

    }

    @JvmStatic
    fun openFile(ctx: Context, fileToOpen: File) {

        val uri = if (Build.VERSION.SDK_INT >= 24) {

            FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", fileToOpen)

        } else {

            Uri.fromFile(fileToOpen)

        }

        val intent = Intent(Intent.ACTION_VIEW)

        if (Build.VERSION.SDK_INT >= 24) {

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }

        if (fileToOpen.extension.isBlank()) {

            intent.type = "application/octet-stream"

        } else {

            intent.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileToOpen.extension)

        }

        intent.data = uri

        ctx.startActivity(intent)

    }

}

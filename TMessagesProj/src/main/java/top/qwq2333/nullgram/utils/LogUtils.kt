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

import android.util.Log

object LogUtils {
    const val TAG = "Nullgram"

    /**
     * 日志等级 Debug
     * @param msg 日志内容
     */
    @JvmStatic
    fun d(msg: String) {
        Log.d(TAG, msg)
    }

    /**
     * 日志等级 Info
     * @param msg 日志内容
     */
    @JvmStatic
    fun i(msg: String) {
        Log.i(TAG, msg)
    }

    /**
     * 日志等级 Warn
     * @param msg 日志内容
     */
    @JvmStatic
    fun w(msg: String) {
        Log.w(TAG, msg)
    }

    /**
     * 日志等级 Error
     * @param msg 日志内容
     */
    @JvmStatic
    fun e(msg: String) {
        Log.e(TAG, msg)
    }

    /**
     * 日志等级 Debug
     * @param throwable 异常
     * @param msg 日志内容
     */
    @JvmStatic
    fun d(msg: String, throwable: Throwable) {
        Log.i(TAG, msg, throwable)
    }

    /**
     * 日志等级 Info
     * @param throwable 异常
     * @param msg 日志内容
     */
    @JvmStatic
    fun i(msg: String, throwable: Throwable) {
        Log.i(TAG, msg, throwable)
    }

    /**
     * 日志等级 Warn
     * @param throwable 异常
     * @param msg 日志内容
     */
    @JvmStatic
    fun w(msg: String, throwable: Throwable) {
        Log.w(TAG, msg, throwable)
    }

    /**
     * 日志等级 Error
     * @param throwable 异常
     * @param msg 日志内容
     */
    @JvmStatic
    fun e(msg: String, throwable: Throwable) {
        Log.e(TAG, msg, throwable)
        AppcenterUtils.trackCrashes(throwable)
    }

    /**
     * 日志等级 Info
     * @param throwable 异常
     */
    @JvmStatic
    fun i(throwable: Throwable) {
        Log.i(TAG, "", throwable)
    }

    /**
     * 日志等级 Warn
     * @param throwable 异常
     */
    @JvmStatic
    fun w(throwable: Throwable) {
        Log.w(TAG, "", throwable)
    }

    /**
     * 日志等级 Error
     * @param throwable 异常
     */
    @JvmStatic
    fun e(throwable: Throwable) {
        Log.e(TAG, "", throwable)
        AppcenterUtils.trackCrashes(throwable)
    }
}

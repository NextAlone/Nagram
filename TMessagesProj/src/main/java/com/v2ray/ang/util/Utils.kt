package com.v2ray.ang.util

import android.text.TextUtils
import android.util.Base64
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import java.io.IOException
import java.net.Socket
import java.net.URLDecoder
import java.net.URLEncoder
import java.net.UnknownHostException


object Utils {


    /**
     * parseInt
     */
    fun parseInt(str: String): Int {
        try {
            return Integer.parseInt(str)
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }


    /**
     * base64 decode
     */
    fun decode(text: String): String {
        return runCatching {
            Base64.decode(text, Base64.URL_SAFE or Base64.NO_WRAP).toString(charset("UTF-8"))
        }.recoverCatching {
            Base64.decode(text, Base64.NO_WRAP).toString(charset("UTF-8"))
        }.recoverCatching {
            Base64.decode(text, Base64.DEFAULT).toString(charset("UTF-8"))
        }.getOrThrow()
    }

    fun decodeJson(text: String): String {
        return runCatching {
            Base64.decode(text, Base64.URL_SAFE or Base64.NO_WRAP).toString(charset("UTF-8")).also {
                JSONObject(it)
            }
        }.recoverCatching {
            Base64.decode(text, Base64.NO_WRAP).toString(charset("UTF-8")).also {
                JSONObject(it)
            }
        }.recoverCatching {
            Base64.decode(text, Base64.DEFAULT).toString(charset("UTF-8")).also {
                JSONObject(it)
            }
        }.getOrThrow()
    }


    /**
     * base64 encode
     */
    fun encode(text: String): String {
        try {
            return Base64.encodeToString(text.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * is ip address
     */
    fun isIpAddress(value: String): Boolean {
        try {
            var addr = value
            if (addr.isEmpty() || addr.isBlank()) {
                return false
            }
            //CIDR
            if (addr.indexOf("/") > 0) {
                val arr = addr.split("/")
                if (arr.count() == 2 && Integer.parseInt(arr[1]) > 0) {
                    addr = arr[0]
                }
            }

            // "::ffff:192.168.173.22"
            // "[::ffff:192.168.173.22]:80"
            if (addr.startsWith("::ffff:") && '.' in addr) {
                addr = addr.drop(7)
            } else if (addr.startsWith("[::ffff:") && '.' in addr) {
                addr = addr.drop(8).replace("]", "")
            }

            // addr = addr.toLowerCase()
            var octets = addr.split('.').toTypedArray()
            if (octets.size == 4) {
                if (octets[3].indexOf(":") > 0) {
                    addr = addr.substring(0, addr.indexOf(":"))
                }
                return isIpv4Address(addr)
            }

            // Ipv6addr [2001:abc::123]:8080
            return isIpv6Address(addr)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun isPureIpAddress(value: String): Boolean {
        return (isIpv4Address(value) || isIpv6Address(value))
    }

    fun isIpv4Address(value: String): Boolean {
        val regV4 = Regex("^([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])\\.([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])\\.([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])\\.([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])$")
        return regV4.matches(value)
    }

    @JvmStatic
    fun isIpv6Address(value: String): Boolean {
        var addr = value
        if (addr.indexOf("[") == 0 && addr.lastIndexOf("]") > 0) {
            addr = addr.drop(1)
            addr = addr.dropLast(addr.count() - addr.lastIndexOf("]"))
        }
        val regV6 = Regex("^((?:[0-9A-Fa-f]{1,4}))?((?::[0-9A-Fa-f]{1,4}))*::((?:[0-9A-Fa-f]{1,4}))?((?::[0-9A-Fa-f]{1,4}))*|((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4})){7}$")
        return regV6.matches(addr)
    }

    fun urlDecode(url: String): String {
        try {
            return URLDecoder.decode(url, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
            return url
        }
    }

    fun urlEncode(url: String): String {
        try {
            return URLEncoder.encode(url, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
            return url
        }
    }

    /**
     * readTextFromAssets
     */
    fun readTextFromAssets(fileName: String): String {
        val content = ApplicationLoader.applicationContext.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
        return content
    }

    /**
     * ping
     */
    fun ping(url: String): String {
        try {
            val command = "/system/bin/ping -c 3 $url"
            val process = Runtime.getRuntime().exec(command)
            val allText = process.inputStream.bufferedReader().use { it.readText() }
            if (!TextUtils.isEmpty(allText)) {
                val tempInfo = allText.substring(allText.indexOf("min/avg/max/mdev") + 19)
                val temps = tempInfo.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (temps.count() > 0 && temps[0].length < 10) {
                    return temps[0].toFloat().toInt().toString() + "ms"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "-1ms"
    }

    /**
     * tcping
     */
    fun tcping(url: String, port: Int): String {
        var time = -1L
        for (k in 0 until 2) {
            val one = socketConnectTime(url, port)
            if (one != -1L)
                if (time == -1L || one < time) {
                    time = one
                }
        }
        return time.toString() + "ms"
    }

    fun socketConnectTime(url: String, port: Int): Long {
        try {
            val start = System.currentTimeMillis()
            val socket = Socket(url, port)
            val time = System.currentTimeMillis() - start
            socket.close()
            return time
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }
}


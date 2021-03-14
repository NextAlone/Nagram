package tw.nekomimi.nekogram.parts

import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.RandomUtil
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.MessagesController

fun randomDevice(accountNum: Int): String {
    val currDevice = MessagesController.getMainSettings(accountNum).getString("fake_device", "")
    if (!currDevice.isNullOrBlank()) return currDevice
    val devices = IoUtil.readUtf8(ApplicationLoader.applicationContext.assets.open("devices.csv"))
    val device = RandomUtil.randomEle(devices.split("\n"))
    MessagesController.getMainSettings(accountNum).edit().putString("fake_device", device).apply()
    return device
}

fun randomSystemVersion(accountNum: Int): String {
    val currVer = MessagesController.getMainSettings(accountNum).getString("fake_sdk", "")
    if (!currVer.isNullOrBlank()) return currVer
    val version = "SDK " + RandomUtil.randomInt(16, 31)
    MessagesController.getMainSettings(accountNum).edit().putString("fake_sdk", version).apply()
    return version
}
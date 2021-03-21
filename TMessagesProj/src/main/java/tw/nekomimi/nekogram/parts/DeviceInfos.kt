package tw.nekomimi.nekogram.parts

import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.RandomUtil
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.MessagesController
import tw.nekomimi.nekogram.NekoConfig

fun randomDevice(): String {
    val currDevice = MessagesController.getGlobalEmojiSettings().getString("fake_device", "")
    if (!currDevice.isNullOrBlank()) return currDevice
    val devices = IoUtil.readUtf8(ApplicationLoader.applicationContext.assets.open("devices.csv"))
    val device = RandomUtil.randomEle(devices.split("\n"))
    NekoConfig.preferences.edit().putString("fake_device", device).apply()
    return device
}

fun randomSystemVersion(): String {
    val currVer = MessagesController.getGlobalEmojiSettings().getString("fake_sdk", "")
    if (!currVer.isNullOrBlank()) return currVer
    val version = "SDK " + RandomUtil.randomInt(16, 31)
    NekoConfig.preferences.edit().putString("fake_sdk", version).apply()
    return version
}
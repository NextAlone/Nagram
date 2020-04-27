package tw.nekomimi.nekogram

import com.v2ray.ang.util.Utils
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.MessagesController
import org.telegram.tgnet.AbstractSerializedData
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.SerializedData
import tw.nekomimi.nekogram.utils.AlertUtil
import tw.nekomimi.nekogram.utils.receive
import java.io.File

object DataCenter {

    val ConnectionsManager.tgnetFile by receive<ConnectionsManager, File> {

        var config = ApplicationLoader.getFilesDirFixed()
        if (currentAccount != 0) {
            config = File(config, "account$currentAccount")
            config.mkdirs()
        }

        return@receive File(config,"tgnet.dat")

    }

    val ConnectionsManager.tgnetFileNew by receive<ConnectionsManager, File> { File(tgnetFile.parentFile!!, "${tgnetFile.name}.new") }

    @JvmStatic
    fun applyOfficalDataCanter(account: Int) {

        MessagesController.getMainSettings(account).edit().remove("layer").remove("custom_dc").apply()

        ConnectionsManager.getInstance(account).tgnetFileNew.delete()

        if (ConnectionsManager.native_isTestBackend(account) != 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        } else {

            applyDataCanter(account, 1, "149.154.175.50", 0)
            applyDataCanter(account, 1, "2001:b28:f23d:f001:0000:0000:0000:000a", 1)

            applyDataCanter(account, 2, "149.154.167.51", 0)
            applyDataCanter(account, 2, "2001:67c:4e8:f002:0000:0000:0000:000a", 1)

            applyDataCanter(account, 3, "149.154.175.100", 0)
            applyDataCanter(account, 3, "2001:b28:f23d:f003:0000:0000:0000:000a", 1)

            applyDataCanter(account, 4, "149.154.167.91", 0)
            applyDataCanter(account, 4, "2001:67c:4e8:f004:0000:0000:0000:000a", 1)

            applyDataCanter(account, 5, "149.154.171.5", 0)
            applyDataCanter(account, 5, "2001:67c:4e8:f005:0000:0000:0000:000a", 1)

        }

        ConnectionsManager.native_cleanUp(account, true)

    }

    @JvmStatic
    fun applyTestDataCenter(account: Int) {

        MessagesController.getMainSettings(account).edit().remove("layer").remove("custom_dc").apply()

        if (ConnectionsManager.native_isTestBackend(account) == 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        }

    }

    @JvmStatic
    fun applyCustomDataCenter(account: Int, ipv4Address: String = "", ipv6Address: String = "", port: Int, layer: Int) {

        MessagesController.getMainSettings(account).edit().putInt("layer", layer).putBoolean("custom_dc", true).apply()

        if (ConnectionsManager.native_isTestBackend(account) != 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        }

        ConnectionsManager.getInstance(account).apply {

            tgnetFile.delete()
            tgnetFileNew.delete()

        }

        val buffer = SerializedData()

        val time = (System.currentTimeMillis() / 1000).toInt()

        buffer.writeInt32(5) // configVersion
        buffer.writeBool(false) // testBackend
        buffer.writeBool(true) // clientBlocked
        buffer.writeString("en") // lastInitSystemLangcode
        buffer.writeBool(true) // currentDatacenter != nullptr

        buffer.writeInt32(2) // currentDatacenterId
        buffer.writeInt32(0) // timeDifference
        buffer.writeInt32(time) // lastDcUpdateTime
        buffer.writeInt64(0L) // pushSessionId
        buffer.writeBool(false) // registeredForInternalPush
        buffer.writeInt32(time) // getCurrentTime()
        buffer.writeInt32(0) // sessions.size

        buffer.writeInt32(5) // datacenters.size

        repeat(5) {

            buffer.writeDataCenter(it + 1, ipv4Address, ipv6Address, port)

        }

        ConnectionsManager.getInstance(account).tgnetFileNew.apply {

            createNewFile()
            writeBytes(buffer.toByteArray())

        }

        AlertUtil.showToast("Restart required.")

    }

    private fun AbstractSerializedData.writeDataCenter(id: Int, ipv4Address: String, ipv6Address: String, port: Int) {

        writeInt32(13) // configVersion
        writeInt32(id) // datacenterId
        writeInt32(13) // lastInitVersion
        writeInt32(13) //lastInitMediaVersion

        writeDataCenterAddress(ipv4Address, port)
        writeDataCenterAddress(ipv6Address, port)
        writeDataCenterAddress("", port)
        writeDataCenterAddress("", port)

        writeBool(false) // isCdnDatacenter
        writeInt32(0) // authKeyPerm
        writeInt64(0L) // authKeyPermId
        writeInt32(0) // authKeyTemp
        writeInt64(0L) // authKeyTempId
        writeInt32(0) // authKeyMediaTemp
        writeInt64(0L) // authKeyMediaTempId
        writeInt32(0) // authorized
        writeInt32(0) // serverSalts.size
        writeInt32(0) // mediaServerSalts.size

    }

    private fun AbstractSerializedData.writeDataCenterAddress(address: String, port: Int) {

        writeInt32(if (address.isBlank()) 0 else 1) // array->size()

        if (address.isNotBlank()) {

            writeString(address) // address
            writeInt32(port) // port
            writeInt32(if (Utils.isIpv6Address(address)) 1 else 0) // flags
            writeString("") // secret

        }


    }

    private fun applyDataCanter(account: Int, dataCenter: Int, address: String, flag: Int, port: Int = 443) {

        ConnectionsManager.getInstance(account).applyDatacenterAddress(dataCenter, address, port, flag)

    }

}
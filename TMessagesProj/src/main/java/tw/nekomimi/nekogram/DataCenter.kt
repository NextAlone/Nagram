package tw.nekomimi.nekogram

import com.v2ray.ang.util.Utils
import org.telegram.messenger.MessagesController
import org.telegram.tgnet.ConnectionsManager
import tw.nekomimi.nekogram.utils.AlertUtil

object DataCenter {

    @JvmStatic
    fun applyOfficalDataCanter(account: Int) {

        MessagesController.getMainSettings(account).edit().remove("layer").remove("custom_dc").apply()

        if (ConnectionsManager.native_isTestBackend(account) != 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        }

        applyDataCanter(account, 1, "149.154.175.50",0)
        applyDataCanter(account, 1, "2001:b28:f23d:f001:0000:0000:0000:000a",1)

        applyDataCanter(account, 2, "149.154.167.51",0)
        applyDataCanter(account, 2, "2001:67c:4e8:f002:0000:0000:0000:000a",1)

        applyDataCanter(account, 3, "149.154.175.100",0)
        applyDataCanter(account, 3, "2001:b28:f23d:f003:0000:0000:0000:000a",1)

        applyDataCanter(account, 4, "149.154.167.91",0)
        applyDataCanter(account, 4, "2001:67c:4e8:f004:0000:0000:0000:000a",1)

        applyDataCanter(account, 5, "149.154.171.5",0)
        applyDataCanter(account, 5, "2001:67c:4e8:f005:0000:0000:0000:000a",1)

        ConnectionsManager.native_cleanUp(account,true)

    }

    @JvmStatic
    fun applyTestDataCenter(account: Int) {

        MessagesController.getMainSettings(account).edit().remove("layer").remove("custom_dc").apply()

        if (ConnectionsManager.native_isTestBackend(account) == 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        }

    }

    @JvmStatic
    fun applyCustomDataCenter(account: Int, ipv4Address: String = "", ipv6Address: String = "", port: Int,layer: Int) {

        MessagesController.getMainSettings(account).edit().putInt("layer", layer).putBoolean("custom_dc",true).apply()

        if (ConnectionsManager.native_isTestBackend(account) != 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        }

        if (ipv4Address.isNotBlank()) {

            for (dc in 1..5) applyDataCanter(account, dc, ipv4Address,0, port)

            if (ipv6Address.isBlank()) {

                for (dc in 1..5) applyDataCanter(account, dc, "",1, port)

            }

        }

        if (ipv6Address.isNotBlank()) {

            for (dc in 1..5) applyDataCanter(account, dc, ipv6Address, 1, port)

            if (ipv4Address.isBlank()) {

                for (dc in 1..5) applyDataCanter(account, dc, "", 0, port)

            }

        }

        AlertUtil.showToast("Restart required.")

    }

    private fun applyDataCanter(account: Int, dataCenter: Int, address: String,flag: Int, port: Int = 443) {

        ConnectionsManager.getInstance(account).applyDatacenterAddress(dataCenter, address, port, flag)

    }

}
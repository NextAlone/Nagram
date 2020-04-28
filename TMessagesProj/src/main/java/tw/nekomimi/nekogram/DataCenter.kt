package tw.nekomimi.nekogram

import cn.hutool.crypto.digest.DigestUtil
import org.telegram.messenger.MessagesController
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.SerializedData
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.interfaces.RSAPublicKey

object DataCenter {

    //    func calcAuthKeyId(keyData []byte) int64 {
    //        sha1 := Sha1Digest(keyData)
    //        // Lower 64 bits = 8 bytes of 20 byte SHA1 hash.
    //        return int64(binary.LittleEndian.Uint64(sha1[12:]))
    //    }
    @JvmStatic
    fun calcAuthKeyId(publicKey: RSAPublicKey): Long {

        val key = SerializedData()

        key.writeByteArray(publicKey.modulus.toByteArray())
        key.writeByteArray(publicKey.publicExponent.toByteArray())

        return BigInteger(DigestUtil.sha1(key.toByteArray()).slice(12 until 20).toByteArray()).toLong()

    }

    @JvmStatic
    fun applyOfficalDataCanter(account: Int) {

        MessagesController.getMainSettings(account).edit().remove("layer").remove("custom_dc").apply()

        if (ConnectionsManager.native_isTestBackend(account) != 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        }

        applyDataCanter(account, 1, "149.154.175.50", "2001:b28:f23d:f001:0000:0000:0000:000a")
        applyDataCanter(account, 2, "149.154.167.51", "2001:67c:4e8:f002:0000:0000:0000:000a")
        applyDataCanter(account, 3, "149.154.175.100", "2001:b28:f23d:f003:0000:0000:0000:000a")
        applyDataCanter(account, 4, "149.154.167.91", "2001:67c:4e8:f004:0000:0000:0000:000a")
        applyDataCanter(account, 5, "149.154.171.5", "2001:67c:4e8:f005:0000:0000:0000:000a")

        ConnectionsManager.native_cleanUp(account, true)

        repeat(5) {

            ConnectionsManager.native_setDatacenterPublicKey(account, it + 1, "", 0)

        }

    }

    @JvmStatic
    fun applyTestDataCenter(account: Int) {

        MessagesController.getMainSettings(account).edit().remove("layer").remove("custom_dc").apply()

        if (ConnectionsManager.native_isTestBackend(account) == 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        }

    }

    @JvmStatic
    fun applyCustomDataCenter(account: Int, ipv4Address: String = "", ipv6Address: String = "", port: Int, layer: Int, publicKey: String, fingerprint: Long) {

        MessagesController.getMainSettings(account).edit().putInt("layer", layer).putBoolean("custom_dc", true).apply()

        if (ConnectionsManager.native_isTestBackend(account) != 0) {

            ConnectionsManager.getInstance(account).switchBackend()

        }

        repeat(5) {

            ConnectionsManager.native_setDatacenterAddress(account, it + 1, ipv4Address, ipv6Address, port)

        }

        ConnectionsManager.native_saveDatacenters(account)
        ConnectionsManager.native_setLayer(account, layer)

        repeat(5) {

            ConnectionsManager.native_setDatacenterPublicKey(account, it + 1, publicKey, fingerprint);

        }


    }

    private fun applyDataCanter(account: Int, dataCenterId: Int, ipv4Address: String, ipv6Address: String, port: Int = 443) {

        ConnectionsManager.native_setDatacenterAddress(account, dataCenterId, ipv4Address, ipv4Address, port)

    }

}
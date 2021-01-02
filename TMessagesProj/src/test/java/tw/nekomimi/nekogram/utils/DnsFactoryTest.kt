package tw.nekomimi.nekogram.utils

import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DnsFactoryTest {

    @Test
    fun testDns() {

        val result = DnsFactory.lookup("google.com")

        println(result.joinToString("\n"))

    }


}
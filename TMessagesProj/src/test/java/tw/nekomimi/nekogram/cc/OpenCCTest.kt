package tw.nekomimi.nekogram.cc

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenCCTest {

    @Test
    fun ccTest() {

        val example = "你好， 开放中文转换！"

        for (target in CCTarget.values()) {
            print(target.name + ": ")
            println(CCConverter(target).convert(example))
        }

    }

}
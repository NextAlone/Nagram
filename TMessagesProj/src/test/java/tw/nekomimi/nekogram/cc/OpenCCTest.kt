package tw.nekomimi.nekogram.cc

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class OpenCCTest {

    @Test
    fun ccTest() {

        File("src/main/res/values-zh-rTW/strings_neko.xml").writeText(
                CCConverter.get(CCTarget.TT)
                        .convert(File("src/main/res/values-zh-rCN/strings_neko.xml").readText())
        )

        File("src/main/res/values-zh-rTW/strings_nekox.xml").writeText(
                CCConverter.get(CCTarget.TT)
                        .convert(File("src/main/res/values-zh-rCN/strings_nekox.xml").readText())
        )


    }

}
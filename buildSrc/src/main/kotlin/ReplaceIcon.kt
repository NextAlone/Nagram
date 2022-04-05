
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.random.Random

abstract class ReplaceIcon : DefaultTask() {
    @TaskAction
    fun run() {
        val iconsDir = File(project.projectDir, "icons")
        val iconFileDirs = listOf(
            File(iconsDir, "anime"),
        )
        val fileCount = iconFileDirs.fold(0) { i: Int, file: File ->
            i + file.listFiles()!!.size
        }
        val md5 = MessageDigest.getInstance("MD5")
        val arrays = md5.digest(Common.getGitHeadRefsSuffix(project.rootProject).toByteArray(Charsets.UTF_8))
        val bigInteger = BigInteger(1, arrays)
        var number = Random(bigInteger.toInt()).nextInt(fileCount)

        //for (aNumber in 0..fileCount) {
        //var number = aNumber
        var iconFile: File? = null
        for (iconFileDir in iconFileDirs) {
            if (number < iconFileDir.listFiles()!!.size) {
                iconFile = iconFileDir.listFiles()!![number]
                break
            }
            number -= iconFileDir.listFiles()!!.size
        }
        println("Select Icon: $iconFile")
        iconFile!!.copyTo(File(project.projectDir, "src/main/res/drawable/icon.png"), true)
        //}
    }
}

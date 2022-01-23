import org.gradle.api.Project
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Common {

    @JvmStatic
    fun getGitHeadRefsSuffix(project: Project): String {
        // .git/HEAD描述当前目录所指向的分支信息，内容示例："ref: refs/heads/master\n"
        val headFile = File(project.rootProject.projectDir, ".git" + File.separator + "HEAD")
        if (headFile.exists()) {
            val string: String = headFile.readText(Charsets.UTF_8)
            val string1 = string.replace(Regex("""ref:|\s"""), "")
            val result = if (string1.isNotBlank() && string1.contains('/')) {
                val refFilePath = ".git" + File.separator + string1
                // 根据HEAD读取当前指向的hash值，路径示例为：".git/refs/heads/master"
                val refFile = File(project.rootProject.projectDir, refFilePath)
                // 索引文件内容为hash值+"\n"，
                // 示例："90312cd9157587d11779ed7be776e3220050b308\n"
                refFile.readText(Charsets.UTF_8).replace(Regex("""\s"""), "").subSequence(0, 7)
            } else {
                string.substring(0, 7)
            }
            println("commit_id: $result")
            return "$result"
        } else {
            println("WARN: .git/HEAD does NOT exist")
            return ""
        }
    }

    @JvmStatic
    fun getTimestamp(): Int {
        val currentTimestamp = (System.currentTimeMillis() / 1000L).toInt()
        println("currentTimestamp: $currentTimestamp")
        return currentTimestamp
    }


}

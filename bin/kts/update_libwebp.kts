import java.io.File
import kotlin.system.exitProcess

val projectRoot = File("../..")

val webpSources = File(projectRoot, "TMessagesProj/jni/libwebp/Android.mk")
        .readLines()
        .map { it.trim() }
        .filter { it.startsWith("src/") && it.endsWith("\\") }
        .map { "libwebp/" + it.substring(0, it.length - 2).replace("\$(NEON)", "c") }

val cmakeLists = File(projectRoot, "TMessagesProj/jni/CMakeLists.txt")

var cmakeListsSource = cmakeLists.readText()

val cmakeListsSourceNew = cmakeListsSource.substringBefore("add_library(webp STATIC") + "add_library(webp STATIC" +
        webpSources.joinToString("\n", "\n") { "        $it" } + ")" +
        cmakeListsSource.substringAfter("add_library(webp STATIC").substringAfter(")")

if (cmakeListsSource == cmakeListsSourceNew) {

    println("No changes")

    exitProcess(0)

}

cmakeLists.writeText(cmakeListsSourceNew)

println("Updated sources")
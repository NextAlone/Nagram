package org.telegram.tasks

import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.BufferedOutputStream
import java.io.File
import java.io.OutputStream
import java.nio.charset.StandardCharsets

/**
 * Generates localization .bin assets for the extra string namespaces
 * (values/strings_na.xml, strings_neko.xml, strings_nekox.xml, ...).
 *
 * Unlike [TelegramStringsTask] this task does not touch stable ids or
 * resource shrinker rules: namespace strings remain regular Android
 * resources handled by aapt, only the parallel .bin assets are added.
 */
@CacheableTask
abstract class TelegramNamespaceStringsTask : DefaultTask() {

    // values/strings_*.xml 来自所有模块，默认（en）语言包。
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val stringsXml: ConfigurableFileCollection

    // values-*/strings_*.xml 来自所有模块。
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val localizationFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val assetsOutputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val assetsDir = assetsOutputDir.get().asFile
        assetsDir.mkdirs()

        // 删除旧运行产生的带命名空间后缀的资产文件。
        assetsDir
            .listFiles { file ->
                file.isFile &&
                        file.name.startsWith("localization_") &&
                        LOCALIZATION_ASSET_WITH_NAMESPACE.matches(file.name)
            }
            ?.forEach { file ->
                if (!file.delete()) {
                    error("Unable to delete old localization asset: ${file.absolutePath}")
                }
            }

        // namespace -> languageTag -> files。
        // 列表保持插入顺序：同组内后出现的文件覆盖先出现的（overlay 语义）。
        val filesByNamespace = LinkedHashMap<String, LinkedHashMap<String, MutableList<File>>>()

        for (file in stringsXml.files) {
            filesByNamespace
                .getOrPut(getNamespace(file)) { LinkedHashMap() }
                .getOrPut("en") { ArrayList() }
                .add(file)
        }

        for (file in localizationFiles.files) {
            filesByNamespace
                .getOrPut(getNamespace(file)) { LinkedHashMap() }
                .getOrPut(getLanguageTag(file)) { ArrayList() }
                .add(file)
        }

        for ((namespace, filesByTag) in filesByNamespace) {
            // 命名空间内全部 key 的并集。
            val namespaceStrings = sortedSetOf<String>()

            for (files in filesByTag.values) {
                for (file in files) {
                    collectStrings(
                        file = file,
                        destination = namespaceStrings
                    )
                }
            }

            // 哈希冲突按命名空间隔离检测：不同命名空间允许同名 key。
            val hashesByName = buildHashes(namespaceStrings)

            for ((languageTag, files) in filesByTag) {
                generateLocalization(
                    inputFiles = files,
                    outputFile = assetsDir.resolve(getLocalizationAssetName(languageTag, namespace)),
                    hashesByName = hashesByName
                )
            }
        }
    }

    private fun collectStrings(
        file: File,
        destination: MutableSet<String>
    ) {
        val strings = XmlParser().parse(file)

        for (string in strings["string"] as NodeList) {
            val node = string as Node
            val name = node["@name"].toString()

            destination.add(name)
        }
    }

    private fun buildHashes(
        allStrings: Set<String>
    ): Map<String, Int> {
        val namesByHash = HashMap<Int, String>(allStrings.size)
        val hashesByName = HashMap<String, Int>(allStrings.size)

        for (name in allStrings) {
            val hash = name.hashCode()

            val previous = namesByHash.put(hash, name)

            if (previous != null && previous != name) {
                error(
                    buildString {
                        appendLine("String hash collision:")

                        append("  ")
                        append(formatHash(hash))
                        append(" - R.string.")
                        appendLine(previous)

                        append("  ")
                        append(formatHash(hash))
                        append(" - R.string.")
                        appendLine(name)
                    }
                )
            }

            hashesByName[name] = hash
        }

        return hashesByName
    }

    private data class LocalizationEntry(
        val name: String,
        val hash: Int,
        val value: String
    )

    private fun generateLocalization(
        inputFiles: List<File>,
        outputFile: File,
        hashesByName: Map<String, Int>
    ) {
        val entriesByName = LinkedHashMap<String, LocalizationEntry>()

        for (inputFile in inputFiles) {
            val strings = XmlParser().parse(inputFile)

            for (string in strings["string"] as NodeList) {
                val node = string as Node
                val name = node["@name"].toString()

                val hash = hashesByName[name]
                    ?: error(
                        "Internal error: string is missing from merged namespace: " +
                                "R.string.$name in ${inputFile.absolutePath}"
                    )

                // Same semantics as an overlay: a later strings.xml wins.
                entriesByName[name] = LocalizationEntry(
                    name = name,
                    hash = hash,
                    value = normalizeXmlString(node.text())
                )
            }
        }

        val entries = entriesByName.values
            .sortedWith { first, second ->
                Integer.compareUnsigned(first.hash, second.hash)
            }

        BufferedOutputStream(outputFile.outputStream()).use { output ->
            writeInt32(output, entries.size)

            for (entry in entries) {
                writeInt32(output, entry.hash)
                writeString(
                    output = output,
                    value = entry.value,
                    name = entry.name,
                    inputFiles = inputFiles
                )
            }
        }
    }

    private fun normalizeXmlString(
        value: String
    ): String {
        return value
            .replace("\\n", "\n")
            .replace("\\", "")
            .replace("&lt;", "<")
    }

    private fun writeString(
        output: OutputStream,
        value: String,
        name: String,
        inputFiles: List<File>
    ) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        val length = bytes.size

        if (length >= 1 shl 24) {
            error(
                "Localization string is too long: " +
                        "R.string.$name in ${inputFiles.joinToString { it.absolutePath }}: " +
                        "$length UTF-8 bytes"
            )
        }

        val headerSize: Int

        if (length < 254) {
            output.write(length)
            headerSize = 1
        } else {
            output.write(254)
            output.write(length and 0xFF)
            output.write((length shr 8) and 0xFF)
            output.write((length shr 16) and 0xFF)

            headerSize = 4
        }

        output.write(bytes)

        var totalSize = headerSize + length

        while (totalSize % 4 != 0) {
            output.write(0)
            totalSize++
        }
    }

    private fun writeInt32(
        output: OutputStream,
        value: Int
    ) {
        output.write(value and 0xFF)
        output.write((value ushr 8) and 0xFF)
        output.write((value ushr 16) and 0xFF)
        output.write((value ushr 24) and 0xFF)
    }

    // strings_na.xml -> "na"，strings_neko.xml -> "neko"。
    private fun getNamespace(
        file: File
    ): String {
        val name = file.name

        if (!name.startsWith("strings_") || !name.endsWith(".xml")) {
            error("Invalid namespaced strings file: ${file.absolutePath}")
        }

        val namespace = name
            .removePrefix("strings_")
            .removeSuffix(".xml")

        if (namespace.isEmpty() || !namespace.matches(Regex("[a-z0-9_]+"))) {
            error(
                "Invalid strings namespace \"$namespace\" in ${file.absolutePath}: " +
                        "expected lowercase letters, digits or underscores"
            )
        }

        return namespace
    }

    private fun getLocalizationAssetName(
        languageTag: String,
        namespace: String
    ): String {
        val normalized = buildString(languageTag.length) {
            for (char in languageTag) {
                when {
                    char in 'A'..'Z' -> append(char.lowercaseChar())
                    char in 'a'..'z' || char in '0'..'9' || char == '_' -> append(char)
                    else -> append('_')
                }
            }
        }

        return "localization_${normalized}_$namespace.bin"
    }

    private fun getLanguageTag(
        file: File
    ): String {
        val directoryName = file.parentFile.name

        if (!directoryName.startsWith("values-")) {
            error("Invalid localization directory: ${file.absolutePath}")
        }

        val qualifier = directoryName.substring("values-".length)

        if (qualifier.isEmpty()) {
            error("Unable to determine language code: ${file.absolutePath}")
        }

        if (qualifier.startsWith("b+")) {
            return qualifier.substring(2).replace('+', '-')
        }

        val regionIndex = qualifier.indexOf("-r")

        if (regionIndex != -1) {
            val language = qualifier.substring(0, regionIndex)
            val region = qualifier.substring(regionIndex + 2)
            return "$language-$region"
        }

        return qualifier
    }

    private fun formatHash(
        hash: Int
    ): String {
        val unsigned = hash.toLong() and 0xFFFFFFFFL

        return "0x" +
                unsigned
                    .toString(16)
                    .uppercase()
                    .padStart(8, '0')
    }

    private companion object {
        // localization_<tag>_<namespace>.bin，例如 localization_es-rES_na.bin。
        private val LOCALIZATION_ASSET_WITH_NAMESPACE =
            Regex("""localization_[a-z0-9_]+_[a-z0-9_]+\.bin""")
    }
}

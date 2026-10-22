package xyz.nagram.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * tgcalls stays a pristine submodule; Nagram's changes to it are the patches in jni/patches,
 * applied in place before CMake configures. Patches that are already applied are skipped, so
 * the task is safe to run on every build.
 */
abstract class PatchTgcallsTask : DefaultTask() {

    /** tgcalls-*.patch files, applied in file name order. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val patches: ConfigurableFileCollection

    /** The tgcalls submodule checkout to patch in place. */
    @get:Internal
    abstract val tgcallsDir: DirectoryProperty

    @TaskAction
    fun applyPatches() {
        val tgcalls = tgcallsDir.get().asFile
        if (!File(tgcalls, ".git").exists()) {
            throw GradleException("$tgcalls is not checked out, run: git submodule update --init --recursive")
        }
        for (patch in patches.files.sortedBy { it.name }) {
            val normalized = normalizedCopy(patch)
            // Probe before applying: re-applying an applied patch fails and would break
            // every build. --ignore-whitespace lets the probe and the apply see through
            // line-ending differences between the patch and the submodule sources.
            if (git(tgcalls, "apply", "--check", "--reverse", "--ignore-whitespace", normalized.path).exitCode == 0) {
                logger.lifecycle("${patch.name}: already applied, skipping")
                continue
            }
            val result = git(tgcalls, "apply", "--ignore-whitespace", normalized.path)
            if (result.exitCode != 0) {
                throw GradleException(
                    "Cannot apply ${patch.name} to $tgcalls, reset it with: " +
                        "git submodule update --init --force TMessagesProj/jni/tgcalls\n${result.output}"
                )
            }
            logger.lifecycle("${patch.name}: applied")
        }
    }

    /**
     * LF-normalized copy of [patch] under [temporaryDir]. A patch checked out with CRLF
     * (core.autocrlf turns *.patch files into CRLF on Windows) must not carry those CRs
     * into the LF sources it is applied to. ISO-8859-1 keeps the mapping byte-exact so
     * binary hunks survive.
     */
    private fun normalizedCopy(patch: File): File {
        val normalized = File(temporaryDir, patch.name)
        val text = patch.readText(Charsets.ISO_8859_1).replace("\r\n", "\n")
        normalized.writeText(text, Charsets.ISO_8859_1)
        return normalized
    }

    private class GitResult(val exitCode: Int, val output: String)

    private fun git(tgcalls: File, vararg args: String): GitResult {
        val process = ProcessBuilder(listOf("git", "-C", tgcalls.path) + args)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        return GitResult(process.waitFor(), output)
    }
}

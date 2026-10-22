package xyz.nagram.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import xyz.nagram.tasks.PatchTgcallsTask

class NagramPatchPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // tgcalls stays a pristine submodule; Nagram's changes to it are the patches in jni/patches,
        // applied in place before CMake configures. Patches that are already applied are skipped.
        var patchTgcalls = project.tasks.register<PatchTgcallsTask>("patchTgcalls") {
            tgcallsDir.set(project.layout.projectDirectory.dir("jni/tgcalls"))
            patches.from(project.fileTree("jni/patches") { include("tgcalls-*.patch") })
        }
        project.tasks.configureEach {
            if (name.startsWith("configureCMake")) {
                dependsOn(patchTgcalls)
            }
        }
    }
}
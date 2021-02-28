import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.library")
    id("org.mozilla.rust-android-gradle.rust-android")
}

var targetAbi = ""
if (gradle.startParameter.taskNames.isNotEmpty()) {
    if (gradle.startParameter.taskNames.size == 1) {
        val targetTask = gradle.startParameter.taskNames[0].toLowerCase()
        if (targetTask.contains("arm64")) {
            targetAbi = "arm64"
        } else if (targetTask.contains("arm")) {
            targetAbi = "arm"
        }
    } else {
        targetAbi = "~"
    }
}

android {

    ndkVersion = rootProject.extra.get("ndkVersion").toString()

    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }
    buildToolsVersion = "30.0.3"

    if (targetAbi.isNotBlank()) {
        splits.abi.exclude(* when (targetAbi) {
            "arm" -> arrayOf("x86", "x86_64", "arm64-v8a")
            "arm64" -> arrayOf("x86", "x86_64", "armeabi-v7a")
            else -> arrayOf("x86", "x86_64")
        })
    }

}

cargo {
    module = "src/main/rust/shadowsocks-rust"
    libname = "ss-local"
    targets = when {
        targetAbi.isBlank() -> listOf("arm", "arm64", "x86", "x86_64")
        targetAbi == "arm" -> listOf("arm")
        targetAbi == "arm64" -> listOf("arm64")
        else -> listOf("arm", "arm64")
    }
    profile = findProperty("CARGO_PROFILE")?.toString() ?: "release"
    extraCargoBuildArguments = listOf("--bin", "sslocal")
    featureSpec.noDefaultBut(arrayOf(
            "stream-cipher",
            "logging",
            "local-flow-stat",
            "local-dns"))
    exec = { spec, toolchain ->
        spec.environment("RUST_ANDROID_GRADLE_LINKER_WRAPPER_PY", "$projectDir/$module/../linker-wrapper.py")
        spec.environment("RUST_ANDROID_GRADLE_TARGET", "target/${toolchain.target}/$profile/lib$libname.so")
    }
}

tasks.whenTaskAdded {
    when (name) {
        "mergeDebugJniLibFolders", "mergeReleaseJniLibFolders" -> dependsOn("cargoBuild")
    }
}

tasks.register<Exec>("cargoClean") {
    executable("cargo")     // cargo.cargoCommand
    args("clean")
    workingDir("$projectDir/${cargo.module}")
}

tasks.clean.dependsOn("cargoClean")
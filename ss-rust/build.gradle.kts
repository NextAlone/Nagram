import cn.hutool.core.codec.Base64
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.apache.tools.ant.filters.StringInputStream
import java.util.*
import java.io.*

plugins {
    id("com.android.library")
    id("org.mozilla.rust-android-gradle.rust-android")
}

var ignoreX86 = false

lateinit var properties: Properties
val base64 = System.getenv("LOCAL_PROPERTIES")
if (!base64.isNullOrBlank()) {
    properties = Properties()
    properties.load(ByteArrayInputStream(Base64.decode(base64)))
} else if (project.rootProject.file("local.properties").exists()) {
    properties = Properties()
    properties.load(StringInputStream(project.rootProject.file("local.properties").readText()))
}

if (::properties.isInitialized) {
    ignoreX86 = properties.getProperty("IGNORE_X86") == "true"
}


android {

    ndkVersion = rootProject.extra.get("ndkVersion").toString()

    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }
    buildToolsVersion = "30.0.3"

    if (ignoreX86) {
        splits.abi {
            exclude("x86", "x86_64")
        }
    }

}

cargo {
    module = "src/main/rust/shadowsocks-rust"
    libname = "ss-local"
    targets = if (ignoreX86) {
        listOf("arm", "arm64")
    } else {
        listOf("arm", "arm64", "x86", "x86_64")
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
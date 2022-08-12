@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.github.triplet.play") version "3.7.0"
}

configurations {
    all {
        exclude(group = "com.google.firebase", module = "firebase-core")
        exclude(group = "androidx.recyclerview", module = "recyclerview")
    }
}

var serviceAccountCredentialsFile = File(rootProject.projectDir, "service_account_credentials.json")

if (serviceAccountCredentialsFile.isFile) {
    setupPlay(Version.isStable)
    play.serviceAccountCredentials.set(serviceAccountCredentialsFile)
} else if (System.getenv().containsKey("ANDROID_PUBLISHER_CREDENTIALS")) {
    setupPlay(Version.isStable)
}

fun setupPlay(stable: Boolean) {
    val targetTrace = if (stable) "production" else "beta"
    play {
        track.set(targetTrace);
        defaultToAppBundles.set(true)
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:30.2.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.exifinterface:exifinterface:1.3.3")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.interpolator:interpolator:1.0.0")
    implementation("androidx.sharetarget:sharetarget:1.2.0-rc02")

    compileOnly("org.checkerframework:checker-qual:3.22.0")
    compileOnly("org.checkerframework:checker-compat-qual:2.5.5")
    implementation("com.google.firebase:firebase-messaging:23.0.6")
    implementation("com.google.firebase:firebase-config:21.1.1")
    implementation("com.google.firebase:firebase-datatransport:18.1.6")
    implementation("com.google.firebase:firebase-appindexing:20.0.0")
    implementation("com.google.android.gms:play-services-auth:20.2.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.android.gms:play-services-wearable:17.1.0")
    implementation("com.google.android.gms:play-services-location:20.0.0")
    implementation("com.google.android.gms:play-services-wallet:19.1.0")
    implementation("com.googlecode.mp4parser:isoparser:1.1.22")
    implementation("com.stripe:stripe-android:2.0.2")
    implementation("com.google.mlkit:language-id:17.0.4")
    implementation(files("libs/libgsaverification-client.aar"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.jakewharton:process-phoenix:2.1.2")
    // https://mvnrepository.com/artifact/de.psdev.licensesdialog/licensesdialog
    implementation("de.psdev.licensesdialog:licensesdialog:2.2.0")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation("org.codeberg.qwerty287:prism4j:003cb5e380")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("androidx.core:core-ktx:1.8.0")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-common
    implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${Version.kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}")
    implementation("org.osmdroid:osmdroid-android:6.1.13")
    implementation("com.android.billingclient:billing:5.0.0")
}


dependencies {
    val appCenterSdkVersion = "4.4.3"
    implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")
}

android {
    compileSdk = 33
    buildToolsVersion = "32.0.0"
    ndkVersion = "21.4.7075529"

    defaultConfig.applicationId = "top.qwq2333.nullgram"

    sourceSets.getByName("main") {
        java.srcDir("src/main/java")
        jniLibs.srcDirs("./jni/")
    }

    externalNativeBuild {
        cmake {
            path = File(projectDir, "jni/CMakeLists.txt")
        }
    }

    lint {
        checkReleaseBuilds = false
        disable += listOf(
            "MissingTranslation", "ExtraTranslation", "BlockedPrivateApi"
        )
    }

    packagingOptions {
        resources.excludes += "**"
    }

    kotlinOptions {
        jvmTarget = Version.JavaVersion
    }

    var keystorePwd: String? = null
    var alias: String? = null
    var pwd: String? = null
    if (project.rootProject.file("local.properties").exists()) {
        keystorePwd = gradleLocalProperties(rootDir).getProperty("RELEASE_STORE_PASSWORD")
        alias = gradleLocalProperties(rootDir).getProperty("RELEASE_KEY_ALIAS")
        pwd = gradleLocalProperties(rootDir).getProperty("RELEASE_KEY_PASSWORD")
    }

    signingConfigs {
        create("release") {
            storeFile = File(projectDir, "config/release.keystore")
            storePassword = (keystorePwd ?: System.getenv("KEYSTORE_PASS"));
            keyAlias = (alias ?: System.getenv("ALIAS_NAME"))
            keyPassword = (pwd ?: System.getenv("ALIAS_PASS"))
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(File(projectDir, "proguard-rules.pro"))
        }
    }

    defaultConfig {
        externalNativeBuild {
            cmake {
                version = "3.22.1"
                arguments += listOf(
                    "-DANDROID_STL=c++_static",
                    "-DANDROID_PLATFORM=android-21",
                    "-DCMAKE_C_COMPILER_LAUNCHER=ccache",
                    "-DCMAKE_CXX_COMPILER_LAUNCHER=ccache",
                    "-DNDK_CCACHE=ccache"
                )
            }
        }
    }

    flavorDimensions += "abi"
    productFlavors {
        create("arm32") {
            dimension = "abi"
            buildConfigField("boolean", "isPlay", "false")
            ndk {
                abiFilters.add("armeabi-v7a")
            }
        }
        create("arm64") {
            dimension = "abi"
            buildConfigField("boolean", "isPlay", "false")
            ndk {
                abiFilters.add("arm64-v8a")
            }
        }
        create("x86") {
            dimension = "abi"
            buildConfigField("boolean", "isPlay", "false")
            ndk {
                abiFilters.add("x86")
            }
        }
        create("x86_64") {
            dimension = "abi"
            buildConfigField("boolean", "isPlay", "false")
            ndk {
                abiFilters.add("x86_64")
            }
        }

        create("play") {
            dimension = "abi"
            buildConfigField("boolean", "isPlay", "true")
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            }
        }
    }

    applicationVariants.all {
        val outputFileName =
            "Nullgram-${defaultConfig.versionName}-${productFlavors.first().name}.apk"
        outputs.all {
            val output = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output?.outputFileName = outputFileName

        }
    }

    dependenciesInfo.includeInApk = false
}


tasks.register<ReplaceIcon>("replaceIcon") {}
tasks.getByName("preBuild").dependsOn(tasks.getByName("replaceIcon"))

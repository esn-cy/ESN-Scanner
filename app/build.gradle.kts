@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())

    androidLibrary {
        namespace = libs.versions.packageName.get()
        compileSdk = libs.versions.targetAndroid.get().toInt()
        minSdk = libs.versions.minAndroid.get().toInt()

        withJava()
        androidResources {
            enable = true
        }
        compilerOptions {
            jvmTarget.set(JvmTarget.valueOf("JVM_" + libs.versions.java.get()))
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = "App"
            isStatic = true
            binaryOption("bundleId", libs.versions.packageName.get())
            binaryOption("bundleShortVersionString", libs.versions.versionName.get())
            binaryOption("bundleVersion", libs.versions.versionCode.get())
        }
    }

    cocoapods {
        version = libs.versions.versionName.get()
        summary = "Scans ESNcards and Free Passes"
        homepage = "https://github.com/esn-cy/ESN-Scanner"
        source = "https://github.com/esn-cy/ESN-Scanner"
        authors = "Andreas Michael <ateasm03@gmail.com>"
        license = "Apache 2.0"
        ios.deploymentTarget = libs.versions.minIOS.get()
        name = "ESNScanner"
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "App"
            isStatic = true
            linkerOpts.add("-ObjC")
        }

        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE

        pod("GoogleMLKit/BarcodeScanning") {
            version = libs.versions.ios.mlkit.get()
            moduleName = "MLKitBarcodeScanning"
        }
        pod("GoogleMLKit/Vision") {
            version = libs.versions.ios.mlkit.get()
            moduleName = "MLKitVision"
        }
        pod("FirebaseCore") {
            version = libs.versions.ios.firebase.get()
        }
        pod("FirebaseAnalytics") {
            version = libs.versions.ios.firebase.get()
        }
        pod("FirebaseCrashlytics") {
            version = libs.versions.ios.firebase.get()
        }
        pod("FirebasePerformance") {
            version = libs.versions.ios.firebase.get()
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(layout.buildDirectory.dir("generated/esnscanner/kotlin"))

            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.lifecycle.runtime.compose)
                implementation(libs.navigation.compose)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
                implementation(libs.datastore)
                implementation(libs.datastore.preferences)
                implementation(kotlincrypto.random.crypto.rand)
                implementation(kotlincrypto.hash.sha2)
                implementation(libs.kotlinx.datetime)
                implementation(libs.qr.kit)
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        androidMain.dependencies {
            implementation(libs.material)

            implementation(libs.ktor.client.android)

            implementation(libs.camera.core)
            implementation(libs.camera.lifecycle)
            implementation(libs.camera.view)
            implementation(libs.camera.camera2)

            implementation(libs.barcode.scanning)

            implementation(libs.browser)

            implementation(libs.app.update)
            implementation(libs.app.update.ktx)

            implementation(libs.google.services)
            implementation(libs.coroutines.play.services)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.crashlytics.ndk)
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.perf)
        }
        iosArm64Main.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

tasks.register("generateVersionXcconfig") {
    val configFile = project.rootDir.resolve("iosApp/Configuration/Version.xcconfig")

    doLast {
        val content = """
            // This file is auto-generated by Gradle. Do not edit.
            MARKETING_VERSION = ${libs.versions.versionName.get()}
            CURRENT_PROJECT_VERSION = ${libs.versions.versionCode.get()}
        """.trimIndent()
        configFile.writeText(content)
    }
}
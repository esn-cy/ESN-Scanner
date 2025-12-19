@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = libs.versions.packageName.get()
    compileSdk = libs.versions.targetAndroid.get().toInt()
    ndkVersion = "29.0.14206865"

    defaultConfig {
        applicationId = libs.versions.packageName.get()
        minSdk = libs.versions.minAndroid.get().toInt()
        targetSdk = libs.versions.targetAndroid.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isJniDebuggable = false
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_" + libs.versions.java.get())
        targetCompatibility = JavaVersion.valueOf("VERSION_" + libs.versions.java.get())
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":app"))
    implementation(libs.activity.compose)
    implementation(libs.material)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    implementation(libs.core.splashscreen)

    implementation(libs.qr.kit)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.valueOf("JVM_" + libs.versions.java.get()))
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}
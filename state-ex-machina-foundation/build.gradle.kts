import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

ext["pomName"] = "State Ex Machina - foundation"
ext["pomDescription"] = "MVI-like library written in Kotlin Multiplatform"
apply(from = "$rootDir/publish.gradle")

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.coroutine)
            }
        }
    }
}

android {
    namespace = "state.ex.machina"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

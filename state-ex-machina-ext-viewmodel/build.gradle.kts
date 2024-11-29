import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

ext["pomName"] = "State Ex Machina - View Model extensions"
ext["pomDescription"] = "Some utilities to better fit Android View Model with State Ex Machina"
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
                implementation(project(":state-ex-machina-foundation"))
                implementation(libs.androidx.lifecycle.viewmodel)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "state.ex.machina.viewmodel"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

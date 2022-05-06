/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("publication-convention")
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
        val commonMain by getting
        val darwinMain by creating{
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktorClientDarwin)
            }
        }
        val darwinTest by creating
        val iosArm64Main by getting {
            dependsOn(darwinMain)
        }
        val iosX64Main by getting {
            dependsOn(darwinMain)
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(darwinMain)
        }
        val macosArm64Main by getting {
            dependsOn(darwinMain)
        }
        val macosX64Main by getting {
            dependsOn(darwinMain)
        }
        val iosArm64Test by getting {
            dependsOn(darwinTest)
        }
        val iosX64Test by getting {
            dependsOn(darwinTest)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(darwinTest)
        }
        val macosArm64Test by getting {
            dependsOn(darwinTest)
        }
        val macosX64Test by getting {
            dependsOn(darwinTest)
        }
    }
}

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainImplementation(libs.kbignum)
    commonMainImplementation(libs.kotlinSerialization)
    commonMainImplementation(libs.klock)
    commonMainImplementation(libs.ktorClient)
    commonMainImplementation(libs.ktorClientLogigng)
    commonMainImplementation(libs.ktorWebsockets)
    
    commonTestImplementation(libs.kotlinTestCommon)
    commonTestImplementation(libs.kotlinTestAnnotations)
    commonTestImplementation(libs.ktorClientMock)

    jvmMainImplementation(libs.ktorClientOkHttp)
    jvmTestImplementation(libs.kotlinTest)
    jvmTestImplementation(libs.kotlinTestJunit)
}

// now standard test task use --standalone but it broke network calls
val newTestTask = tasks.create("iosX64TestWithNetwork") {
    val linkTask = tasks.getByName("linkDebugTestIosX64") as org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
    dependsOn(linkTask)

    group = JavaBasePlugin.VERIFICATION_GROUP
    description = "Runs tests for target 'ios' on an iOS simulator"

    doLast {
        val binary = linkTask.binary.outputFile
        val device = "iPhone 8"
        exec {
            commandLine = listOf("xcrun", "simctl", "boot", device)
            isIgnoreExitValue = true
        }
        exec {
            commandLine = listOf(
                "xcrun",
                "simctl",
                "spawn",
                device,
                binary.absolutePath
            )
        }
        exec {
            commandLine = listOf("xcrun", "simctl", "shutdown", device)
        }
    }
}

with(tasks.getByName("iosX64Test")) {
    dependsOn(newTestTask)
    onlyIf { false }
}

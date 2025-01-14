/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath(":web3-build-logic")
    }
}


allprojects {
    plugins.withId("org.gradle.maven-publish") {
        group = "io.github.luca992.dev.icerock.moko"
        version = libs.versions.mokoWeb3Version.get()
    }
}

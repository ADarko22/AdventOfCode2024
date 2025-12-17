pluginManagement {
    repositories {
        maven(url = "https://repox.jfrog.io/repox/plugins.gradle.org/")
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version "2.2.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "AdventOfCode2024"

include("solutions")
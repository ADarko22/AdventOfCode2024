import org.example.PrepareWorkTask

plugins {
    java
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
}

// creates the stub class for the specified day
// Usage: ./gradlew PrepareWorkTask -Pday="day14" -Ppkg="org.example"
tasks.register<PrepareWorkTask>("PrepareWorkTask") {
    day = project.findProperty("day") as String? ?: ""
    pkg = project.findProperty("pkg") as String? ?: "org.example"
}

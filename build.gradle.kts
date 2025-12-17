import edu.adarko22.GenerateJar
import edu.adarko22.PrepareWorkTask

plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

// creates the stub class for the specified day
// Usage: ./gradlew PrepareWorkTask -Pday="day14" -Ppkg="edu.adarko22"
tasks.register<PrepareWorkTask>("PrepareWorkTask") {
    day = project.findProperty("day") as String? ?: ""
    pkg = project.findProperty("pkg") as String? ?: "edu.adarko22"
}


// creates a JAR for the specified day file
// Usage: ./gradlew build generateJar -PmainFile=Day15.kt
// or for all day files: ./gradlew build generateJar -Pall=true
tasks.register<GenerateJar>("generateJar") {
    dependsOn(tasks.named("build"))
    mainFile = project.findProperty("mainFile") as String?
    all = (project.findProperty("all") as String?)?.toBoolean()
}
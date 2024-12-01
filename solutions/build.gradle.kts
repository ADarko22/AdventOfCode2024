plugins {
    kotlin("jvm")
    java
    // Apply the wrapper plugin
    id("org.gradle.wrapper")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
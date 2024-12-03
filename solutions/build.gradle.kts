plugins {
    kotlin("jvm")
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register<Wrapper>("wrapper") {

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
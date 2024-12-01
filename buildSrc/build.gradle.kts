plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
}


dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.9.21")
}
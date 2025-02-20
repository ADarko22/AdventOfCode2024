import org.example.PrepareWorkTask
import java.nio.file.Files
import java.nio.file.Paths

plugins {
    java
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
    // Ensure the Java Library plugin is applied
    `java-library`
}

repositories {
    mavenCentral()
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

// creates the stub class for the specified day
// Usage: ./gradlew PrepareWorkTask -Pday="day14" -Ppkg="org.example"
tasks.register<PrepareWorkTask>("PrepareWorkTask") {
    day = project.findProperty("day") as String? ?: ""
    pkg = project.findProperty("pkg") as String? ?: "org.example"
}


// Define a custom task that accepts a file path and creates a JAR for that file
// Usage: ./gradlew build generateJarFor -PmainFile=Day15.kt
tasks.register<Jar>("generateJarFor") {
    description = "Generates a JAR file from a Kotlin file containing a main method"

    // Make sure `generateJarFor` runs after the build task
    dependsOn(tasks.named("build"))

    doFirst {
        // Define the input property for the task and allow it to be set via the command line
        val mainFile = project.objects.property<String>()
        mainFile.set(project.findProperty("mainFile") as String? ?: "")

        // Ensure a main file path is provided
        val filePath = mainFile.getOrNull()
        if (filePath.isNullOrEmpty()) {
            throw GradleException("You must specify a Kotlin file containing the main method using -PmainFile=<file-path>")
        }

        val file = Files.walk(Paths.get(project.projectDir.path))
            .filter { it.toFile().isFile && it.fileName.endsWith(filePath) }
            .findFirst().get().toFile()
        if (!file.exists()) {
            throw GradleException("The file at '$filePath' does not exist.")
        }

        // Extract the class name based on the file name and package structure
        val packageName = file.bufferedReader().useLines { lines ->
            lines.firstOrNull { it.trim().startsWith("package") }?.substringAfter("package")?.trim() ?: ""
        }

        val className = file.nameWithoutExtension
        // when the main is defined at the top level, then it will be available in <className>Kt.class file
        val fullyQualifiedClassName = "$packageName.${className}Kt"

        archiveBaseName.set(className)
        archiveVersion.set("")

        manifest {
            attributes(
                "Main-Class" to fullyQualifiedClassName // Set the main class in the JAR manifest
            )
        }

        // Include the main file in the JAR
        from(project.subprojects.find { it.name == "solutions" }?.sourceSets?.main?.get()?.output)

        // Include project's dependencies (JARs and their contents) to the final JAR
        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

        // Handle duplicate entries (META-INF/INDEX.LIST or other files)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        // Exclude the signed JAR files
        exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
    }
}

package edu.adarko22

import org.gradle.api.GradleException
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.attributes
import java.nio.file.Files
import java.io.File
import kotlin.io.path.nameWithoutExtension

val executableDayFilenameRegex = "Day[0-9]+.kt".toRegex()

abstract class GenerateJar : Jar() {

    @get:Input
    @get:Optional
    abstract val mainFile: Property<String>

    @get:Input
    @get:Optional
    abstract val all: Property<Boolean>

    init {
        group = "build"
        description = "Generates Fat JAR(s) for AoC Days"

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveVersion.set("")
        exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
        destinationDirectory.set(project.layout.buildDirectory.dir("libs/days"))

        // Default Sources: Classes from :solutions subproject or root
        val solutions = project.subprojects.find { it.name == "solutions" } ?: project
        val sourceSets = solutions.extensions.findByType(SourceSetContainer::class.java)
        sourceSets?.let { from(it.getByName("main").output) }

        // Default Dependencies: Fat JAR logic
        from(project.configurations.named("runtimeClasspath").map { conf ->
            conf.map { if (it.isDirectory) it else project.zipTree(it) }
        })
    }

    override fun copy() {
        val isAll = all.getOrElse(false)
        val specificFile = mainFile.getOrNull()

        if (!isAll && specificFile == null) {
            throw GradleException("You must specify -PmainFile=DayXX.kt or -Pall=true")
        }

        val projectDir = project.projectDir.toPath()
        val filesToProcess = if (isAll) {
            Files.walk(projectDir)
                .filter { path -> executableDayFilenameRegex.matches(path.fileName.toString()) }
                .map { it.toFile() }
                .toList()
        } else {
            val found = Files.walk(projectDir)
                .filter { it.toFile().isFile && it.fileName.toString().endsWith(specificFile!!) }
                .findFirst()
                .map { it.toFile() }
                .orElseThrow { GradleException("The file at '$specificFile' does not exist.") }
            listOf(found)
        }

        if (filesToProcess.isEmpty()) {
            logger.warn("No matching Day files found to package.")
            return
        }

        filesToProcess.forEach { file ->
            processSingleFile(file)
        }
    }

    private fun processSingleFile(file: File) {
        val packageName = file.bufferedReader().useLines { lines ->
            lines.firstOrNull { it.trim().startsWith("package") }
                ?.substringAfter("package")
                ?.trim()
                ?.removeSuffix(";") ?: ""
        }

        val className = file.nameWithoutExtension
        val fullyQualifiedClassName = if (packageName.isEmpty()) "${className}Kt" else "$packageName.${className}Kt"

        // Re-configure task state for this specific iteration
        archiveBaseName.set(className)
        manifest {
            attributes("Main-Class" to fullyQualifiedClassName)
        }

        logger.lifecycle("Building Fat JAR: $className.jar (Main: $fullyQualifiedClassName)")

        // Execute the internal Jar copy action for the current configuration
        super.copy()
    }
}
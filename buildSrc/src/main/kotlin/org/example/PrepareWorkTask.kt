package org.example

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.writeText
import kotlin.system.exitProcess

val dayClassDir = Path.of("solutions", "src", "main", "kotlin", "org", "example")
val dayInputFileDir = Path.of("solutions", "src", "main", "resources")

abstract class PrepareWorkTask @Inject constructor() : DefaultTask() {

    @Input
    lateinit var day: String

    @Input
    @Optional
    var pkg: String = "org.example"

    @TaskAction
    fun execute() {
        if ("day[0-9]{1,2}".toRegex().find(day) == null) {
            logger.error("""ERROR: the day property should match the regex: "day[0-9]{1,2}" """)
            exitProcess(0)
        }

        val dayClassName = day.replaceFirstChar { it.uppercase() }
        val dayNumber = "([0-9]{1,2})".toRegex().find(day)!!.value!!.toInt()

        logger.info("Using properties: ")
        logger.info("  day: $day")
        logger.info("  package: $pkg")

        val finishedTasks = listOf(
            "Create Check Class" to createDayClass(day, dayClassName, dayNumber),
            "Create Input File" to createInputFile(day),
        )

        logger.info("--------- Done ---------")
        finishedTasks.forEach { logger.info("${it.first}: ${if (it.second) "successful" else "FAILED"}") }
    }

    private fun createDayClass(dayInputFile: String, dayClassName: String, dayNumber: Int) =
        createNewFile(
            dayClassDir.resolve("$dayClassName.kt"),
            generateCheckClass(dayInputFile, dayClassName, dayNumber)
        )

    private fun createInputFile(day: String): Boolean {
        return createNewFile(dayInputFileDir.resolve(day), "")
    }

    private fun generateCheckClass(dayInputFile: String, dayClassName: String, dayNumber: Int): String {
        return """
                package $pkg
                
                fun main() {
                    $dayClassName().part1()
                    $dayClassName().part2()
                }
    
                class $dayClassName: Day {
                
                    private val input = "/$dayInputFile".readResourceLines()
                
                    override fun part1() {
                        val solution = "todo"
                        println("Day $dayNumber part 1: ${'$'}solution")
                    }
    
                    override fun part2() {
                        val solution = "todo"
                        println("Day $dayNumber part 2: ${'$'}solution")
                    }
                }
            """.trimIndent()
    }

    private fun createNewFile(targetFile: Path, content: String) = if (targetFile.notExists()) {
        targetFile.createFile()
        targetFile.writeText(content)
        true
    } else {
        logger.warn("WARNING: File '$targetFile' exists. Not creating a new one.")
        false
    }

}
package org.example

import java.io.File

interface Day {
    fun part1()
    fun part2()
    fun String.readResourceLines() =
        Thread.currentThread().contextClassLoader.getResource(this)?.readText()!!.split("\n")
}
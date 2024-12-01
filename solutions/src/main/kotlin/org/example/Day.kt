package org.example

import java.io.File

interface Day {
    fun part1()
    fun part2()
    fun String.readResourceLines() =
        File(object {}.javaClass.getResource(this)?.toURI()!!.path!!)
            .readLines()
}
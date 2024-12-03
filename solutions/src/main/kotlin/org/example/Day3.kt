package org.example

fun main() {
    Day3().part1()
    Day3().part2()
}

class Day3 : Day {

    private val mulRegex = Regex("mul\\((\\d+),(\\d+)\\)")
    private val input = "/day3".readResourceLines().joinToString { it }

    override fun part1() {
        val solution = input.sumOfMultiplications()
        println("Day 3 part 1: $solution")
    }

    override fun part2() {
        var solution = 0L
        var doMulIdx = 0
        while (doMulIdx != -1) {
            val doNotMulIdx = input.indexOf("don't()", doMulIdx).let { if (it == -1) input.length else it }
            solution += input.substring(doMulIdx, doNotMulIdx).sumOfMultiplications()
            doMulIdx = input.indexOf("do()", doNotMulIdx)
        }
        println("Day 3 part 2: $solution")
    }

    private fun String.sumOfMultiplications(): Long {
        return mulRegex.findAll(this)
            .map { matchResult ->
                val a = matchResult.groupValues[1].toLong()
                val b = matchResult.groupValues[2].toLong()
                a * b
            }
            .sum()
    }
}
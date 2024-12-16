package org.example

fun main() {
    Day11().part1()
    Day11().part2()
}

class Day11 : Day {

    private val input = "day11".readResourceLines()[0]
        .split(Regex("\\s"))
        .map { Stone(it.toLong(), 1L) }
        .toList()

    override fun part1() {
        val solution = applyBlinks(25).sumOf { it.count }
        println("Day 11 part 1: $solution")
    }

    override fun part2() {
        val solution = applyBlinks(75).sumOf { it.count }
        println("Day 11 part 2: $solution")
    }

    private data class Stone(val number: Long, val count: Long)

    private fun applyBlinks(times: Int): List<Stone> {
        var stones = input
        repeat(times) {
            stones = stones.flatMap { applyRule(it) }
                .groupBy { it.number }
                .map { Stone(it.key, it.value.sumOf { stone -> stone.count }) }
        }
        return stones
    }

    private fun applyRule(stone: Stone): List<Stone> = when {
        isZero(stone) -> listOf(Stone(1L, stone.count))

        hasEvenDigits(stone) -> {
            val stoneAsString = stone.number.toString()
            val leftHalf = stoneAsString.substring(0, stoneAsString.length / 2).toLong()
            val rightHalf = stoneAsString.substring(stoneAsString.length / 2).toLong()
            listOf(Stone(leftHalf, stone.count), Stone(rightHalf, stone.count))
        }

        else -> listOf(Stone(stone.number * 2024, stone.count))
    }

    private fun isZero(stone: Stone): Boolean = stone.number == 0L
    private fun hasEvenDigits(stone: Stone): Boolean = stone.number.toString().length % 2 == 0
}
package org.example

fun main() {
    Day7().part1()
    Day7().part2()
}

class Day7 : Day {

    private val OPS_PART_1: List<(Long, Long) -> Long> =
        listOf(
            { a, b -> a * b },
            { a, b -> a + b }
        )

    private val OPS_PART_2: List<(Long, Long) -> Long> =
        listOf(
            { a, b -> a * b },
            { a, b -> a + b },
            { a, b -> "$a$b".toLong() }
        )

    private val input = "/day7".readResourceLines()
        .map { it.split(":") }
        .map { parts -> parts[0].toLong() to parts[1].split(" ").filter { it.isNotEmpty() }.map { it.toLong() } }

    override fun part1() {
        val solution = sumOfTestValuesThatCanBeCombinedWith(OPS_PART_1)
        println("Day 7 part 1: $solution")
    }

    override fun part2() {
        val solution = sumOfTestValuesThatCanBeCombinedWith(OPS_PART_2)
        println("Day 7 part 2: $solution")
    }

    private fun sumOfTestValuesThatCanBeCombinedWith(operations: List<(Long, Long) -> Long>): Long {
        return input.filter { (target, numbers) -> canBeCombinedWithOperations(target, numbers, operations) }
            .sumOf { it.first }
    }

    private fun canBeCombinedWithOperations(
        target: Long,
        numbers: List<Long>,
        operations: List<(Long, Long) -> Long>
    ): Boolean {
        return canBeCombinedWithOperations(numbers, 1, operations, 0, numbers[0], target)
    }

    private fun canBeCombinedWithOperations(
        numbers: List<Long>, numIdx: Int,
        operations: List<(Long, Long) -> Long>, operationIdx: Int,
        total: Long, target: Long
    ): Boolean {
        if (numIdx == numbers.size) return (total == target)

        var isPossible = false

        for (operation in operations) {
            val newTotal = operation(total, numbers[numIdx])
            isPossible =
                canBeCombinedWithOperations(numbers, numIdx + 1, operations, operationIdx + 1, newTotal, target)

            if (isPossible) break
        }

        return isPossible
    }
}
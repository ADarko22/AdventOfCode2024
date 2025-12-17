package edu.adarko22

fun main() {
    Day25().part1()
    Day25().part2()
}

class Day25 : Day {

    private val input = "day25".readResourceLines()

    override fun part1() {
        val solution = countUniqueFittingLockKeyPairs()
        println("Day 25 part 1: $solution")
    }

    override fun part2() {
        val (_, _) = parseInput()
        val solution = "todo"
        println("Day 25 part 2: $solution")
    }

    private fun countUniqueFittingLockKeyPairs(): Int {
        val (locks, keys) = parseInput()
        return keys.sumOf { key -> locks.count { lock -> lock.matches(key) } }
    }

    private data class Lock(val pinHeights: List<Int>) {
        fun matches(key: Key) = pinHeights.zip(key.pinHeights).all { (lockPin, keyPin) -> lockPin + keyPin <= 5 }
    }

    private data class Key(val pinHeights: List<Int>)

    private fun parseInput(): Pair<List<Lock>, List<Key>> {
        val locks = mutableListOf<Lock>()
        val keys = mutableListOf<Key>()

        var idx = 0
        while (idx < input.size) {
            if (input[idx] == "#####") {
                val lockPins = List(size = 5) { 0 }.toMutableList()

                for (col in 0 until 5)
                    for (row in idx + 1..idx + 5)
                        if (input[row][col] == '#') lockPins[col]++

                locks.add(Lock(lockPins))
            } else {
                val keyPins = List(size = 5) { 0 }.toMutableList()

                for (col in 0 until 5)
                    for (row in idx + 5 downTo idx + 1)
                        if (input[row][col] == '#') keyPins[col]++

                keys.add(Key(keyPins))
            }
            idx += 8
        }
        return locks to keys
    }
}
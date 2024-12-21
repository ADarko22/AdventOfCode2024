package org.example

fun main() {
    Day21().part1()
    Day21().part2()
}

class Day21 : Day {

    private val input = "day21".readResourceLines().associateWith { it.substring(0, it.length - 1).toInt() }

    override fun part1() {
        val solution = input.keys.sumOf { shortestCommandLength(it, 3) * input[it]!! }
        println("Day 21 part 1: $solution")
    }

    override fun part2() {
        val solution = input.keys.sumOf { shortestCommandLength(it, 26) * input[it]!! }
        println("Day 21 part 2: $solution")
    }

    companion object {
        private const val UP = '^'
        private const val DOWN = 'v'
        private const val LEFT = '<'
        private const val RIGHT = '>'
    }

    private val numericKeypadGraph = mapOf(
        'A' to mapOf('3' to UP, '0' to LEFT),
        '0' to mapOf('2' to UP, 'A' to RIGHT),
        '1' to mapOf('4' to UP, '2' to RIGHT),
        '2' to mapOf('5' to UP, '3' to RIGHT, '0' to DOWN, '1' to LEFT),
        '3' to mapOf('6' to UP, 'A' to DOWN, '2' to LEFT),
        '4' to mapOf('7' to UP, '5' to RIGHT, '1' to DOWN),
        '5' to mapOf('8' to UP, '6' to RIGHT, '2' to DOWN, '4' to LEFT),
        '6' to mapOf('9' to UP, '3' to DOWN, '5' to LEFT),
        '7' to mapOf('8' to RIGHT, '4' to DOWN),
        '8' to mapOf('9' to RIGHT, '5' to DOWN, '7' to LEFT),
        '9' to mapOf('6' to DOWN, '8' to LEFT)
    )

    private val directionalKeypadGraph = mapOf(
        'A' to mapOf('^' to LEFT, '>' to DOWN),
        '^' to mapOf('A' to RIGHT, 'v' to DOWN),
        '>' to mapOf('A' to UP, 'v' to LEFT),
        'v' to mapOf('^' to UP, '>' to RIGHT, '<' to LEFT),
        '<' to mapOf('v' to RIGHT)
    )

    private val numKeypad = Keypad(numericKeypadGraph)
    private val dirKeypad = Keypad(directionalKeypadGraph)

    // key = Pair of code and level, value = shortest command length
    private val cache: MutableMap<Pair<String, Int>, Long> = mutableMapOf()

    private fun shortestCommandLength(code: String, levels: Int, keypad: Keypad = numKeypad): Long =
        cache.getOrPut(code to levels) {
            if (levels == 0) code.length.toLong()
            else "A$code".zipWithNext()
                .sumOf { (key, nextKey) ->
                    keypad.directionalCommands(key, nextKey)
                        .minOf { command -> shortestCommandLength(command, levels - 1, dirKeypad) }
                }
        }

    data class Keypad(val keypadGraph: Map<Char, Map<Char, Char>>) {
        private val allDirectionalCommands = buildAllShortestPaths()

        fun directionalCommands(key: Char, nextKey: Char) = allDirectionalCommands[key]!![nextKey]!!

        private fun buildAllShortestPaths() =
            keypadGraph.keys.associateWith { fromKey ->
                keypadGraph.keys.associateWith { toKey -> dfsAllShortestDirectionalCommands(fromKey, toKey) }
            }

        private fun dfsAllShortestDirectionalCommands(fromKey: Char, toKey: Char): List<String> {
            // DFS to build all the shortest paths between two keys
            val distances = mutableMapOf(0 to mutableListOf(fromKey))
            var distance = 0
            var toVisit = mutableListOf(fromKey)
            val visited = mutableSetOf<Char>()

            while (toVisit.contains(toKey).not()) {
                val toVisitNext = mutableListOf<Char>()
                distance++
                toVisit.forEach { key ->
                    keypadGraph[key]!!.keys.forEach { adjacentKey ->
                        if (visited.contains(adjacentKey)) return@forEach

                        distances[distance] = distances.getOrDefault(distance, mutableListOf())
                        distances[distance]!!.add(adjacentKey)
                        toVisitNext.add(adjacentKey)
                    }
                    visited.add(key)
                }
                toVisit = toVisitNext
            }
            return constructAllDirectionalCommandsFrom(toKey, distances)
        }

        private fun constructAllDirectionalCommandsFrom(toKey: Char, distances: Map<Int, List<Char>>): List<String> {
            var shortestPaths = mutableListOf("$toKey")
            var currentDistance = distances.keys.max() - 1

            while (currentDistance >= 0) {
                shortestPaths = distances[currentDistance]!!
                    .map { previousKey ->
                        shortestPaths
                            // filter out keys that are not adjacent to the current previousKey
                            .filter { shortestPath -> keypadGraph[previousKey]!!.containsKey(shortestPath[0]) }
                            .map { shortestPath -> previousKey + shortestPath }
                    }
                    .flatten()
                    .toMutableList()
                currentDistance--
            }

            // Translate the shortest paths between two keys to the actual directional commands
            return shortestPaths
                .map { it.zipWithNext { key, nextKey -> keypadGraph[key]!![nextKey] }.joinToString("") }
                .map { "${it}A" }
        }
    }
}
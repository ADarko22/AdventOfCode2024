package edu.adarko22

fun main() {
    Day10().part1()
    Day10().part2()
}

class Day10 : Day {

    private val directions = arrayOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1))

    private val input = "day10".readResourceLines()
        .map { line -> line.toCharArray().map { it.digitToInt() }.toTypedArray() }
        .toTypedArray()

    override fun part1() {
        val solution = findAllTrailHeads().sumOf { trailHead -> reachablePeaks(input, trailHead).size }
        println("Day 10 part 1: $solution")
    }

    override fun part2() {
        val solution = findAllTrailHeads().sumOf { trailHead -> countAllDistinctHikingTrails(input, trailHead) }
        println("Day 10 part 2: $solution")
    }

    private fun findAllTrailHeads(): List<Pair<Int, Int>> {
        return input.mapIndexed { rowIdx, row ->
            row.mapIndexed { columnIdx, height -> if (height == 0) Pair(rowIdx, columnIdx) else null }
        }.flatten().filterNotNull()
    }

    private fun reachablePeaks(topographicMap: Array<Array<Int>>, position: Pair<Int, Int>): Set<Pair<Int, Int>> {
        if (topographicMap[position.first][position.second] == 9) return setOf(position)

        val currentHeight = topographicMap[position.first][position.second]
        val reachablePeaks = mutableSetOf<Pair<Int, Int>>()

        for (direction in directions) {
            val newPosition = Pair(position.first + direction.first, position.second + direction.second)

            if (isInside(topographicMap, newPosition)
                && topographicMap[newPosition.first][newPosition.second] == currentHeight + 1
            ) {
                reachablePeaks.addAll(reachablePeaks(topographicMap, newPosition))
            }
        }

        return reachablePeaks
    }

    private fun countAllDistinctHikingTrails(topographicMap: Array<Array<Int>>, position: Pair<Int, Int>): Int {
        if (topographicMap[position.first][position.second] == 9) return 1

        val currentHeight = topographicMap[position.first][position.second]
        var count = 0

        for (direction in directions) {
            val newPosition = Pair(position.first + direction.first, position.second + direction.second)

            if (isInside(topographicMap, newPosition)
                && topographicMap[newPosition.first][newPosition.second] == currentHeight + 1
            ) {
                count += countAllDistinctHikingTrails(topographicMap, newPosition)
            }
        }

        return count
    }

    private fun isInside(topographicMap: Array<Array<Int>>, position: Pair<Int, Int>): Boolean {
        return position.first >= 0 && position.first < topographicMap.size
                && position.second >= 0 && position.second < topographicMap[0].size
    }
}
package org.example

import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
    Day20().part1()
    Day20().part2()
}

class Day20 : Day {

    private val input = "day20".readResourceLines().map { it.toCharArray().toTypedArray() }.toTypedArray()
    private val minTimeToSave = 100

    override fun part1() {
        val solution = countCheatsSavingAtLeast(2)
        println("Day 20 part 1: $solution")
    }

    override fun part2() {
        val solution = countCheatsSavingAtLeast(20)
        println("Day 20 part 2: $solution")
    }

    private enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0);
    }

    private data class Position(val x: Int, val y: Int) {
        fun manhattanDistance(other: Position): Int = abs(x - other.x) + abs(y - other.y)

        fun move(direction: Direction): Position = Position(x + direction.dx, y + direction.dy)

        fun freeAdjacentPositions(grid: Array<Array<Char>>) =
            Direction.entries.map { move(it) }.filter { it.isValid(grid) }.filter { !it.isWall(grid) }.toSet()

        fun isValid(grid: Array<Array<Char>>): Boolean = y in grid.indices && x in grid[y].indices

        fun isWall(grid: Array<Array<Char>>): Boolean = grid[y][x] == '#'

        fun isStart(grid: Array<Array<Char>>): Boolean = grid[y][x] == 'S'

        fun isEnd(grid: Array<Array<Char>>): Boolean = grid[y][x] == 'E'
    }

    // start is the position before the wall and end is the position after the wall
    private data class Cheat(val start: Position, val end: Position, val picoseconds: Int)

    private fun shortestPath(start: Position, end: Position): List<Position> {
        // Dijkstra's algorithm
        val previous = mutableMapOf<Position, Position?>()
        previous[start] = null
        val distances = mutableMapOf(start to 0)
        val queue = PriorityQueue<Pair<Int, Position>>(compareBy { it.first })
        queue.add(0 to start)

        while (!queue.isEmpty()) {
            val (distance, current) = queue.poll()
            if (current == end) {
                val path = mutableListOf<Position>()
                var position: Position? = current
                while (position != null) {
                    path.addFirst(position)
                    position = previous[position]
                }
                return path
            }

            current.freeAdjacentPositions(input).forEach { neighbor ->
                val newDistance = distance + 1
                if (newDistance < distances.getOrDefault(neighbor, Int.MAX_VALUE)) {
                    previous[neighbor] = current
                    distances[neighbor] = newDistance
                    queue.add(newDistance to neighbor)
                }
            }
        }
        throw IllegalArgumentException("No path found")
    }

    private fun countCheatsSavingAtLeast(maxCheatTime: Int): Long {
        val (start, end) = startAndEndPositions()
        val shortestPath = shortestPath(start, end)

        val shortestPathPositionToIdx = shortestPath.mapIndexed { idx, position -> position to idx }.toMap()

        val cheats = allCheats(shortestPath, maxCheatTime)

        var savedTimeOccurrences = mutableMapOf<Int, Long>()
        var countCheatsSavingEnough = 0L

        for (cheat in cheats) {
            val pathTime = shortestPathPositionToIdx[cheat.end]!! - shortestPathPositionToIdx[cheat.start]!!
            val savedTime = pathTime - cheat.picoseconds
            if (savedTime >= minTimeToSave) {
                countCheatsSavingEnough++
                savedTimeOccurrences[savedTime] = savedTimeOccurrences.getOrDefault(savedTime, 0) + 1
            }
        }

        return countCheatsSavingEnough
    }

    // cheats are allowed from any position on the path to any other position on the path and can pass through walls
    // the best cheat is the one that saves the most time, that is the Manhattan distance between the points
    private fun allCheats(shortestPath: List<Position>, maxCheatTime: Int) =
        IntRange(0, shortestPath.size - minTimeToSave)
            .flatMap { startCheatIdx ->
                IntRange(startCheatIdx + minTimeToSave, shortestPath.size - 1)
                    .map { endCheatIdx ->
                        val startCheat = shortestPath[startCheatIdx]
                        val endCheat = shortestPath[endCheatIdx]
                        val minTime = startCheat.manhattanDistance(endCheat)
                        if (minTime <= maxCheatTime) Cheat(startCheat, endCheat, minTime) else null
                    }
            }.filterNotNull()

    private fun startAndEndPositions(): Pair<Position, Position> {
        val start = input.flatMapIndexed { y, row -> row.mapIndexed { x, _ -> Position(x, y) } }
            .first { it.isStart(input) }

        val end = input.flatMapIndexed { y, row -> row.mapIndexed { x, _ -> Position(x, y) } }
            .first { it.isEnd(input) }

        return start to end
    }
}
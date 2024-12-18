package org.example

import java.util.PriorityQueue

fun main() {
    Day18().part1()
    Day18().part2()
}

class Day18 : Day {

    private val input = "day18".readResourceLines()
        .map { it.split(",") }
        .map { Position(it[0].toInt(), it[1].toInt()) }
        .toList()

    override fun part1() {
        val grid = createGrid(71, input.subList(0, 1024))
        val solution = shortestPath(grid, Position(0, 0), Position(70, 70))
        println("Day 18 part 1: $solution")
    }

    override fun part2() {
        val solution = findObstacleClosingThePath(71, input)
        println("Day 18 part 2: $solution")
    }

    private enum class Directions(val dx: Int, val dy: Int) {
        UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);
    }

    private enum class AllSurroundingDirections(val dx: Int, val dy: Int) {
        UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1),
        UP_LEFT(-1, -1), UP_RIGHT(-1, 1), DOWN_LEFT(1, -1), DOWN_RIGHT(1, 1);
    }

    private data class Position(val x: Int, val y: Int) {
        companion object {
            val ORIGIN = Position(0, 0)
        }

        fun neighbors(grid: Array<Array<Char>>): List<Position> {
            return Directions.entries
                .map { Position(x + it.dx, y + it.dy) }
                .filter { it.y in grid.indices && it.x in grid[it.y].indices }
                .filter { grid[it.y][it.x] != '#' }
        }

        fun surroundingObstacles(grid: Array<Array<Char>>): List<Position> {
            return AllSurroundingDirections.entries
                .map { Position(x + it.dx, y + it.dy) }
                .filter { it.y in grid.indices && it.x in grid[it.y].indices }
                .filter { grid[it.y][it.x] == '#' }
        }
    }

    private fun createGrid(size: Int, obstacles: List<Position>): Array<Array<Char>> {
        val grid = Array(size) { Array(size) { '.' } }
        obstacles.forEach { grid[it.y][it.x] = '#' }
        return grid
    }

    private fun shortestPath(grid: Array<Array<Char>>, start: Position, end: Position): Int {
        // Dijkstra's algorithm
        val queue: PriorityQueue<Pair<Position, Int>> = PriorityQueue(compareBy { it.second })
        queue.add(Pair(start, 0))

        val visited: MutableSet<Position> = mutableSetOf()
        val distance = mutableMapOf<Position, Int>()

        while (queue.isNotEmpty()) {
            val (current, currentDistance) = queue.poll()

            if (current == end) return currentDistance
            if (visited.contains(current)) continue

            visited.add(current)
            distance[current] = currentDistance
            current.neighbors(grid)
                .filter { !visited.contains(it) }
                .forEach { queue.add(Pair(it, currentDistance + 1)) }
        }

        throw IllegalArgumentException("No path found")
    }

    private fun findObstacleClosingThePath(gridSize: Int, obstacles: List<Position>): Position {
        val findUnion = FindUnion(gridSize)

        val grid = createGrid(gridSize, emptyList())

        for (obstacle in obstacles) {
            val obstacleIdx = obstacle.y * gridSize + obstacle.x

            val surroundingObstacles = obstacle.surroundingObstacles(grid)
            surroundingObstacles.forEach {
                val neighborIdx = it.y * gridSize + it.x
                findUnion.union(obstacleIdx, neighborIdx)
            }

            // add the obstacle to the grid
            grid[obstacle.y][obstacle.x] = '#'

            if (findUnion.regions().any { cutsTheGrid(it, gridSize) }) return obstacle
        }

        throw IllegalArgumentException("No obstacle cutting the grid was found")
    }

    private fun cutsTheGrid(touchingObstacles: Set<Position>, gridSize: Int): Boolean {
        val obstaclesTouchingLeft = touchingObstacles.filter { it != Position.ORIGIN }.any { it.x == 0 }
        val obstaclesTouchingRight = touchingObstacles.filter { it != Position.ORIGIN }.any { it.x == gridSize - 1 }
        val obstaclesTouchingTop = touchingObstacles.filter { it != Position.ORIGIN }.any { it.y == 0 }
        val obstaclesTouchingBottom = touchingObstacles.filter { it != Position.ORIGIN }.any { it.y == gridSize - 1 }
        // if here is a set of touching obstacle cutting top left corner out of the grid
        return obstaclesTouchingLeft && obstaclesTouchingRight
                || obstaclesTouchingLeft && obstaclesTouchingTop
                || obstaclesTouchingTop && obstaclesTouchingBottom
    }

    private data class FindUnion(val gridSize: Int) {
        private val size = gridSize * gridSize
        private val parent = IntArray(size)
        private val rank = IntArray(size)

        init {
            for (i in 0 until size) {
                parent[i] = i
                rank[i] = 0
            }
        }

        fun find(x: Int): Int {
            if (parent[x] != x) {
                parent[x] = find(parent[x])
            }
            return parent[x]
        }

        fun union(x: Int, y: Int) {
            val xRoot = find(x)
            val yRoot = find(y)

            if (xRoot == yRoot) return

            if (rank[xRoot] < rank[yRoot]) {
                parent[xRoot] = yRoot
            } else if (rank[xRoot] > rank[yRoot]) {
                parent[yRoot] = xRoot
            } else {
                parent[yRoot] = xRoot
                rank[xRoot]++
            }
        }

        fun regions(): List<Set<Position>> = parent.indices.groupBy { find(it) }
            .map { it.value.map { index -> Position(index % gridSize, index / gridSize) }.toSet() }
            .filter { it.isNotEmpty() }
    }
}
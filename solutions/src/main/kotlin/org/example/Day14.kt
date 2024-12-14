package org.example

fun main() {
    Day14().part1()
    Day14().part2()
}

class Day14 : Day {
    private val regex = "p=(?<posX>\\d+),(?<posY>\\d+)\\sv=(?<velX>-?\\d+),(?<velY>-?\\d+)".toRegex()

    private val input = "/day14".readResourceLines()
        .map { regex.matchEntire(it)!!.destructured }
        .map { (posX, posY, velX, velY) ->
            Robot(
                Vector(posX.toInt(), posY.toInt()),
                Vector(velX.toInt(), velY.toInt())
            )
        }

    override fun part1() {
        val robotsAfterMoves = simulateRobotsAfter100Moves()
        val solution = countRobotsPerQuadrants(robotsAfterMoves)
            .map { it.toLong() }
            .reduce(Long::times)
        println("Day 14 part 1: $solution")
    }

    override fun part2() {
        val solution = simulateRobotsMovesUntilTheEasterEggAppears()
        println("Day 14 part 2: $solution")
    }

    private data class Vector(val x: Int, val y: Int) {
        companion object {
            const val MAX_X = 101
            const val MAX_Y = 103
        }

        operator fun plus(other: Vector) = Vector((MAX_X + x + other.x) % MAX_X, (MAX_Y + y + other.y) % MAX_Y)
    }

    private data class Robot(var pos: Vector, val vel: Vector) {
        fun move() {
            pos = pos.plus(vel)
        }
    }

    private data class Quadrant(val minX: Int, val minY: Int, val maxX: Int, val maxY: Int) {
        companion object {
            val Q1 = Quadrant(0, 0, Vector.MAX_X / 2, Vector.MAX_Y / 2)
            val Q2 = Quadrant(Vector.MAX_X / 2 + 1, 0, Vector.MAX_X, Vector.MAX_Y / 2)
            val Q3 = Quadrant(0, Vector.MAX_Y / 2 + 1, Vector.MAX_X / 2, Vector.MAX_Y)
            val Q4 = Quadrant(Vector.MAX_X / 2 + 1, Vector.MAX_Y / 2 + 1, Vector.MAX_X, Vector.MAX_Y)

            val quadrants = listOf(Q1, Q2, Q3, Q4)
        }

        fun contains(vector: Vector) = vector.x in minX until maxX && vector.y in minY until maxY
    }

    private fun simulateRobotsAfter100Moves(): List<Robot> {
        for (second in 1..100)
            for (robot in input)
                robot.move()
        return input
    }

    private fun countRobotsPerQuadrants(robots: List<Robot>): List<Int> {
        return Quadrant.quadrants.map { q -> robots.count { q.contains(it.pos) } }
    }

    private fun simulateRobotsMovesUntilTheEasterEggAppears(): Long {
        var seconds = 0L

        // Easter Egg appears when no robot is overlapping
        while (!input.groupBy { it.pos }.none { it.value.size > 1 }) {
            for (robot in input)
                robot.move()
            seconds++
        }

        printRobots()

        return seconds
    }

    private fun printRobots() {
        val grid = Array(Vector.MAX_Y) { Array(Vector.MAX_X) { '.' } }
        input.forEach { grid[it.pos.y][it.pos.x] = '#' }
        grid.forEach { println(it.joinToString("")) }
    }
}
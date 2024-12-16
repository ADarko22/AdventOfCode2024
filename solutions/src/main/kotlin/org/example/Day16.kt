package org.example

import java.util.PriorityQueue

fun main() {
    Day16().part1()
    Day16().part2()
}

class Day16 : Day {

    private val input = "day16".readResourceLines()
        .map { it.toCharArray().toTypedArray() }
        .toTypedArray()

    override fun part1() {
        val start = Position(findStartCoordinate(), Direction.EAST)
        val solution = findMinScorePathFromStartToEnd(start)
        println("Day 16 part 1: $solution")
    }

    override fun part2() {
        val start = Position(findStartCoordinate(), Direction.EAST)
        val minScore = findMinScorePathFromStartToEnd(start)
        val solution = findAllPathsWithScore(start, minScore)
            .flatMap { it.getPath() }
            .distinct()
            .size
        println("Day 16 part 2: $solution")
    }

    private fun findMinScorePathFromStartToEnd(start: Position): Long {
        // Djiikstra's algorithm
        val visited = mutableSetOf<Position>()
        val queue = PriorityQueue<Position> { p1, p2 -> p1.score.compareTo(p2.score) }
        queue.add(Position.copyOf(start))

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            if (input[current.coordinate.y][current.coordinate.x] == 'E')
                return current.score

            if (visited.contains(current)) continue
            visited.add(current)

            val left = Position.copyOf(current).apply { turnLeft() }
            queue.add(left)

            val right = Position.copyOf(current).apply { turnRight() }
            queue.add(right)

            val forward = Position.copyOf(current).apply { moveForward() }
            // Check if the next position is a wall
            if (input[forward.coordinate.y][forward.coordinate.x] != '#') queue.add(forward)
        }
        throw IllegalArgumentException("No path found")
    }

    private fun findAllPathsWithScore(start: Position, score: Long): List<Position> {
        val visited = mutableSetOf<Position>()
        val paths = mutableListOf<Position>()

        val queue = PriorityQueue<Position> { p1, p2 -> p1.score.compareTo(p2.score) }
        queue.add(Position.copyOf(start))

        while (queue.isNotEmpty()) {
            val current = queue.poll()

            if (current.score > score) break

            if (input[current.coordinate.y][current.coordinate.x] == 'E') {
                paths.add(current)
                continue
            }

            visited.add(current)

            // Avoid rotations
            val left = Position.copyOf(current).apply { turnLeft() }
            if (!visited.contains(left)) queue.add(left)
            val right = Position.copyOf(current).apply { turnRight() }
            if (!visited.contains(right)) queue.add(right)

            val forward = Position.copyOf(current).apply { moveForward() }
            // Check if the next position is a wall
            if (input[forward.coordinate.y][forward.coordinate.x] != '#') queue.add(forward)
        }

        return paths
    }

    private fun findStartCoordinate(): Coordinate {
        input.forEachIndexed { y, row -> row.forEachIndexed { x, cell -> if (cell == 'S') return Coordinate(x, y) } }
        throw IllegalArgumentException("Start coordinate not found")
    }

    private data class Position(var coordinate: Coordinate, var direction: Direction) {
        var score = 0L
        var previous: Position? = null

        companion object {
            fun copyOf(position: Position): Position {
                val copy = Position(Coordinate(position.coordinate.x, position.coordinate.y), position.direction)
                copy.score = position.score
                copy.previous = position
                return copy
            }
        }

        fun moveForward() {
            coordinate.x += direction.x
            coordinate.y += direction.y
            score += 1
        }

        fun turnLeft() {
            direction = direction.turnLeft()
            score += 1000L
        }

        fun turnRight() {
            direction = direction.turnRight()
            score += 1000L
        }

        fun getPath(): List<Coordinate> {
            val path = mutableListOf(this.coordinate)
            var current = this
            while (current.previous != null) {
                if (path.last() != current.previous!!.coordinate) {
                    path.add(current.previous!!.coordinate)
                }
                current = current.previous!!
            }
            return path.reversed()
        }
    }

    private data class Coordinate(var x: Int, var y: Int)

    private enum class Direction(val x: Int, val y: Int) {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        fun turnLeft(): Direction {
            return when (this) {
                NORTH -> WEST
                EAST -> NORTH
                SOUTH -> EAST
                WEST -> SOUTH
            }
        }

        fun turnRight(): Direction {
            return when (this) {
                NORTH -> EAST
                EAST -> SOUTH
                SOUTH -> WEST
                WEST -> NORTH
            }
        }
    }
}
package org.example

fun main() {
    Day6().part1()
    Day6().part2()
}

class Day6 : Day {

    private val input = "day6".readResourceLines().map { it.toCharArray().toTypedArray() }.toTypedArray()

    override fun part1() {
        val laboratoryGrid = getLaboratoryGrid()
        val start = getStartCoordinates(laboratoryGrid)
        val solution = patrolledCoordinates(laboratoryGrid, start).size
        println("Day 6 part 1: $solution")
    }

    override fun part2() {
        val laboratoryGrid = getLaboratoryGrid()
        val start = getStartCoordinates(laboratoryGrid)
        val patrolCoordinates = patrolledCoordinates(laboratoryGrid, start)
        val solution = patrolCoordinates.count { createsLoopAsObstacle(laboratoryGrid, start, it) }
        println("Day 6 part 2: $solution")
    }

    private data class Coordinate(val row: Int, val col: Int) {
        fun isOutOfBounds(laboratoryGrid: Array<Array<Char>>) =
            row < 0 || col < 0 || row >= laboratoryGrid.size || col >= laboratoryGrid[0].size

        fun isObstacle(laboratoryGrid: Array<Array<Char>>) = laboratoryGrid[row][col] == '#'

        fun stepOn(direction: Direction) = Coordinate(row + direction.row, col + direction.col)
    }

    private enum class Direction(val row: Int, val col: Int) {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);

        fun turnRight() = when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    private fun getLaboratoryGrid() = input.map { it.toCharArray().toTypedArray() }.toTypedArray()

    private fun getStartCoordinates(laboratoryGrid: Array<Array<Char>>): Coordinate {
        laboratoryGrid.forEachIndexed { x, row ->
            row.forEachIndexed { y, char ->
                if (char == '^') {
                    return Coordinate(x, y)
                }
            }
        }
        throw IllegalArgumentException("No start coordinates found")
    }

    private fun patrolledCoordinates(laboratoryGrid: Array<Array<Char>>, start: Coordinate): Set<Coordinate> {
        val visited = mutableSetOf<Coordinate>()
        var nextCoordinate = start
        var currentDirection = Direction.UP

        while (true) {
            visited.add(nextCoordinate)
            val candidateNextCoordinate = nextCoordinate.stepOn(currentDirection)

            if (candidateNextCoordinate.isOutOfBounds(laboratoryGrid)) {
                break
            } else if (candidateNextCoordinate.isObstacle(laboratoryGrid)) {
                currentDirection = currentDirection.turnRight()
            } else {
                nextCoordinate = candidateNextCoordinate
            }
        }

        return visited
    }

    private fun createsLoopAsObstacle(laboratoryGrid: Array<Array<Char>>, start: Coordinate, obstacle: Coordinate): Boolean {
        if (obstacle == start) return false
        laboratoryGrid[obstacle.row][obstacle.col] = '#'
        val isLoop = visitForLoop(laboratoryGrid, start)
        laboratoryGrid[obstacle.row][obstacle.col] = '.'
        return isLoop
    }

    private fun visitForLoop(laboratoryGrid: Array<Array<Char>>, start: Coordinate): Boolean {
        val visited = mutableSetOf<Pair<Coordinate, Direction>>()
        var nextCoordinate = start
        var currentDirection = Direction.UP

        while (true) {
            visited.add(nextCoordinate to currentDirection)
            val candidateNextCoordinate = nextCoordinate.stepOn(currentDirection)

            if (candidateNextCoordinate.isOutOfBounds(laboratoryGrid)) {
                return false
            } else if (candidateNextCoordinate.isObstacle(laboratoryGrid)) {
                currentDirection = currentDirection.turnRight()
            } else {
                nextCoordinate = candidateNextCoordinate
            }

            if (visited.contains(nextCoordinate to currentDirection)) {
                return true
            }
        }
    }
}
package org.example

import java.util.concurrent.TimeUnit

fun main() {
    Day15().part1()
    Day15().part2()
}

class Day15 : Day {

    private val input = "day15".readResourceLines()

    override fun part1() {
        val (warehouse, movements) = parseInput(doubleShapes = false)
        warehouse.moveRobotInVisualMode(movements)
        val solution = warehouse.getBoxPositions().sumOf { it.toGoodsPositioningSystemLocation() }
        println("Day 15 part 1: $solution")
    }

    override fun part2() {
        val (bigWarehouse, movements) = parseInput(doubleShapes = true)
        bigWarehouse.moveRobotInVisualMode(movements)
        val solution = bigWarehouse.getBoxPositions().sumOf { it.toGoodsPositioningSystemLocation() }
        println("Day 15 part 2: $solution")
    }

    private enum class Movement(val value: Char, val row: Int, val col: Int) {
        NONE('.', 0, 0),
        UP('^', -1, 0),
        DOWN('v', 1, 0),
        RIGHT('>', 0, 1),
        LEFT('<', 0, -1);

        companion object {
            fun fromValue(value: Char) =
                entries.find { it.value == value } ?: throw IllegalArgumentException("Invalid movement value: $value")
        }

        fun isHorizontal() = this == RIGHT || this == LEFT
    }

    private data class Position(val row: Int, val col: Int) {
        fun move(movement: Movement) = Position(row + movement.row, col + movement.col)
        fun toGoodsPositioningSystemLocation(): Long = 100L * row + col
    }

    private data class Warehouse(val grid: Array<Array<Char>>) {
        val rows = grid.size
        val cols = grid[0].size
        var robotPosition: Position = robotPosition()

        private fun robotPosition(): Position {
            for (row in 0 until rows)
                for (col in 0 until cols)
                    if (grid[row][col] == '@') return Position(row, col)
            throw IllegalStateException("Robot not found in warehouse")
        }

        fun moveRobot(movements: List<Movement>) = movements.forEach { moveRobot(it) }

        private fun moveRobot(movement: Movement) {
            val newRobotPosition = robotPosition.move(movement)

            val boxesMoved = isAnyBox(newRobotPosition) && tryMoveBox(newRobotPosition, movement)

            if (isFreeSpace(newRobotPosition) || boxesMoved) {
                grid[robotPosition.row][robotPosition.col] = '.'
                grid[newRobotPosition.row][newRobotPosition.col] = '@'
                robotPosition = newRobotPosition
            }
        }

        private fun tryMoveBox(boxPosition: Position, movement: Movement) =
            if (movement.isHorizontal()) tryMoveBoxesHorizontally(boxPosition, movement)
            else tryMoveBoxesVertically(boxPosition, movement)

        private fun tryMoveBoxesHorizontally(boxPosition: Position, movement: Movement): Boolean {
            var lastBoxPosition = adjustPositionForBigBox(boxPosition)

            while (isAnyBox(lastBoxPosition)) {
                val newBoxPosition = lastBoxPosition.move(movement)

                if (isFreeSpace(newBoxPosition)) {
                    moveBoxesHorizontally(movement, boxPosition.col, newBoxPosition.col, lastBoxPosition.row)
                    return true
                }

                lastBoxPosition = newBoxPosition
            }
            return false
        }

        private fun moveBoxesHorizontally(movement: Movement, startCol: Int, finalCol: Int, row: Int) =
            when (movement) {
                Movement.RIGHT -> for (col in finalCol downTo startCol) grid[row][col] = grid[row][col - 1]
                Movement.LEFT -> for (col in finalCol until startCol) grid[row][col] = grid[row][col + 1]
                else -> throw IllegalArgumentException("Invalid movement for horizontal box movement")
            }

        private fun tryMoveBoxesVertically(boxPosition: Position, movement: Movement): Boolean {
            val boxStartPosition = adjustPositionForBigBox(boxPosition)

            // DFS to find all boxes touched
            val boxesToMovePerRow = mutableMapOf<Int, Set<Position>>()
            boxesToMovePerRow[boxStartPosition.row] = mutableSetOf(boxStartPosition)

            var currentRow = boxStartPosition.row
            var nextRow = currentRow + movement.row

            while (!areBoxesBlockedOnNextRow(nextRow, boxesToMovePerRow[currentRow]!!)) {

                if (areBoxesFreeOnNextRow(nextRow, boxesToMovePerRow[currentRow]!!)) {
                    moveBoxesVertically(
                        movement, boxesToMovePerRow, isBigBox(boxStartPosition),
                        startRow = boxStartPosition.row, finalRow = nextRow
                    )
                    return true
                }

                boxesToMovePerRow[nextRow] = boxesToMovePerRow[currentRow]!!.map { it.col }
                    .flatMap { boxesToMoveVertically(nextRow, it) }
                    .toSet()
                currentRow = nextRow
                nextRow = currentRow + movement.row
            }

            return false
        }

        private fun boxesToMoveVertically(nextRow: Int, it: Int): List<Position> {
            val nextRowBoxes = mutableListOf<Position>()
            if (grid[nextRow][it] == 'O') nextRowBoxes.add(Position(nextRow, it))
            if (grid[nextRow][it] == '[') nextRowBoxes.add(Position(nextRow, it))
            if (grid[nextRow][it] == ']') nextRowBoxes.add(Position(nextRow, it - 1))
            if (grid[nextRow][it + 1] == '[') nextRowBoxes.add(Position(nextRow, it + 1))
            return nextRowBoxes
        }

        private fun moveBoxesVertically(
            movement: Movement, boxesToMovePerRow: MutableMap<Int, Set<Position>>, isBigBox: Boolean,
            startRow: Int, finalRow: Int
        ) {
            val rows = if (movement == Movement.DOWN) finalRow downTo startRow + 1 else finalRow until startRow

            for (row in rows) {
                val previousRow = row - movement.row

                for (boxPosition in boxesToMovePerRow[previousRow]!!) {
                    grid[row][boxPosition.col] = grid[previousRow][boxPosition.col]
                    grid[previousRow][boxPosition.col] = '.'

                    if (isBigBox) {
                        grid[row][boxPosition.col + 1] = grid[previousRow][boxPosition.col + 1]
                        grid[previousRow][boxPosition.col + 1] = '.'
                    }
                }
            }
        }

        private fun areBoxesBlockedOnNextRow(row: Int, boxPositions: Set<Position>) =
            boxPositions.any { grid[row][it.col] == '#' || (isBigBox(it) && grid[row][it.col + 1] == '#') }

        private fun areBoxesFreeOnNextRow(row: Int, boxPositions: Set<Position>) =
            boxPositions.all { grid[row][it.col] == '.' && (!isBigBox(it) || grid[row][it.col + 1] == '.') }

        private fun isFreeSpace(position: Position) = grid[position.row][position.col] == '.'

        private fun isAnyBox(position: Position) = isBox(position) || isBigBox(position)

        private fun isBox(position: Position) = grid[position.row][position.col] == 'O'

        private fun isBigBox(position: Position) =
            grid[position.row][position.col] == '[' || grid[position.row][position.col] == ']'

        // if the box is a BigBox, the position is adjusted to the left side
        private fun adjustPositionForBigBox(boxPosition: Position) =
            if (grid[boxPosition.row][boxPosition.col] == ']') boxPosition.move(Movement.LEFT)
            else boxPosition

        fun getBoxPositions(): List<Position> {
            val boxPositions = mutableListOf<Position>()
            for (row in 0 until rows)
                for (col in 0 until cols)
                // The BigBoxes are identified by '['
                    if (grid[row][col] == '[' || grid[row][col] == 'O')
                        boxPositions.add(Position(row, col))
            return boxPositions
        }

        // INTERACTIVE VERSION
        fun moveRobotInVisualMode(movements: List<Movement>) {
            printWarehouse(Movement.NONE, 0.0, clearScreen = false)
            movements.forEachIndexed { index, movement ->
                moveRobot(movement)
                val progress = ((index + 1) * 100.0) / movements.size
                printWarehouse(movement, progress)

            }
        }

        fun printWarehouse(movement: Movement, progress: Double, clearScreen: Boolean = true) {
            val reset = "\u001B[0m"
            val red = "\u001B[31m"
            val green = "\u001B[32m"
            val blue = "\u001B[34m"

            val formattedProgress = "%.2f".format(progress)
            val progressAsString = "${red}Movement: ${movement.value}, $formattedProgress%${reset}"

            val gridString = grid.joinToString("\n") { row ->
                row.map { cell ->
                    when (cell) {
                        '@' -> "${red}${cell}${reset}"
                        '.' -> "${green}${cell}${reset}"
                        'O' -> "${blue}${cell}${reset}"
                        '[' -> "${blue}${cell}${reset}"
                        ']' -> "${blue}${cell}${reset}"
                        else -> cell
                    }
                }.joinToString("") // Use space to separate the elements in a row
            }

            // Move cursor up to the top of the grid (rows + 2 to include the progress line)
            val cursorUp = if (clearScreen) "\u001B[${rows + 1}A" else ""
            print("\r$cursorUp$progressAsString\n$gridString\n")
            TimeUnit.MILLISECONDS.sleep(1000)
        }
    }

    private fun parseInput(doubleShapes: Boolean): Pair<Warehouse, List<Movement>> {
        return input.indexOf("")
            .let { splitInputIdx ->
                val warehouseGrid = input.subList(0, splitInputIdx)
                    .map {
                        if (doubleShapes)
                            it.replace("O", "[]")
                                .replace("#", "##")
                                .replace(".", "..")
                                .replace("@", "@.")
                        else it
                    }.map {
                        it.toCharArray()
                            .toTypedArray()
                    }
                    .toTypedArray()
                val movements = input.subList(splitInputIdx + 1, input.size)
                    .joinToString("")
                    .map { Movement.fromValue(it) }
                Pair(Warehouse(warehouseGrid), movements)
            }
    }
}
package org.example

fun main() {
    Day15().part1()
    Day15().part2()
}

class Day15 : Day {

    private val input = "day15".readResourceLines()

    override fun part1() {
        val (warehouse, movements) = parseInput()
        warehouse.moveRobot(movements)
        val solution = warehouse.getBoxPositions().sumOf { it.toGoodsPositioningSystemLocation() }
        println("Day 15 part 1: $solution")
    }

    override fun part2() {
        val (bigWarehouse, movements) = parseInput2()
        bigWarehouse.moveRobot(movements)
        val solution = bigWarehouse.getBoxPositions().sumOf { it.toGoodsPositioningSystemLocation() }
        println("Day 15 part 2: $solution")
    }

    private data class Warehouse(val grid: Array<Array<Char>>) {
        val rows = grid.size
        val cols = grid[0].size
        var robotPosition: Position = robotPosition()

        private fun robotPosition(): Position {
            for (row in 0 until rows)
                for (col in 0 until cols)
                    if (grid[row][col] == '@')
                        return Position(row, col)
            throw IllegalStateException("Robot not found in warehouse")
        }

        fun moveRobot(movements: List<Movement>) = movements.forEach { moveRobot(it) }


        private fun moveRobot(movement: Movement) {
            val newRobotPosition = robotPosition.move(movement)

            when {
                isFreeSpace(newRobotPosition) -> {
                    grid[robotPosition.row][robotPosition.col] = '.'
                    grid[newRobotPosition.row][newRobotPosition.col] = '@'
                    robotPosition = newRobotPosition
                }

                isBox(newRobotPosition) -> {
                    // try to move the robot and all the boxes
                    // if there is a free space in front of the last consecutively placed box
                    var lastBoxPosition = newRobotPosition
                    while (isBox(lastBoxPosition)) {
                        val newBoxPosition = lastBoxPosition.move(movement)

                        if (isFreeSpace(newBoxPosition)) {
                            grid[newBoxPosition.row][newBoxPosition.col] = 'O'
                            grid[robotPosition.row][robotPosition.col] = '.'
                            grid[newRobotPosition.row][newRobotPosition.col] = '@'
                            robotPosition = newRobotPosition
                            break
                        }

                        lastBoxPosition = newBoxPosition
                    }
                }
            }
        }

        private fun isFreeSpace(position: Position) = grid[position.row][position.col] == '.'

        private fun isBox(position: Position) = grid[position.row][position.col] == 'O'

        fun getBoxPositions(): List<Position> {
            val boxPositions = mutableListOf<Position>()
            for (row in 0 until rows)
                for (col in 0 until cols)
                    if (grid[row][col] == 'O')
                        boxPositions.add(Position(row, col))
            return boxPositions
        }
    }

    private enum class Movement(val value: Char, val row: Int, val col: Int) {
        NORTH('^', -1, 0),
        SOUTH('v', 1, 0),
        EAST('>', 0, 1),
        WEST('<', 0, -1);

        companion object {
            fun fromValue(value: Char) =
                entries.find { it.value == value } ?: throw IllegalArgumentException("Invalid movement value: $value")
        }

        fun isHorizontal() = this == EAST || this == WEST
    }

    private data class Position(val row: Int, val col: Int) {
        fun move(movement: Movement): Position {
            return Position(row + movement.row, col + movement.col)
        }

        fun toGoodsPositioningSystemLocation(): Long = 100L * row + col
    }

    private fun parseInput(): Pair<Warehouse, List<Movement>> {
        return input.indexOf("")
            .let { splitInputIdx ->
                val warehouseGrid = input.subList(0, splitInputIdx)
                    .map { it.toCharArray().toTypedArray() }
                    .toTypedArray()
                val movements = input.subList(splitInputIdx + 1, input.size)
                    .joinToString("")
                    .map { Movement.fromValue(it) }
                Pair(Warehouse(warehouseGrid), movements)
            }
    }

    private data class BigWarehouse(val grid: Array<Array<Char>>) {
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

            if (isFreeSpace(newRobotPosition)
                || (isBox(newRobotPosition) && tryMoveBoxes(newRobotPosition, movement))
            ) {
                grid[robotPosition.row][robotPosition.col] = '.'
                grid[newRobotPosition.row][newRobotPosition.col] = '@'
                robotPosition = newRobotPosition
            }
        }

        private fun tryMoveBoxes(boxInitialPosition: Position, movement: Movement): Boolean {
            val boxInitialLeftSidePosition =
                if (grid[boxInitialPosition.row][boxInitialPosition.col] == '[') boxInitialPosition
                else boxInitialPosition.move(Movement.WEST)

            return if (movement.isHorizontal()) tryMoveBoxesHorizontally(boxInitialPosition, movement)
            else tryMoveBoxesVertically(boxInitialLeftSidePosition, movement)
        }

        private fun tryMoveBoxesHorizontally(boxInitialPosition: Position, movement: Movement): Boolean {
            var lastBoxPosition = boxInitialPosition
            while (isBox(lastBoxPosition)) {
                val newBoxPosition = lastBoxPosition.move(movement)

                if (isFreeSpace(newBoxPosition)) {
                    moveBoxesHorizontally(movement, boxInitialPosition.col, newBoxPosition.col, lastBoxPosition.row)
                    return true
                }

                lastBoxPosition = newBoxPosition
            }
            return false
        }

        private fun moveBoxesHorizontally(movement: Movement, startCol: Int, finalCol: Int, row: Int) =
            when (movement) {
                Movement.EAST -> for (col in finalCol downTo startCol) grid[row][col] = grid[row][col - 1]
                Movement.WEST -> for (col in finalCol until startCol) grid[row][col] = grid[row][col + 1]
                else -> throw IllegalArgumentException("Invalid movement for horizontal box movement")
            }

        // boxInitialPosition is the left side of the box
        private fun tryMoveBoxesVertically(boxInitialPosition: Position, movement: Movement): Boolean {
            val boxesToMovePerRow = mutableMapOf<Int, List<Position>>()
            boxesToMovePerRow[boxInitialPosition.row] = mutableListOf(boxInitialPosition)

            var currentRow = boxInitialPosition.row
            var nextRow = currentRow + movement.row

            while (!isBlockedByWallRowForBoxes(nextRow, boxesToMovePerRow[currentRow]!!)) {
                if (isFreeRowForBoxes(nextRow, boxesToMovePerRow[currentRow]!!)) {
                    moveBoxesVertically(movement, startRow = boxInitialPosition.row, nextRow, boxesToMovePerRow)
                    return true
                }

                val nextRowBoxes = boxesToMovePerRow[currentRow]!!.map { it.col }.flatMap { boxesToMove(nextRow, it) }
                boxesToMovePerRow[nextRow] = nextRowBoxes
                currentRow = nextRow
                nextRow = currentRow + movement.row
            }

            return false
        }

        private fun boxesToMove(nextRow: Int, it: Int): List<Position> {
            val nextRowBoxes = mutableListOf<Position>()
            if (grid[nextRow][it] == '[') nextRowBoxes.add(Position(nextRow, it))
            if (grid[nextRow][it] == ']') nextRowBoxes.add(Position(nextRow, it - 1))
            if (grid[nextRow][it + 1] == '[') nextRowBoxes.add(Position(nextRow, it + 1))
            return nextRowBoxes
        }

        private fun moveBoxesVertically(
            movement: Movement,
            startRow: Int,
            finalRow: Int,
            boxesToMovePerRow: MutableMap<Int, List<Position>>
        ) {
            if (movement == Movement.SOUTH) {
                for (row in finalRow downTo startRow + 1) {
                    for (col in boxesToMovePerRow[row - 1]!!.map { it.col }) {
                        grid[row][col] = grid[row - 1][col]
                        grid[row - 1][col] = '.'
                        grid[row][col + 1] = grid[row - 1][col + 1]
                        grid[row - 1][col + 1] = '.'
                    }
                }
            } else {
                for (row in finalRow until startRow) {
                    for (col in boxesToMovePerRow[row + 1]!!.map { it.col }) {
                        grid[row][col] = grid[row + 1][col]
                        grid[row + 1][col] = '.'
                        grid[row][col + 1] = grid[row + 1][col + 1]
                        grid[row + 1][col + 1] = '.'
                    }
                }
            }
        }

        private fun isBlockedByWallRowForBoxes(row: Int, boxLeftSidePositions: List<Position>) =
            boxLeftSidePositions.any { grid[row][it.col] == '#' || grid[row][it.col + 1] == '#' }

        private fun isFreeRowForBoxes(row: Int, boxLeftSidePositions: List<Position>) =
            boxLeftSidePositions.all { grid[row][it.col] == '.' && grid[row][it.col + 1] == '.' }

        private fun isFreeSpace(position: Position) = grid[position.row][position.col] == '.'

        private fun isBox(position: Position) =
            grid[position.row][position.col] == '[' || grid[position.row][position.col] == ']'

        fun getBoxPositions(): List<Position> {
            val boxPositions = mutableListOf<Position>()
            for (row in 0 until rows)
                for (col in 0 until cols)
                // identify the box position by the left side
                    if (grid[row][col] == '[')
                        boxPositions.add(Position(row, col))
            return boxPositions
        }
    }

    private fun parseInput2(): Pair<BigWarehouse, List<Movement>> {
        val (warehouse, movements) = parseInput()

        val bigWarehouseGrid = Array(warehouse.rows) { row ->
            Array(warehouse.cols * 2) { col ->
                when (warehouse.grid[row][col / 2]) {
                    '@' -> if (col % 2 == 0) '@' else '.'
                    '#' -> '#'
                    'O' -> if (col % 2 == 0) '[' else ']'
                    else -> '.'
                }
            }
        }

        return Pair(BigWarehouse(bigWarehouseGrid), movements)
    }
}
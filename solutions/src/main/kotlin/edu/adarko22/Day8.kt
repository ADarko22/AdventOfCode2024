package edu.adarko22

import org.apache.commons.math3.fraction.Fraction
import kotlin.math.abs

fun main() {
    Day8().part1()
    Day8().part2()
}

class Day8 : Day {

    private val input = "day8".readResourceLines()

    override fun part1() {
        val solution = findAntiNodes().size
        println("Day 8 part 1: $solution")
    }

    override fun part2() {
        val solution = findAntiNodes(disableAntiNodesFilter = true).size
        println("Day 8 part 2: $solution")
    }

    data class Coordinate(val row: Int, val col: Int) {
        fun distanceTo(other: Coordinate) = abs(row - other.row) + abs(col - other.col)

        fun isInBounds(rows: Int, cols: Int) = row in 0 until rows && col in 0 until cols
    }

    data class Line(val slope: Fraction, val intercept: Fraction) {
        companion object {
            fun ofCoordinates(c1: Coordinate, c2: Coordinate): Line {
                if (c1.col == c2.col) {
                    throw IllegalArgumentException("Vertical line not supported")
                }
                val slope = Fraction(c2.col - c1.col, (c2.row - c1.row))
                val intercept = Fraction(c1.col).subtract(slope.multiply((c1.row)))
                return Line(slope, intercept)
            }
        }

        fun getColumn(row: Int): Double = slope.multiply(row).add(intercept).toDouble()
    }

    private fun Double.isInteger() = this % 1 == 0.0

    private fun getGridBoundaries(): Pair<Int, Int> = Pair(input.size, input[0].length)

    private fun getAntennasCoordinates(): Map<Char, List<Coordinate>> {
        val antennas = mutableMapOf<Char, MutableList<Coordinate>>()

        for (row in input.indices) {
            for (col in input[row].indices) {
                if (input[row][col] != '.') {
                    antennas.computeIfAbsent(input[row][col]) { mutableListOf() }.add(Coordinate(row, col))
                }
            }
        }

        return antennas
    }

    private fun findAntiNodes(disableAntiNodesFilter: Boolean = false): Set<Coordinate> {
        val (rows, cols) = getGridBoundaries()
        val antennasByType = getAntennasCoordinates()
        // distinguish antiNodes for different antenna type
        val antiNodesByType = mutableMapOf<Char, MutableSet<Coordinate>>()

        for ((type, coordinates) in antennasByType) {
            val antiNodes = mutableSetOf<Coordinate>()

            for (i in coordinates.indices) {
                for (j in i + 1 until coordinates.size) {
                    // assuming no vertical lines!
                    val line = Line.ofCoordinates(coordinates[i], coordinates[j])
                    val candidateAntiNodes = findCoordinatesInLine(line, rows, cols)

                    if (disableAntiNodesFilter) {
                        antiNodes.addAll(candidateAntiNodes)
                    } else {
                        val validAntiNodes = candidateAntiNodes
                            .filter { coordinate ->
                                val distance1 = coordinate.distanceTo(coordinates[i])
                                val distance2 = coordinate.distanceTo(coordinates[j])
                                return@filter (distance1 == 2 * distance2 || 2 * distance1 == distance2)
                            }

                        antiNodes.addAll(validAntiNodes)
                    }
                }
            }

            antiNodesByType[type] = antiNodes
        }

        return antiNodesByType.values.flatten().toSet()
    }

    private fun findCoordinatesInLine(line: Line, rows: Int, cols: Int): List<Coordinate> {
        val coordinates = mutableListOf<Coordinate>()

        for (row in 0 until rows) {
            val col = line.getColumn(row)
            val coordinate = Coordinate(row, col.toInt())

            if (col.isInteger() && coordinate.isInBounds(rows, cols)) {
                coordinates.add(coordinate)
            }
        }

        return coordinates
    }
}
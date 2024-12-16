package org.example

import kotlin.math.abs

fun main() {
    Day12().part1()
    Day12().part2()
}

class Day12 : Day {

    private val input = "day12".readResourceLines().map { it.toCharArray().toTypedArray() }.toTypedArray()

    override fun part1() {
        val solution = findAllPlantRegions().sumOf { it.area() * it.perimeter(input) }
        println("Day 12 part 1: $solution")
    }

    override fun part2() {
        val solution = findAllPlantRegions().sumOf { it.area() * it.sides() }
        println("Day 12 part 2: $solution")
    }


    private enum class Direction(val row: Int, val col: Int) {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1)
    }

    private data class GardenCoordinate(
        val row: Int,
        val col: Int,
    ) {
        companion object {
            fun fromIndex(index: Int, gardenGrid: Array<Array<Char>>): GardenCoordinate =
                GardenCoordinate(index / gardenGrid[0].size, index % gardenGrid[0].size)
        }

        fun index(gardenGrid: Array<Array<Char>>): Int = row * gardenGrid[0].size + col

        fun getNeighbors(): List<GardenCoordinate> =
            Direction.entries.map { GardenCoordinate(row + it.row, col + it.col) }

        fun neighborsWithSameTypeWithinGardenGrid(gardenGrid: Array<Array<Char>>): List<GardenCoordinate> =
            getNeighbors()
                .filter { it.row in gardenGrid.indices && it.col in gardenGrid[it.row].indices }
                .filter { gardenGrid[it.row][it.col] == gardenGrid[row][col] }

        fun countPerimeterSides(gardenGrid: Array<Array<Char>>): Int =
            getNeighbors()
                .filter {
                    it.row !in gardenGrid.indices
                            || it.col !in gardenGrid[it.row].indices
                            || gardenGrid[it.row][it.col] != gardenGrid[row][col]
                }.size
    }

    private data class PlantRegion(val plants: Set<GardenCoordinate>) {
        fun area(): Long = plants.size.toLong()
        fun perimeter(gardenGrid: Array<Array<Char>>): Long =
            plants.sumOf { it.countPerimeterSides(gardenGrid) }.toLong()

        fun sides(): Long {
            val perimeterCoordinatesByFacingDirection = plantsByFacingEdgeDirection(plants)

            var count = 0L

            for ((direction, coordinates) in perimeterCoordinatesByFacingDirection) {
                if (direction == Direction.UP || direction == Direction.DOWN) {
                    count += coordinates.groupBy { it.row }
                        .map {
                            1L + it.value.zipWithNext()
                                // Count the number of gaps between two consecutive coordinates
                                .count { (coordinate, nextCoordinate) -> abs(coordinate.col - nextCoordinate.col) > 1 }
                        }
                        .sum()
                } else {
                    count += coordinates.groupBy { it.col }
                        .map {
                            1L + it.value.zipWithNext()
                                // Count the number of gaps between two consecutive coordinates
                                .count { (coordinate, nextCoordinate) -> abs(coordinate.row - nextCoordinate.row) > 1 }
                        }
                        .sum()
                }
            }

            return count
        }

        private fun plantsByFacingEdgeDirection(gardenCoordinates: Set<GardenCoordinate>): Map<Direction, List<GardenCoordinate>> {
            val gardenCoordinatesByFacingDirection: MutableMap<Direction, List<GardenCoordinate>> = mutableMapOf()

            for (direction in Direction.entries) {
                val coordinates = gardenCoordinates.filter {
                    val neighbor = GardenCoordinate(it.row + direction.row, it.col + direction.col)
                    !gardenCoordinates.contains(neighbor)
                }.toList()

                gardenCoordinatesByFacingDirection[direction] = coordinates
            }

            return gardenCoordinatesByFacingDirection
        }
    }

    private fun findAllPlantRegions(): List<PlantRegion> {
        val findUnion = FindUnion(input)

        for (row in input.indices) {
            for (col in input[row].indices) {
                val current = GardenCoordinate(row, col)
                val currentIdx = current.index(input)

                for (neighbor in current.neighborsWithSameTypeWithinGardenGrid(input)) {
                    val neighborIdx = neighbor.index(input)
                    findUnion.union(currentIdx, neighborIdx)
                }
            }
        }

        return findUnion.parent.indices
            .groupBy { findUnion.find(it) }
            .values
            .map { region -> region.map { GardenCoordinate.fromIndex(it, input) }.toSet() }
            .map { PlantRegion(it) }
    }

    private class FindUnion(grid: Array<Array<Char>>) {
        private val size = grid.size * grid[0].size
        private val rank = IntArray(size)
        val parent = IntArray(size) { it }

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
    }
}
package org.example

import java.util.stream.Collectors

fun main() {
    Day13().part1()
    Day13().part2()
}

class Day13 : Day {

    private val input = "/day13".readResourceLines()

    override fun part1() {
        val clawMachines = ClawMachine.fromInput(input)
        val solution = clawMachines.mapNotNull { playClawMachine(it) }.sum()
        println("Day 13 part 1: $solution")
    }

    override fun part2() {
        val prizePositionModifier = 10000000000000
        val clawMachines = ClawMachine.fromInput(input).map {
            ClawMachine(
                it.AButtonMove,
                it.BButtonMove,
                Coordinate(it.prize.x + prizePositionModifier, it.prize.y + prizePositionModifier)
            )
        }
        val solution = clawMachines.mapNotNull { playClawMachine(it) }.sum()
        println("Day 13 part 2: $solution")
    }

    private data class Coordinate(val x: Long, val y: Long)

    private data class ClawMachine(val AButtonMove: Coordinate, val BButtonMove: Coordinate, val prize: Coordinate) {
        companion object {
            private val regex =
                "Button A: X\\+(?<AX>\\d+), Y\\+(?<AY>\\d+)\\nButton B: X\\+(?<BX>\\d+), Y\\+(?<BY>\\d+)\\nPrize: X=(?<PX>\\d+), Y=(?<PY>\\d+)".trimIndent()
                    .trimIndent()
                    .toRegex()

            fun fromInput(input: List<String>): List<ClawMachine> {
                return input.stream().collect(Collectors.joining("\n"))
                    .split("\n\n").map { regex.find(it)!! }
                    .map {
                        ClawMachine(
                            Coordinate(it.groups["AX"]!!.value.toLong(), it.groups["AY"]!!.value.toLong()),
                            Coordinate(it.groups["BX"]!!.value.toLong(), it.groups["BY"]!!.value.toLong()),
                            Coordinate(it.groups["PX"]!!.value.toLong(), it.groups["PY"]!!.value.toLong())
                        )
                    }
                    .toList()
            }
        }
    }

    private fun playClawMachine(clawMachine: ClawMachine): Long? {
        val (x0, y0) = clawMachine.AButtonMove
        val (x1, y1) = clawMachine.BButtonMove
        val (x2, y2) = clawMachine.prize

        // resolve the equation system:
        // a * x0 + b * y0 = x2
        // a * x1 + b * y1 = y2
        val a = (y2 * x1 - x2 * y1) / (x1 * y0 - x0 * y1)
        val b = (x2 - a * x0) / x1

        if (a > 0 && b > 0 && a * x0 + b * x1 == x2 && a * y0 + b * y1 == y2) {
            return 3 * a + b
        }

        return null
    }
}
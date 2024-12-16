package org.example

fun main() {
    Day2().part1()
    Day2().part2()
}

class Day2 : Day {

    private val input = "day2".readResourceLines()

    override fun part1() {
        val solution = input.stream()
            .map { it.split(Regex("\\s+")) }
            .map { it.stream().map { it.toInt() }.toList() }
            .filter { it.isMonotonicWithStepBetween1And3() }
            .count()
        println("Day 2 part 1: $solution")
    }

    override fun part2() {
        val solution = input.stream()
            .map { it.split(Regex("\\s+")) }
            .map { it.stream().map { it.toInt() }.toList() }
            .filter { isMonotonicWithStepBetween1And3WithTolerance(it) }
            .count()
        println("Day 2 part 2: $solution")
    }
}

private fun List<Int>.isMonotonicWithStepBetween1And3(): Boolean {
    // check that it is either increasing or decreasing with a step between minStep and maxStep
    return this.windowed(2).all { it[1] - it[0] in 1..3 }
            || this.windowed(2).all { it[0] - it[1] in 1..3 }
}


private fun isMonotonicWithStepBetween1And3WithTolerance(list: List<Int>): Boolean {
    val tmpList = list.toMutableList()
    for (i in list.indices) {
        val tmpVal = tmpList.removeAt(i);

        if (tmpList.isMonotonicWithStepBetween1And3()) {
            return true;
        }

        tmpList.add(i, tmpVal);
    }
    return false;
}

package org.example

import java.util.stream.Collectors
import kotlin.math.abs

fun main() {
    Day1().part1()
    Day1().part2()
}

class Day1 : Day {

    private val input = "/day1".readResourceLines()

    override fun part1() {
        val lists = extractListsFromInput()
        val list1: List<Long> = lists.first.sorted()
        val list2: List<Long> = lists.second.sorted()
        val solution = list1.zip(list2).sumOf { abs(it.first - it.second) }
        println("Day 1 part 1: $solution")
    }

    override fun part2() {
        val lists = extractListsFromInput()
        val list1: List<Long> = lists.first
        val occurrences2: Map<Long, Long> = lists.second
            .stream()
            .collect(Collectors.groupingBy({ it }, Collectors.counting()))
        val solution = list1.sumOf { it * (occurrences2.getOrDefault(it, 0)) }
        println("Day 1 part 2: $solution")
    }

    fun extractListsFromInput(): Pair<List<Long>, List<Long>> {
        val list1 = mutableListOf<Long>()
        val list2 = mutableListOf<Long>()
        input.stream()
            .map { it.split(Regex("\\s+")) }
            .forEach { it ->
                list1.add(it[0].toLong())
                list2.add(it[1].toLong())
            }
        return Pair(list1, list2)
    }
}
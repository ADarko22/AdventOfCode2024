package org.example

import kotlin.jvm.optionals.getOrNull

fun main() {
    Day5().part1()
    Day5().part2()
}

class Day5 : Day {

    private val input = "/day5".readResourceLines()

    override fun part1() {
        val (orderingRules, updatePages) = splitInput()
        val precedenceGraph = buildPrecedenceGraph(orderingRules)
        val solution = updatePages.stream()
            .map { commaSeparatedPages -> commaSeparatedPages.split(",").map { it.toInt() }.toList() }
            .filter { isCorrectlyOrderedUpdate(it, precedenceGraph) }
            .map { getMiddlePage(it) }
            .reduce { a, b -> a + b }
            .getOrNull()
        println("Day 5 part 1: $solution")
    }

    override fun part2() {
        val (orderingRules, updatePages) = splitInput()
        val precedenceGraph = buildPrecedenceGraph(orderingRules)
        val solution = updatePages.stream()
            .map { commaSeparatedPages -> commaSeparatedPages.split(",").map { it.toInt() }.toList() }
            .filter { !isCorrectlyOrderedUpdate(it, precedenceGraph) }
            .map { it.sortedWith { page1, page2 -> pageComparator(page1, page2, precedenceGraph) } }
            .map { getMiddlePage(it) }
            .reduce { a, b -> a + b }
            .getOrNull()
        println("Day 5 part 2: $solution")
    }

    private fun splitInput(): Pair<List<String>, List<String>> {
        val splitIndex = input.indexOf("")
        return input.subList(0, splitIndex) to input.subList(splitIndex + 1, input.size)
    }

    // Build a map of before -> after pages; the values are lists of pages that must come after the key page
    private fun buildPrecedenceGraph(orderingRules: List<String>): Map<Int, List<Int>> {
        val precedenceGraph = mutableMapOf<Int, List<Int>>()
        for (rule in orderingRules) {
            val (before, after) = rule.split("|").map { it.toInt() }
            precedenceGraph[before] = precedenceGraph.getOrDefault(before, emptyList()) + after
        }
        return precedenceGraph
    }

    private fun isCorrectlyOrderedUpdate(pages: List<Int>, precedenceGraph: Map<Int, List<Int>>): Boolean {
        val happenedBeforePages = mutableSetOf<Int>()
        for (page in pages) {
            val pagesThatMustComeAfter = precedenceGraph.getOrDefault(page, emptyList())
            if (pagesThatMustComeAfter.any { it in happenedBeforePages }) {
                return false
            }
            happenedBeforePages.add(page)
        }
        return true
    }

    private fun getMiddlePage(pages: List<Int>) = pages[pages.size / 2]

    private fun pageComparator(page1: Int, page2: Int, precedenceGraph: Map<Int, List<Int>>): Int {
        return when {
            page2 in precedenceGraph.getOrDefault(page1, emptyList()) -> -1
            page1 in precedenceGraph.getOrDefault(page2, emptyList()) -> 1
            else -> 0
        }
    }
}
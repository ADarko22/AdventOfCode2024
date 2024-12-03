package org.example

import java.util.stream.Stream

fun main() {
    Day4().part1()
    Day4().part2()
}

class Day4 : Day {

    private val XMAS = "XMAS"
    private val input = "/day4".readResourceLines().map { it.toCharArray().toTypedArray() }.toTypedArray()

    override fun part1() {
        val solution = searchXMASOccurrences()
        println("Day 4 part 1: $solution")
    }

    override fun part2() {
        val solution = searchXShapedMASOccurrences()
        println("Day 4 part 2: $solution")
    }

    private fun searchXMASOccurrences(): Long {
        var count = 0L;

        for (i in input.indices) {
            for (j in input[i].indices) {
                count += Stream.of(
                    isHorizontalXMAS(input, i, j, XMAS),
                    isHorizontalXMAS(input, i, j, XMAS.reversed()),
                    isVerticalXMAS(input, i, j, XMAS),
                    isVerticalXMAS(input, i, j, XMAS.reversed()),
                    isPrimaryDiagonal(input, i, j, XMAS),
                    isPrimaryDiagonal(input, i, j, XMAS.reversed()),
                    isSecondaryDiagonal(input, i, j, XMAS),
                    isSecondaryDiagonal(input, i, j, XMAS.reversed())
                ).filter { it }.count()
            }
        }

        return count;
    }

    private fun isHorizontalXMAS(input: Array<Array<Char>>, i: Int, j: Int, word: String) =
        if (j + word.length - 1 >= input[i].size) false
        else IntRange(0, word.length - 1).all { input[i][j + it] == word[it] }

    private fun isVerticalXMAS(input: Array<Array<Char>>, i: Int, j: Int, word: String) =
        if (i + word.length - 1 >= input.size) false
        else IntRange(0, word.length - 1).all { input[i + it][j] == word[it] }

    private fun isPrimaryDiagonal(input: Array<Array<Char>>, i: Int, j: Int, word: String) =
        if (i + word.length - 1 >= input.size || j + word.length - 1 >= input[i].size) false
        else IntRange(0, word.length - 1).all { input[i + it][j + it] == word[it] }

    private fun isSecondaryDiagonal(input: Array<Array<Char>>, i: Int, j: Int, word: String) =
        if (i + word.length - 1 >= input.size || j - word.length + 1 < 0) false
        else IntRange(0, word.length - 1).all { input[i + it][j - it] == word[it] }

    private fun searchXShapedMASOccurrences(): Long {
        var count = 0L;

        for (i in input.indices) {
            for (j in input[i].indices) {
                if (isXShapedMAS(input, i, j)) {
                    count++
                }
            }
        }

        return count;
    }

    private fun isXShapedMAS(input: Array<Array<Char>>, i: Int, j: Int) =
        if (i + 2 >= input.size || j + 2 >= input[i].size) false
        else (isPrimaryDiagonal(input, i, j, "MAS") || isPrimaryDiagonal(input, i, j, "MAS".reversed())) &&
                (isSecondaryDiagonal(input, i, j + 2, "MAS") || isSecondaryDiagonal(input, i, j + 2, "MAS".reversed()))

}
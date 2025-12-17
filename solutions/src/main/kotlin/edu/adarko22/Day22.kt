package edu.adarko22

import kotlin.math.max

fun main() {
    Day22().part1()
    Day22().part2()
}

class Day22 : Day {

    private val input = "day22".readResourceLines().map { it.toLong() }

    override fun part1() {
        val solution = input.sumOf { nSecrets(it, 2000).last() }
        println("Day 22 part 1: $solution")
    }

    override fun part2() {
        val solution = maximumBananas()
        println("Day 22 part 2: $solution")
    }

    private data class ChangeQuartet(val val1: Int, val val2: Int, val val3: Int, val val4: Int)

    private fun maximumBananas(): Long {
        val sellersBananas = input.map { seller -> nSecrets(seller, 2000).map { (it % 10).toInt() } }
        val sellerBananasChangesQuartetsToIdx = groupByQuartetsOfChanges(sellersBananas)

        var maxBananasSum = 0L
        val quartetChanges = allQuartetChanges()
        for (quartetChange in quartetChanges) {
            var sum = 0L
            for (sellerIdx in sellersBananas.indices) {
                val quartetsToIdx = sellerBananasChangesQuartetsToIdx[sellerIdx]
                val idx = quartetsToIdx[quartetChange] ?: continue
                sum += sellersBananas[sellerIdx][idx]
            }

            maxBananasSum = max(maxBananasSum, sum)
        }

        return maxBananasSum
    }

    private fun groupByQuartetsOfChanges(sellersBananas: List<List<Int>>): List<Map<ChangeQuartet, Int>> {
        val sellersBananasChanges = sellersBananas
            .map { bananas -> bananas.zipWithNext().map { (a, b) -> b - a } }

        val sellerBananasChangeQuartetsToIdx: MutableList<Map<ChangeQuartet, Int>> = mutableListOf()

        for (changes in sellersBananasChanges) {
            val quartetsToIdx = mutableMapOf<ChangeQuartet, Int>()

            for (idx in 3 until changes.size) {
                val quartet = ChangeQuartet(changes[idx - 3], changes[idx - 2], changes[idx - 1], changes[idx])
                if (quartet !in quartetsToIdx) {
                    quartetsToIdx[quartet] = idx + 1
                }
            }

            sellerBananasChangeQuartetsToIdx.add(quartetsToIdx)
        }

        return sellerBananasChangeQuartetsToIdx
    }

    private fun allQuartetChanges(): List<ChangeQuartet> {
        val range = (-9..9)
        return range
            .flatMap { a -> range.flatMap { b -> range.flatMap { c -> range.map { d -> ChangeQuartet(a, b, c, d) } } } }
    }

    private fun nSecrets(secret: Long, n: Int): List<Long> {
        val secrets = mutableListOf(secret)
        repeat(n) { secrets.add(nextSecret(secrets.last())) }
        return secrets
    }

    private fun nextSecret(secret: Long): Long {
        var newSecret = mix(secret, secret shl 6)
        newSecret = prune(newSecret)
        newSecret = mix(newSecret, newSecret shr 5)
        newSecret = prune(newSecret)
        newSecret = mix(newSecret, newSecret shl 11)
        newSecret = prune(newSecret)
        return newSecret
    }

    private fun mix(num1: Long, num2: Long) = num1 xor num2

    private fun prune(num: Long) = num % 16777216
}
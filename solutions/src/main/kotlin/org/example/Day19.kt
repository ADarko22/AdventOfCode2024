package org.example

fun main() {
    Day19().part1()
    Day19().part2()
}

class Day19 : Day {

    private val input = "day19".readResourceLines().joinToString("\n")

    override fun part1() {
        val (patterns, designs) = parseInput()
        val solution = designs.count { design -> isPossibleDesign(design, patterns) }
        println("Day 19 part 1: $solution")
    }

    override fun part2() {
        val (patterns, designs) = parseInput()
        val patternsTrie = Trie(patterns)
        val solution = designs.sumOf { design -> allPossibleWaysToDesign(design, patternsTrie) }
        println("Day 19 part 2: $solution")
    }

    private fun parseInput(): Pair<List<String>, List<String>> {
        val regex = "(?<patterns>( ?[wubgr]+,?)+)\\n\\n(?<designs>([wubgr]+\\n?)+)".toRegex()
        val matchResult = regex.matchEntire(input)!!
        val patterns = matchResult.groups["patterns"]!!.value.split(",").map { it.trim() }
        val designs = matchResult.groups["designs"]!!.value.split("\n").map { it.trim() }
        return Pair(patterns, designs)
    }

    private fun isPossibleDesign(design: String, patterns: List<String>): Boolean {
        var startIndices = setOf(0)

        while (startIndices.isNotEmpty()) {
            if (startIndices.contains(design.length)) break
            startIndices = startIndices
                .flatMap { startIdx ->
                    patterns.filter { pattern -> design.startsWith(pattern, startIdx) }
                        .filter { pattern -> (startIdx + pattern.length) <= design.length }
                        .map { pattern -> startIdx + pattern.length }
                }.toSet()
        }

        return startIndices.contains(design.length)
    }

    private fun allPossibleWaysToDesign(design: String, patternsTrie: Trie): Long {
        var startIndices = mapOf(0 to 1L)

        while (startIndices.keys.any { it != design.length }) {
            val nextStartIndices: MutableMap<Int, Long> = mutableMapOf()

            for (startIndex in startIndices.keys) {
                if (startIndex == design.length) {
                    nextStartIndices[startIndex] = (nextStartIndices[startIndex] ?: 0L) + startIndices[startIndex]!!
                    continue
                }

                val designCount = startIndices[startIndex]!!
                val prefixesLengths = patternsTrie.findAllPrefixesLengths(design, startIndex)

                for (prefixLen in prefixesLengths) {
                    val newStartIndex = startIndex + prefixLen
                    nextStartIndices[newStartIndex] = (nextStartIndices[newStartIndex] ?: 0L) + designCount
                }
            }
            startIndices = nextStartIndices
        }

        return startIndices[design.length] ?: 0L
    }

    private data class Trie(val dictionary: List<String>) {
        val root = TrieNode()

        init {
            dictionary.forEach { word -> insert(word) }
        }

        private fun insert(word: String) {
            var current = root
            word.forEach { char -> current = current.children.getOrPut(char) { TrieNode() } }
            current.isEndOfWord = true
        }

        fun findAllPrefixesLengths(word: String, startIdx: Int): List<Int> {
            val prefixesLengths = mutableListOf<Int>()
            var current = root

            for (i in startIdx until word.length) {
                val char = word[i]
                current = current.children[char] ?: return prefixesLengths

                if (current.isEndOfWord)
                    prefixesLengths.add(i - startIdx + 1)
            }

            return prefixesLengths
        }
    }

    private data class TrieNode(
        val children: MutableMap<Char, TrieNode> = mutableMapOf(),
        var isEndOfWord: Boolean = false
    )
}
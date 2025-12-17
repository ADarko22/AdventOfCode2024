package edu.adarko22

fun main() {
    Day23().part1()
    Day23().part2()
}

class Day23 : Day {

    private val input = "day23".readResourceLines()
        .map { it.split("-") }
        .map { Edge(it[0], it[1]) }

    override fun part1() {
        val graph = Graph(input)
        val solution = graph.findAllGroupsOfSizeNWithChiefHistorian(3).size
        println("Day 23 part 1: $solution")
    }

    override fun part2() {
        val graph = Graph(input)
        val groups = graph.findAllGroupsOfSizeN(graph.totalOfNodes())
        val largestGroup = groups.maxByOrNull { it.size() }!!
        val solution = groups.first { it.size() == largestGroup.size() }
            .nodes
            .sorted()
            .joinToString(",")
        println("Day 23 part 2: $solution (slow: 40s)")
    }

    private data class Edge(val from: String, val to: String)

    private data class Graph(val edges: List<Edge>) {
        val map = mutableMapOf<String, MutableSet<String>>()

        init {
            edges.forEach { edge ->
                map[edge.from] = map.getOrDefault(edge.from, mutableSetOf()).apply { add(edge.to) }
                map[edge.to] = map.getOrDefault(edge.to, mutableSetOf()).apply { add(edge.from) }
            }
        }

        fun isConnected(node1: String, node2: String) = map.getOrDefault(node1, mutableSetOf()).contains(node2)

        fun totalOfNodes() = map.keys.size

        class Group(firstNode: String) {
            val nodes = mutableSetOf(firstNode)

            fun isConnected(node: String, graph: Graph) = nodes.all { graph.isConnected(it, node) }

            fun addNodeIfConnected(node: String, graph: Graph): Group {
                if (isConnected(node, graph)) nodes.add(node)
                return this
            }

            fun makeCopy(): Group {
                val copy = Group(nodes.first())
                copy.nodes.addAll(nodes)
                return copy
            }

            fun size() = nodes.size

            override fun toString() = nodes.toString()

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Group

                return nodes == other.nodes
            }

            override fun hashCode() = nodes.hashCode()
        }

        fun findAllGroupsOfSizeNWithChiefHistorian(n: Int) = map.keys.filter { it.startsWith("t") }
            .flatMap { findGroupOfSizeN(it, n) }
            .toSet()

        fun findAllGroupsOfSizeN(n: Int) = map.keys
            .flatMap { findGroupOfSizeN(it, n) }
            .toSet()

        fun findGroupOfSizeN(node: String, n: Int): Set<Group> {
            var groups = setOf(Group(node))
            var connections = mutableSetOf(node)

            for (i in 0 until n - 1) {
                val newConnections = mutableSetOf<String>()
                connections.forEach { connection ->
                    newConnections.addAll(map.getOrDefault(connection, mutableListOf()))
                }
                connections = newConnections
            }

            for (i in 0 until n - 1) {
                val newGroups = groups.flatMap { group ->
                    connections.filter { connection -> group.isConnected(connection, this) }
                        .map { connection -> group.makeCopy().addNodeIfConnected(connection, this) }
                }.toSet()

                if (newGroups.isNotEmpty()) groups = newGroups
            }

            return groups
        }
    }
}
package edu.adarko22

fun main() {
    Day24().part1()
    Day24().part2()
}

// Note that the input is bugged because with the first 3 swaps the circuit output is equals to x + y.
// In order to find the correct solution to part 2 I simply changed the values for the x and y.
class Day24 : Day {

    private val input = "day24".readResourceLines()

    override fun part1() {
        val solution = Circuit(wires(), logicPorts()).runCircuit()
        println("Day 24 part 1: $solution")
    }

    override fun part2() {
        val solution = findFourSwaps(wires(), logicPorts())
        println("Day 24 part 2: $solution")
    }

    private data class LogicPort(val inWire1: String, val inWire2: String, var outWire: String, val type: Type) {
        enum class Type(val apply: (Boolean, Boolean) -> Boolean) {
            AND({ a, b -> a && b }),
            OR({ a, b -> a || b }),
            XOR({ a, b -> a xor b })
        }
    }

    private fun findFourSwaps(wires: Map<String, Boolean>, ports: List<LogicPort>): String {
        val circuit = Circuit(wires, ports)

        val lastZ = circuit.filterPortsByOutWirePrefix("z").maxOf { it.outWire }
        val notXorZs = circuit.filterPortsByOutWirePrefix("z")
            .filter { it.type != LogicPort.Type.XOR }
            .filter { it.outWire != lastZ }
            .toMutableList()

        val xorMiddlePorts = circuit.filterNonXorYInWires()
            .filter { it.type == LogicPort.Type.XOR && !it.outWire.startsWith("z") }
            .toMutableList()

        for (xorMiddle in xorMiddlePorts) {
            val notXorZ = notXorZs.first { it.outWire == circuit.firstZThatUses(xorMiddle.outWire) }
            val temp = xorMiddle.outWire
            xorMiddle.outWire = notXorZ.outWire
            notXorZ.outWire = temp
        }

        val falseCarry = (circuit.runCircuit() xor (circuit.x + circuit.y)).countTrailingZeroBits().toString()
        val outWiresWithFalseCarry =
            circuit.logicPorts.filter { it.inWire1.endsWith(falseCarry) && it.inWire2.endsWith(falseCarry) }

        return (notXorZs + xorMiddlePorts + outWiresWithFalseCarry).map { it.outWire }.sorted().joinToString(",")
    }

    private data class Circuit(val wires: Map<String, Boolean>, val logicPorts: List<LogicPort>) {
        val x = convertWiresToLong(wires, "x")
        val y = convertWiresToLong(wires, "y")

        fun filterPortsByOutWirePrefix(outWirePrefix: String) =
            logicPorts.filter { it.outWire.startsWith(outWirePrefix) }

        fun filterNonXorYInWires() = logicPorts.filter { it.inWire1.first() !in "xy" && it.inWire2.first() !in "xy" }

        fun firstZThatUses(outWire: String): String? {
            val portUsingOutWireAsInWire = logicPorts.filter { it.inWire1 == outWire || it.inWire2 == outWire }

            portUsingOutWireAsInWire.find { it.outWire.startsWith('z') }
                ?.let { return "z" + (it.outWire.drop(1).toInt() - 1).toString().padStart(2, '0') }

            return portUsingOutWireAsInWire.firstNotNullOfOrNull { firstZThatUses(it.outWire) }
        }

        fun runCircuit(): Long {
            val curWires = wires.toMutableMap()
            val currLogicPorts = logicPorts.toMutableList()

            while (currLogicPorts.isNotEmpty()) {
                val updatedLogicPorts = currLogicPorts.filter { logicPort ->
                    logicPort.inWire1 in curWires && logicPort.inWire2 in curWires
                }

                if (updatedLogicPorts.isEmpty()) throw IllegalStateException("No logic port can be updated")

                currLogicPorts.removeAll(updatedLogicPorts)

                updatedLogicPorts.forEach { logicPort ->
                    val in1 = curWires[logicPort.inWire1]!!
                    val in2 = curWires[logicPort.inWire2]!!
                    curWires[logicPort.outWire] = logicPort.type.apply(in1, in2)
                }
            }

            return convertWiresToLong(curWires, "z")
        }

        private fun convertWiresToLong(wires: Map<String, Boolean>, wirePrefix: String): Long {
            val sortedOutputKeys = wires.map { it.key }
                .filter { it.startsWith(wirePrefix) }
                .sorted()
                .reversed()

            var output = 0L
            for (key in sortedOutputKeys) {
                output = output shl 1
                output += if (wires[key]!!) 1L else 0L
            }
            return output
        }
    }

    private fun wires(): Map<String, Boolean> {
        val wiresRegex = "(?<wire>.*): (?<status>[01])".toRegex()
        val splitIndex = input.indexOf("")

        return input.subList(0, splitIndex)
            .map { wiresRegex.matchEntire(it)!!.destructured }
            .associate { (wire, status) -> wire to (status == "1") }
    }


    private fun logicPorts(): List<LogicPort> {
        val logicPortsRegex = "(?<inWire>.*) (?<logicPortType>AND|OR|XOR) (?<inWire2>.*) -> (?<outWire>.*)"
        val splitIndex = input.indexOf("")

        return input.subList(splitIndex + 1, input.size)
            .mapNotNull { logicPortsRegex.toRegex().matchEntire(it) }
            .map { it.destructured }
            .map { (inWire, logicPortType, inWire2, outWire) ->
                LogicPort(inWire, inWire2, outWire, LogicPort.Type.valueOf(logicPortType))
            }
    }
}

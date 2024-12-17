package org.example

fun main() {
    Day17().part1()
    Day17().part2()
}

class Day17 : Day {

    private val input = "day17".readResourceLines()

    override fun part1() {
        val (computer, program) = parseInput()
        computer.runProgram(program)
        val solution = computer.outputs.joinToString(",")
        println("Day 17 part 1: $solution")
    }

    override fun part2() {
        val (_, program) = parseInput()
        printProgram(program)
        val solution = findRegisterAThatMakesOutputEqualsToProgram(program)
        println("Day 17 part 2: $solution")
    }

    private enum class OpCode(val code: Int) {
        ADV(0), BXL(1), BST(2), JNZ(3), BXC(4), OUT(5), BDV(6), CDV(7);

        companion object {
            fun fromCode(code: Int) = OpCode.entries.find { it.code == code }!!
        }
    }

    private data class Computer(var A: Long = 0, var B: Long = 0, var C: Long = 0) {
        var ic: Int = 0
        var outputs: MutableList<Int> = mutableListOf()

        fun runProgram(program: List<Int>) {
            while (ic < program.size) {
                val opCode = OpCode.fromCode(program[ic])
                val operand = program[ic + 1]
                runOp(opCode, operand)

                ic = if (opCode != OpCode.JNZ) ic + 2 else ic
            }
        }

        private fun runOp(opCode: OpCode, operand: Int) =
            // Assuming that the shift operations are done with operands that are integers
            when (opCode) {
                OpCode.ADV -> A = A shr toComboOperand(operand).toInt()
                OpCode.BXL -> B = B xor operand.toLong()
                OpCode.BST -> B = toComboOperand(operand) % 8
                OpCode.JNZ -> ic = if (A == 0L) ic + 2 else operand
                OpCode.BXC -> B = B xor C
                OpCode.OUT -> outputs.add((toComboOperand(operand) % 8).toInt())
                OpCode.BDV -> B = A shr toComboOperand(operand).toInt()
                OpCode.CDV -> C = A shr toComboOperand(operand).toInt()
            }

        fun toComboOperand(operand: Int) =
            when (operand) {
                in 0..3 -> operand.toLong()
                4 -> A
                5 -> B
                6 -> C
                else -> throw IllegalArgumentException("Operand not recognized: $operand")
            }
    }

    // Looking at the program for the specific input, we can make the hypothesis that
    // A is shifted to right by 3 at every loop
    // B is the result of several operations based on A; A is being reduced to a 3 bits number at the beginning of the program
    // A can be built by shifting it to the left by 3 after computing B and matching it with the program
    fun findRegisterAThatMakesOutputEqualsToProgram(program: List<Int>): Long {
        var potentialAs = listOf(0L)

        for (i in program.size - 1 downTo 0) {
            val expectedOutput = program.subList(i, program.size)
            val nextPotentialAs = mutableListOf<Long>()

            for (potentialA in potentialAs) {
                for (aSuffix in 0..7) {
                    val a = (potentialA shl 3) + aSuffix
                    val computer = Computer(A = a)
                    computer.runProgram(program)

                    if (computer.outputs == expectedOutput) {
                        nextPotentialAs.add(a)
                    }
                }
            }
            potentialAs = nextPotentialAs
        }

        return potentialAs.first()
    }

    private fun parseInput(): Pair<Computer, List<Int>> {
        val regex =
            "Register A: (?<A>\\d+)\nRegister B: (?<B>\\d+)\nRegister C: (?<C>\\d+)\n\nProgram: (?<program>.*)".toRegex()
        val (A, B, C, program) = regex.matchEntire(input.joinToString("\n"))!!.destructured
        val programAsList = program.split(",").map { it.toInt() }
        return Computer(A.toLong(), B.toLong(), C.toLong()) to programAsList
    }


    private fun printProgram(program: List<Int>) =
        program.windowed(size = 2, step = 2)
            .forEachIndexed { index, operation -> println("Op #$index: ${representOp(operation[0], operation[1])}") }


    private fun representOp(opCode: Int, operand: Int) =
        when (OpCode.fromCode(opCode)) {
            OpCode.ADV -> "A = A >> ${toComboOperandName(operand)}"
            OpCode.BXL -> "B = B xor $operand"
            OpCode.BST -> "B = ${toComboOperandName(operand)} % 8"
            OpCode.JNZ -> "JUMP to Op #$operand if A != 0"
            OpCode.BXC -> "B = B xor C"
            OpCode.OUT -> "OUT ${toComboOperandName(operand)} % 8"
            OpCode.BDV -> "B = A >> ${toComboOperandName(operand)}"
            OpCode.CDV -> "C = A >> ${toComboOperandName(operand)}"
        }

    private fun toComboOperandName(operand: Int) =
        when (operand) {
            in 0..3 -> operand.toString()
            4 -> "A"
            5 -> "B"
            6 -> "C"
            else -> throw IllegalArgumentException("Operand not recognized: $operand")
        }
}
package day21

import getInput

fun main() {
    day21part1(getInput(21, true))
    day21part1(getInput(21, false))
    day21part2(getInput(21, true))
    day21part2(getInput(21, false))
    println("Plug the above into an equation solver of your choosing ;)")
}

private fun day21part1(lines: List<String>) {
    monkeys = lines.associate { it.split(": ")[0] to parseMonkey(it) }
    println(monkeys.getValue("root").number)
}

private fun day21part2(lines: List<String>) {
    monkeys2 = lines.associate { it.split(": ")[0] to parseMonkey2(it) }
    (monkeys2.getValue("root") as Monkey2.Composite).symbol = "="
    println(monkeys2.getValue("root").toString().removeSurrounding("(", ")"))
}

private lateinit var monkeys: Map<String, Monkey>
private lateinit var monkeys2: Map<String, Monkey2>

private data class Monkey(val name: String, private val operation: () -> ULong) {
    val number by lazy(operation)
}

private sealed class Monkey2(val name: String) {
    abstract val stringRep: String
    override fun toString() = stringRep

    class Raw(name: String, override val stringRep: String) : Monkey2(name)

    class Composite(name: String, var symbol: String, val left: String, val right: String) : Monkey2(name) {
        override val stringRep by lazy {
            val leftValue = monkeys2.getValue(left)
            val rightValue = monkeys2.getValue(right)
            val leftNum = leftValue.stringRep.toULongOrNull()
            val rightNum = rightValue.stringRep.toULongOrNull()
            if (leftNum != null && rightNum != null) {
                when (symbol) {
                    "+" -> leftNum + rightNum
                    "-" -> leftNum - rightNum
                    "*" -> leftNum * rightNum
                    "/" -> leftNum / rightNum
                    else -> error("Invalid symbol $symbol")
                }.toString()
            } else {
                "($leftValue $symbol $rightValue)"
            }
        }
    }
}

private fun parseMonkey(line: String): Monkey {
    if (line.any { it.isDigit() }) {
        val (name, num) = line.split(": ")
        return Monkey(name) { num.toULong() }
    }
    val (name, left, symbol, right) = line.split(": ", " ")
    val operator: (ULong, ULong) -> ULong = when (symbol) {
        "+" -> ULong::plus
        "-" -> ULong::minus
        "*" -> ULong::times
        "/" -> ULong::div
        else -> error("Invalid symbol $symbol")
    }
    return Monkey(name) { operator(monkeys.getValue(left).number, monkeys.getValue(right).number) }
}

private fun parseMonkey2(line: String): Monkey2 {
    if (line.any { it.isDigit() }) {
        val (name, num) = line.split(": ")
        return Monkey2.Raw(name, if (name == "humn") "x" else num)
    }
    val (name, left, symbol, right) = line.split(": ", " ")
    return Monkey2.Composite(name, symbol, left, right)
}
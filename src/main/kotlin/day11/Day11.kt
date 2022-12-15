package day11

import getInput
import split
import kotlin.properties.Delegates

fun main() {
    day11part1(getInput(11, true))
    day11part1(getInput(11, false))
    day11part2(getInput(11, true))
    day11part2(getInput(11, false))
}

private fun day11part1(lines: List<String>) {
    DIVISOR = 3u
    val (monkeys, lcm) = parseInput(lines)
    MODULO = lcm
    repeat(20) {
        doRound(monkeys)
    }
    println(monkeys.map { it.inspections }.sortedDescending().let { (a, b) -> a * b })
}

private fun day11part2(lines: List<String>) {
    DIVISOR = 1u
    val (monkeys, lcm) = parseInput(lines)
    MODULO = lcm
    repeat(10000) {
        doRound(monkeys)
    }
    println(monkeys.map { it.inspections }.sortedDescending().let { (a, b) -> a * b })
}

private fun doRound(monkeys: List<Monkey>) {
    monkeys.forEach { it.doTurn(monkeys) }
}

private var DIVISOR by Delegates.notNull<UInt>()
private var MODULO by Delegates.notNull<UInt>()

private class Monkey(private val items: MutableList<Int>, private val operation: (old: Int) -> ULong, private val throwDecision: (worry: Int) -> Int) {
    var inspections = 0L
        private set

    fun doTurn(monkeys: List<Monkey>) {
        inspections += items.size
        items.replaceAll { ((operation(it) / DIVISOR) % MODULO).toInt() }
        for (item in items) {
            monkeys[throwDecision(item)].give(item)
        }
        items.clear()
    }

    fun give(item: Int) {
        items.add(item)
    }
}

private fun parseInput(lines: List<String>): Pair<List<Monkey>, UInt> {
    var lcm = 1u
    return lines.split("").map { (_, itemsLine, opLine, testLine, trueLine, falseLine) ->
        val items = itemsLine.substringAfter(": ").split(", ").map(String::toInt).toMutableList()
        val (operator, operand) = opLine.substringAfter(" old ").split(' ')
        val operatorFun: (Int, Int) -> ULong = if (operator == "*") { a, b -> a.toULong() * b.toULong() } else { a, b -> a.toULong() + b.toULong() }
        val operation: (Int) -> ULong = when (operand) {
            "old" -> { old -> operatorFun(old, old) }
            else -> { old -> operatorFun(old, operand.toInt()) }
        }
        val testDivisor = testLine.substringAfter(" by ").toInt()
        lcm *= testDivisor.toUInt()
        val trueMonkey = trueLine.substringAfter(" monkey ").toInt()
        val falseMonkey = falseLine.substringAfter(" monkey ").toInt()
        val throwDecision: (Int) -> Int = { if (it % testDivisor == 0) trueMonkey else falseMonkey }
        Monkey(items, operation, throwDecision)
    } to lcm
}

private operator fun <E> List<E>.component6() = this[5]

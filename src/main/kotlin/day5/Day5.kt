package day5

import getInput
import split

private typealias Stack = ArrayDeque<Char>

fun main() {
    day5part1(getInput(5, true))
    day5part1(getInput(5, false))
    day5part2(getInput(5, true))
    day5part2(getInput(5, false))
}

private fun day5part1(lines: List<String>) {
    val (stacks, moves) = parse(lines)
    for (move in moves) {
        repeat(move.count) {
            stacks[move.to - 1].addFirst(stacks[move.from - 1].removeFirst())
        }
    }
    println(stacks.joinToString("") { it.first().toString() })
}

private fun day5part2(lines: List<String>) {
    val (stacks, moves) = parse(lines)
    for (move in moves) {
        stacks[move.to - 1].addAll(0, (1..move.count).map { stacks[move.from - 1].removeFirst() })
    }
    println(stacks.joinToString("") { it.first().toString() })
}

private fun parse(lines: List<String>): Pair<List<Stack>, List<Move>> {
    val stackList = mutableListOf<Stack>()
    var (stacks, moves) = lines.split("")
    stacks = stacks.dropLast(1)
    for (col in 1 until stacks[0].length step 4) {
        val stack = Stack().also { stackList.add(it) }
        for (row in stacks.indices.reversed()) {
            val char = stacks[row][col].takeUnless { it == ' ' } ?: continue
            stack.addFirst(char)
        }
    }
    val moveList = moves.map { it.removePrefix("move ").split(" from ", " to ").map(String::toInt).let { (count, from, to) -> Move(count, from, to) } }
    return stackList to moveList
}

private data class Move(val count: Int, val from: Int, val to: Int)
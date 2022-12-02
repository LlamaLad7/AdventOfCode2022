package day2

import getInput

fun main() {
    day2part1(getInput(2, true))
    day2part1(getInput(2, false))
    day2part2(getInput(2, true))
    day2part2(getInput(2, false))
}

private val orders = mapOf(
        'A' to arrayOf('Y', 'X', 'Z'),
        'B' to arrayOf('Z', 'Y', 'X'),
        'C' to arrayOf('X', 'Z', 'Y'),
)

private fun outcome(theirs: Char, mine: Char): Int {
    val order = orders.getValue(theirs)
    return 6 - order.indexOf(mine) * 3
}

private fun day2part1(lines: List<String>) {
    var score = 0
    for ((theirs, mine) in lines.map { it.split(" ").map(String::first) }) {
        score += mine - 'W'
        score += outcome(theirs, mine)
    }
    println(score)
}

private fun day2part2(lines: List<String>) {
    var score = 0
    for ((theirs, mine) in lines.map { it.split(" ").map(String::first) }) {
        score += (mine - 'X') * 3
        score += orders.getValue(theirs)[2 - (mine - 'X')] - 'W'
    }
    println(score)
}
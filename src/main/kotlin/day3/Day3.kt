package day3

import getInput

fun main() {
    day3part1(getInput(3, true))
    day3part1(getInput(3, false))
    day3part2(getInput(3, true))
    day3part2(getInput(3, false))
}

private fun day3part1(lines: List<String>) {
    println(lines.sumOf { it.halves().map(String::toSet).let { (a, b) -> a intersect b }.first().toPriority() })
}

private fun day3part2(lines: List<String>) {
    println(lines.chunked(3).sumOf { it.map(String::toSet).let { (a, b, c) -> a intersect b intersect c }.first().toPriority() })
}

private fun Char.toPriority() = when (this) {
    in 'A'..'Z' -> this - 'A' + 27
    in 'a'..'z' -> this - 'a' + 1
    else -> error("Invalid character $this")
}

private fun String.halves() = listOf(substring(0 until length / 2), substring(length / 2))
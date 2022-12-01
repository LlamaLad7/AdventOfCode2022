package day1

import getInput
import split

fun main() {
    day1part1(getInput(1, true))
    day1part1(getInput(1, false))
    day1part2(getInput(1, true))
    day1part2(getInput(1, false))
}

private fun day1part1(lines: List<String>) {
    println(lines.split("").map { it.sumOf(String::toInt) }.max())
}

private fun day1part2(lines: List<String>) {
    println(lines.split("").map { it.sumOf(String::toInt) }.sorted().takeLast(3).sum())
}
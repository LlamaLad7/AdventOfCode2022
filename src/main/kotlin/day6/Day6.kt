package day6

import getInput

fun main() {
    day6part1(getInput(6, true))
    day6part1(getInput(6, false))
    day6part2(getInput(6, true))
    day6part2(getInput(6, false))
}

private fun day6part1(lines: List<String>) {
    val (data) = lines
    println(data.indices.first { data.substring(it..it + 3).toSet().size == 4 } + 4)
}

private fun day6part2(lines: List<String>) {
    val (data) = lines
    println(data.indices.first { data.substring(it..it + 13).toSet().size == 14 } + 14)
}
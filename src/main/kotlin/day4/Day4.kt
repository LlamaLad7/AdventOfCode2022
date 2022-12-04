package day4

import getInput

fun main() {
    day4part1(getInput(4, true))
    day4part1(getInput(4, false))
    day4part2(getInput(4, true))
    day4part2(getInput(4, false))
}

private fun day4part1(lines: List<String>) {
    println(lines.count { it.readRanges().let { (a, b) -> a in b || b in a } })
}

private fun day4part2(lines: List<String>) {
    println(lines.count { it.readRanges().let { (a, b) -> a overlaps b || b overlaps a } })
}

private fun String.readRanges() = split(',').map { it.split('-').map(String::toInt).let { (a, b) -> a..b } }

private operator fun IntRange.contains(other: IntRange) = other.first in this && other.last in this

private infix fun IntRange.overlaps(other: IntRange) = other.first in this || other.last in this
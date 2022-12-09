package day8

import getInput

fun main() {
    day8part1(getInput(8, true))
    day8part1(getInput(8, false))
    day8part2(getInput(8, true))
    day8part2(getInput(8, false))
}

private fun day8part1(lines: List<String>) {
    val trees = lines.map { it.toList().map(Char::digitToInt) }
    var count = 0
    for ((y, row) in trees.withIndex()) {
        for ((x, height) in row.withIndex()) {
            if (arrayOf(
                    row.take(x),
                    row.drop(x + 1),
                    trees.take(y).map { it[x] },
                    trees.drop(y + 1).map { it[x] },
                ).any { list -> list.all { it < height } }
            ) {
                count++
            }
        }
    }
    println(count)
}

private fun day8part2(lines: List<String>) {
    val trees = lines.map { it.toList().map(Char::digitToInt) }
    var max = Int.MIN_VALUE
    for ((y, row) in trees.withIndex()) {
        for ((x, height) in row.withIndex()) {
            max = maxOf(
                max, arrayOf(
                    row.take(x).asReversed(),
                    row.drop(x + 1),
                    trees.take(y).map { it[x] }.asReversed(),
                    trees.drop(y + 1).map { it[x] },
                ).map { it.indexBlocked(height) }.product()
            )
        }
    }
    println(max)
}

private fun List<Int>.indexBlocked(height: Int) =
    (indexOfFirst { it >= height }.takeIf { it != -1 } ?: (size - 1)) + 1

private fun List<Int>.product() = reduce(Int::times)

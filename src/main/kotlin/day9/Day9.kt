package day9

import getInput
import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    day9part1(getInput(9, true))
    day9part1(getInput(9, false))
    day9part2(getInput(9, true))
    day9part2(getInput(9, false))
}

private fun day9part1(lines: List<String>) {
    var head = Pos(0, 0)
    var tail = Pos(0, 0)
    val seen = hashSetOf(tail)
    for ((dir, count) in lines.map { it.split(' ') }) {
        repeat(count.toInt()) {
            head = head.move(dir)
            tail = tail.follow(head)
            seen.add(tail)
        }
    }
    println(seen.size)
}

private fun day9part2(lines: List<String>) {
    val rope = Array(10) { Pos(0, 0) }
    val seen = hashSetOf(rope.last())
    for ((dir, count) in lines.map { it.split(' ') }) {
        repeat(count.toInt()) {
            rope[0] = rope[0].move(dir)
            for (i in 1 until rope.size) {
                rope[i] = rope[i].follow(rope[i - 1])
            }
            seen.add(rope.last())
        }
    }
    println(seen.size)
}

private data class Pos(val x: Int, val y: Int) {
    fun move(dir: String) = when (dir) {
        "R" -> copy(x = x + 1)
        "L" -> copy(x = x - 1)
        "U" -> copy(y = y + 1)
        "D" -> copy(y = y - 1)
        else -> error("Invalid dir $dir")
    }

    fun follow(pos: Pos): Pos {
        val dx = pos.x - x
        val dy = pos.y - y
        if (dx.absoluteValue < 2 && dy.absoluteValue < 2) return this
        return Pos(x + dx.sign, y + dy.sign)
    }
}
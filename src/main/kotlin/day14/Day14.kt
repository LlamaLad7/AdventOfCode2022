package day14

import getInput

fun main() {
    day14part1(getInput(14, true))
    day14part1(getInput(14, false))
    day14part2(getInput(14, true))
    day14part2(getInput(14, false))
}

private fun day14part1(lines: List<String>) {
    val points = parseInput(lines)
    val bottom = points.maxOf { it.y }
    val sim = SandSimulation(points, bottom)
    var count = 0
    while (true) {
        when (sim.addGrain()) {
            Result.REST -> count++
            Result.FALL -> {
                println(count)
                return
            }
            Result.DONE -> error("Illegal state for part 1")
        }
    }
}

private fun day14part2(lines: List<String>) {
    val points = parseInput(lines)
    val bottom = points.maxOf { it.y }
    val sim = SandSimulation(object : MutableSet<Point> by points {
        override fun contains(element: Point): Boolean {
            if (element.y == bottom + 2) return true
            return element in points
        }
    }, bottom + 2)
    var count = 0
    while (true) {
        when (sim.addGrain()) {
            Result.REST -> count++
            Result.DONE -> {
                println(count + 1)
                return
            }
            Result.FALL -> error("Illegal state for part 2")
        }
    }
}

private class SandSimulation(private val points: MutableSet<Point>, private val bottom: Int) {
    fun addGrain(): Result {
        var pos = Point(500, 0)
        while (true) {
            pos = nextPositions(pos).firstOrNull() ?: run {
                points.add(pos)
                return if (pos == Point(500, 0)) Result.DONE else Result.REST
            }
            if (pos.y > bottom) {
                return Result.FALL
            }
        }
    }

    private fun nextPositions(pos: Point) = sequence {
        yield(pos + down)
        yield(pos + downAndLeft)
        yield(pos + downAndRight)
    }.filter { it !in points }
}

private val down = Point(0, 1)
private val downAndLeft = Point(-1, 1)
private val downAndRight = Point(1, 1)

private enum class Result {
    REST, FALL, DONE
}

private data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}

private fun parseInput(lines: List<String>): MutableSet<Point> {
    return lines
        .flatMap { line ->
            line.split(" -> ")
                .map {
                    it.split(',')
                        .map(String::toInt)
                        .let { (x, y) -> Point(x, y) }
                }.zipWithNext()
                .flatMap {
                    it.toLine()
                }
        }.toMutableSet()
}

private fun Pair<Point, Point>.toLine() = sequence {
    for (x in minOf(first.x, second.x)..maxOf(first.x, second.x)) {
        for (y in minOf(first.y, second.y)..maxOf(first.y, second.y)) {
            yield(Point(x, y))
        }
    }
}
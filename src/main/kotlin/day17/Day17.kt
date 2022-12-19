package day17

import getInput

fun main() {
    day17part1(getInput(17, true))
    day17part1(getInput(17, false))
    day17part2(getInput(17, true))
    day17part2(getInput(17, false))
}

private fun day17part1(lines: List<String>) {
    val sim = Simulation(lines[0])
    repeat(2022) {
        sim.drop()
    }
    println(sim.top)
}

private fun day17part2(lines: List<String>) {
    val sim = Simulation(lines[0])
    val firstSeenOn = hashMapOf<Triple<Set<Pos>, Int, Int>, Int>()
    val heights = mutableListOf<Int>()
    for (turn in 0..Int.MAX_VALUE) {
        val shape = Triple(sim.getExposedPoints(), sim.nextShape, sim.nextGust)
        if (shape in firstSeenOn) {
            val first = firstSeenOn.getValue(shape)
            val heightDiff = sim.top - heights[first]
            val turnDiff = turn - firstSeenOn.getValue(shape)
            val furtherTurns = 1_000_000_000_000 - turn
            val total = sim.top + furtherTurns / turnDiff * heightDiff + heights[first + (furtherTurns % turnDiff).toInt()] - heights[first]
            println(total)
            return
        } else {
            firstSeenOn[shape] = turn
        }
        heights.add(sim.top)
        sim.drop()
    }
    println("Unsolvable")
}

private class Simulation(private val gusts: String) {
    var top = 0
        private set
    private val settledPoints = hashSetOf<Pos>()
    var nextShape = 0
        private set
    var nextGust = 0
        private set

    fun drop() {
        val shape = Shape(nextShape++, 2, top + 3)
        nextShape %= 5
        while (true) {
            shape.tryBlow(gusts[nextGust++], this::isValidPos)
            nextGust %= gusts.length
            if (!shape.tryFall(this::isValidPos)) {
                break
            }
        }
        top = maxOf(top, shape.topY() + 1)
        settledPoints.addAll(shape.getCoordinates())
    }

    fun getExposedPoints(): MutableSet<Pos> {
        val result = hashSetOf<Pos>()
        findExposedPoints(Pos(0, top), result)
        val min = result.minOf { it.y }
        return result.mapTo(hashSetOf()) { it.copy(y = it.y - min) }
    }

    private fun findExposedPoints(pos: Pos, result: MutableSet<Pos>, seen: MutableSet<Pos> = hashSetOf()) {
        if (pos.x !in 0..6 || pos.y > top) return
        if (!seen.add(pos)) return
        if (pos.y == -1 || pos in settledPoints) {
            result.add(pos)
        } else {
            findExposedPoints(pos.copy(x = pos.x + 1), result, seen)
            findExposedPoints(pos.copy(x = pos.x - 1), result, seen)
            findExposedPoints(pos.copy(y = pos.y - 1), result, seen)
            findExposedPoints(pos.copy(y = pos.y + 1), result, seen)
        }
    }

    private fun isValidPos(x: Int, y: Int): Boolean {
        return x in 0..6 && y >= 0 && Pos(x, y) !in settledPoints
    }
}

private class Shape(index: Int, var x: Int, var y: Int) {
    private val points = when (index) {
        0 -> arrayOf(Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(3, 0))
        1 -> arrayOf(Pos(1, 0), Pos(0, 1), Pos(1, 1), Pos(2, 1), Pos(1, 2))
        2 -> arrayOf(Pos(2, 2), Pos(2, 1), Pos(0, 0), Pos(1, 0), Pos(2, 0))
        3 -> arrayOf(Pos(0, 0), Pos(0, 1), Pos(0, 2), Pos(0, 3))
        4 -> arrayOf(Pos(0, 0), Pos(0, 1), Pos(1, 0), Pos(1, 1))
        else -> error("Invalid index $index")
    }

    private val height = when (index) {
        0 -> 1
        1 -> 3
        2 -> 3
        3 -> 4
        4 -> 2
        else -> error("Invalid index $index")
    }

    fun tryBlow(direction: Char, isValidPos: (Int, Int) -> Boolean) {
        val offset = if (direction == '<') -1 else 1
        if (canMoveTo(x + offset, y, isValidPos)) {
            x += offset
        }
    }

    fun tryFall(isValidPos: (Int, Int) -> Boolean): Boolean {
        if (canMoveTo(x, y - 1, isValidPos)) {
            y -= 1
            return true
        }
        return false
    }

    fun canMoveTo(x: Int, y: Int, isValidPos: (Int, Int) -> Boolean): Boolean {
        for ((xOffset, yOffset) in points) {
            if (!isValidPos(x + xOffset, y + yOffset)) {
                return false
            }
        }
        return true
    }

    fun topY() = y + height - 1

    fun getCoordinates() = points.map { Pos(x + it.x, y + it.y) }
}

private data class Pos(val x: Int, val y: Int)
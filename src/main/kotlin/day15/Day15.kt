package day15

import getInput
import kotlin.math.abs

fun main() {
    day15part1(getInput(15, true), true)
    day15part1(getInput(15, false), false)
    day15part2(getInput(15, true), true)
    day15part2(getInput(15, false), false)
}

private fun day15part1(lines: List<String>, test: Boolean) {
    val sensors = parseInput(lines)
    val beacons = sensors.map { it.nearestBeacon }.toSet()
    println(parseInput(lines).flatMap { it.impossiblePointsAt(if (test) 10 else 2000000) }.toSet().filter { it !in beacons }.size)
}

private fun day15part2(lines: List<String>, test: Boolean) {
    val sensors = parseInput(lines)
    val limit = if (test) 20 else 4000000
    for (y in 0..limit) {
        val rangeSet = RangeSet(limit)
        for (sensor in sensors) {
            rangeSet.offer(sensor.impossibleRange(y))
        }
        if (rangeSet.size() < limit + 1) {
            val x = (0..limit).first { it !in rangeSet }
            println(x.toBigInteger() * 4000000.toBigInteger() + y.toBigInteger())
            return
        }
    }
}

private fun parseInput(lines: List<String>): List<Sensor> {
    return lines.asSequence()
        .map { it.removePrefix("Sensor at ").replace("x=", "").replace("y=", "").split(", ", ": closest beacon is at ") }
        .map { it.map(String::toInt) }
        .map { (x1, y1, x2, y2) -> Sensor(Pos(x1, y1), Pos(x2, y2)) }
        .toList()
}

private data class Sensor(val pos: Pos, val nearestBeacon: Pos) {
    private val distance = pos.distanceTo(nearestBeacon)

    fun impossiblePointsAt(y: Int) = sequence {
        val offset = abs(pos.y - y)
        val xLeeway = distance - offset
        for (possible in -xLeeway..xLeeway) {
            yield(Pos(pos.x + possible, y))
        }
    }

    fun impossibleRange(y: Int): IntRange {
        val offset = abs(pos.y - y)
        val xLeeway = distance - offset
        return (pos.x - xLeeway)..(pos.x + xLeeway)
    }
}

private class RangeSet(private val limit: Int) {
    private var ranges = mutableListOf<IntRange>()

    fun offer(range: IntRange) {
        val clamped = range.first.coerceAtLeast(0)..range.last.coerceAtMost(limit)
        if (clamped.isEmpty()) return
        ranges = ranges.flatMapTo(mutableListOf()) { it.removeIntersectionWith(clamped) }
        ranges.add(clamped)
    }

    fun size() = ranges.sumOf { it.last - it.first + 1 }

    operator fun contains(item: Int) = ranges.any { item in it }
}

private fun IntRange.removeIntersectionWith(other: IntRange) = when {
    first in other && last in other -> emptyList()
    other.first !in this && other.last !in this -> listOf(this)
    other.first in this && other.last in this -> listOf(first until other.first, (other.last + 1)..last)
    other.first in this -> listOf(first until other.first)
    other.last in this -> listOf((other.last + 1)..last)
    else -> error("aaa")
}.filterNot { it.isEmpty() }

private data class Pos(val x: Int, val y: Int) {
    fun distanceTo(other: Pos) = abs(other.x - x) + abs(other.y - y)
}
package day22

import getInput
import kotlin.properties.Delegates
import day22.Face.*
import day22.Direction.*

fun main() {
    day22part1(getInput(22, true))
    day22part1(getInput(22, false))
    TEST = true
    day22part2(getInput(22, true))
    TEST = false
    day22part2(getInput(22, false))
}

private fun day22part1(lines: List<String>) {
    val board = Board(lines.dropLast(2))
    lines.last().forEachMove(board::move, board::turn)
    println(board.getPassword())
}

private fun day22part2(lines: List<String>) {
    if (TEST) {
        faceRanges = mapOf(
            FRONT to (8..11 to 4..7),
            BACK to (0..3 to 4..7),
            UP to (8..11 to 0..3),
            DOWN to (8..11 to 8..11),
            LEFT to (4..7 to 4..7),
            RIGHT to (12..15 to 8..11),
        )
        faceConnections = mapOf(
            FRONT to mapOf(
                RIGHT to (EAST to SOUTH),
            ),
            BACK to mapOf(
                UP to (NORTH to SOUTH),
                DOWN to (SOUTH to NORTH),
                RIGHT to (WEST to NORTH),
            ),
            UP to mapOf(
                BACK to (NORTH to SOUTH),
                LEFT to (WEST to SOUTH),
                RIGHT to (EAST to WEST),
            ),
            DOWN to mapOf(
                BACK to (SOUTH to NORTH),
                LEFT to (WEST to NORTH),
            ),
            LEFT to mapOf(
                UP to (NORTH to EAST),
                DOWN to (SOUTH to EAST),
            ),
            RIGHT to mapOf(
                FRONT to (NORTH to WEST),
                BACK to (SOUTH to EAST),
                UP to (EAST to WEST),
            ),
        )
    } else {
        faceRanges = mapOf(
            FRONT to (50..99 to 100..149),
            BACK to (50..99 to 0..49),
            UP to (50..99 to 50..99),
            DOWN to (0..49 to 150..199),
            LEFT to (0..49 to 100..149),
            RIGHT to (100..149 to 0..49),
        )
        faceConnections = mapOf(
            FRONT to mapOf(
                RIGHT to (EAST to WEST),
                DOWN to (SOUTH to WEST),
            ),
            BACK to mapOf(
                DOWN to (NORTH to EAST),
                LEFT to (WEST to EAST),
            ),
            UP to mapOf(
                LEFT to (WEST to SOUTH),
                RIGHT to (EAST to NORTH),
            ),
            DOWN to mapOf(
                FRONT to (EAST to NORTH),
                BACK to (WEST to SOUTH),
                RIGHT to (SOUTH to SOUTH),
            ),
            LEFT to mapOf(
                UP to (NORTH to EAST),
                BACK to (WEST to EAST),
            ),
            RIGHT to mapOf(
                FRONT to (EAST to WEST),
                DOWN to (NORTH to NORTH),
                UP to (SOUTH to WEST),
            ),
        )
    }
    val board = Board2(lines.dropLast(2))
    lines.last().forEachMove(board::move, board::turn)
    println(board.getPassword())
}

private open class Board(lines: List<String>) {
    protected val walls = hashSetOf<Pos>()
    protected val spaces = hashSetOf<Pos>()
    protected var pos: Pos
    protected var facing = 0

    init {
        for ((y, row) in lines.withIndex()) {
            for ((x, c) in row.withIndex()) {
                when (c) {
                    '#' -> walls.add(Pos(x, y))
                    '.' -> spaces.add(Pos(x, y))
                }
            }
        }
        val minY = spaces.minOf { it.y }
        pos = Pos(spaces.filter { it.y == minY }.minOf { it.x }, minY)
    }

    fun move(number: Int) {
        repeat(number) {
            val next = nextPos()
            if (next == pos) return
            pos = next
        }
    }

    fun turn(left: Boolean) {
        facing += if (left) 3 else 1
        facing %= 4
    }

    fun getPassword() = 1000 * (pos.y + 1) + 4 * (pos.x + 1) + facing

    protected open fun nextPos(): Pos {
        var move = pos.move(facing)
        if (move in spaces) return move
        if (move in walls) return pos
        val oppositeFacing = (facing + 2) % 4
        while (true) {
            val new = move.move(oppositeFacing)
            if (new !in spaces && new !in walls) {
                return if (move in walls) pos else move
            }
            move = new
        }
    }
}

private var TEST by Delegates.notNull<Boolean>()
private val SIZE get() = if (TEST) 4 else 50

private class Board2(lines: List<String>) : Board(lines) {
    override fun nextPos(): Pos {
        val move = pos.move(facing)
        if (move in spaces) return move
        if (move in walls) return pos
        val (wrapped, newFacing) = wrapMove()
        if (wrapped in spaces) {
            facing = newFacing
            return wrapped
        }
        if (wrapped in walls) return pos
        panic()
    }

    private fun wrapMove(): Pair<Pos, Int> {
        val (x, y) = pos
        val face = faceRanges.entries.first { (_, v) -> x in v.first && y in v.second }.key
        val (xRange, yRange) = faceRanges.getValue(face)
        val offset = when (facing) {
            0 -> SIZE - 1 - (y - yRange.first)
            1 -> SIZE - 1 - (x - xRange.first)
            2 -> y - yRange.first
            3 -> x - xRange.first
            else -> panic()
        }
        val (newFace, newDir) = faceConnections.getValue(face).entries.first { (_, v) -> v.first.value == facing }.let { (k, v) -> k to v.second }
        val isFlipped1 = newDir.value % 2 == facing % 2
        val newOffset = if (isFlipped1) SIZE - 1 - offset else offset
        val (newX, newY) = when (newDir) {
            EAST -> Pos(0, newOffset)
            SOUTH -> Pos(newOffset, 0)
            WEST -> Pos(SIZE - 1, SIZE - 1 - newOffset)
            NORTH -> Pos(SIZE - 1 - newOffset, SIZE - 1)
        }
        val (newXRange, newYRange) = faceRanges.getValue(newFace)
        return Pos(newX + newXRange.first, newY + newYRange.first) to newDir.value
    }
}

private enum class Face {
    FRONT,
    BACK,
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

private lateinit var faceRanges: Map<Face, Pair<IntRange, IntRange>>
private lateinit var faceConnections: Map<Face, Map<Face, Pair<Direction, Direction>>>

private enum class Direction(val value: Int) {
    EAST(0), SOUTH(1), WEST(2), NORTH(3)
}

private data class Pos(val x: Int, val y: Int) {
    fun move(facing: Int) = when (facing) {
        0 -> copy(x = x + 1)
        1 -> copy(y = y + 1)
        2 -> copy(x = x - 1)
        3 -> copy(y = y - 1)
        else -> error("Invalid facing $facing")
    }
}

private fun String.forEachMove(onMove: (Int) -> Unit, onTurn: (Boolean) -> Unit) {
    var num = 0
    for (c in this) {
        when (c) {
            in '0'..'9' -> {
                num *= 10
                num += c - '0'
            }
            else -> {
                onMove(num)
                num = 0
                onTurn(c == 'L')
            }
        }
    }
    if (num != 0) {
        onMove(num)
    }
}

private fun panic(): Nothing = error("Illegal state")
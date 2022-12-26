package day24

import getInput

fun main() {
    day24part1(getInput(24, true))
    day24part1(getInput(24, false))
    day24part2(getInput(24, true))
    day24part2(getInput(24, false))
}

private fun day24part1(lines: List<String>) {
    println(bfs(readInput(lines)))
}

private fun day24part2(lines: List<String>) {
    val blizzards = readInput(lines)
    val trip1 = bfs(blizzards)
    val trip2 = bfs(blizzards, startTime = trip1, startPos = Pos(WIDTH - 1, HEIGHT), endPos = Pos(0, -1))
    val trip3 = bfs(blizzards, startTime = trip2)
    println(trip3)
}

private fun bfs(blizzards: Blizzards, startTime: Int = 0, startPos: Pos = Pos(0, -1), endPos: Pos = Pos(WIDTH - 1, HEIGHT)): Int {
    val seen = hashSetOf(startTime to startPos)
    val q = ArrayDeque(seen)
    while (q.isNotEmpty()) {
        val (time, player) = q.removeFirst()
        val blizzardsNow = blizzards.getPositionsAt(time)
        for (newPos in player.connections()) {
            if (newPos in blizzardsNow) {
                continue
            }
            if (newPos == endPos) {
                return time
            }
            val (newX, newY) = newPos
            if (newX in 0 until WIDTH && newY in 0 until HEIGHT || newPos == startPos) {
                val newState = time + 1 to newPos
                if (seen.add(newState)) {
                    q.add(newState)
                }
            }
        }
    }
    error("Unsolvable")
}

private fun readInput(lines: List<String>): Blizzards {
    Blizzards.reset()
    HEIGHT = lines.size - 2
    WIDTH = lines[0].length - 2
    val result = hashMapOf<Pos, CharArray>()
    for ((y, row) in lines.drop(1).dropLast(1).withIndex()) {
        for ((x, c) in row.drop(1).dropLast(1).withIndex()) {
            if (c == '.') continue
            result[Pos(x, y)] = charArrayOf(c)
        }
    }
    return Blizzards(result)
}

private var WIDTH = -1
private var HEIGHT = -1

@JvmInline
private value class Blizzards(private val blizzards: Map<Pos, CharArray>) {
    fun getPositionsAt(minute: Int): Set<Pos> = cache.getOrPut(minute) { blizzards.flatMapTo(hashSetOf()) { (k, v) -> v.map { k.move(it, minute) } } }

    companion object {
        private val cache = hashMapOf<Int, Set<Pos>>()

        fun reset() = cache.clear()
    }
}

private data class Pos(val x: Int, val y: Int) {
    fun move(dir: Char, by: Int) = when (dir) {
        '>' -> copy(x = (x + by).mod(WIDTH))
        'v' -> copy(y = (y + by).mod(HEIGHT))
        '<' -> copy(x = (x - by).mod(WIDTH))
        '^' -> copy(y = (y - by).mod(HEIGHT))
        else -> error("Invalid dir $dir")
    }

    fun connections() = arrayOf(
        copy(x = x + 1),
        copy(y = y + 1),
        copy(x = x - 1),
        copy(y = y - 1),
        this,
    )
}
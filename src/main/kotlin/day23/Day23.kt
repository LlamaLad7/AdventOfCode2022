package day23

import getInput

fun main() {
    day23part1(getInput(23, true))
    day23part1(getInput(23, false))
    day23part2(getInput(23, true))
    day23part2(getInput(23, false))
}

private fun day23part1(lines: List<String>) {
    val grid = Grid(lines)
    repeat(10) {
        grid.doRound()
    }
    println(grid.getSize())
}

private fun day23part2(lines: List<String>) {
    val grid = Grid(lines)
    for (i in 1..Int.MAX_VALUE) {
        if (!grid.doRound()) {
            println(i)
            return
        }
    }
}

private class Grid(lines: List<String>) {
    val elves = hashSetOf<Pos>()
    private val proposals = hashMapOf<Pos, MutableList<Pos>>()
    private val directions = mutableListOf(Direction.NORTHS, Direction.SOUTHS, Direction.WESTS, Direction.EASTS)

    init {
        for ((y, row) in lines.withIndex()) {
            for ((x, c) in row.withIndex()) {
                if (c == '#') elves.add(Pos(x, y))
            }
        }
    }

    fun doRound(): Boolean {
        proposals.clear()
        for (elf in elves) {
            val proposal = getProposal(elf) ?: continue
            proposals.getOrPut(proposal) { mutableListOf() }.add(elf)
        }
        var moved = false
        for ((target, current) in proposals) {
            if (current.size != 1) continue
            moved = true
            elves.remove(current.first())
            elves.add(target)
        }
        directions.add(directions.removeFirst())
        return moved
    }

    fun getSize() = (elves.maxOf { it.x } - elves.minOf { it.x } + 1) * (elves.maxOf { it.y } - elves.minOf { it.y } + 1) - elves.size

    private fun getProposal(elf: Pos): Pos? {
        if (Direction.values().none { elf.move(it) in elves }) {
            return null
        }
        for (direction in directions) {
            if (direction.none { elf.move(it) in elves }) {
                return elf.move(direction.first())
            }
        }
        return null
    }
}

private data class Pos(val x: Int, val y: Int) {
    fun move(direction: Direction) = Pos(x + direction.dx, y + direction.dy)
}

private enum class Direction(val dx: Int, val dy: Int) {
    N(0, -1),
    NE(1, -1),
    E(1, 0),
    SE(1, 1),
    S(0, 1),
    SW(-1, 1),
    W(-1, 0),
    NW(-1, -1);

    companion object {
        val NORTHS = arrayOf(N, NE, NW)
        val SOUTHS = arrayOf(S, SE, SW)
        val WESTS = arrayOf(W, NW, SW)
        val EASTS = arrayOf(E, NE, SE)
    }
}
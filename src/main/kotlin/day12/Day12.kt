package day12

import getInput

fun main() {
    day12part1(getInput(12, true))
    day12part1(getInput(12, false))
    day12part2(getInput(12, true))
    day12part2(getInput(12, false))
}

private fun day12part1(lines: List<String>) {
    val (grid, start, end) = readInput(lines)
    println(bfs(grid, start, end))
}

private fun day12part2(lines: List<String>) {
    val (grid, _, end) = readInput(lines)
    var best = Int.MAX_VALUE
    for ((y, row) in grid.withIndex()) {
        for ((x, cell) in row.withIndex()) {
            if (cell == 'a') {
                best = minOf(best, bfs(grid, Pos(x, y), end))
            }
        }
    }
    println(best)
}

private fun bfs(grid: Grid, start: Pos, end: Pos): Int {
    val seen = hashSetOf(start)
    val q = ArrayDeque(listOf(start to 0))
    while (q.isNotEmpty()) {
        val (pos, dist) = q.removeFirst()
        if (pos == end) {
            return dist
        }
        for (connection in pos.getConnections(grid)) {
            if (seen.add(connection)) {
                q.add(connection to dist + 1)
            }
        }
    }
    return Int.MAX_VALUE
}

private data class Pos(val x: Int, val y: Int) {
    fun getConnections(grid: Grid) = sequenceOf(
        Pos(x + 1, y),
        Pos(x - 1, y),
        Pos(x, y + 1),
        Pos(x, y - 1),
    ).filter { (x, y) -> y in grid.indices && x in grid[y].indices && grid[y][x] - grid[this.y][this.x] <= 1 }
}

private typealias Grid = List<List<Char>>

private fun readInput(lines: List<String>): Triple<Grid, Pos, Pos> {
    val grid = lines.map(String::toMutableList)
    lateinit var start: Pos
    lateinit var end: Pos
    for ((y, row) in lines.withIndex()) {
        for ((x, cell) in row.withIndex()) {
            when (cell) {
                'S' -> {
                    start = Pos(x, y)
                    grid[y][x] = 'a'
                }
                'E' -> {
                    end = Pos(x, y)
                    grid[y][x] = 'z'
                }
            }
        }
    }
    return Triple(grid, start, end)
}
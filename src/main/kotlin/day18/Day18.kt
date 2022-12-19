package day18

import getInput
import kotlin.properties.Delegates

fun main() {
    day18part1(getInput(18, true))
    day18part1(getInput(18, false))
    day18part2(getInput(18, true))
    day18part2(getInput(18, false))
}

private fun day18part1(lines: List<String>) {
    val cubes = lines.map { it.split(',').map(String::toInt).let { (x, y, z) -> Pos(x, y, z) } }.toSet()
    println(cubes.sumOf { cube ->
        cube.adjacentCubes().count { it !in cubes }
    })
}

private fun day18part2(lines: List<String>) {
    val cubes = lines.map { it.split(',').map(String::toInt).let { (x, y, z) -> Pos(x, y, z) } }.toSet()
    minX = cubes.minOf { it.x } - 1
    maxX = cubes.maxOf { it.x } + 1
    minY = cubes.minOf { it.y } - 1
    maxY = cubes.maxOf { it.y } + 1
    minZ = cubes.minOf { it.z } - 1
    maxZ = cubes.maxOf { it.z } + 1
    outerCubes = hashSetOf()
    air = hashSetOf()
    cubeExists = { it in cubes }
    findExposedPoints(Pos(minX, minY, minZ))
    println(outerCubes.sumOf { cube ->
        cube.adjacentCubes().count { it in air }
    })
}

private var minX by Delegates.notNull<Int>()
private var minY by Delegates.notNull<Int>()
private var minZ by Delegates.notNull<Int>()
private var maxX by Delegates.notNull<Int>()
private var maxY by Delegates.notNull<Int>()
private var maxZ by Delegates.notNull<Int>()
private lateinit var outerCubes: MutableSet<Pos>
private lateinit var air: MutableSet<Pos>
private lateinit var cubeExists: (Pos) -> Boolean

private val findExposedPoints = DeepRecursiveFunction<Pos, Unit> { pos ->
    if (pos.x !in minX..maxX || pos.y !in minY..maxY || pos.z !in minZ..maxZ) return@DeepRecursiveFunction
    if (pos in outerCubes || pos in air) return@DeepRecursiveFunction
    if (cubeExists(pos)) {
        outerCubes.add(pos)
    } else {
        air.add(pos)
        for (adj in pos.adjacentCubes()) {
            callRecursive(adj)
        }
    }
}

private data class Pos(val x: Int, val y: Int, val z: Int) {
    fun adjacentCubes() = arrayOf(
        copy(x = x - 1),
        copy(x = x + 1),
        copy(y = y - 1),
        copy(y = y + 1),
        copy(z = z - 1),
        copy(z = z + 1),
    )
}
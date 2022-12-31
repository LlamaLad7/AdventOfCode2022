package day19

import getInput
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

fun main() {
    day19part1(getInput(19, true))
    day19part1(getInput(19, false))
    day19part2(getInput(19, true))
    day19part2(getInput(19, false))
}

private fun day19part1(lines: List<String>) {
    END_TIME = 24
    val blueprints = parseInput(lines)
    val results = mutableListOf<Deferred<Int>>()
    for (blueprint in blueprints) {
        results.add(GlobalScope.async { mostGeodesBetter(blueprint, 0, Resources(), Resources(ore = 1)) * blueprint.id })
    }
    runBlocking {
        println(results.awaitAll().sum())
    }
}

private fun day19part2(lines: List<String>) {
    END_TIME = 32
    val blueprints = parseInput(lines).take(3)
    val results = mutableListOf<Deferred<Int>>()
    for (blueprint in blueprints) {
        results.add(GlobalScope.async { mostGeodesBetter(blueprint, 0, Resources(), Resources(ore = 1)) })
    }
    runBlocking {
        println(results.awaitAll().reduce(Int::times))
    }
}

private var END_TIME = -1

private fun mostGeodesBetter(blueprint: Blueprint, time: Int, materials: Resources, robots: Resources): Int {
    if (time == END_TIME) {
        blueprint.offerBest(materials.geodes)
        return materials.geodes
    }
    if (time > END_TIME) {
        println("AAA")
        return Int.MIN_VALUE
    }
    val timeLeft = END_TIME - time
    var best = materials.geodes + robots.geodes * timeLeft
    // If we can keep making 1 geode robot every turn, do
    if (robots >= blueprint.geodeRobot) {
        return best + timeLeft * (timeLeft - 1) / 2
    }
    // Consider building each type of robot next
    // Geode
    if (robots.ore > 0 && robots.obsidian > 0) {
        val oreNeeded = (blueprint.geodeRobot.ore - materials.ore).coerceAtLeast(0)
        val obsidianNeeded = (blueprint.geodeRobot.obsidian - materials.obsidian).coerceAtLeast(0)
        val turnsNeeded = maxOf(ceil(oreNeeded.toFloat() / robots.ore), ceil(obsidianNeeded.toFloat() / robots.obsidian)).toInt() + 1
        if (timeLeft > turnsNeeded) {
            best = maxOf(
                best, mostGeodesBetter(
                    blueprint,
                    time + turnsNeeded,
                    materials + robots * turnsNeeded - blueprint.geodeRobot,
                    robots.copy(geodes = robots.geodes + 1)
                )
            )
        }
    }
    // Obsidian
    if (robots.ore > 0 && robots.clay > 0 && robots.obsidian < blueprint.geodeRobot.obsidian) {
        val oreNeeded = (blueprint.obsidianRobot.ore - materials.ore).coerceAtLeast(0)
        val clayNeeded = (blueprint.obsidianRobot.clay - materials.clay).coerceAtLeast(0)
        val turnsNeeded = maxOf(ceil(oreNeeded.toFloat() / robots.ore), ceil(clayNeeded.toFloat() / robots.clay)).toInt() + 1
        if (timeLeft > turnsNeeded) {
            best = maxOf(
                best, mostGeodesBetter(
                    blueprint,
                    time + turnsNeeded,
                    materials + robots * turnsNeeded - blueprint.obsidianRobot,
                    robots.copy(obsidian = robots.obsidian + 1)
                )
            )
        }
    }
    // Clay
    if (robots.ore > 0 && robots.clay < blueprint.obsidianRobot.clay) {
        val oreNeeded = (blueprint.clayRobot.ore - materials.ore).coerceAtLeast(0)
        val turnsNeeded = ceil(oreNeeded.toFloat() / robots.ore).toInt() + 1
        if (timeLeft > turnsNeeded) {
            best = maxOf(
                best, mostGeodesBetter(
                    blueprint,
                    time + turnsNeeded,
                    materials + robots * turnsNeeded - blueprint.clayRobot,
                    robots.copy(clay = robots.clay + 1)
                )
            )
        }
    }
    // Ore
    if (robots.ore in 0..blueprint.maxOre) {
        val oreNeeded = (blueprint.oreRobot.ore - materials.ore).coerceAtLeast(0)
        val turnsNeeded = ceil(oreNeeded.toFloat() / robots.ore).toInt() + 1
        if (timeLeft > turnsNeeded) {
            best = maxOf(
                best, mostGeodesBetter(
                    blueprint,
                    time + turnsNeeded,
                    materials + robots * turnsNeeded - blueprint.oreRobot,
                    robots.copy(ore = robots.ore + 1)
                )
            )
        }
    }
    blueprint.offerBest(best)
    return best
}

//private fun mostGeodesNaive(blueprint: Blueprint, time: Int, materials: Resources, robots: Resources): Int {
//    return cache.getOrPut(Triple(time, materials, robots)) {
//        if (time == END_TIME) {
//            if (materials.geodes > maxGeodes) {
//                maxGeodes = materials.geodes
//                println("Got $maxGeodes geodes!")
//            }
//            return@getOrPut materials.geodes
//        }
//        val nextStates = mutableListOf(materials to robots)
//        if (materials >= blueprint.geodeRobot) {
//            nextStates.add(materials - blueprint.geodeRobot to robots.copy(geodes = robots.geodes + 1))
//        }
//        if (materials >= blueprint.obsidianRobot) {
//            nextStates.add(materials - blueprint.obsidianRobot to robots.copy(obsidian = robots.obsidian + 1))
//        }
//        if (materials >= blueprint.clayRobot) {
//            nextStates.add(materials - blueprint.clayRobot to robots.copy(clay = robots.clay + 1))
//        }
//        if (materials >= blueprint.oreRobot) {
//            nextStates.add(materials - blueprint.oreRobot to robots.copy(ore = robots.ore + 1))
//        }
//        return@getOrPut nextStates.maxOf { (newMaterials, newRobots) ->
//            mostGeodesNaive(blueprint, time + 1, newMaterials + robots, newRobots)
//        }
//    }
//}

private fun mostGeodesBfs(blueprint: Blueprint): Int {
    val start = State(Resources(), Resources(ore = 1))
    val seen = hashSetOf(start)
    val q = ArrayDeque(listOf(0 to start))
    var best = Int.MIN_VALUE
    var maxTime = Int.MIN_VALUE
    while (q.isNotEmpty()) {
        val (time, state) = q.removeFirst()
        if (time > maxTime) {
            println("Reached time $time")
            maxTime = time
        }
        if (time == END_TIME) {
            best = maxOf(best, state.materials.geodes)
            continue
        }
        for (connection in state.getConnectedStates(blueprint)) {
            if (seen.add(connection)) {
                q.add(time + 1 to connection)
            }
        }
    }
    return best
}

private data class State(val materials: Resources, val robots: Resources) {
    fun getConnectedStates(blueprint: Blueprint): List<State> {
        val nextStates = mutableListOf(this)
        if (materials.ore > 20) {
            nextStates.clear()
        }
        if (materials >= blueprint.geodeRobot) {
            nextStates.add(State(materials - blueprint.geodeRobot, robots.copy(geodes = robots.geodes + 1)))
        }
        if (materials >= blueprint.obsidianRobot) {
            nextStates.add(State(materials - blueprint.obsidianRobot, robots.copy(obsidian = robots.obsidian + 1)))
        }
        if (materials >= blueprint.clayRobot) {
            nextStates.add(State(materials - blueprint.clayRobot, robots.copy(clay = robots.clay + 1)))
        }
        if (materials >= blueprint.oreRobot) {
            nextStates.add(State(materials - blueprint.oreRobot, robots.copy(ore = robots.ore + 1)))
        }
        return nextStates.map { it.copy(materials = it.materials + robots) }
    }
}

private fun parseInput(lines: List<String>): List<Blueprint> {
    val result = mutableListOf<Blueprint>()
    for ((index, line) in lines.withIndex()) {
        val nums = line.split(' ').mapNotNull(String::toIntOrNull)
        result.add(
            Blueprint(
                index + 1,
                Resources(ore = nums[0]),
                Resources(ore = nums[1]),
                Resources(ore = nums[2], clay = nums[3]),
                Resources(ore = nums[4], obsidian = nums[5])
            )
        )
    }
    return result
}

private data class Resources(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geodes: Int = 0) {
    operator fun compareTo(other: Resources): Int {
        if (ore >= other.ore && clay >= other.clay && obsidian >= other.obsidian && geodes >= other.geodes) {
            return 1
        }
        return -1
    }

    operator fun plus(other: Resources) = Resources(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geodes + other.geodes)

    operator fun minus(other: Resources) = Resources(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geodes - other.geodes)

    operator fun times(multiplier: Int) = Resources(ore * multiplier, clay * multiplier, obsidian * multiplier, geodes * multiplier)
}

private data class Blueprint(val id: Int, val oreRobot: Resources, val clayRobot: Resources, val obsidianRobot: Resources, val geodeRobot: Resources) {
    private var best = Int.MIN_VALUE
    val maxOre = maxOf(oreRobot.ore, clayRobot.ore, obsidianRobot.ore, geodeRobot.ore)

    fun offerBest(newBest: Int) {
        if (newBest > best) {
            println("[${LocalDateTime.now().format(DateTimeFormatter.ISO_TIME)}] New best of $newBest for blueprint $id")
            best = newBest
        }
    }
}
@file:OptIn(DelicateCoroutinesApi::class)

package day19

import getInput
import kotlinx.coroutines.*
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
        results.add(GlobalScope.async { mostGeodes(blueprint, 0, Resources(), Resources(ore = 1)) * blueprint.id })
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
        results.add(GlobalScope.async { mostGeodes(blueprint, 0, Resources(), Resources(ore = 1)) })
    }
    runBlocking {
        println(results.awaitAll().reduce(Int::times))
    }
}

private var END_TIME = -1

private fun mostGeodes(blueprint: Blueprint, time: Int, materials: Resources, robots: Resources): Int {
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
    if (robots.obsidian > 0) {
        val oreNeeded = (blueprint.geodeRobot.ore - materials.ore).coerceAtLeast(0)
        val obsidianNeeded = (blueprint.geodeRobot.obsidian - materials.obsidian).coerceAtLeast(0)
        val turnsNeeded = maxOf(ceil(oreNeeded.toFloat() / robots.ore), ceil(obsidianNeeded.toFloat() / robots.obsidian)).toInt() + 1
        if (timeLeft > turnsNeeded) {
            best = maxOf(
                best, mostGeodes(
                    blueprint,
                    time + turnsNeeded,
                    materials + robots * turnsNeeded - blueprint.geodeRobot,
                    robots.copy(geodes = robots.geodes + 1)
                )
            )
        }
    }
    // Obsidian
    if (robots.clay > 0 && robots.obsidian < blueprint.geodeRobot.obsidian) {
        val oreNeeded = (blueprint.obsidianRobot.ore - materials.ore).coerceAtLeast(0)
        val clayNeeded = (blueprint.obsidianRobot.clay - materials.clay).coerceAtLeast(0)
        val turnsNeeded = maxOf(ceil(oreNeeded.toFloat() / robots.ore), ceil(clayNeeded.toFloat() / robots.clay)).toInt() + 1
        if (timeLeft > turnsNeeded) {
            best = maxOf(
                best, mostGeodes(
                    blueprint,
                    time + turnsNeeded,
                    materials + robots * turnsNeeded - blueprint.obsidianRobot,
                    robots.copy(obsidian = robots.obsidian + 1)
                )
            )
        }
    }
    // Clay
    if (robots.clay < blueprint.obsidianRobot.clay) {
        val oreNeeded = (blueprint.clayRobot.ore - materials.ore).coerceAtLeast(0)
        val turnsNeeded = ceil(oreNeeded.toFloat() / robots.ore).toInt() + 1
        if (timeLeft > turnsNeeded) {
            best = maxOf(
                best, mostGeodes(
                    blueprint,
                    time + turnsNeeded,
                    materials + robots * turnsNeeded - blueprint.clayRobot,
                    robots.copy(clay = robots.clay + 1)
                )
            )
        }
    }
    // Ore
    if (robots.ore < blueprint.maxOre) {
        val oreNeeded = (blueprint.oreRobot.ore - materials.ore).coerceAtLeast(0)
        val turnsNeeded = ceil(oreNeeded.toFloat() / robots.ore).toInt() + 1
        if (timeLeft > turnsNeeded) {
            best = maxOf(
                best, mostGeodes(
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
    val maxOre = maxOf(clayRobot.ore, obsidianRobot.ore, geodeRobot.ore)

    fun offerBest(newBest: Int) {
        if (newBest > best) {
            println("New best of $newBest for blueprint $id")
            best = newBest
        }
    }
}
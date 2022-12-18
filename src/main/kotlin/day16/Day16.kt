package day16

import getInput
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    day16part1(getInput(16, true))
    day16part1(getInput(16, false))
    day16part2(getInput(16, true))
    day16part2(getInput(16, false))
}

private fun day16part1(lines: List<String>) {
    val raw = parseInput(lines)
    val graph = raw.filter { it.value.flowRate != 0 || it.key == "AA" }.mapValues { (_, v) -> Valve(v.name, v.flowRate, v.findUsefulConnections(raw)) }
    println(maxPressure(graph, emptySet(), "AA", 0, 0))
}

private fun day16part2(lines: List<String>) {
    globalMax = 0
    val raw = parseInput(lines)
    val graph = raw.filter { it.value.flowRate != 0 || it.key == "AA" }.mapValues { (_, v) -> Valve(v.name, v.flowRate, v.findUsefulConnections(raw)) }
    println(maxPressureWithElephant(graph, arrayOf(AnimalState(emptySet(), "AA", 0, 0), AnimalState(emptySet(), "AA", 0, 0)), true))
}

private fun maxPressure(graph: Map<String, Valve>, openValves: Set<String>, pos: String, pressure: Int, time: Int): Int {
    if (time > 30) {
        return Int.MIN_VALUE
    }
    val newPressure = openValves.sumOf { graph.getValue(it).flowRate }
    var max = pressure + newPressure * (30 - time)
    for ((weight, dest) in graph.getValue(pos).connections) {
        if (dest in openValves) continue
        max = maxOf(
            max,
            maxPressure(
                graph,
                openValves + dest,
                dest,
                pressure + (weight + 1) * newPressure,
                time + weight + 1
            )
        )
    }
    return max
}

private var globalMax = Int.MIN_VALUE

private fun maxPressureWithElephant(graph: Map<String, Valve>, animals: Array<AnimalState>, first: Boolean): Pair<Int, Int> {
    val openValves = animals[0].openedValves + animals[1].openedValves
    val index = if (first) 0 else 1
    val animal = animals[index]
    val newPressure = animal.openedValves.sumOf { graph.getValue(it).flowRate }
    var bestPair = animals.map { it.finalPressure(graph) }.let { (a, b) -> a to b }

    if (animal.time > 26) {
        bestPair = Int.MIN_VALUE to Int.MIN_VALUE
    } else {
        for ((weight, dest) in graph.getValue(animal.pos).connections) {
            if (dest in openValves) continue
            val newState = AnimalState(
                animal.openedValves + dest,
                dest,
                animal.pressure + (weight + 1) * newPressure,
                animal.time + weight + 1
            )
            val newBest = maxPressureWithElephant(
                graph,
                if (first) arrayOf(newState, animals[1]) else arrayOf(animals[0], newState),
                !first
            )
            if (newBest.first + newBest.second > bestPair.first + bestPair.second) {
                bestPair = newBest
            }
        }
    }
    val sum = bestPair.first + bestPair.second
    if (sum > globalMax) {
        globalMax = sum
        println("[${LocalDateTime.now().format(DateTimeFormatter.ISO_TIME)}] New best is $sum")
    }
    return bestPair
}

private fun parseInput(lines: List<String>): Map<String, Valve> {
    val result = hashMapOf<String, Valve>()
    for (line in lines) {
        val name = line.substringBetween("Valve ", " has")
        result[name] = Valve(
            name,
            line.substringBetween("rate=", ";").toInt(),
            line.substringAfter("to valve").removePrefix("s").removePrefix(" ").split(", ").map { Edge(1, it) }
        )
    }
    return result
}

private data class AnimalState(val openedValves: Set<String>, val pos: String, val pressure: Int, val time: Int) {
    fun finalPressure(graph: Map<String, Valve>): Int {
        val newPressure = openedValves.sumOf { graph.getValue(it).flowRate }
        return pressure + newPressure * (26 - time)
    }
}

private data class Valve(val name: String, val flowRate: Int, val connections: List<Edge>) {
    fun findUsefulConnections(valves: Map<String, Valve>): List<Edge> {
        val dists = valves.filterValues { it.flowRate != 0 }.mapValuesTo(hashMapOf()) { 0 }
        dists.remove(name)
        val seen = hashSetOf(name)
        val q = ArrayDeque(listOf(0 to name))
        while (q.isNotEmpty()) {
            val (dist, newName) = q.removeFirst()
            val valve = valves.getValue(newName)
            for (connection in valve.connections) {
                if (connection.dest in dists && dists[connection.dest] == 0) {
                    dists[connection.dest] = dist + 1
                }
                if (seen.add(connection.dest)) {
                    q.add(dist + 1 to connection.dest)
                }
            }
        }
        return dists.map { (k, v) -> Edge(v, k) }
    }
}

private data class Edge(val weight: Int, val dest: String)

private fun String.substringBetween(start: String, end: String) = substringAfter(start).substringBefore(end)
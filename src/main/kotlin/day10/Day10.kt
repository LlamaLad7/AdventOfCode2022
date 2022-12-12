package day10

import getInput

fun main() {
    day10part1(getInput(10, true))
    day10part1(getInput(10, false))
    day10part2(getInput(10, true))
    day10part2(getInput(10, false))
}

private fun day10part1(lines: List<String>) {
    var sum = 0
    lines.onEachCycle { cycle, x ->
        if ((cycle - 20) % 40 == 0) {
            sum += x * cycle
        }
    }
    println(sum)
}

private fun day10part2(lines: List<String>) {
    lines.onEachCycle { cycle, x ->
        if ((cycle - 1) % 40 - x in -1..1) {
            print('#')
        } else {
            print('.')
        }
        if (cycle % 40 == 0) {
            println()
        }
    }
}

private fun List<String>.onEachCycle(block: (cycle: Int, x: Int) -> Unit) {
    var cycle = 1
    var x = 1
    for (line in this) {
        if (line.startsWith("noop")) {
            block(cycle, x)
            cycle++
        } else {
            val operand = line.split(' ')[1].toInt()
            block(cycle, x)
            cycle++
            block(cycle, x)
            cycle++
            x += operand
        }
    }
}
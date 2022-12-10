package day7

import getInput
import java.math.BigInteger

fun main() {
    day7part1(getInput(7, true))
    day7part1(getInput(7, false))
    day7part2(getInput(7, true))
    day7part2(getInput(7, false))
}

private fun day7part1(lines: List<String>) {
    var result = BigInteger.ZERO
    val root = readInput(lines)
    root.walk {
        if (it.totalSize <= 100000.toBigInteger()) {
            result += it.totalSize
        }
    }
    println(result)
}

private fun day7part2(lines: List<String>) {
    var result = Long.MAX_VALUE.toBigInteger()
    val root = readInput(lines)
    val freeSpace = 70000000.toBigInteger() - root.totalSize
    val requiredSpace = 30000000.toBigInteger() - freeSpace
    root.walk {
        if (it.totalSize >= requiredSpace) {
            result = minOf(result, it.totalSize)
        }
    }
    println(result)
}

private fun readInput(lines: List<String>): Directory {
    val root = Directory(null, "/")
    var currentDirectory = root
    for (line in lines.drop(1).map { it.trimEnd() }) {
        when {
            line == "$ ls" -> continue
            line == "$ cd .." -> currentDirectory = currentDirectory.parent!!
            line.startsWith("$ cd ") -> currentDirectory = currentDirectory.findDirectory(line.removePrefix("$ cd "))
            line.startsWith("dir ") -> currentDirectory.offerDirectory(line.removePrefix("dir "))
            else -> currentDirectory.offerFile(line.substringBefore(' '))
        }
    }
    root.resolveSizes()
    return root
}

private class Directory(val parent: Directory?, val name: String) {
    private val children = mutableMapOf<String, Directory>()
    var totalSize: BigInteger = BigInteger.ZERO
        private set

    fun offerFile(size: String) {
        totalSize += size.toBigInteger()
    }

    fun offerDirectory(name: String) {
        children[name] = Directory(this, name)
    }

    fun findDirectory(name: String) = children.getValue(name)

    fun resolveSizes() {
        children.values.forEach(Directory::resolveSizes)
        totalSize += children.values.sumOf { it.totalSize }
    }

    fun walk(block: (Directory) -> Unit) {
        block(this)
        children.values.forEach { it.walk(block) }
    }

    override fun toString() = name
}


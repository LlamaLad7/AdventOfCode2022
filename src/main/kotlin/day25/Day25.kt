package day25

import getInput
import java.math.BigInteger
import kotlin.math.log

fun main() {
    day25part1(getInput(25, true))
    day25part1(getInput(25, false))
    day25part2(getInput(25, true))
    day25part2(getInput(25, false))
}

private fun day25part1(lines: List<String>) {
    println(Snafu.ofBigInteger(lines.sumOf { Snafu(it).toBigInteger() }))
}

@JvmInline
private value class Snafu(private val str: String) {
    fun toBigInteger(): BigInteger {
        var result = BigInteger.ZERO
        var multiplier = BigInteger.ONE
        for (c in str.reversed()) {
            result += c.value() * multiplier
            multiplier *= five
        }
        return result
    }

    companion object {
        private val five = 5.toBigInteger()

        fun ofBigInteger(input: BigInteger): Snafu {
            return Snafu(buildString {
                val targetLength = input.snafuLength()
                var num = input
                var multiplier = five.pow(targetLength - 1)
                repeat(targetLength) {
                    val next = "210-=".minBy { (it.value() * multiplier - num).abs() }
                    append(next)
                    num -= next.value() * multiplier
                    multiplier /= five
                }
            })
        }

        private fun Char.value() = when (this) {
            '2' -> BigInteger.TWO
            '1' -> BigInteger.ONE
            '0' -> BigInteger.ZERO
            '-' -> -BigInteger.ONE
            '=' -> -BigInteger.TWO
            else -> error("")
        }

        private fun BigInteger.snafuLength() = log((this + 3.toBigInteger()).toDouble(), 5.0).toInt() + 1
    }
}

private fun day25part2(lines: List<String>) {
    
}
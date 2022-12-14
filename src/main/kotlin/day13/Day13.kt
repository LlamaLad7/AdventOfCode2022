package day13

import getInput
import split

fun main() {
    day13part1(getInput(13, true))
    day13part1(getInput(13, false))
    day13part2(getInput(13, true))
    day13part2(getInput(13, false))
}

private fun day13part1(lines: List<String>) {
    println(
        lines.split("")
            .map { (a, b) -> Element.of(a) to Element.of(b) }
            .withIndex()
            .filter { (_, it) -> it.first <= it.second }
            .sumOf { it.index + 1 }
    )
}

private fun day13part2(lines: List<String>) {
    val div1 = Element.of("[[2]]")
    val div2 = Element.of("[[6]]")
    println(
        lines
            .filter { it.isNotBlank() }
            .map(Element::of)
            .let { it + div1 + div2 }
            .sorted()
            .let { (it.indexOf(div1) + 1) * (it.indexOf(div2) + 1) }
    )
}

private sealed class Element(protected val parent: AList?): Comparable<Element> {
    class AnInt(private var value: Int, parent: AList?) : Element(parent) {
        fun offerDigit(digit: Int) {
            value *= 10
            value += digit
        }

        override fun toString() = value.toString()

        override fun compareTo(other: Element) = when (other) {
            is AnInt -> value.compareTo(other.value)
            is AList -> AList(parent).also { it.offerElement(this) }.compareTo(other)
        }
    }

    class AList(parent: AList?) : Element(parent) {
        private val value = mutableListOf<Element>()

        fun offerElement(element: Element) {
            value.add(element)
        }

        override fun toString() = value.toString()

        override fun compareTo(other: Element): Int = when (other) {
            is AnInt -> -other.compareTo(this)
            is AList -> value.zip(other.value).map { (a, b) -> a.compareTo(b) }.firstOrNull { it != 0 } ?: value.size.compareTo(other.value.size)
        }
    }

    companion object {
        fun of(string: String): AList {
            var currentList = AList(null)
            var currentInt: AnInt? = null
            for (c in string.drop(1).dropLast(1)) {
                when (c) {
                    '[' -> currentList = AList(currentList).also { currentList.offerElement(it) }
                    in '0'..'9' -> {
                        if (currentInt == null) {
                            currentInt = AnInt(0, currentList).also { currentList.offerElement(it) }
                        }
                        currentInt.offerDigit(c.digitToInt())
                    }
                    ']' -> currentList = currentList.parent!!
                    ',' -> currentInt = null
                }
            }
            return currentList
        }
    }
}
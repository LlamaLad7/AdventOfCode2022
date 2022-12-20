package day20

import getInput

fun main() {
    day20part1(getInput(20, true))
    day20part1(getInput(20, false))
    day20part2(getInput(20, true))
    day20part2(getInput(20, false))
}

private fun day20part1(lines: List<String>) {
    val nums = lines.mapTo(mutableListOf(), String::toLong)
    val start = mix(nums, 1)
    println((1000..3000 step 1000).sumOf { nums[(start + it).mod(nums.size)] })
}

private fun day20part2(lines: List<String>) {
    val nums = lines.mapTo(mutableListOf()) { it.toLong() * 811589153 }
    val start = mix(nums, 10)
    println((1000..3000 step 1000).sumOf { nums[(start + it).mod(nums.size)] })
}

private fun mix(nums: MutableList<Long>, times: Int): Int {
    val indices = nums.indices.toMutableList()
    repeat(times) {
        for (i in nums.indices) {
            val currentIndex = indices.indexOf(i)
            val num = nums[currentIndex]
            val newIndex = (num + currentIndex).mod(nums.size - 1)
            nums.add(newIndex, nums.removeAt(currentIndex))
            indices.add(newIndex, indices.removeAt(currentIndex))
        }
    }
    return nums.indexOf(0)
}
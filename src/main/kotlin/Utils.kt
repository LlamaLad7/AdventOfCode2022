import java.io.File

fun getInput(day: Int, test: Boolean): List<String> {
    return File(".", "src/main/resources/${if (test) "test_inputs" else "inputs"}/day$day.txt").readText().split('\n')
}

fun <T> List<T>.split(separator: T): List<List<T>> {
    val result = mutableListOf(mutableListOf<T>())
    for (item in this) {
        if (item == separator) {
            result.add(mutableListOf())
        } else {
            result.last().add(item)
        }
    }
    return result
}
package day5

fun operateOnList(list: List<Int>, op: (Int) -> Int): List<Int> {
    return list.map { op(it) }
}

fun main() {
    val result = operateOnList(listOf(1, 2, 3)) { it * it }
    println("Squares: $result")  // Output: [1, 4, 9]
}

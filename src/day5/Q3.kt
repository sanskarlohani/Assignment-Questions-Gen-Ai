package day5

fun applyTwice(x: Int, op: (Int) -> Int): Int {
    return op(op(x))
}

fun main() {
    val result = applyTwice(3) { it + 1 }  // 3 -> 4 -> 5
    println("Result after applying twice: $result")
}

package day5

//A higher-order function takes functions as parameters or returns a function.

fun applyOperation(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

fun main() {
    val result = applyOperation(5, 3) { x, y -> x + y }
    println("Result: $result")
}

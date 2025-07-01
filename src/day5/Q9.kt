package day5

fun divide(a: Int, b: Int) {
    try {
        val result = a / b
        println("Result: $result")
    } catch (e: ArithmeticException) {
        println("Cannot divide by zero!")
    } finally {
        println("Execution done")
    }
}

fun main() {
    divide(10, 0)
}

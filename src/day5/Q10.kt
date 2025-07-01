package day5

class NegativeNumberException(message: String): Exception(message)

fun checkPositive(number: Int) {
    if (number < 0) {
        throw NegativeNumberException("Number is negative!")
    } else {
        println("Number is positive: $number")
    }
}

fun main() {
    try {
        checkPositive(-5)
    } catch (e: NegativeNumberException) {
        println(e.message)
    }
}

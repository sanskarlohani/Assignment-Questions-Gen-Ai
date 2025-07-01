package day5

fun main() {
    try {
        val number = "abc".toInt()
        println("Parsed number: $number")
    } catch (e: NumberFormatException) {
        println("Invalid input! Cannot convert to integer.")
    }
}

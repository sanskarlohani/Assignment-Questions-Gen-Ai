package day3

fun main() {
    val tempCelsius = 100
    when {
        tempCelsius < 50 -> println("Too Cold")
        tempCelsius in 50..70 -> println("Perfect")
        else -> println("Too Hot")
    }
}
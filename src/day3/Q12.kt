package day3

fun main() {
    val coffeePrice = 20
    when (coffeePrice) {
        in 1..3 -> println("Low price! Take a sip")
        in 4..6 -> println("Medium price. Worth it to take")
        else -> println("You Cannot Afford it")
    }
}
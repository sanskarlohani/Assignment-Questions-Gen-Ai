package day3

fun main () {
    val tipNumber = (1..4).random()
    when(tipNumber) {
        1 -> println("Tip: Always stir your coffee!")
        2 -> println("Tip: Try it black once in a while!")
        3 -> println("Tip: Don't forget to smile while sipping!")
        4 -> println("Tip: Double shot means double energy!")
    }
}
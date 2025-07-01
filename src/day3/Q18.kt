package day3

fun main () {
    val tipNumber = (1..4).random()
    when(tipNumber) {
        1 -> println("Tip: Always stir your coffee!")
        2 -> println("Tip: Try Black Coffee SomeDay")
        3 -> println("Tip: Don't forget to give a Tip ")
        4 -> println("Tip: double energy drink coffee")
    }
}
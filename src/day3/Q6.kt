package day3
fun main() {
    val day = 9
    val coffee = when (day) {
        1 -> "Espresso"
        2 -> "Latte"
        3 -> "Cappuccino"
        4 -> "Mocha"
        5 -> "Soft Coffee"
        6 -> "Hot Coffee"
        7 -> "Cold Coffee"
        else -> "Water"
    }
    println("Today's coffee: $coffee")
}
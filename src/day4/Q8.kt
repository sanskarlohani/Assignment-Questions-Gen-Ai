package day4


data class Barista(
    val name: String,
    val favoriteCoffee: String?)

fun printFavorite(barista: Barista) {
    println("${barista.name}'s favorite: ${barista.favoriteCoffee ?: "Unknown"}")
}
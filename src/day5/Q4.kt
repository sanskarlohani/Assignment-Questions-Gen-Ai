package day5

fun main() {
    val names = listOf("Sam", "Rose", "Lina")

    val upperNames = names.map { it.uppercase() }  // returns new list
    println("Mapped list: $upperNames")

    names.forEach { println("Name: $it") }  // just iterates
}

// map() returns a new list;
//forEach() performs an action but returns nothing.
package day5

fun readElement(list: List<String>, index: Int) {
    try {
        println("Element: ${list[index]}")
    } catch (e: IndexOutOfBoundsException) {
        println("Index is out of range.")
    }
}

fun main() {
    val names = listOf("Sam", "Rose")
    readElement(names, 5)
}

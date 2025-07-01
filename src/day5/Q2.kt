package day5

fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6)

    // Using inbuilt filter
    val evens = numbers.filter { it % 2 == 0 }
    println("Even numbers using filter: $evens")

    // Custom HOF
    fun filterList(list: List<Int>, condition: (Int) -> Boolean): List<Int> {
        val result = mutableListOf<Int>()
        for (item in list) {
            if (condition(item)) result.add(item)
        }
        return result
    }

    val customEvens = filterList(numbers) { it % 2 == 0 }
    println("Even numbers using custom HOF: $customEvens")
}

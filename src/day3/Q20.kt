package day3

fun main() {
    for (i in 1..10) {
        if (i == 3 || i == 7) {
            println("Skipping non caffeine contained order $i")
            continue
        }
        println("Processing coffee order $i")
    }
}
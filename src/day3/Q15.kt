package day3

fun main() {
    var stock = 10
    do {
        println("Coffee stock: $stock")
        stock-- // it decreases the stock by one (--)
    } while (stock > 0)
}
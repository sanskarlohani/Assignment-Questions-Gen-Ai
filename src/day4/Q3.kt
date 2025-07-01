package day4

data class CoffeeOrder(val type: String, val size: String)
fun printOrder(order: CoffeeOrder) = println("Order: ${order.size} ${order.type}")
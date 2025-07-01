package day4

fun calculateTotal(quantity: Int, pricePerCup: Double?): Double {
    return quantity * (pricePerCup ?: 0.0)
}
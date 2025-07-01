package day4

// 9. Filter coffee types starting with 'C'
fun filterCoffeeTypes(coffees: List<String>): List<String> =
    coffees.filter { it.startsWith("C") }


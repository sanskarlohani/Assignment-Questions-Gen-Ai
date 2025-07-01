package day4

fun greetCustomer(name: String?): String = "Hello, ${name ?: "Guest"}!"
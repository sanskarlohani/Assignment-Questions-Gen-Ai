package day5

//throw is used to actually throw an exception.

//throws is not used in Kotlin like in Java.

class LoginFailedException(message: String): Exception(message)

fun login(username: String, password: String) {
    if (username != "admin" || password != "1234") {
        throw LoginFailedException("Login failed! Invalid credentials.")
    } else {
        println("Login successful!")
    }
}

fun main() {
    try {
        login("user", "wrongpass")
    } catch (e: LoginFailedException) {
        println(e.message)
    }
}

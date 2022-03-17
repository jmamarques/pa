package com.pa

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Class2Application

fun main(args: Array<String>) {

//    runApplication<Class2Application>(*args)
//    extraMain(args)
    // ex 1
    println(factorial(4))
    // ex tail recursive
    println(factorialTail(5))

}
fun factorialTail(n: Int): Int {
    tailrec fun tailFactorial(n: Int, r: Int): Int = if (n <= 1) r else tailFactorial(n-1, r*n)
    return tailFactorial(n, 1)
}

fun factorial(n: Int): Int = if( n <= 1) 1
        else n * factorial(n - 1)

fun extraMain(args: Array<String>) {
    println(args.contentToString())
    println(describe(1))
    println(describe("Hello"))
    println(describe(1000L))
    println(describe(2))
    println(describe("other"))


    val x = 10
    val y = 9
    if (x in 1..y+1) {
        println("fits in range")
    }

    val list = listOf("a", "b", "c")

    if (-1 !in 0..list.lastIndex) {
        println("-1 is out of range")
    }
    if (list.size !in list.indices) {
        println("list size is out of valid list indices range, too")
    }

    when {
        "orange" in list -> println("juicy")
        "apple" in list -> println("apple is fine too")
    }

    list.filter { it.startsWith("a") }
        .sortedBy { it }
        .map { it.uppercase() }
        .forEach { println(it) }
}

fun describe(obj: Any): String =
    when (obj) {
        1          -> "One"
        "Hello"    -> "Greeting"
        is Long    -> "Long"
        !is String -> "Not a string"
        else       -> "Unknown"
    }




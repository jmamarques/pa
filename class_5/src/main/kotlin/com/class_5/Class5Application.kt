package com.class_5

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Class5Application

fun main(args: Array<String>) {
//    runApplication<Class5Application>(*args)

}
@Target(AnnotationTarget.FUNCTION)
annotation class TestCase (val desc: String)

class MyTest {
    @TestCase("grandessíssimo teste")
    fun testMyClass() {
    }
}
package kr.app.calto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CalToApplication

fun main(args: Array<String>) {
    runApplication<CalToApplication>(*args)
}

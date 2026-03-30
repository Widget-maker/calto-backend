package kr.app.calto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [
    DataSourceAutoConfiguration::class
])
class CalToApplication

fun main(args: Array<String>) {
    runApplication<CalToApplication>(*args)
}

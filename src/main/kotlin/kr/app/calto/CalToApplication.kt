package kr.app.calto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class CalToApplication

fun main(args: Array<String>) {
    runApplication<CalToApplication>(*args)
}

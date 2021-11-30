package it.tarczynski.r4j

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class R4jApplication

fun main(args: Array<String>) {
    runApplication<R4jApplication>(*args)
}

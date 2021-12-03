package it.tarczynski.r4j

import it.tarczynski.r4j.infrastructure.config.PriceServiceProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(PriceServiceProperties::class)
class R4jApplication

fun main(args: Array<String>) {
    runApplication<R4jApplication>(*args)
}

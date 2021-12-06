package it.tarczynski.r4j.test

import it.tarczynski.r4j.R4jApplication
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [R4jApplication::class],
    properties = ["application.environment=integration"],
)
internal class BaseIntegrationTest

internal fun times(repetitions: Int, possiblyThrowing: () -> Unit) {
    for (i in 0..repetitions) {
        try {
            possiblyThrowing.invoke()
        } catch (_: Exception) {
        }
    }
}

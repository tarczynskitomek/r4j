package it.tarczynski.r4j.infrastructure.config

object Constants {

    object CircuitBreaker {
        const val PRICE_REPOSITORY = "price-repository"
    }

    object Retry {
        const val PRICE_REPOSITORY = "price-repository"
    }

    object SpringProfile {
        const val INTEGRATION = "integration"
        const val NOT_INTEGRATION = "!$INTEGRATION"
        const val WITH_RESILIENCE = "with-resilience"
    }
}

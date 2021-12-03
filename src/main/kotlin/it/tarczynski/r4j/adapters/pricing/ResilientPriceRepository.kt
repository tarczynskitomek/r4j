package it.tarczynski.r4j.adapters.pricing

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.decorators.Decorators
import io.github.resilience4j.retry.Retry
import it.tarczynski.r4j.domain.product.ProductId
import it.tarczynski.r4j.domain.product.Price
import it.tarczynski.r4j.domain.product.PriceRepository
import it.tarczynski.r4j.infrastructure.loggerFor
import org.slf4j.Logger

class ResilientPriceRepository(
    private val delegate: PriceRepository,
    private val circuitBreaker: CircuitBreaker,
    private val retry: Retry,
) : PriceRepository {

    private val logger: Logger = loggerFor<ResilientPriceRepository>()

    override fun findPricesBy(
        productId: ProductId,
    ): Set<Price> {
        logger.info("Fetching prices...")
        return Decorators.ofSupplier { delegate.findPricesBy(productId) }
            .withRetry(retry)
            .withCircuitBreaker(circuitBreaker)
            .withFallback { ex ->
                // don't do this, log full exception with stacktrace!
                logger.warn("Failed to fetch prices for product [${productId.raw}]", ex.message)
                emptySet()
            }
            .decorate()
            .get()
    }
}

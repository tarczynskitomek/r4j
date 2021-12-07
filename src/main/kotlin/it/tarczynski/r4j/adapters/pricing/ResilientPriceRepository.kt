package it.tarczynski.r4j.adapters.pricing

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.decorators.Decorators
import io.github.resilience4j.retry.Retry
import it.tarczynski.r4j.domain.product.Price
import it.tarczynski.r4j.domain.product.PriceRepository
import it.tarczynski.r4j.domain.product.ProductId
import it.tarczynski.r4j.infrastructure.loggerFor
import org.slf4j.Logger

class ResilientPriceRepository(
    private val delegate: PriceRepository,
    private val circuitBreaker: CircuitBreaker,
    private val retry: Retry,
    private val priceCache: PriceCache,
) : PriceRepository {

    private val logger: Logger = loggerFor<ResilientPriceRepository>()

    override fun findPricesBy(
        productId: ProductId,
    ): Set<Price> {
        logger.info("Fetching prices...")
        return Decorators.ofSupplier { calculateAndCachePrices(productId) }
            .withRetry(retry)
            .withCircuitBreaker(circuitBreaker)
            .withFallback { ex ->
                logger.warn("Failed to fetch prices for product [${productId.raw}]", ex)
                priceCache[productId] ?: emptySet()
            }
            .decorate()
            .get()
    }

    /**
     * Resilience4J also offers caching!
     */
    private fun calculateAndCachePrices(
        productId: ProductId,
    ): Set<Price> {
        return delegate.findPricesBy(productId).also { prices -> priceCache[productId] = prices }
    }
}

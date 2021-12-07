package it.tarczynski.r4j.adapters.pricing

import it.tarczynski.r4j.domain.product.Price
import it.tarczynski.r4j.domain.product.ProductId
import it.tarczynski.r4j.infrastructure.loggerFor
import org.slf4j.Logger
import java.util.concurrent.ConcurrentHashMap

class ConcurrentMapPriceCache : PriceCache {

    private val logger: Logger = loggerFor<ConcurrentMapPriceCache>()
    private val prices: MutableMap<ProductId, Set<Price>> = ConcurrentHashMap()

    override fun set(
        productId: ProductId,
        productPrices: Set<Price>,
    ) {
        prices[productId] = productPrices
    }

    override fun get(productId: ProductId): Set<Price>? {
        return prices[productId]
            .also { prices ->
                logger.info("Returning cached prices [{}] for product [{}]", prices, productId)
            }
    }
}

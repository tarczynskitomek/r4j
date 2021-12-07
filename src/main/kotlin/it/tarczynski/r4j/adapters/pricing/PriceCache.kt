package it.tarczynski.r4j.adapters.pricing

import it.tarczynski.r4j.domain.product.Price
import it.tarczynski.r4j.domain.product.ProductId

interface PriceCache {

    operator fun set(productId: ProductId, productPrices: Set<Price>)

    operator fun get(productId: ProductId): Set<Price>?
}

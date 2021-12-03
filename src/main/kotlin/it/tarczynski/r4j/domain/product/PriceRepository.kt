package it.tarczynski.r4j.domain.product

interface PriceRepository {

    fun findPricesBy(
        productId: ProductId,
    ): Set<Price>
}

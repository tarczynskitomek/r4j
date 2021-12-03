package it.tarczynski.r4j.domain.product

import java.math.BigDecimal

private val fallbackPrice = Price(BigDecimal.TEN, Currency.PLN)

class PriceService(
    private val priceRepository: PriceRepository,
    private val pricingPolicy: PricingPolicy,
) {

    fun getBestPriceFor(
        product: ProductId,
    ): Price {
        val prices: Set<Price> = priceRepository.findPricesBy(product)
        return pricingPolicy.currentBestPrice(prices) ?: fallbackPrice
    }
}

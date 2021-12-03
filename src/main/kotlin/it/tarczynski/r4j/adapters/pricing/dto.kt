package it.tarczynski.r4j.adapters.pricing

import it.tarczynski.r4j.domain.product.Currency
import it.tarczynski.r4j.domain.product.Price
import java.math.BigDecimal

data class PricesData(
    val prices: List<PriceData>
) {

    data class PriceData(
        val amount: BigDecimal,
        val baseCurrency: Currency,
        val conversionRate: BigDecimal,
    ) {

        fun toDomain(): Price {
            return Price(
                amount = amount,
                currency = baseCurrency,
            )
        }
    }
}

data class CalculatePricesRequest(
    val productId: String,
)

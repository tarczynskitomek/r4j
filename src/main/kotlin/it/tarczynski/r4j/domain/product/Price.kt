package it.tarczynski.r4j.domain.product

import java.math.BigDecimal

data class Price(
    val amount: BigDecimal,
    val currency: Currency,
)

enum class Currency {
    PLN, EUR, USD, GBP
}

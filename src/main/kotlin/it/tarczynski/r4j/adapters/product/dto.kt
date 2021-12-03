package it.tarczynski.r4j.adapters.product

import it.tarczynski.r4j.domain.product.Currency
import it.tarczynski.r4j.domain.product.Price
import it.tarczynski.r4j.domain.product.Product
import it.tarczynski.r4j.domain.product.ProductId
import it.tarczynski.r4j.domain.product.Manufacturer
import it.tarczynski.r4j.domain.product.Name
import java.math.BigDecimal

data class ProductData(
    val id: String,
    val name: String,
    val manufacturer: String,
    val price: PriceData?,
) {

    companion object {

        fun from(
            product: Product,
            price: Price?,
        ): ProductData {
            val (productId: ProductId, name: Name, manufacturer: Manufacturer) = product
            return ProductData(
                id = productId.raw,
                name = name.full,
                manufacturer = manufacturer.name,
                price = price?.let {
                    PriceData(
                        value = it.amount,
                        currency = it.currency,
                    )
                }
            )
        }

    }

    data class PriceData(
        val value: BigDecimal,
        val currency: Currency,
    )
}

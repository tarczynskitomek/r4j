package it.tarczynski.r4j.adapters.product

import it.tarczynski.r4j.domain.product.ProductId
import it.tarczynski.r4j.domain.product.Price
import it.tarczynski.r4j.domain.product.PriceService
import it.tarczynski.r4j.domain.product.Product
import it.tarczynski.r4j.domain.product.ProductRepository

class ProductFacade(
    private val priceService: PriceService,
    private val productRepository: ProductRepository,
) {

    fun productWithPrice(
        productId: String,
    ): ProductData {
        val productId: ProductId = ProductId(productId)
        val price: Price = priceService.getBestPriceFor(productId)
        val product: Product = productRepository.get(productId)
        return ProductData.from(product, price)
    }
}

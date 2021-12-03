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
        val id = ProductId(productId)
        val price: Price = priceService.getBestPriceFor(id)
        val product: Product = productRepository.getBy(id)
        return ProductData.from(product, price)
    }
}

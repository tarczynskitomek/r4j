package it.tarczynski.r4j.adapters.product.web

import it.tarczynski.r4j.adapters.product.ProductData
import it.tarczynski.r4j.adapters.product.ProductFacade
import it.tarczynski.r4j.adapters.annotation.ApiV1
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@ApiV1(path = "/products")
class ProductResource(
    private val productFacade: ProductFacade,
) {

    @GetMapping("/{id}")
    fun getProduct(
        @PathVariable id: String,
    ): ProductData {
        return productFacade.productWithPrice(id)
    }
}

package it.tarczynski.r4j.adapters.product.repository

import it.tarczynski.r4j.adapters.product.ProductNotFoundException
import it.tarczynski.r4j.domain.makeProduct
import it.tarczynski.r4j.domain.product.Product
import it.tarczynski.r4j.domain.product.ProductId
import it.tarczynski.r4j.domain.product.ProductRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryProductRepository : ProductRepository {

    private val products: MutableMap<ProductId, Product> = ConcurrentHashMap(
        mapOf(
            ProductId("slow") to makeProduct("slow", "Slow I am", "ACME Inc."),
            ProductId("fast") to makeProduct("fast", "Fast I am", "FOO Inc."),
            ProductId("flaky") to makeProduct("flaky", "Flaky I am", "BAR Inc."),
            ProductId("failing") to makeProduct("failing", "Failing I am", "BAZ Inc."),
        )
    )

    override fun save(product: Product): Product {
        products[product.id] = product
        return product
    }

    override fun findBy(id: ProductId): Product? = products[id]

    override fun findAll(): List<Product> = products.values.toList()

    override fun getBy(id: ProductId): Product = findBy(id) ?: throw ProductNotFoundException(id)
}


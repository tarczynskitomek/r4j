package it.tarczynski.r4j.domain.product

interface ProductRepository {

    fun save(product: Product): Product

    fun find(id: ProductId): Product?

    fun findAll(): List<Product>

    fun get(id: ProductId): Product
}

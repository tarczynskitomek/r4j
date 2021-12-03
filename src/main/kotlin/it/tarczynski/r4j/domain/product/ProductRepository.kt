package it.tarczynski.r4j.domain.product

interface ProductRepository {

    fun save(product: Product): Product

    fun findBy(id: ProductId): Product?

    fun findAll(): List<Product>

    fun getBy(id: ProductId): Product
}

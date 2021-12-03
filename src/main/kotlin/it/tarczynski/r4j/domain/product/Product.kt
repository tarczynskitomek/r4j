package it.tarczynski.r4j.domain.product


@JvmInline
value class ProductId(val raw: String)

@JvmInline
value class Manufacturer(val name: String)

data class Name(
    val short: String,
    val full: String,
)

data class Product(
    val id: ProductId,
    val name: Name,
    val manufacturer: Manufacturer,
) {

    fun changeName(newName: Name): Product = copy(name = newName)

    fun updateManufacturer(manufacturer: Manufacturer) = copy(manufacturer = manufacturer)

    private fun copy(
        name: Name = this.name,
        manufacturer: Manufacturer = this.manufacturer,
    ): Product {
        return Product(id = this.id, name = name, manufacturer = manufacturer)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}

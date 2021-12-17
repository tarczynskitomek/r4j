package it.tarczynski.r4j.domain

import it.tarczynski.r4j.domain.product.Manufacturer
import it.tarczynski.r4j.domain.product.Name
import it.tarczynski.r4j.domain.product.Product
import it.tarczynski.r4j.domain.product.ProductId

fun makeProduct(
    id: String,
    name: String,
    manufacturer: String,
) = Product(
    id = ProductId(id),
    name = Name(name, "This product is called: '$name'"),
    manufacturer = Manufacturer(manufacturer),
)

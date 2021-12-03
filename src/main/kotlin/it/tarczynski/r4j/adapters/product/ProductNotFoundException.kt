package it.tarczynski.r4j.adapters.product

import it.tarczynski.r4j.adapters.NotFoundException
import it.tarczynski.r4j.domain.product.ProductId

class ProductNotFoundException(id: ProductId) : NotFoundException("Product [${id.raw}] not found")

package it.tarczynski.r4j.infrastructure.error

import it.tarczynski.r4j.domain.product.ProductId

class ProductNotFoundException(id: ProductId) : NotFoundException("Product [${id.raw}] not found")

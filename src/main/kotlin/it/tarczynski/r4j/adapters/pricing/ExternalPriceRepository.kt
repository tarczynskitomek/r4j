package it.tarczynski.r4j.adapters.pricing

import it.tarczynski.r4j.adapters.pricing.PricesData.PriceData
import it.tarczynski.r4j.domain.product.ProductId
import it.tarczynski.r4j.domain.product.Price
import it.tarczynski.r4j.domain.product.PriceRepository
import it.tarczynski.r4j.infrastructure.config.PriceServiceProperties
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.*

class ExternalPriceRepository(
    private val restTemplate: RestTemplate,
    private val priceServiceConfig: PriceServiceProperties,
) : PriceRepository {

    override fun findPricesBy(
        productId: ProductId,
    ): Set<Price> {
        val request: RequestEntity<CalculatePricesRequest> = createRequest(productId)
        val response: ResponseEntity<PricesData> = send(request)
        return extractPrices(response)
    }

    private fun createRequest(
        productId: ProductId,
    ): RequestEntity<CalculatePricesRequest> {
        val uri: URI = createUri()
        val body = CalculatePricesRequest(productId.raw)
        return RequestEntity.put(uri).body(body)
    }

    private fun send(
        request: RequestEntity<CalculatePricesRequest>,
    ): ResponseEntity<PricesData> {
        return restTemplate.exchange(request, PricesData::class.java)
    }

    private fun extractPrices(
        response: ResponseEntity<PricesData>
    ): Set<Price> {
        return response.body
            ?.prices
            ?.map(PriceData::toDomain)
            ?.toSet()
            ?: throw IllegalStateException("Response body cannot be null")
    }

    private fun createUri(): URI {
        return UriComponentsBuilder.fromUriString(priceServiceConfig.uri)
            .path("/prices/do-calculate/${UUID.randomUUID()}")
            .build()
            .toUri()
    }
}

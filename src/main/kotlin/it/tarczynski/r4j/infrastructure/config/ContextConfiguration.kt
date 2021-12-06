package it.tarczynski.r4j.infrastructure.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryRegistry
import it.tarczynski.r4j.adapters.pricing.ExternalPriceRepository
import it.tarczynski.r4j.adapters.pricing.ResilientPriceRepository
import it.tarczynski.r4j.adapters.product.ProductFacade
import it.tarczynski.r4j.adapters.product.repository.InMemoryProductRepository
import it.tarczynski.r4j.domain.TimeMachine
import it.tarczynski.r4j.domain.product.PriceRepository
import it.tarczynski.r4j.domain.product.PriceService
import it.tarczynski.r4j.domain.product.PricingPolicy
import it.tarczynski.r4j.infrastructure.config.Constants.SpringProfile.NOT_INTEGRATION
import it.tarczynski.r4j.infrastructure.config.Constants.SpringProfile.WITH_RESILIENCE
import it.tarczynski.r4j.infrastructure.loggerFor
import org.slf4j.Logger
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate
import it.tarczynski.r4j.infrastructure.config.Constants.CircuitBreaker.PRICE_REPOSITORY as PRICE_REPOSITORY_CIRCUIT_BREAKER
import it.tarczynski.r4j.infrastructure.config.Constants.Retry.PRICE_REPOSITORY as PRICE_REPOSITORY_RETRY

@Configuration
class ContextConfiguration {

    private val log: Logger = loggerFor<ContextConfiguration>()

    @Bean
    fun productFacade(
        priceRepository: PriceRepository,
    ): ProductFacade {
        val priceService = priceService(priceRepository)
        return ProductFacade(priceService, InMemoryProductRepository())
    }

    @Bean
    fun priceRepository(
        properties: PriceServiceProperties,
        restTemplateBuilder: RestTemplateBuilder,
    ): PriceRepository {
        val restTemplate = restTemplate(restTemplateBuilder, properties)
        return ExternalPriceRepository(restTemplate, properties)
    }

    @Bean
    @Primary
    @Profile(NOT_INTEGRATION, WITH_RESILIENCE)
    fun resilientPriceRepository(
        priceRepository: PriceRepository,
        circuitBreakerRegistry: CircuitBreakerRegistry,
        retryRegistry: RetryRegistry,
    ): PriceRepository {
        val retry: Retry = retry(retryRegistry)
        val circuitBreaker: CircuitBreaker = circuitBreaker(circuitBreakerRegistry)
        return ResilientPriceRepository(priceRepository, circuitBreaker, retry)
    }

    fun priceService(
        priceRepository: PriceRepository,
    ): PriceService {
        val pricingPolicy = PricingPolicy(TimeMachine())
        return PriceService(priceRepository, pricingPolicy)
    }

    private fun restTemplate(
        restTemplateBuilder: RestTemplateBuilder,
        properties: PriceServiceProperties
    ): RestTemplate {
        return restTemplateBuilder
            .setReadTimeout(properties.readTimeout)
            .setConnectTimeout(properties.connectTimeout)
            .build()
    }

    private fun circuitBreaker(
        circuitBreakerRegistry: CircuitBreakerRegistry,
    ): CircuitBreaker =
        circuitBreakerRegistry.circuitBreaker(PRICE_REPOSITORY_CIRCUIT_BREAKER)
            .also { circuitBreaker: CircuitBreaker ->
                circuitBreaker.eventPublisher.onEvent { e -> log.info("Circuit Breaker event: [{}]", e.eventType) }
            }

    private fun retry(retryRegistry: RetryRegistry): Retry {
        return retryRegistry.retry(PRICE_REPOSITORY_RETRY)
            .also { retry: Retry ->
                retry.eventPublisher.onEvent { e -> log.info("Retry event: [{}]", e.eventType) }
            }
    }
}

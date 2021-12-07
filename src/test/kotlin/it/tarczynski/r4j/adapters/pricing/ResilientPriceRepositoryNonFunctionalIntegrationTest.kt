package it.tarczynski.r4j.adapters.pricing

import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.resetAllRequests
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.client.WireMock.verify
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent.Type.ERROR
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent.Type.FAILURE_RATE_EXCEEDED
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent.Type.NOT_PERMITTED
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent.Type.STATE_TRANSITION
import it.tarczynski.r4j.domain.product.PriceRepository
import it.tarczynski.r4j.domain.product.ProductId
import it.tarczynski.r4j.infrastructure.config.Constants.CircuitBreaker.PRICE_REPOSITORY
import it.tarczynski.r4j.test.BaseIntegrationTest
import it.tarczynski.r4j.test.times
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("with-resilience")
internal class ResilientPriceRepositoryNonFunctionalIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var resilientPriceRepository: PriceRepository

    @Autowired
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry

    @AfterEach
    fun cleanup() {
        // reset circuit breaker's stats between tests
        circuitBreakerRegistry.circuitBreaker(PRICE_REPOSITORY).reset()
        resetAllRequests()
    }

    @Test
    fun `should retry three times if remote service fails`() {
        // when
        resilientPriceRepository.findPricesBy(ProductId("failing"))

        // then
        verify(
            3,
            putRequestedFor(
                urlMatching("\\/prices\\/do-calculate\\/.*")
            ).withRequestBody(
                equalToJson("""{ "productId":"failing" }""")
            )
        )
    }

    @Test
    fun `should retry three times on timeouts`() {
        // when
        resilientPriceRepository.findPricesBy(ProductId("slow"))

        // then
        verify(
            3,
            putRequestedFor(
                urlMatching("\\/prices\\/do-calculate\\/.*")
            ).withRequestBody(
                equalToJson("""{ "productId":"slow" }""")
            )
        )
    }

    @Test
    fun `should open circuit breaker when error threshold is exceeded`() {
        // given
        val events: MutableList<CircuitBreakerEvent> = mutableListOf()

        // and
        circuitBreakerRegistry.circuitBreaker(PRICE_REPOSITORY).eventPublisher.onEvent { e -> events += e }

        // when
        times(repetitions = 5) {
            resilientPriceRepository.findPricesBy(ProductId("failing"))
        }

        // then
        assertThat(events.isNotEmpty()).isTrue
        assertThat(events.map { it.eventType }).isEqualTo(
            listOf(
                ERROR,
                ERROR,
                ERROR,
                ERROR,
                ERROR, // minimal number of calls = 5, all failed
                FAILURE_RATE_EXCEEDED, // we try again, failing
                STATE_TRANSITION,      // from CLOSE to OPEN
                NOT_PERMITTED,         // next call is rejected early
            )
        )
    }

    @Test
    fun `should not retry on not found`() {
        // when
        resilientPriceRepository.findPricesBy(ProductId("not-found"))

        // then
        verify(
            1, putRequestedFor(
                urlMatching("\\/prices\\/do-calculate\\/.*")
            ).withRequestBody(
                equalToJson("""{ "productId":"not-found" }""")
            )
        )
    }
}

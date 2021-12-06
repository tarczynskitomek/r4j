package it.tarczynski.r4j.infrastructure.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.put
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.http.UniformDistribution
import com.github.tomakehurst.wiremock.matching.RegexPattern
import com.github.tomakehurst.wiremock.matching.UrlPattern
import it.tarczynski.r4j.infrastructure.loggerFor
import org.slf4j.Logger
import org.springframework.context.annotation.Configuration
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
class WireMockConfigurer {

    private val logger: Logger = loggerFor<WireMockConfigurer>()
    private val wiremock: WireMockServer = WireMockServer(8090)

    companion object {
        private val hasStarted: AtomicBoolean = AtomicBoolean(false)
    }

    @PostConstruct
    fun startWireMockServer() {
        if (!hasStarted.get()) {
            logger.info("Starting WireMock on port 8090")
            wiremock.start()
            setupStubs()
            hasStarted.set(true)
        }
    }

    @PreDestroy
    fun stopWireMockSever() {
        if (hasStarted.get()) {
            logger.info("Stopping WireMock")
            wiremock.stop()
            hasStarted.set(false)
        }
    }

}

private fun setupStubs() {
    WireMock.configureFor(8090)
    stubFast()
    stubSlow()
    stubError()
    stubFlaky()
}

private fun stubFast() {
    stubFor(
        put(UrlPattern(RegexPattern("\\/prices\\/do-calculate\\/.*"), true))
            .withRequestBody(equalToJson("""{ "productId":"fast" }"""))
            .willReturn(okJsonBuilder())
    )
}

private fun stubSlow() {
    stubFor(
        put(UrlPattern(RegexPattern("\\/prices\\/do-calculate\\/.*"), true))
            .withRequestBody(equalToJson("""{ "productId":"slow" }"""))
            .willReturn(okJsonBuilder().withFixedDelay(150))
    )
}

private fun stubFlaky() {
    stubFor(
        put(UrlPattern(RegexPattern("\\/prices\\/do-calculate\\/.*"), true))
            .withRequestBody(equalToJson("""{ "productId":"flaky" }"""))
            .willReturn(
                okJsonBuilder().withRandomDelay(
                    UniformDistribution(50, 150)
                )
            )
    )
}

private fun stubError() {
    stubFor(
        put(UrlPattern(RegexPattern("\\/prices\\/do-calculate\\/.*"), true))
            .withRequestBody(equalToJson("""{ "productId":"failing" }"""))
            .willReturn(serverError())
    )
}

private fun okJsonBuilder() = okJson(
    """{ "prices": [
                    |{"amount":"10.99", "baseCurrency": "PLN", "conversionRate": "1.0"},
                    |{"amount":"11.99", "baseCurrency": "PLN", "conversionRate": "1.0"},
                    |{"amount":"9.99", "baseCurrency": "PLN", "conversionRate": "1.0"}
                    |] }""".trimMargin()
)



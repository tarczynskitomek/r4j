package it.tarczynski.r4j.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "services.price-service")
data class PriceServiceProperties(
    val uri: String,
    val readTimeout: Duration,
    val connectTimeout: Duration,
)

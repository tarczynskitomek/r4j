package it.tarczynski.r4j.infrastructure.error

import it.tarczynski.r4j.adapters.NotFoundException
import it.tarczynski.r4j.adapters.product.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(
        ProductNotFoundException::class
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(
        ex: NotFoundException,
    ): ErrorResponse {
        return ErrorResponse(
            message = ex.message ?: "Requested resource was not found"
        )
    }
}

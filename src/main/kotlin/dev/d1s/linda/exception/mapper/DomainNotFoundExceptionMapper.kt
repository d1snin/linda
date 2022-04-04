package dev.d1s.linda.exception.mapper

import dev.d1s.advice.domain.ErrorResponseData
import dev.d1s.advice.mapper.ExceptionMapper
import dev.d1s.linda.exception.DomainNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class DomainNotFoundExceptionMapper : ExceptionMapper {

    override fun map(exception: Exception): ErrorResponseData? = if (exception is DomainNotFoundException) {
        ErrorResponseData(HttpStatus.NOT_FOUND, exception.message!!)
    } else {
        null
    }
}
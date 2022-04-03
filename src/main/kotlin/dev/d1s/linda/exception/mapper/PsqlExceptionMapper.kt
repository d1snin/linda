package dev.d1s.linda.exception.mapper

import dev.d1s.advice.domain.ErrorResponseData
import dev.d1s.advice.mapper.ExceptionMapper
import org.postgresql.util.PSQLException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class PsqlExceptionMapper : ExceptionMapper {

    override fun map(exception: Exception): ErrorResponseData? = if (exception is PSQLException) {
        ErrorResponseData(HttpStatus.BAD_REQUEST, exception.serverErrorMessage?.detail ?: "No details provided.")
    } else {
        null
    }
}
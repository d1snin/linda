package dev.d1s.linda.controller.advice

import dev.d1s.advice.exception.HttpStatusException
import dev.d1s.linda.exception.DomainNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerControllerAdvice {

    @ExceptionHandler
    fun handleDomainNotFoundException(exception: DomainNotFoundException) {
        throw HttpStatusException(HttpStatus.NOT_FOUND, exception.message!!)
    }
}
package uno.d1s.linda.exception

import org.springframework.http.HttpStatus
import uno.d1s.advice.exception.AbstractHttpStatusException

open class DomainNotFoundException(message: String) : AbstractHttpStatusException(HttpStatus.NOT_FOUND, message)
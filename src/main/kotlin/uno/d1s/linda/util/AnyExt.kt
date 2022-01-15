package uno.d1s.linda.util

import org.springframework.http.ResponseEntity

fun <T> T?.checkNotNull(property: String) = this ?: throw IllegalArgumentException("$property must be not null.")
fun <T> T.ok() = ResponseEntity.ok(this)
package uno.d1s.linda.dto.redirect

import java.time.Instant

data class RedirectDto(
    val id: String,
    val shortLink: String,
    val creationTime: Instant
)

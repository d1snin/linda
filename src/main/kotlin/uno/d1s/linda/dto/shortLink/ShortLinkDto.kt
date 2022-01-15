package uno.d1s.linda.dto.shortLink

import java.time.Instant

data class ShortLinkDto(
    val id: String,
    val url: String,
    val alias: String,
    val creationTime: Instant,
    val redirects: List<String>
)
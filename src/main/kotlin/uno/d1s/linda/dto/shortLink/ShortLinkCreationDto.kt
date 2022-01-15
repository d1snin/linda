package uno.d1s.linda.dto.shortLink

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class ShortLinkCreationDto(
    // see https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
    @field:Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
    val url: String,

    @field:NotBlank
    val aliasGeneratorId: String
)
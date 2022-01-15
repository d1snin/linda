package uno.d1s.linda.dto.shortLink

import uno.d1s.linda.dto.BulkRemovalDto
import javax.validation.constraints.NotEmpty

data class BulkShortLinkRemovalDto(
    @field:NotEmpty override val identifiers: List<String>
) : BulkRemovalDto
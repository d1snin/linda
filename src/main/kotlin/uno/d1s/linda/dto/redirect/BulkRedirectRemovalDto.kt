package uno.d1s.linda.dto.redirect

import uno.d1s.linda.dto.BulkRemovalDto
import javax.validation.constraints.NotEmpty

data class BulkRedirectRemovalDto(
    @field:NotEmpty override val identifiers: List<String>
) : BulkRemovalDto

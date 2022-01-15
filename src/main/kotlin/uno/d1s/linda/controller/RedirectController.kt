package uno.d1s.linda.controller

import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import uno.d1s.linda.constant.mapping.api.*
import uno.d1s.linda.dto.redirect.BulkRedirectRemovalDto
import uno.d1s.linda.dto.redirect.RedirectDto
import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import javax.validation.constraints.NotBlank

@Validated
interface RedirectController {

    @GetMapping(
        REDIRECTS_FIND_ALL_MAPPING,
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findAll(@RequestParam page: Int?, @RequestParam size: Int?): ResponseEntity<Page<RedirectDto>>

    @GetMapping(
        REDIRECTS_FIND_ALL_BY_SHORT_LINK_MAPPING,
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findAllByShortLink(
        @PathVariable @NotBlank identifier: String,
        @RequestParam(
            "shortLinkFindingStrategyType",
            required = false
        ) shortLinkFindingStrategyType: ShortLinkFindingStrategyType?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ): ResponseEntity<Page<RedirectDto>>

    @GetMapping(REDIRECTS_FIND_BY_ID_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable @NotBlank identifier: String): ResponseEntity<RedirectDto>

    @DeleteMapping(REDIRECTS_REMOVE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun remove(@PathVariable @NotBlank identifier: String): ResponseEntity<*>

    @DeleteMapping(
        REDIRECTS_BULK_REMOVE_MAPPING,
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun removeAll(@RequestBody bulkRedirectRemovalDto: BulkRedirectRemovalDto): ResponseEntity<*>

    @DeleteMapping(REDIRECTS_REMOVE_ALL_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun removeAll(): ResponseEntity<*>

    @DeleteMapping(REDIRECTS_REMOVE_ALL_BY_SHORT_LINK_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun removeAllByShortLink(
        @PathVariable identifier: String,
        @RequestParam("strategy", required = false) shortLinkFindingStrategyType: ShortLinkFindingStrategyType?
    ): ResponseEntity<*>
}
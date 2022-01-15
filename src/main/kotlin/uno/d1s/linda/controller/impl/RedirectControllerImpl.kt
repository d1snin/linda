package uno.d1s.linda.controller.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import uno.d1s.linda.controller.RedirectController
import uno.d1s.linda.converter.DtoConverter
import uno.d1s.linda.domain.Redirect
import uno.d1s.linda.dto.redirect.BulkRedirectRemovalDto
import uno.d1s.linda.dto.redirect.RedirectDto
import uno.d1s.linda.service.RedirectService
import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import uno.d1s.linda.strategy.shortLink.byType
import uno.d1s.linda.util.noContent
import uno.d1s.linda.util.ok
import uno.d1s.linda.util.pagination.toPage

@RestController
class RedirectControllerImpl : RedirectController {

    @Autowired
    private lateinit var redirectService: RedirectService

    @Autowired
    private lateinit var redirectDtoConverter: DtoConverter<Redirect, RedirectDto>

    private val Redirect.dto get() = redirectDtoConverter.convertToDto(this)
    private val List<Redirect>.dtoList get() = redirectDtoConverter.convertToDtoList(this)

    override fun findAll(page: Int?, size: Int?): ResponseEntity<Page<RedirectDto>> =
        redirectService.findAll().dtoList.toPage(page, size).ok()

    override fun findAllByShortLink(
        identifier: String,
        shortLinkFindingStrategyType: ShortLinkFindingStrategyType?,
        page: Int?,
        size: Int?
    ): ResponseEntity<Page<RedirectDto>> =
        redirectService.findAllByShortLink(
            byType(shortLinkFindingStrategyType, identifier)
        ).dtoList.toPage(
            page, size
        ).ok()


    override fun findById(identifier: String): ResponseEntity<RedirectDto> =
        redirectService.findById(identifier).dto.ok()

    override fun remove(identifier: String): ResponseEntity<*> {
        redirectService.remove(identifier)
        return noContent
    }

    override fun removeAll(bulkRedirectRemovalDto: BulkRedirectRemovalDto): ResponseEntity<*> {
        redirectService.removeAll(bulkRedirectRemovalDto)
        return noContent
    }

    override fun removeAll(): ResponseEntity<*> {
        redirectService.removeAll()
        return noContent
    }

    override fun removeAllByShortLink(
        identifier: String,
        shortLinkFindingStrategyType: ShortLinkFindingStrategyType?
    ): ResponseEntity<*> {
        redirectService.removeAllByShortLink(byType(shortLinkFindingStrategyType, identifier))
        return noContent
    }
}
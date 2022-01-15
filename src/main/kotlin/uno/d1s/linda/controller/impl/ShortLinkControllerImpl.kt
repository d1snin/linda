package uno.d1s.linda.controller.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import uno.d1s.linda.controller.ShortLinkController
import uno.d1s.linda.converter.DtoConverter
import uno.d1s.linda.domain.ShortLink
import uno.d1s.linda.dto.shortLink.BulkShortLinkRemovalDto
import uno.d1s.linda.dto.shortLink.ShortLinkCreationDto
import uno.d1s.linda.dto.shortLink.ShortLinkDto
import uno.d1s.linda.service.ShortLinkService
import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import uno.d1s.linda.strategy.shortLink.byType
import uno.d1s.linda.util.noContent
import uno.d1s.linda.util.ok
import uno.d1s.linda.util.pagination.toPage

@RestController
class ShortLinkControllerImpl : ShortLinkController {

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Autowired
    private lateinit var shortLinkDtoConverter: DtoConverter<ShortLink, ShortLinkDto>

    private val ShortLink.dto get() = shortLinkDtoConverter.convertToDto(this)

    override fun findAll(page: Int?, size: Int?): ResponseEntity<Page<ShortLinkDto>> =
        shortLinkDtoConverter.convertToDtoList(
            shortLinkService.findAll()
        ).toPage(page, size).ok()

    override fun find(
        identifier: String,
        shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<ShortLinkDto> =
        shortLinkService.find(
            byType(shortLinkFindingStrategy, identifier)
        ).dto.ok()

    override fun create(shortLinkCreationDto: ShortLinkCreationDto):
            ResponseEntity<ShortLinkDto> =
        shortLinkService.create(shortLinkCreationDto).dto.ok()

    override fun remove(
        identifier: String,
        shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<Any> {
        shortLinkService.remove(
            shortLinkService.find(byType(shortLinkFindingStrategy, identifier))
        )
        return noContent
    }

    override fun removeAll(): ResponseEntity<Any> {
        shortLinkService.removeAll()
        return noContent
    }

    override fun removeAll(
        bulkShortLinkRemovalDto: BulkShortLinkRemovalDto
    ): ResponseEntity<*> {
        shortLinkService.removeAll(bulkShortLinkRemovalDto)
        return noContent
    }
}
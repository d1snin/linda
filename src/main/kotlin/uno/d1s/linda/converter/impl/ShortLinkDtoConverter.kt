package uno.d1s.linda.converter.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uno.d1s.linda.converter.AbstractDtoConverter
import uno.d1s.linda.domain.ShortLink
import uno.d1s.linda.dto.shortLink.ShortLinkDto
import uno.d1s.linda.service.RedirectService
import uno.d1s.linda.util.checkNotNull

@Component
class ShortLinkDtoConverter : AbstractDtoConverter<ShortLink, ShortLinkDto>() {

    @Autowired
    private lateinit var redirectService: RedirectService

    override fun convertToDto(entity: ShortLink): ShortLinkDto =
        ShortLinkDto(
            entity.id.checkNotNull("id"),
            entity.url,
            entity.alias,
            entity.creationTime.checkNotNull("creation time"),
            entity.redirects.map {
                it.id.checkNotNull("redirect id")
            })

    override fun convertToEntity(dto: ShortLinkDto): ShortLink =
        ShortLink(dto.url, dto.alias).apply {
            id = dto.id
            creationTime = dto.creationTime
            redirects = dto.redirects.map {
                redirectService.findById(it)
            }
        }
}
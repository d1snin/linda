package uno.d1s.linda.converter.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uno.d1s.linda.converter.AbstractDtoConverter
import uno.d1s.linda.domain.Redirect
import uno.d1s.linda.dto.redirect.RedirectDto
import uno.d1s.linda.service.ShortLinkService
import uno.d1s.linda.strategy.shortLink.byId
import uno.d1s.linda.util.checkNotNull

@Component
class RedirectDtoConverter : AbstractDtoConverter<Redirect, RedirectDto>() {

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    override fun convertToDto(entity: Redirect): RedirectDto =
        RedirectDto(
            entity.id.checkNotNull("id"),
            entity.shortLink.id.checkNotNull("short link id"),
            entity.creationTime.checkNotNull("creation time")
        )

    override fun convertToEntity(dto: RedirectDto): Redirect =
        Redirect(shortLinkService.find(byId(dto.shortLink))).apply {
            id = dto.id
            creationTime = dto.creationTime
        }
}
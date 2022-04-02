package uno.d1s.linda.service

import uno.d1s.linda.domain.ShortLink
import uno.d1s.linda.dto.shortLink.BulkShortLinkRemovalDto
import uno.d1s.linda.dto.shortLink.ShortLinkCreationDto
import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy

interface ShortLinkService {

    fun findAll(): List<ShortLink>

    fun find(shortLinkFindingStrategy: ShortLinkFindingStrategy): ShortLink

    fun create(url: String, aliasGeneratorId: String): ShortLink

    fun create(shortLinkCreationDto: ShortLinkCreationDto): ShortLink

    fun remove(shortLink: ShortLink): ShortLink

    fun remove(shortLinkFindingStrategy: ShortLinkFindingStrategy): ShortLink

    fun removeAll(): List<ShortLink>

    fun removeAll(shortLinks: List<ShortLink>): List<ShortLink>

    fun removeAll(bulkShortLinkRemovalDto: BulkShortLinkRemovalDto): List<ShortLink>

    fun doesAliasExist(alias: String): Boolean
}
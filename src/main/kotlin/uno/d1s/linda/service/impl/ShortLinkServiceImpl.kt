package uno.d1s.linda.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uno.d1s.caching.annotation.aop.CacheEvictByIdProvider
import uno.d1s.caching.annotation.aop.CachePutByIdProvider
import uno.d1s.caching.annotation.aop.CacheableList
import uno.d1s.linda.cache.idProvider.ShortLinkIdProvider
import uno.d1s.linda.constant.cache.SHORT_LINKS_CACHE
import uno.d1s.linda.domain.ShortLink
import uno.d1s.linda.dto.shortLink.BulkShortLinkRemovalDto
import uno.d1s.linda.dto.shortLink.ShortLinkCreationDto
import uno.d1s.linda.exception.impl.ShortLinkNotFoundException
import uno.d1s.linda.repository.ShortLinkRepository
import uno.d1s.linda.service.AliasGeneratorService
import uno.d1s.linda.service.ShortLinkService
import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy
import uno.d1s.linda.strategy.shortLink.byAlias
import uno.d1s.linda.strategy.shortLink.byId

@Service
class ShortLinkServiceImpl : ShortLinkService {

    @Autowired
    private lateinit var shortLinkRepository: ShortLinkRepository

    @Autowired
    private lateinit var aliasGeneratorService: AliasGeneratorService

    @Lazy
    @Autowired
    private lateinit var shortLinkService: ShortLinkServiceImpl

    @Transactional(readOnly = true)
    @CacheableList(cacheName = SHORT_LINKS_CACHE, idProvider = ShortLinkIdProvider::class)
    override fun findAll(): List<ShortLink> =
        shortLinkRepository.findAll()

    @Transactional(readOnly = true)
    @CacheableList(cacheName = SHORT_LINKS_CACHE, idProvider = ShortLinkIdProvider::class)
    override fun find(shortLinkFindingStrategy: ShortLinkFindingStrategy): ShortLink =
        when (shortLinkFindingStrategy) {
            is ShortLinkFindingStrategy.ById -> shortLinkRepository.findById(shortLinkFindingStrategy.id)
            is ShortLinkFindingStrategy.ByAlias -> shortLinkRepository.findShortLinkByAliasEquals(
                shortLinkFindingStrategy.alias
            )
            is ShortLinkFindingStrategy.ByUrl -> shortLinkRepository.findShortLinkByUrlEquals(shortLinkFindingStrategy.url)
        }.orElseThrow {
            ShortLinkNotFoundException
        }

    override fun create(url: String, aliasGeneratorId: String): ShortLink =
        shortLinkService.create(ShortLinkCreationDto(url, aliasGeneratorId))

    @Transactional
    @CachePutByIdProvider(cacheName = SHORT_LINKS_CACHE, idProvider = ShortLinkIdProvider::class)
    override fun create(shortLinkCreationDto: ShortLinkCreationDto): ShortLink =
        shortLinkRepository.save(
            ShortLink(
                shortLinkCreationDto.url,
                aliasGeneratorService.getAliasGenerator(shortLinkCreationDto.aliasGeneratorId).generateAlias()
            )
        )

    @Transactional
    @CacheEvictByIdProvider(cacheName = SHORT_LINKS_CACHE, idProvider = ShortLinkIdProvider::class)
    override fun remove(shortLink: ShortLink): ShortLink {
        shortLinkRepository.delete(shortLink)
        return shortLink
    }

    override fun remove(shortLinkFindingStrategy: ShortLinkFindingStrategy) =
        shortLinkService.remove(shortLinkService.find(shortLinkFindingStrategy))

    override fun removeAll(): List<ShortLink> =
        shortLinkService.removeAll(shortLinkService.findAll())

    override fun removeAll(shortLinks: List<ShortLink>): List<ShortLink> =
        shortLinks.onEach {
            shortLinkService.remove(it)
        }

    override fun removeAll(bulkShortLinkRemovalDto: BulkShortLinkRemovalDto): List<ShortLink> =
        bulkShortLinkRemovalDto.identifiers.map {
            shortLinkService.remove(byId(it))
        }

    override fun doesAliasExist(alias: String): Boolean = try {
        shortLinkService.find(byAlias(alias))
        true
    } catch (_: ShortLinkNotFoundException) {
        false
    }
}
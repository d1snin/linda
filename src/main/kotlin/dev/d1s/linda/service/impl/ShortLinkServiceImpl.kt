/*
 * Copyright 2022 Linda project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.linda.service.impl

import dev.d1s.caching.annotation.CacheEvictByIdProvider
import dev.d1s.caching.annotation.CachePutByIdProvider
import dev.d1s.caching.annotation.CacheableList
import dev.d1s.linda.cache.idProvider.ShortLinkIdProvider
import dev.d1s.linda.constant.cache.SHORT_LINKS_CACHE
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.BulkRemovalDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.exception.impl.ShortLinkNotFoundException
import dev.d1s.linda.repository.ShortLinkRepository
import dev.d1s.linda.service.AliasGeneratorService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.linda.strategy.shortLink.byId
import dev.d1s.teabag.stdlib.collection.mapToSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

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
    override fun findAll(): Set<ShortLink> =
        shortLinkRepository.findAll().toSet()

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [SHORT_LINKS_CACHE])
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

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @CachePutByIdProvider(cacheName = SHORT_LINKS_CACHE, idProvider = ShortLinkIdProvider::class)
    override fun create(shortLinkCreationDto: ShortLinkCreationDto): ShortLink =
        shortLinkRepository.save(
            ShortLink(
                shortLinkCreationDto.url,
                aliasGeneratorService.getAliasGenerator(shortLinkCreationDto.aliasGeneratorId)
                    .generateAlias()
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

    override fun removeAll(): Set<ShortLink> =
        shortLinkService.removeAll(shortLinkService.findAll())

    override fun removeAll(shortLinks: Set<ShortLink>): Set<ShortLink> =
        shortLinks.onEach {
            shortLinkService.remove(it)
        }

    override fun removeAll(bulkShortLinkRemovalDto: BulkRemovalDto): Set<ShortLink> =
        bulkShortLinkRemovalDto.identifiers.mapToSet {
            shortLinkService.remove(byId(it))
        }

    override fun doesAliasExist(alias: String): Boolean = try {
        shortLinkService.find(byAlias(alias))
        true
    } catch (_: ShortLinkNotFoundException) {
        false
    }
}
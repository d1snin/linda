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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import dev.d1s.caching.annotation.CacheEvictByIdProvider
import dev.d1s.caching.annotation.CachePutByIdProvider
import dev.d1s.caching.annotation.CacheableList
import dev.d1s.linda.cache.idProvider.RedirectIdProvider
import dev.d1s.linda.constant.cache.REDIRECTS_CACHE
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.redirect.BulkRedirectRemovalDto
import dev.d1s.linda.exception.impl.RedirectNotFoundException
import dev.d1s.linda.repository.RedirectRepository
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy

@Service
class RedirectServiceImpl : RedirectService {

    @Autowired
    private lateinit var redirectRepository: RedirectRepository

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Lazy
    @Autowired
    private lateinit var redirectService: RedirectServiceImpl

    @Transactional(readOnly = true)
    @CacheableList(cacheName = REDIRECTS_CACHE, idProvider = RedirectIdProvider::class)
    override fun findAll(): List<Redirect> =
        redirectRepository.findAll()

    override fun findAllByShortLink(shortLinkFindingStrategy: ShortLinkFindingStrategy): List<Redirect> =
        shortLinkService.find(shortLinkFindingStrategy).redirects

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = [REDIRECTS_CACHE])
    @CachePutByIdProvider(cacheName = REDIRECTS_CACHE, idProvider = RedirectIdProvider::class)
    override fun findById(id: String): Redirect =
        redirectRepository.findById(id).orElseThrow {
            RedirectNotFoundException
        }

    @Transactional
    @CachePutByIdProvider(cacheName = REDIRECTS_CACHE, idProvider = RedirectIdProvider::class)
    override fun create(shortLink: ShortLink): Redirect =
        redirectRepository.save(Redirect(shortLink))

    override fun create(shortLinkFindingStrategy: ShortLinkFindingStrategy): Redirect =
        redirectService.create(shortLinkService.find(shortLinkFindingStrategy))

    @Transactional
    @CacheEvictByIdProvider(cacheName = REDIRECTS_CACHE, idProvider = RedirectIdProvider::class)
    override fun remove(redirect: Redirect): Redirect {
        redirectRepository.delete(redirect)

        return redirect
    }

    override fun remove(id: String): Redirect =
        redirectService.remove(redirectService.findById(id))

    override fun removeAll(redirects: List<Redirect>): List<Redirect> = redirects.onEach {
        redirectService.remove(it)
    }

    override fun removeAll(bulkRedirectRemovalDto: BulkRedirectRemovalDto): List<Redirect> =
        bulkRedirectRemovalDto.identifiers.map {
            redirectService.remove(it)
        }

    override fun removeAll(): List<Redirect> =
        redirectService.removeAll(redirectService.findAll())

    override fun removeAllByShortLink(shortLinkFindingStrategy: ShortLinkFindingStrategy): List<Redirect> =
        redirectService.removeAll(redirectService.findAllByShortLink(shortLinkFindingStrategy))
}
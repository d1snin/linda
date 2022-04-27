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

import dev.d1s.linda.configuration.properties.AvailabilityChecksConfigurationProperties
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.exception.impl.notFound.ShortLinkNotFoundException
import dev.d1s.linda.repository.ShortLinkRepository
import dev.d1s.linda.service.AvailabilityChangeService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.linda.strategy.shortLink.byId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ShortLinkServiceImpl : ShortLinkService {

    @Autowired
    private lateinit var shortLinkRepository: ShortLinkRepository

    @Autowired
    private lateinit var availabilityChangeService: AvailabilityChangeService

    @Autowired
    private lateinit var availabilityChecksConfigurationProperties: AvailabilityChecksConfigurationProperties

    @Lazy
    @Autowired
    private lateinit var shortLinkService: ShortLinkServiceImpl

    @Transactional(readOnly = true)
    override fun findAll(): Set<ShortLink> =
        shortLinkRepository.findAll().toSet()

    @Transactional(readOnly = true)
    override fun find(shortLinkFindingStrategy: ShortLinkFindingStrategy): ShortLink =
        when (shortLinkFindingStrategy) {
            is ShortLinkFindingStrategy.ById -> shortLinkRepository.findById(shortLinkFindingStrategy.identifier)
            is ShortLinkFindingStrategy.ByAlias -> shortLinkRepository.findShortLinkByAliasEquals(
                shortLinkFindingStrategy.identifier
            )
        }.orElseThrow {
            ShortLinkNotFoundException
        }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun create(shortLink: ShortLink): ShortLink =
        shortLinkRepository.save(
            shortLink.apply {
                availabilityChanges = if (availabilityChecksConfigurationProperties.eagerAvailabilityCheck) {
                    setOf(
                        availabilityChangeService.checkAvailability(shortLink)
                    )
                } else {
                    setOf()
                }
            }
        )

    @Transactional
    override fun update(id: String, shortLink: ShortLink): ShortLink {
        val foundShortLink = shortLinkService.find(byId(id))

        foundShortLink.url = shortLink.url
        foundShortLink.alias = shortLink.alias
        foundShortLink.redirects = shortLink.redirects

        return shortLinkRepository.save(foundShortLink)
    }

    @Transactional
    override fun removeById(id: String) =
        shortLinkRepository.deleteById(id)

    override fun doesAliasExist(alias: String): Boolean = try {
        shortLinkService.find(byAlias(alias))
        true
    } catch (_: ShortLinkNotFoundException) {
        false
    }
}
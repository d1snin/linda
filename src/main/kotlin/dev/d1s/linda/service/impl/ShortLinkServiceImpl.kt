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
import dev.d1s.linda.constant.lp.SHORT_LINK_CREATED_GROUP
import dev.d1s.linda.constant.lp.SHORT_LINK_REMOVED_GROUP
import dev.d1s.linda.constant.lp.SHORT_LINK_UPDATED_GROUP
import dev.d1s.linda.entity.ShortLink
import dev.d1s.linda.entity.utmParameter.UtmParameterPurpose
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.event.data.shortLink.CommonShortLinkEventData
import dev.d1s.linda.event.data.shortLink.ShortLinkUpdatedEventData
import dev.d1s.linda.exception.badRequest.impl.utmParameter.DefaultUtmParametersNotAllowedException
import dev.d1s.linda.exception.notFound.impl.ShortLinkNotFoundException
import dev.d1s.linda.repository.ShortLinkRepository
import dev.d1s.linda.service.AvailabilityChangeService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.linda.strategy.shortLink.byId
import dev.d1s.linda.util.mapToIdSet
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoSet
import dev.d1s.teabag.dto.util.convertToDtoIf
import dev.d1s.teabag.dto.util.convertToDtoSetIf
import dev.d1s.teabag.dto.util.converterForSet
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Service
class ShortLinkServiceImpl : ShortLinkService {

    @Autowired
    private lateinit var shortLinkRepository: ShortLinkRepository

    @Autowired
    private lateinit var availabilityChangeService: AvailabilityChangeService

    @Autowired
    private lateinit var availabilityChecksConfigurationProperties: AvailabilityChecksConfigurationProperties

    @Autowired
    private lateinit var scheduler: ThreadPoolTaskScheduler

    @Autowired
    private lateinit var shortLinkDtoConverter: DtoConverter<ShortLinkDto, ShortLink>

    @Autowired
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Lazy
    @Autowired
    private lateinit var shortLinkService: ShortLinkServiceImpl

    private val shortLinkDtoSetConverter by lazy {
        shortLinkDtoConverter.converterForSet()
    }

    private val log = logging()

    private val scheduledDeletions =
        mutableMapOf<String, ScheduledFuture<*>>()

    @Transactional(readOnly = true)
    override fun findAll(requireDto: Boolean): EntityWithDtoSet<ShortLink, ShortLinkDto> {
        val shortLinks = shortLinkRepository.findAll().toSet()

        log.debug {
            "found all short links: ${
                shortLinks.mapToIdSet()
            }"
        }

        return shortLinks to shortLinkDtoSetConverter
            .convertToDtoSetIf(shortLinks, requireDto)
    }

    @Transactional(readOnly = true)
    override fun find(
        shortLinkFindingStrategy: ShortLinkFindingStrategy,
        requireDto: Boolean
    ): EntityWithDto<ShortLink, ShortLinkDto> {
        val shortLink = when (shortLinkFindingStrategy) {
            is ShortLinkFindingStrategy.ById -> shortLinkRepository.findById(shortLinkFindingStrategy.identifier)
            is ShortLinkFindingStrategy.ByAlias -> shortLinkRepository.findByAlias(
                shortLinkFindingStrategy.identifier
            )
        }.orElseThrow {
            ShortLinkNotFoundException(shortLinkFindingStrategy.identifier)
        }

        log.debug {
            "found short link using $shortLinkFindingStrategy strategy: $shortLink"
        }

        return shortLink to shortLinkDtoConverter
            .convertToDtoIf(shortLink, requireDto)
    }

    @Transactional
    override fun create(shortLink: ShortLink): EntityWithDto<ShortLink, ShortLinkDto> {
        shortLink.validate()

        shortLinkService.assignUtmParameters(
            shortLink,
            shortLink,
            UtmParameterPurpose.DEFAULT
        )

        shortLinkService.assignUtmParameters(
            shortLink,
            shortLink,
            UtmParameterPurpose.ALLOWED
        )

        var savedShortLink = shortLinkRepository.save(
            shortLink
        )

        if (
            availabilityChecksConfigurationProperties.eagerAvailabilityCheck
        ) {
            val (availabilityChange, _) = availabilityChangeService.checkAvailability(savedShortLink)

            shortLink.availabilityChanges += availabilityChange
        }

        savedShortLink = shortLinkRepository.save(
            shortLink
        )

        shortLinkService.scheduleForDeletion(
            savedShortLink
        )

        log.debug {
            "created short link: $savedShortLink"
        }

        val dto = shortLinkDtoConverter.convertToDto(
            savedShortLink
        )

        publisher.publish(
            SHORT_LINK_CREATED_GROUP,
            dto.id,
            CommonShortLinkEventData(dto)
        )

        return savedShortLink to dto
    }

    @Transactional
    override fun update(id: String, shortLink: ShortLink): EntityWithDto<ShortLink, ShortLinkDto> {
        shortLink.validate()

        val (foundShortLink, _) = shortLinkService.find(byId(id))

        val oldShortLinkDto = shortLinkDtoConverter.convertToDto(foundShortLink)

        val willSchedule = foundShortLink.deleteAfter != shortLink.deleteAfter

        foundShortLink.url = shortLink.url
        foundShortLink.alias = shortLink.alias
        foundShortLink.allowUtmParameters = shortLink.allowUtmParameters
        foundShortLink.deleteAfter = shortLink.deleteAfter
        foundShortLink.redirects = shortLink.redirects

        shortLinkService.assignUtmParameters(
            foundShortLink,
            shortLink,
            UtmParameterPurpose.DEFAULT
        )

        shortLinkService.assignUtmParameters(
            foundShortLink,
            shortLink,
            UtmParameterPurpose.ALLOWED
        )

        val savedShortLink = shortLinkRepository.save(foundShortLink)

        log.debug {
            "updated short link: $savedShortLink"
        }

        if (willSchedule) {
            shortLinkService.scheduleForDeletion(savedShortLink)
        }

        val dto = shortLinkDtoConverter.convertToDto(
            savedShortLink
        )

        publisher.publish(
            SHORT_LINK_UPDATED_GROUP,
            id,
            ShortLinkUpdatedEventData(
                oldShortLinkDto,
                dto
            )
        )

        return savedShortLink to shortLinkDtoConverter
            .convertToDto(savedShortLink)
    }

    override fun assignUtmParameters(
        shortLink: ShortLink,
        associatedShortLink: ShortLink,
        purpose: UtmParameterPurpose
    ) {
        val utmParameters = associatedShortLink.chooseUtmParameters(purpose)
        val originUtmParameters = shortLink.chooseUtmParameters(purpose)

        originUtmParameters.forEach {
            if (!utmParameters.contains(it)) {
                originUtmParameters.remove(it)
            }
        }

        when (purpose) {
            UtmParameterPurpose.DEFAULT -> {
                shortLink.defaultUtmParameters = utmParameters.toMutableSet()
            }

            UtmParameterPurpose.ALLOWED -> {
                shortLink.allowedUtmParameters = utmParameters.toMutableSet()
            }
        }

        utmParameters.forEach {
            when (purpose) {
                UtmParameterPurpose.DEFAULT -> {
                    it.defaultForShortLinks += shortLink
                }

                UtmParameterPurpose.ALLOWED -> {
                    it.allowedForShortLinks += shortLink
                }
            }
        }
    }

    @Transactional
    override fun removeById(id: String) {
        val (shortLink, shortLinkDto) = shortLinkService.find(byId(id), true)

        shortLinkRepository.delete(shortLink)

        log.debug {
            "removed short link with id $id"
        }

        publisher.publish(
            SHORT_LINK_REMOVED_GROUP,
            id,
            CommonShortLinkEventData(
                shortLinkDto
            )
        )
    }

    override fun doesAliasExist(alias: String): Boolean = try {
        shortLinkService.find(byAlias(alias))
        true
    } catch (_: ShortLinkNotFoundException) {
        false
    }

    override fun isExpired(shortLink: ShortLink): Boolean =
        (shortLink.deleteAfter?.let { deleteAfter ->
            (shortLink.creationTime!! + deleteAfter) < Instant.now()
        } ?: false).also {
            log.debug {
                "isExpired: $it; shortLink: $shortLink"
            }
        }

    override fun scheduleForDeletion(shortLink: ShortLink) {
        val id = shortLink.id!!

        log.debug {
            "scheduling $id for deletion"
        }

        shortLink.deleteAfter?.let { deleteAfter ->
            scheduledDeletions.put(
                id, scheduler.schedule({
                    shortLinkService.removeById(id)
                }, shortLink.creationTime!! + deleteAfter)
            )?.let {
                if (!it.isDone) {
                    it.cancel(true)
                }
            }

            log.debug {
                "scheduled $id for deletion."
            }
        } ?: run {
            log.debug {
                "deleteAfter is null, won't schedule for deletion."
            }
        }
    }

    @Transactional(readOnly = true)
    override fun scheduleAllEphemeralShortLinksForDeletion() {
        log.debug {
            "scheduling all ephemeral short links for deletion"
        }

        shortLinkRepository.findByDeleteAfterIsNotNull()
            .forEach(shortLinkService::scheduleForDeletion)

        log.debug {
            "scheduled all ephemeral short links for deletion."
        }
    }

    private fun ShortLink.validate() {
        if (!allowUtmParameters && defaultUtmParameters.isNotEmpty()) {
            throw DefaultUtmParametersNotAllowedException(defaultUtmParameters)
        }
    }

    private fun ShortLink.chooseUtmParameters(purpose: UtmParameterPurpose) = when (purpose) {
        UtmParameterPurpose.DEFAULT -> this.defaultUtmParameters
        UtmParameterPurpose.ALLOWED -> this.allowedUtmParameters
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    protected fun cleanScheduledDeletions() {
        scheduledDeletions.forEach { (id, scheduledFuture) ->
            if (scheduledFuture.isDone) {
                scheduledDeletions.remove(id)
            }
        }
    }
}
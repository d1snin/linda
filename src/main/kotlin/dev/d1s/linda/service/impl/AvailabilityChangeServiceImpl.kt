/*
 * Copyright 2022 Linda project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.linda.service.impl

import dev.d1s.linda.configuration.properties.AvailabilityChecksConfigurationProperties
import dev.d1s.linda.constant.lp.AVAILABILITY_CHANGE_CREATED_GROUP
import dev.d1s.linda.constant.lp.AVAILABILITY_CHANGE_REMOVED_GROUP
import dev.d1s.linda.constant.lp.AVAILABILITY_CHECK_PERFORMED_GROUP
import dev.d1s.linda.constant.lp.GLOBAL_AVAILABILITY_CHECK_PERFORMED_GROUP
import dev.d1s.linda.entity.ShortLink
import dev.d1s.linda.entity.availability.AvailabilityChange
import dev.d1s.linda.entity.availability.UnavailabilityReason
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoSet
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto
import dev.d1s.linda.event.data.availabilityChange.AvailabilityCheckPerformedEventData
import dev.d1s.linda.event.data.availabilityChange.CommonAvailabilityChangeEventData
import dev.d1s.linda.event.data.availabilityChange.GlobalAvailabilityCheckPerformedEventData
import dev.d1s.linda.exception.unprocessableEntity.impl.AvailabilityCheckInProgressException
import dev.d1s.linda.exception.notFound.impl.AvailabilityChangeNotFoundException
import dev.d1s.linda.repository.AvailabilityChangeRepository
import dev.d1s.linda.service.AvailabilityChangeService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.byId
import dev.d1s.teabag.dto.util.convertToDtoIf
import dev.d1s.teabag.dto.util.convertToDtoSetIf
import dev.d1s.linda.util.mapToIdSet
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.util.converterForSet
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

@Service
class AvailabilityChangeServiceImpl : AvailabilityChangeService {

    @Autowired
    private lateinit var availabilityChangeRepository: AvailabilityChangeRepository

    @Autowired
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var properties: AvailabilityChecksConfigurationProperties

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Autowired
    private lateinit var availabilityChangeDtoConverter: DtoConverter<AvailabilityChangeDto, AvailabilityChange>

    @Autowired
    private lateinit var unsavedAvailabilityChangeDtoConverter: DtoConverter<UnsavedAvailabilityChangeDto, AvailabilityChange>

    @Lazy
    @Autowired
    private lateinit var availabilityChangeService: AvailabilityChangeServiceImpl

    private val availabilityChangeSetDtoConverter by lazy {
        availabilityChangeDtoConverter.converterForSet()
    }

    private val checksRunning = AtomicBoolean(false)

    private val requestFactory by lazy {
        restTemplate.requestFactory
    }

    private val log = logging()

    @Transactional(readOnly = true)
    override fun findAll(requireDto: Boolean): EntityWithDtoSet<AvailabilityChange, AvailabilityChangeDto> {
        val all = availabilityChangeRepository.findAll().toSet()

        log.debug {
            "found all availability changes: $all"
        }

        return all to availabilityChangeSetDtoConverter
            .convertToDtoSetIf(all, requireDto)
    }

    @Transactional(readOnly = true)
    override fun findById(id: String, requireDto: Boolean): EntityWithDto<AvailabilityChange, AvailabilityChangeDto> {
        val availabilityChange = availabilityChangeRepository.findById(id).orElseThrow {
            AvailabilityChangeNotFoundException(id)
        }

        log.debug {
            "found availability change by id: $availabilityChange"
        }

        return availabilityChange to availabilityChangeDtoConverter
            .convertToDtoIf(availabilityChange, requireDto)
    }

    @Transactional(readOnly = true)
    override fun findLast(shortLinkId: String): AvailabilityChange? =
        availabilityChangeRepository.findLast(shortLinkId)
            .orElse(null)
            .also {
                log.debug {
                    "found the last availability change: $it"
                }
            }

    @Transactional
    override fun create(availability: AvailabilityChange): EntityWithDto<AvailabilityChange, AvailabilityChangeDto> {
        val saved = availabilityChangeRepository.save(availability)
        val dto = availabilityChangeDtoConverter.convertToDto(saved)

        publisher.publish(
            AVAILABILITY_CHANGE_CREATED_GROUP,
            dto.shortLink,
            CommonAvailabilityChangeEventData(dto)
        )

        log.debug {
            "created availability change: $saved"
        }

        return saved to dto
    }

    @Transactional
    override fun removeById(id: String) {
        val (availabilityChangeToRemove, dto) =
            availabilityChangeService.findById(id, true)

        availabilityChangeRepository.delete(availabilityChangeToRemove)

        log.debug {
            "removed availability change with id $id"
        }

        publisher.publish(
            AVAILABILITY_CHANGE_REMOVED_GROUP,
            dto!!.id,
            CommonAvailabilityChangeEventData(dto)
        )
    }

    override fun checkAvailability(shortLink: ShortLink): EntityWithDto<AvailabilityChange, UnsavedAvailabilityChangeDto> {
        var available = true
        var unavailabilityReason: UnavailabilityReason? = null
        var response: ClientHttpResponse by Delegates.notNull()

        try {
            response = requestFactory.createRequest(
                URI.create(shortLink.url),
                HttpMethod.GET
            ).execute()
        } catch (_: IOException) {
            available = false
            unavailabilityReason = UnavailabilityReason.CONNECTION_ERROR
        } catch (_: IllegalArgumentException) {
            available = false
            unavailabilityReason = UnavailabilityReason.MALFORMED_URL
        }

        // if still available
        if (available) {
            response.use {
                properties.badStatusCodeIntRanges.forEach { range ->
                    if (it.rawStatusCode in range) {
                        available = false
                        unavailabilityReason = UnavailabilityReason.BAD_STATUS
                    }
                }
            }
        }

        val availabilityChange = AvailabilityChange(
            shortLink,
            unavailabilityReason
        )

        log.debug {
            "checked the availability of $shortLink: $availabilityChange"
        }

        val dto = unsavedAvailabilityChangeDtoConverter
            .convertToDto(availabilityChange)

        publisher.publish(
            AVAILABILITY_CHECK_PERFORMED_GROUP,
            shortLink.id,
            AvailabilityCheckPerformedEventData(
                dto
            )
        )

        return availabilityChange to dto
    }

    override fun checkAvailability(shortLinkId: String): EntityWithDto<AvailabilityChange, UnsavedAvailabilityChangeDto> {
        val (shortLink, _) = shortLinkService.find(byId(shortLinkId))

        return availabilityChangeService.checkAvailability(
            shortLink
        )
    }

    override fun checkAndSaveAvailability(shortLink: ShortLink): AvailabilityChange? {
        val lastChange = availabilityChangeService.findLast(shortLink.id!!)
        val (availabilityChange, _) = availabilityChangeService.checkAvailability(shortLink)

        return if (lastChange == null || lastChange.available != availabilityChange.available) {
            val (createdAvailabilityChange, _) = availabilityChangeService.create(availabilityChange)
            createdAvailabilityChange
        } else {
            null
        }
    }

    // using runBlocking {} because I don't want coroutines to be used anywhere else, yet.
    override fun checkAvailabilityOfAllShortLinks(): EntityWithDtoSet<AvailabilityChange, AvailabilityChangeDto> =
        runBlocking {
            log.debug {
                "checking the availability of all short links"
            }

            if (checksRunning.get()) {
                throw AvailabilityCheckInProgressException
            }

            var changes: Set<AvailabilityChange> by Delegates.notNull()

            checksRunning.set(true)

            val (shortLinks, _) = shortLinkService.findAll()

            changes = shortLinks.map {
                async {
                    availabilityChangeService.checkAndSaveAvailability(it)
                }
            }.awaitAll()
                .filterNotNull()
                .toSet()

            checksRunning.set(false)

            log.debug {
                "checked the availability of all short links: ${
                    changes.mapToIdSet().filterNotNull()
                }"
            }

            val changesDtoSet = availabilityChangeSetDtoConverter
                .convertToDtoSet(changes)

            publisher.publish(
                GLOBAL_AVAILABILITY_CHECK_PERFORMED_GROUP,
                null,
                GlobalAvailabilityCheckPerformedEventData(
                    changesDtoSet
                )
            )

            changes to changesDtoSet
        }
}
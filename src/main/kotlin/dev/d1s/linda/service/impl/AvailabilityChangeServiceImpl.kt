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
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.domain.availability.UnavailabilityReason
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.event.data.AvailabilityChangeEventData
import dev.d1s.linda.exception.AvailabilityCheckInProgressException
import dev.d1s.linda.exception.notFound.impl.AvailabilityChangeNotFoundException
import dev.d1s.linda.repository.AvailabilityChangeRepository
import dev.d1s.linda.service.AvailabilityChangeService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.util.mapToIdSet
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.log4j.logger
import dev.d1s.teabag.log4j.util.lazyDebug
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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
    private lateinit var availabilityChangeDtoConverter: DtoConverter<AvailabilityChangeDto, AvailabilityChange>

    @Autowired
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var properties: AvailabilityChecksConfigurationProperties

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Lazy
    @Autowired
    private lateinit var availabilityChangeService: AvailabilityChangeServiceImpl

    private val checksRunning = AtomicBoolean(false)

    private val requestFactory by lazy {
        restTemplate.requestFactory
    }

    private val log = logger()

    @Transactional(readOnly = true)
    override fun findAll(): Set<AvailabilityChange> =
        availabilityChangeRepository.findAll().toSet().also {
            log.lazyDebug {
                "found all availability changes: ${
                    it.mapToIdSet()
                }"
            }
        }

    @Transactional(readOnly = true)
    override fun findById(id: String): AvailabilityChange =
        availabilityChangeRepository.findById(id).orElseThrow {
            AvailabilityChangeNotFoundException(id)
        }.also {
            log.lazyDebug {
                "found availability change by id: $it"
            }
        }

    @Transactional(readOnly = true)
    override fun findLast(shortLinkId: String): AvailabilityChange? =
        availabilityChangeRepository.findLast(shortLinkId)
            .orElse(null)
            .also {
                log.lazyDebug {
                    "found the last availability change: $it"
                }
            }

    @Transactional
    override fun create(availability: AvailabilityChange): AvailabilityChange {
        val saved = availabilityChangeRepository.save(availability)
        val dto = availabilityChangeDtoConverter.convertToDto(saved)

        // doing this on a service layer because POST requests are not supported for this entity
        publisher.publish(
            AVAILABILITY_CHANGE_CREATED_GROUP,
            dto.id,
            AvailabilityChangeEventData(dto)
        )

        log.lazyDebug {
            "created availability change: $saved"
        }

        return saved
    }

    @Transactional
    override fun removeById(id: String) {
        availabilityChangeRepository.deleteById(id)

        log.lazyDebug {
            "removed availability change with id $id"
        }
    }

    override fun checkAvailability(shortLink: ShortLink): AvailabilityChange {
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

        return AvailabilityChange(
            shortLink,
            unavailabilityReason
        ).also {
            log.lazyDebug {
                "checked the availability of $shortLink: $it"
            }
        }
    }

    override fun checkAndSaveAvailability(shortLink: ShortLink): AvailabilityChange? {
        val lastChange = availabilityChangeService.findLast(shortLink.id!!)
        val availabilityChange = availabilityChangeService.checkAvailability(shortLink)

        return if (lastChange == null || lastChange.available != availabilityChange.available) {
            availabilityChangeService.create(availabilityChange)
        } else {
            null
        }
    }

    // using runBlocking {} because I don't want coroutines to be used anywhere else, yet.
    override fun checkAvailabilityOfAllShortLinks(): Set<AvailabilityChange> = runBlocking {
        log.lazyDebug {
            "checking the availability of all short links"
        }

        if (checksRunning.get()) {
            throw AvailabilityCheckInProgressException
        }

        var changes: Set<AvailabilityChange> by Delegates.notNull()

        checksRunning.set(true)

        changes = shortLinkService.findAll().map {
            async {
                availabilityChangeService.checkAndSaveAvailability(it)
            }
        }.awaitAll()
            .filterNotNull()
            .toSet()

        checksRunning.set(false)

        log.lazyDebug {
            "checked the availability of all short links: ${
                changes.mapToIdSet().filterNotNull()
            }"
        }

        changes
    }
}
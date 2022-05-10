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

package dev.d1s.linda.controller.impl

import dev.d1s.linda.constant.lp.AVAILABILITY_CHANGE_REMOVED_GROUP
import dev.d1s.linda.controller.AvailabilityChangeController
import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto
import dev.d1s.linda.event.data.AvailabilityChangeEventData
import dev.d1s.linda.service.AvailabilityChangeService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.byId
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.security.configuration.annotation.Secured
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.util.converterForSet
import dev.d1s.teabag.web.noContent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController

@RestController
class AvailabilityChangeControllerImpl : AvailabilityChangeController {

    @Autowired
    private lateinit var availabilityChangeService: AvailabilityChangeService

    @Autowired
    private lateinit var availabilityChangeDtoConverter: DtoConverter<AvailabilityChangeDto, AvailabilityChange>

    @Autowired
    private lateinit var unsavedAvailabilityChangeDtoConverter: DtoConverter<UnsavedAvailabilityChangeDto, AvailabilityChange>

    @Autowired
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    private val availabilityChangeSetDtoConverter by lazy {
        availabilityChangeDtoConverter.converterForSet()
    }

    @Secured
    override fun findAll(): ResponseEntity<Set<AvailabilityChangeDto>> = ok(
        availabilityChangeService.findAll().toDtoSet()
    )

    @Secured
    override fun findById(identifier: String): ResponseEntity<AvailabilityChangeDto> = ok(
        availabilityChangeDtoConverter.convertToDto(
            availabilityChangeService.findById(identifier)
        )
    )

    @Secured
    override fun triggerChecks(): ResponseEntity<Set<AvailabilityChangeDto>> = ok(
        availabilityChangeService.checkAvailabilityOfAllShortLinks().toDtoSet()
    )

    @Secured
    override fun triggerCheckForShortLink(identifier: String): ResponseEntity<UnsavedAvailabilityChangeDto> = ok(
        unsavedAvailabilityChangeDtoConverter.convertToDto(
            availabilityChangeService.checkAvailability(
                shortLinkService.find(byId(identifier))
            )
        )
    )

    @Secured
    override fun removeById(identifier: String): ResponseEntity<*> {
        availabilityChangeService.removeById(identifier)

        publisher.publish(
            AVAILABILITY_CHANGE_REMOVED_GROUP,
            identifier,
            AvailabilityChangeEventData(null)
        )

        return noContent
    }

    private fun Set<AvailabilityChange>.toDtoSet() =
        availabilityChangeSetDtoConverter.convertToDtoSet(this)
}
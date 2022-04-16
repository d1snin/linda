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

package dev.d1s.linda.controller.impl

import dev.d1s.linda.constant.lp.SHORT_LINK_CREATED_GROUP
import dev.d1s.linda.constant.lp.SHORT_LINK_REMOVED_GROUP
import dev.d1s.linda.constant.lp.SHORT_LINK_UPDATED_GROUP
import dev.d1s.linda.controller.ShortLinkController
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.event.data.ShortLinkEventData
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import dev.d1s.linda.strategy.shortLink.byType
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.security.configuration.annotation.Secured
import dev.d1s.teabag.data.toPage
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.util.converterForSet
import dev.d1s.teabag.web.appendUri
import dev.d1s.teabag.web.noContent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController

@RestController
class ShortLinkControllerImpl : ShortLinkController {

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Autowired
    private lateinit var shortLinkDtoConverter: DtoConverter<ShortLinkDto, ShortLink>

    @Autowired
    private lateinit var shortLinkCreationDtoConverter: DtoConverter<ShortLinkCreationDto, ShortLink>

    @Autowired
    private lateinit var shortLinkUpdateDtoConverter: DtoConverter<ShortLinkUpdateDto, ShortLink>

    @Autowired
    private lateinit var publisher: AsyncLongPollingEventPublisher

    private val shortLinkDtoSetConverter by lazy {
        shortLinkDtoConverter.converterForSet()
    }

    private fun ShortLink.toDto() =
        shortLinkDtoConverter.convertToDto(this)

    @Secured
    override fun findAll(page: Int?, size: Int?):
            ResponseEntity<Page<ShortLinkDto>> = ok(
        shortLinkDtoSetConverter.convertToDtoSet(
            shortLinkService.findAll()
        ).toPage(page, size)
    )

    @Secured
    override fun find(
        identifier: String,
        shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<ShortLinkDto> = ok(
        shortLinkService.find(
            byType(shortLinkFindingStrategy, identifier)
        ).toDto()
    )

    @Secured
    override fun create(shortLinkCreationDto: ShortLinkCreationDto):
            ResponseEntity<ShortLinkDto> {
        val shortLink = shortLinkService.create(
            shortLinkCreationDtoConverter.convertToEntity(
                shortLinkCreationDto
            )
        ).toDto()

        publisher.publish(
            SHORT_LINK_CREATED_GROUP,
            shortLink.id,
            ShortLinkEventData(
                shortLink
            )
        )

        return created(
            appendUri(shortLink.id)
        ).body(shortLink)
    }

    @Secured
    override fun update(identifier: String, shortLinkUpdateDto: ShortLinkUpdateDto):
            ResponseEntity<ShortLinkDto> {
        val shortLink = shortLinkService.update(
            identifier,
            shortLinkUpdateDtoConverter.convertToEntity(shortLinkUpdateDto)
        ).toDto()

        publisher.publish(
            SHORT_LINK_UPDATED_GROUP,
            shortLink.id,
            ShortLinkEventData(
                shortLink
            )
        )

        return ok(shortLink)
    }

    @Secured
    override fun remove(identifier: String): ResponseEntity<*> {
        shortLinkService.removeById(identifier)

        publisher.publish(
            SHORT_LINK_REMOVED_GROUP,
            identifier,
            ShortLinkEventData(null)
        )

        return noContent
    }
}
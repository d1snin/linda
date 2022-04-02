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

package uno.d1s.linda.controller.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import uno.d1s.linda.controller.ShortLinkController
import uno.d1s.linda.converter.DtoConverter
import uno.d1s.linda.domain.ShortLink
import uno.d1s.linda.dto.shortLink.BulkShortLinkRemovalDto
import uno.d1s.linda.dto.shortLink.ShortLinkCreationDto
import uno.d1s.linda.dto.shortLink.ShortLinkDto
import uno.d1s.linda.service.ShortLinkService
import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import uno.d1s.linda.strategy.shortLink.byType
import uno.d1s.linda.util.noContent
import uno.d1s.linda.util.ok
import uno.d1s.linda.util.pagination.toPage

@RestController
class ShortLinkControllerImpl : ShortLinkController {

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Autowired
    private lateinit var shortLinkDtoConverter: DtoConverter<ShortLink, ShortLinkDto>

    private val ShortLink.dto get() = shortLinkDtoConverter.convertToDto(this)

    override fun findAll(page: Int?, size: Int?): ResponseEntity<Page<ShortLinkDto>> =
        shortLinkDtoConverter.convertToDtoList(
            shortLinkService.findAll()
        ).toPage(page, size).ok()

    override fun find(
        identifier: String,
        shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<ShortLinkDto> =
        shortLinkService.find(
            byType(shortLinkFindingStrategy, identifier)
        ).dto.ok()

    override fun create(shortLinkCreationDto: ShortLinkCreationDto):
            ResponseEntity<ShortLinkDto> =
        shortLinkService.create(shortLinkCreationDto).dto.ok()

    override fun remove(
        identifier: String,
        shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<Any> {
        shortLinkService.remove(
            shortLinkService.find(byType(shortLinkFindingStrategy, identifier))
        )
        return noContent
    }

    override fun removeAll(): ResponseEntity<Any> {
        shortLinkService.removeAll()
        return noContent
    }

    override fun removeAll(
        bulkShortLinkRemovalDto: BulkShortLinkRemovalDto
    ): ResponseEntity<*> {
        shortLinkService.removeAll(bulkShortLinkRemovalDto)
        return noContent
    }
}
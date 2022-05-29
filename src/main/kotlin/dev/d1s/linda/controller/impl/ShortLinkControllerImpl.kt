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

import dev.d1s.linda.configuration.properties.SslConfigurationProperties
import dev.d1s.linda.controller.ShortLinkController
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.entity.ShortLink
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import dev.d1s.linda.strategy.shortLink.byType
import dev.d1s.security.configuration.annotation.Secured
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.web.buildFromCurrentRequest
import dev.d1s.teabag.web.configureSsl
import dev.d1s.teabag.web.noContent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController

@RestController
class ShortLinkControllerImpl : ShortLinkController {

    @set:Autowired
    lateinit var shortLinkService: ShortLinkService

    @set:Autowired
    lateinit var shortLinkCreationDtoConverter: DtoConverter<ShortLinkCreationDto, ShortLink>

    @set:Autowired
    lateinit var shortLinkUpdateDtoConverter: DtoConverter<ShortLinkUpdateDto, ShortLink>

    @set:Autowired
    lateinit var sslConfigurationProperties: SslConfigurationProperties

    @Secured
    override fun findAll(): ResponseEntity<Set<ShortLinkDto>> {
        val (_, shortLinks) = shortLinkService.findAll(true)

        return ok(shortLinks)
    }

    @Secured
    override fun find(
        identifier: String,
        shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<ShortLinkDto> {
        val (_, shortLink) = shortLinkService.find(
            byType(shortLinkFindingStrategy, identifier),
            true
        )

        return ok(shortLink)
    }

    @Secured
    override fun create(
        shortLinkCreationDto: ShortLinkCreationDto
    ): ResponseEntity<ShortLinkDto> {
        val (_, shortLink) = shortLinkService.create(
            shortLinkCreationDtoConverter.convertToEntity(
                shortLinkCreationDto
            )
        )

        return created(
            buildFromCurrentRequest {
                configureSsl(sslConfigurationProperties.fallbackToHttps)
                path("/${shortLink!!.id}")
                build().toUri()
            }
        ).body(shortLink)
    }

    @Secured
    override fun update(
        identifier: String,
        shortLinkUpdateDto: ShortLinkUpdateDto
    ): ResponseEntity<ShortLinkDto> {
        val (_, shortLink) = shortLinkService.update(
            identifier,
            shortLinkUpdateDtoConverter.convertToEntity(shortLinkUpdateDto)
        )

        return ok(shortLink)
    }

    @Secured
    override fun remove(identifier: String): ResponseEntity<*> {
        shortLinkService.removeById(identifier)

        return noContent
    }
}
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
import dev.d1s.linda.constant.lp.REDIRECT_CREATED_GROUP
import dev.d1s.linda.constant.lp.REDIRECT_REMOVED_GROUP
import dev.d1s.linda.constant.lp.REDIRECT_UPDATED_GROUP
import dev.d1s.linda.controller.RedirectController
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.dto.redirect.RedirectAlterationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.event.data.RedirectEventData
import dev.d1s.linda.service.RedirectService
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.security.configuration.annotation.Secured
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.util.converterForSet
import dev.d1s.teabag.web.buildFromCurrentRequest
import dev.d1s.teabag.web.configureSsl
import dev.d1s.teabag.web.noContent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController

@RestController
class RedirectControllerImpl : RedirectController {

    @Autowired
    private lateinit var redirectService: RedirectService

    @Autowired
    private lateinit var redirectDtoConverter: DtoConverter<RedirectDto, Redirect>

    @Autowired
    private lateinit var redirectAlterationDtoConverter: DtoConverter<RedirectAlterationDto, Redirect>

    @Autowired
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Autowired
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    private val redirectSetDtoConverter by lazy {
        redirectDtoConverter.converterForSet()
    }

    private fun Redirect.toDto() = redirectDtoConverter.convertToDto(this)
    private fun Set<Redirect>.toDtoSet() = redirectSetDtoConverter.convertToDtoSet(this)

    @Secured
    override fun findAll(): ResponseEntity<Set<RedirectDto>> = ok(
        redirectService.findAll().toDtoSet()
    )

    @Secured
    override fun findById(identifier: String): ResponseEntity<RedirectDto> = ok(
        redirectService.findById(identifier).toDto()
    )

    @Secured
    override fun create(alteration: RedirectAlterationDto): ResponseEntity<RedirectDto> {
        val redirect = redirectService.create(
            redirectAlterationDtoConverter.convertToEntity(
                alteration
            )
        ).toDto()

        publisher.publish(
            REDIRECT_CREATED_GROUP,
            redirect.id,
            RedirectEventData(
                redirect
            )
        )

        return created(
            buildFromCurrentRequest {
                configureSsl(sslConfigurationProperties.fallbackToHttps)
                path("/${redirect.id}")
                build().toUri()
            }
        ).body(redirect)
    }

    @Secured
    override fun update(identifier: String, alteration: RedirectAlterationDto): ResponseEntity<RedirectDto> {
        val redirect = redirectService.update(
            identifier,
            redirectAlterationDtoConverter.convertToEntity(
                alteration
            )
        ).toDto()

        publisher.publish(
            REDIRECT_UPDATED_GROUP,
            redirect.id,
            RedirectEventData(
                redirect
            )
        )

        return ok(redirect)
    }

    @Secured
    override fun removeById(identifier: String): ResponseEntity<*> {
        redirectService.removeById(identifier)

        publisher.publish(
            REDIRECT_REMOVED_GROUP,
            identifier,
            RedirectEventData(null)
        )

        return noContent
    }
}
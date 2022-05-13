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

import dev.d1s.linda.configuration.properties.SslConfigurationProperties
import dev.d1s.linda.controller.UtmParameterController
import dev.d1s.linda.domain.utmParameter.UtmParameter
import dev.d1s.linda.domain.utmParameter.UtmParameterType
import dev.d1s.linda.dto.utmParameter.UtmParameterAlterationDto
import dev.d1s.linda.dto.utmParameter.UtmParameterDto
import dev.d1s.linda.service.UtmParameterService
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
class UtmParameterControllerImpl : UtmParameterController {

    @Autowired
    private lateinit var utmParameterService: UtmParameterService

    @Autowired
    private lateinit var utmParameterAlterationDtoConverter: DtoConverter<UtmParameterAlterationDto, UtmParameter>

    @Autowired
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    @Secured
    override fun findAll(): ResponseEntity<Set<UtmParameterDto>> {
        val (_, utmParameters) =
            utmParameterService.findAll(true)

        return ok(utmParameters)
    }

    @Secured
    override fun findById(identifier: String): ResponseEntity<UtmParameterDto> {
        val (_, utmParameter) =
            utmParameterService.findById(identifier, true)

        return ok(utmParameter)
    }

    @Secured
    override fun findByTypeAndValue(type: UtmParameterType, value: String): ResponseEntity<UtmParameterDto> {
        val (_, utmParameter) =
            utmParameterService.findByTypeAndValueOrThrow(
                type,
                value,
                true
            )

        return ok(utmParameter)
    }

    @Secured
    override fun create(alteration: UtmParameterAlterationDto): ResponseEntity<UtmParameterDto> {
        val (_, utmParameter) = utmParameterService.create(
            utmParameterAlterationDtoConverter.convertToEntity(alteration)
        )

        return created(
            buildFromCurrentRequest {
                configureSsl(sslConfigurationProperties.fallbackToHttps)
                path("/${utmParameter!!.id}")
                build().toUri()
            }
        ).body(utmParameter)
    }

    @Secured
    override fun update(
        identifier: String,
        alteration: UtmParameterAlterationDto
    ): ResponseEntity<UtmParameterDto> {
        val (_, utmParameter) = utmParameterService.update(
            identifier,
            utmParameterAlterationDtoConverter.convertToEntity(alteration)
        )

        return ok(utmParameter)
    }

    @Secured
    override fun removeById(identifier: String): ResponseEntity<*> {
        utmParameterService.removeById(identifier)

        return noContent
    }
}
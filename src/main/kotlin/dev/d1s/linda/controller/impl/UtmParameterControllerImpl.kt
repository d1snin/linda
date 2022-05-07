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
import dev.d1s.linda.constant.lp.UTM_PARAMETER_CREATED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_REMOVED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_UPDATED_GROUP
import dev.d1s.linda.controller.UtmParameterController
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.dto.utm.UtmParameterAlterationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.linda.event.data.UtmParameterEventData
import dev.d1s.linda.service.UtmParameterService
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
class UtmParameterControllerImpl : UtmParameterController {

    @Autowired
    private lateinit var utmParameterService: UtmParameterService

    @Autowired
    private lateinit var utmParameterDtoConverter: DtoConverter<UtmParameterDto, UtmParameter>

    @Autowired
    private lateinit var utmParameterAlterationDtoConverter: DtoConverter<UtmParameterAlterationDto, UtmParameter>

    @Autowired
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Autowired
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    private val utmParameterDtoSetConverter by lazy {
        utmParameterDtoConverter.converterForSet()
    }

    private fun UtmParameter.toDto() =
        utmParameterDtoConverter.convertToDto(this)

    @Secured
    override fun findAll(): ResponseEntity<Set<UtmParameterDto>> = ok(
        utmParameterDtoSetConverter.convertToDtoSet(
            utmParameterService.findAll()
        )
    )

    @Secured
    override fun findById(identifier: String): ResponseEntity<UtmParameterDto> = ok(
        utmParameterService.findById(identifier).toDto()
    )

    @Secured
    override fun findByTypeAndValue(type: UtmParameterType, value: String): ResponseEntity<UtmParameterDto> = ok(
        utmParameterService.findByTypeAndValueOrThrow(type, value).toDto()
    )

    @Secured
    override fun create(alteration: UtmParameterAlterationDto): ResponseEntity<UtmParameterDto> {
        val utmParameter = utmParameterService.create(
            utmParameterAlterationDtoConverter.convertToEntity(alteration)
        ).toDto()

        publisher.publish(
            UTM_PARAMETER_CREATED_GROUP,
            utmParameter.id,
            UtmParameterEventData(
                utmParameter
            )
        )

        return created(
            buildFromCurrentRequest {
                configureSsl(sslConfigurationProperties.fallbackToHttps)
                path("/${utmParameter.id}")
                build().toUri()
            }
        ).body(utmParameter)
    }

    @Secured
    override fun update(
        identifier: String,
        alteration: UtmParameterAlterationDto
    ): ResponseEntity<UtmParameterDto> {
        val utmParameter = utmParameterService.update(
            identifier,
            utmParameterAlterationDtoConverter.convertToEntity(alteration)
        ).toDto()

        publisher.publish(
            UTM_PARAMETER_UPDATED_GROUP,
            utmParameter.id,
            UtmParameterEventData(
                utmParameter
            )
        )

        return ok(utmParameter)
    }

    @Secured
    override fun removeById(identifier: String): ResponseEntity<*> {
        utmParameterService.removeById(identifier)

        publisher.publish(
            UTM_PARAMETER_REMOVED_GROUP,
            identifier,
            UtmParameterEventData(null)
        )

        return noContent
    }
}
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

import dev.d1s.linda.controller.UtmParameterController
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.dto.utm.UtmParameterCreationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.linda.service.UtmParameterService
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
class UtmParameterControllerImpl : UtmParameterController {

    @Autowired
    private lateinit var utmParameterService: UtmParameterService

    @Autowired
    private lateinit var utmParameterDtoConverter: DtoConverter<UtmParameterDto, UtmParameter>

    @Autowired
    private lateinit var utmParameterCreationDtoConverter: DtoConverter<UtmParameterCreationDto, UtmParameter>

    private val utmParameterDtoSetConverter by lazy {
        utmParameterDtoConverter.converterForSet()
    }

    private val UtmParameter.dto get() = utmParameterDtoConverter.convertToDto(this)

    @Secured
    override fun findAll(page: Int?, size: Int?): ResponseEntity<Page<UtmParameterDto>> = ok(
        utmParameterDtoSetConverter.convertToDtoSet(
            utmParameterService.findAll()
        ).toPage(page, size)
    )

    @Secured
    override fun findById(identifier: String): ResponseEntity<UtmParameterDto> = ok(
        utmParameterService.findById(identifier).dto
    )

    @Secured
    override fun create(creation: UtmParameterCreationDto): ResponseEntity<UtmParameterDto> {
        val utmParameter = utmParameterService.create(
            utmParameterCreationDtoConverter.convertToEntity(creation)
        ).dto

        return created(
            appendUri(utmParameter.id)
        ).body(utmParameter)
    }

    @Secured
    override fun removeById(identifier: String): ResponseEntity<*> {
        utmParameterService.removeById(identifier)
        return noContent
    }
}
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

import dev.d1s.linda.controller.RedirectController
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.service.RedirectService
import dev.d1s.teabag.data.toPage
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.util.converterForSet
import dev.d1s.teabag.web.noContent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController

@RestController
class RedirectControllerImpl : RedirectController {

    @Autowired
    private lateinit var redirectService: RedirectService

    @Autowired
    private lateinit var redirectDtoConverter: DtoConverter<RedirectDto, Redirect>

    private val redirectSetDtoConverter by lazy {
        redirectDtoConverter.converterForSet()
    }

    private fun Redirect.toDto() = redirectDtoConverter.convertToDto(this)
    private fun Set<Redirect>.toDtoSet() = redirectSetDtoConverter.convertToDtoSet(this)

    override fun findAll(page: Int?, size: Int?): ResponseEntity<Page<RedirectDto>> = ok(
        redirectService.findAll().toDtoSet().toPage(page, size)
    )

    override fun findById(identifier: String): ResponseEntity<RedirectDto> = ok(
        redirectService.findById(identifier).toDto()
    )

    override fun removeById(identifier: String): ResponseEntity<*> {
        redirectService.removeById(identifier)
        return noContent
    }
}
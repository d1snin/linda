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

package dev.d1s.linda.controller

import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.dto.redirect.RedirectAlterationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
interface RedirectController {

    @GetMapping(
        REDIRECTS_FIND_ALL_MAPPING,
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findAll(
        @RequestParam page: Int?, @RequestParam size: Int?
    ): ResponseEntity<Page<RedirectDto>>

    @GetMapping(REDIRECTS_FIND_BY_ID_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(
        @PathVariable @NotBlank identifier: String
    ): ResponseEntity<RedirectDto>

    @PostMapping(REDIRECTS_CREATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(
        @RequestBody @Valid alteration: RedirectAlterationDto
    ): ResponseEntity<RedirectDto>

    @PutMapping(REDIRECTS_UPDATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun update(
        @PathVariable @NotBlank identifier: String,
        @RequestBody @Valid alteration: RedirectAlterationDto
    ): ResponseEntity<RedirectDto>

    @DeleteMapping(REDIRECTS_REMOVE_BY_ID_MAPPING)
    fun removeById(
        @PathVariable @NotBlank identifier: String
    ): ResponseEntity<*>
}
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

package dev.d1s.linda.controller

import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.dto.utm.UtmParameterCreationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.linda.dto.utm.UtmParameterUpdateDto
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
interface UtmParameterController {

    @GetMapping(UTM_PARAMETERS_FIND_ALL_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ): ResponseEntity<Page<UtmParameterDto>>

    @GetMapping(UTM_PARAMETERS_FIND_BY_ID_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(
        @PathVariable @NotBlank identifier: String
    ): ResponseEntity<UtmParameterDto>

    @PostMapping(UTM_PARAMETERS_CREATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(
        @RequestBody @Valid alteration: UtmParameterCreationDto
    ): ResponseEntity<UtmParameterDto>

    @PutMapping(UTM_PARAMETERS_UPDATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun update(
        @PathVariable @NotBlank identifier: String,
        @RequestBody @Valid utmParameterUpdateDto: UtmParameterUpdateDto
    ): ResponseEntity<UtmParameterDto>

    @DeleteMapping(UTM_PARAMETERS_REMOVE_BY_ID_MAPPING)
    fun removeById(
        @PathVariable @NotBlank identifier: String
    ): ResponseEntity<*>
}
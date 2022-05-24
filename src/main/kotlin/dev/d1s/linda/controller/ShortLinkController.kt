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
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
interface ShortLinkController {

    @GetMapping(SHORT_LINKS_FIND_ALL_MAPPING)
    fun findAll(): ResponseEntity<Set<ShortLinkDto>>

    @GetMapping(SHORT_LINKS_FIND_MAPPING)
    fun find(
        @PathVariable
        @NotBlank
        identifier: String,
        @RequestParam(
            "strategy",
            required = false
        )
        shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<ShortLinkDto>

    @PostMapping(SHORT_LINKS_CREATE_MAPPING)
    fun create(
        @RequestBody
        @Valid
        shortLinkCreationDto: ShortLinkCreationDto
    ): ResponseEntity<ShortLinkDto>

    @PutMapping(SHORT_LINKS_UPDATE_MAPPING)
    fun update(
        @PathVariable @NotBlank identifier: String,
        @RequestBody @Valid shortLinkUpdateDto: ShortLinkUpdateDto
    ): ResponseEntity<ShortLinkDto>

    @DeleteMapping(SHORT_LINKS_REMOVE_MAPPING)
    fun remove(
        @PathVariable
        @NotBlank
        identifier: String
    ): ResponseEntity<*>
}
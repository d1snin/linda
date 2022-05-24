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
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import javax.validation.constraints.NotBlank

@Validated
interface AvailabilityChangeController {

    @GetMapping(AVAILABILITY_CHANGES_FIND_ALL_MAPPING)
    fun findAll(): ResponseEntity<Set<AvailabilityChangeDto>>

    @GetMapping(AVAILABILITY_CHANGES_FIND_BY_ID_MAPPING)
    fun findById(
        @PathVariable @NotBlank identifier: String
    ): ResponseEntity<AvailabilityChangeDto>

    @PostMapping(AVAILABILITY_CHANGES_TRIGGER_CHECKS)
    fun triggerChecks(): ResponseEntity<Set<AvailabilityChangeDto>>

    @PostMapping(AVAILABILITY_CHANGES_TRIGGER_CHECK_FOR_SHORT_LINK)
    fun triggerCheckForShortLink(
        @PathVariable @NotBlank identifier: String
    ): ResponseEntity<UnsavedAvailabilityChangeDto>

    @DeleteMapping(AVAILABILITY_CHANGES_REMOVE_BY_ID_MAPPING)
    fun removeById(
        @PathVariable @NotBlank identifier: String
    ): ResponseEntity<*>
}
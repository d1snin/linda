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

import dev.d1s.linda.constant.mapping.api.AVAILABILITY_CHANGES_FIND_ALL_MAPPING
import dev.d1s.linda.constant.mapping.api.AVAILABILITY_CHANGES_FIND_BY_ID_MAPPING
import dev.d1s.linda.constant.mapping.api.AVAILABILITY_CHANGES_REMOVE_BY_ID_MAPPING
import dev.d1s.linda.constant.mapping.api.AVAILABILITY_CHANGES_TRIGGER_CHECKS
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.teabag.web.dto.ErrorDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotBlank

@Validated
@Tag(
    name = "Availability changes",
    description = "Find and remove Availability change reports. Trigger Availability checks."
)
interface AvailabilityChangeController {

    @GetMapping(AVAILABILITY_CHANGES_FIND_ALL_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Find all availability change reports.",
        description = "Answers with the entire list of available Availability change objects. Always returns 200."
    )
    fun findAll(
        @RequestParam @Parameter(description = "The page number.") page: Int?,
        @RequestParam @Parameter(description = "The page size.") size: Int?
    ): ResponseEntity<Page<AvailabilityChangeDto>>

    @GetMapping(AVAILABILITY_CHANGES_FIND_BY_ID_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Find Availability change by ID.",
        description = "Returns an Availability change object associated with the provided ID.",
        responses = [
            ApiResponse(
                description = "Found Availability change.",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = AvailabilityChangeDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Requested Availability change was not found.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            )
        ]
    )
    fun findById(
        @PathVariable @NotBlank(message = "identifier must not be blank.") identifier: String
    ): ResponseEntity<AvailabilityChangeDto>

    @PostMapping(AVAILABILITY_CHANGES_TRIGGER_CHECKS, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Manually trigger Availability checks.",
        description = "Manually start the process of checking the availability of all Short links."
    )
    fun triggerChecks(): ResponseEntity<Set<AvailabilityChangeDto>>

    @DeleteMapping(AVAILABILITY_CHANGES_REMOVE_BY_ID_MAPPING)
    @Operation(
        summary = "Delete Availability change.",
        description = "Completely removes an Availability change object found by the provided ID.",
        responses = [
            ApiResponse(
                description = "Deleted the Availability change.",
                responseCode = "204"
            ),
            ApiResponse(
                description = "Requested Availability change was not found.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            )
        ]
    )
    fun removeById(
        @PathVariable @NotBlank(message = "identifier must not be blank.") identifier: String
    ): ResponseEntity<*>
}
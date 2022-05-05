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
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.dto.utm.UtmParameterCreationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.linda.dto.utm.UtmParameterUpdateDto
import dev.d1s.teabag.web.dto.ErrorDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Tag(name = "UTM parameters", description = "Find, create, update and delete UTM parameters.")
interface UtmParameterController {

    @GetMapping(UTM_PARAMETERS_FIND_ALL_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Find all UTM parameters.",
        description = "Answers with the entire list of available UTM parameter objects. Always returns 200."
    )
    fun findAll(): ResponseEntity<Set<UtmParameterDto>>

    @GetMapping(UTM_PARAMETERS_FIND_BY_ID_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Find UTM parameter by ID.",
        description = "Returns the UTM parameter object associated with the provided ID.",
        responses = [
            ApiResponse(
                description = "Found UTM parameter.",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = UtmParameterDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Requested UTM parameter was not found.",
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
    ): ResponseEntity<UtmParameterDto>

    @GetMapping(UTM_PARAMETERS_FIND_BY_TYPE_AND_VALUE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Find UTM parameter by type and value.",
        description = "Returns an UTM parameter object associated with the given type and value.",
        responses = [
            ApiResponse(
                description = "Found UTM parameter.",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = UtmParameterDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Requested UTM parameter was not found.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            )
        ]
    )
    fun findByTypeAndValue(
        @PathVariable type: UtmParameterType,
        @PathVariable @NotBlank(message = "UTM parameter value must not be blank.") value: String
    ): ResponseEntity<UtmParameterDto>

    @PostMapping(UTM_PARAMETERS_CREATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Create UTM parameter.",
        description = "Creates UTM parameter object.",
        responses = [
            ApiResponse(
                description = "Created UTM parameter.",
                responseCode = "201",
                headers = [
                    Header(name = "Location", description = "The location of newly created UTM parameter.")
                ],
                content = [
                    Content(
                        schema = Schema(implementation = UtmParameterDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Request body is invalid.",
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            )
        ]
    )
    fun create(
        @RequestBody @Valid alteration: UtmParameterCreationDto
    ): ResponseEntity<UtmParameterDto>

    @PutMapping(UTM_PARAMETERS_UPDATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Update UTM parameter.",
        description = "Completely updates the UTM parameter object found by the given ID.",
        responses = [
            ApiResponse(
                description = "Updated the UTM parameter.",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = UtmParameterDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Requested UTM parameter or one of Redirects was not found by the provided ID.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "UTM parameter with the same type and value already exists.",
                responseCode = "422",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "The request body is invalid.",
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            )
        ]
    )
    fun update(
        @PathVariable @NotBlank(message = "identifier must not be blank.") identifier: String,
        @RequestBody @Valid utmParameterUpdateDto: UtmParameterUpdateDto
    ): ResponseEntity<UtmParameterDto>

    @DeleteMapping(UTM_PARAMETERS_REMOVE_BY_ID_MAPPING)
    @Operation(
        summary = "Delete UTM parameter.",
        description = "Completely removes the UTM parameter object found by the provided ID.",
        responses = [
            ApiResponse(
                description = "Deleted the UTM parameter.",
                responseCode = "204"
            ),
            ApiResponse(
                description = "Requested UTM parameter was not found.",
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
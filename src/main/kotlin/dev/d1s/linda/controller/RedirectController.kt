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
import dev.d1s.linda.dto.redirect.RedirectCreationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.redirect.RedirectUpdateDto
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
@Tag(name = "Redirects", description = "Find, create, update and delete Redirects.")
interface RedirectController {

    @GetMapping(
        REDIRECTS_FIND_ALL_MAPPING,
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @Operation(
        summary = "Find all Redirects.",
        description = "Answers with the entire list of available Redirect objects. Always returns 200."
    )
    fun findAll(): ResponseEntity<Set<RedirectDto>>

    @GetMapping(REDIRECTS_FIND_BY_ID_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Find Redirect by ID.",
        description = "Returns a Redirect object associated with the provided ID.",
        responses = [
            ApiResponse(
                description = "Found Redirect.",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = RedirectDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Requested Redirect was not found.",
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
    ): ResponseEntity<RedirectDto>

    @PostMapping(REDIRECTS_CREATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Create Redirect.",
        description = "Creates a Redirect object.",
        responses = [
            ApiResponse(
                description = "Created Redirect.",
                responseCode = "201",
                headers = [
                    Header(name = "Location", description = "The location of newly created Redirect.")
                ],
                content = [
                    Content(
                        schema = Schema(implementation = RedirectDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Short link or one of UTM parameters was not found by the provided ID.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
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
        @RequestBody @Valid creation: RedirectCreationDto
    ): ResponseEntity<RedirectDto>

    @PutMapping(REDIRECTS_UPDATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Update Redirect.",
        description = "Completely updates a Redirect object found by the given ID.",
        responses = [
            ApiResponse(
                description = "Updated the Redirect.",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = RedirectDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Requested Redirect, Short link or one of UTM parameters was not found by the provided ID.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
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
    fun update(
        @PathVariable @NotBlank(message = "identifier must not be blank.") identifier: String,
        @RequestBody @Valid alteration: RedirectUpdateDto
    ): ResponseEntity<RedirectDto>

    @DeleteMapping(REDIRECTS_REMOVE_BY_ID_MAPPING)
    @Operation(
        summary = "Delete Redirect.",
        description = "Completely removes a Redirect object found by the provided ID.",
        responses = [
            ApiResponse(
                description = "Deleted the Redirect.",
                responseCode = "204"
            ),
            ApiResponse(
                description = "Requested Redirect was not found.",
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
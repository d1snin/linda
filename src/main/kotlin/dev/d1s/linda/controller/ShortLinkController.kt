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
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import dev.d1s.teabag.web.dto.ErrorDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.repository.query.Param
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Tag(name = "Short links", description = "Find, create, update and delete Short links.")
interface ShortLinkController {

    @GetMapping(SHORT_LINKS_FIND_ALL_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Find all Short links.",
        description = "Answers with the entire list of available Short link objects. Always returns 200."
    )
    fun findAll(): ResponseEntity<Set<ShortLinkDto>>

    @GetMapping(SHORT_LINKS_FIND_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Find Short link by ID or Alias.",
        description = "Returns the Short link object associated with the provided ID or Alias",
        responses = [
            ApiResponse(
                description = "Found Short link.",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = ShortLinkDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Requested Short link was not found.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            )
        ]
    )
    fun find(
        @PathVariable @NotBlank identifier: String,
        @RequestParam(
            "strategy",
            required = false
        ) @Parameter(description = "Finding strategy to use. Default is BY_ID.")
        shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<ShortLinkDto>

    @PostMapping(SHORT_LINKS_CREATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Create Short link.",
        description = "Creates the Short link object.",
        responses = [
            ApiResponse(
                description = "Created Short link.",
                responseCode = "201",
                headers = [
                    Header(name = "Location", description = "The location of newly created Short link.")
                ],
                content = [
                    Content(
                        schema = Schema(implementation = ShortLinkDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Alias generator was not found by the provided ID.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Request body is invalid or the 'customAlias' request parameter is empty " +
                        "(if 'custom' alias generator used).",
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
        @RequestBody @Valid shortLinkCreationDto: ShortLinkCreationDto,
        // note that this parameter is only used for documentation purposes. 'custom' alias generator uses currentRequest to access the parameter.
        @RequestParam(required = false)
        @Param("Custom alias. Must be used together with the 'custom' alias generator.")
        customAlias: String? = null
    ): ResponseEntity<ShortLinkDto>

    @PutMapping(SHORT_LINKS_UPDATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Update Short link.",
        description = "Completely updates the Short link object found by the given ID.",
        responses = [
            ApiResponse(
                description = "Updated the Short link.",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = RedirectDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Requested Short link, one of Redirects or one of Availability changes was not found by the provided ID.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "The request body is invalid. " +
                        "Or in case if you use 'custom' alias generator: " +
                        "'customAlias' request parameter is not defined or 'customAlias' parameter value is empty.",
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "The provided alias already exists with the same name. " +
                        "This happens only in case if you use 'custom' alias generator.",
                responseCode = "422",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            )
        ]
    )
    fun update(
        @PathVariable @NotBlank identifier: String,
        @RequestBody @Valid shortLinkUpdateDto: ShortLinkUpdateDto
    ): ResponseEntity<ShortLinkDto>

    @DeleteMapping(SHORT_LINKS_REMOVE_MAPPING)
    @Operation(
        summary = "Delete Short link.",
        description = "Completely removes the Short link object found by the provided ID. " +
                "Note that this operation will remove all associated Redirects as well.",
        responses = [
            ApiResponse(
                description = "Deleted the Short link.",
                responseCode = "204"
            ),
            ApiResponse(
                description = "Requested Short link was not found.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            )
        ]
    )
    fun remove(
        @PathVariable @NotBlank identifier: String
    ): ResponseEntity<*>
}
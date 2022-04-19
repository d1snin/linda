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
import org.springframework.data.domain.Page
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
    fun findAll(
        @RequestParam(required = false) @Parameter(description = "The page number.") page: Int?,
        @RequestParam(required = false) @Parameter(description = "The page size.") size: Int?
    ): ResponseEntity<Page<ShortLinkDto>>

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
        @RequestBody @Valid shortLinkCreationDto: ShortLinkCreationDto
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
                description = "Requested Short link or one of Redirects was not found by the provided ID.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorDto::class)
                    )
                ]
            ),
            ApiResponse(
                description = "Short link with the same alias already exists or the request body is invalid.",
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
        @PathVariable @NotBlank identifier: String,
        @RequestBody @Valid shortLinkUpdateDto: ShortLinkUpdateDto
    ): ResponseEntity<ShortLinkDto>

    @DeleteMapping(SHORT_LINKS_REMOVE_MAPPING)
    @Operation(
        summary = "Delete Short link.",
        description = "Completely removes the Short link found by the provided. " +
                "Note that this operation will remove all associated Redirects as well.",
        responses = [
            ApiResponse(
                description = "Deleted the Short link.",
                responseCode = "200"
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
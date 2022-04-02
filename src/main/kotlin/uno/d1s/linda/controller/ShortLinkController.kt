/*
   Copyright 2022 Linda project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package uno.d1s.linda.controller

import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import uno.d1s.linda.constant.mapping.api.*
import uno.d1s.linda.dto.shortLink.BulkShortLinkRemovalDto
import uno.d1s.linda.dto.shortLink.ShortLinkCreationDto
import uno.d1s.linda.dto.shortLink.ShortLinkDto
import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
interface ShortLinkController {

    @GetMapping(SHORT_LINKS_FIND_ALL_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ): ResponseEntity<Page<ShortLinkDto>>

    @GetMapping(SHORT_LINKS_FIND_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun find(
        @PathVariable @NotBlank identifier: String,
        @RequestParam("strategy", required = false) shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<ShortLinkDto>

    @PostMapping(SHORT_LINKS_CREATE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody @Valid shortLinkCreationDto: ShortLinkCreationDto): ResponseEntity<ShortLinkDto>

    @DeleteMapping(SHORT_LINKS_REMOVE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun remove(
        @PathVariable @NotBlank identifier: String,
        @RequestParam("strategy", required = false) shortLinkFindingStrategy: ShortLinkFindingStrategyType?
    ): ResponseEntity<*>

    @DeleteMapping(SHORT_LINKS_REMOVE_ALL_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun removeAll(): ResponseEntity<*>

    @DeleteMapping(SHORT_LINKS_BULK_REMOVE_MAPPING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun removeAll(@RequestBody bulkShortLinkRemovalDto: BulkShortLinkRemovalDto): ResponseEntity<*>
}
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

package dev.d1s.linda.service

import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.shortLink.BulkShortLinkRemovalDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy

interface ShortLinkService {

    fun findAll(): List<ShortLink>

    fun find(shortLinkFindingStrategy: ShortLinkFindingStrategy): ShortLink

    fun create(url: String, aliasGeneratorId: String): ShortLink

    fun create(shortLinkCreationDto: ShortLinkCreationDto): ShortLink

    fun remove(shortLink: ShortLink): ShortLink

    fun remove(shortLinkFindingStrategy: ShortLinkFindingStrategy): ShortLink

    fun removeAll(): List<ShortLink>

    fun removeAll(shortLinks: List<ShortLink>): List<ShortLink>

    fun removeAll(bulkShortLinkRemovalDto: BulkShortLinkRemovalDto): List<ShortLink>

    fun doesAliasExist(alias: String): Boolean
}
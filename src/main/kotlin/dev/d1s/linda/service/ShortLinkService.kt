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
import dev.d1s.linda.domain.utmParameter.UtmParameterPurpose
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoSet
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy

interface ShortLinkService {

    fun findAll(requireDto: Boolean = false): EntityWithDtoSet<ShortLink, ShortLinkDto>

    fun find(
        shortLinkFindingStrategy: ShortLinkFindingStrategy,
        requireDto: Boolean = false
    ): EntityWithDto<ShortLink, ShortLinkDto>

    fun create(shortLink: ShortLink): EntityWithDto<ShortLink, ShortLinkDto>

    fun update(id: String, shortLink: ShortLink): EntityWithDto<ShortLink, ShortLinkDto>

    fun assignUtmParameters(shortLink: ShortLink, associatedShortLink: ShortLink, purpose: UtmParameterPurpose)

    fun removeById(id: String)

    fun doesAliasExist(alias: String): Boolean

    fun isExpired(shortLink: ShortLink): Boolean

    fun scheduleForDeletion(shortLink: ShortLink)

    fun scheduleAllEphemeralShortLinksForDeletion()
}
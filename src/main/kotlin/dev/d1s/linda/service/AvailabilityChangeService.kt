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

package dev.d1s.linda.service

import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.dto.EntityWithDto
import dev.d1s.linda.dto.EntityWithDtoSet
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto

interface AvailabilityChangeService {

    fun findAll(
        requireDto: Boolean = false
    ): EntityWithDtoSet<AvailabilityChange, AvailabilityChangeDto>

    fun findById(
        id: String,
        requireDto: Boolean = false
    ): EntityWithDto<AvailabilityChange, AvailabilityChangeDto>

    fun findLast(shortLinkId: String): AvailabilityChange?

    fun create(
        availability: AvailabilityChange
    ): EntityWithDto<AvailabilityChange, AvailabilityChangeDto>

    fun removeById(id: String)

    fun checkAvailability(
        shortLink: ShortLink
    ): EntityWithDto<AvailabilityChange, UnsavedAvailabilityChangeDto>

    fun checkAvailability(
        shortLinkId: String
    ): EntityWithDto<AvailabilityChange, UnsavedAvailabilityChangeDto>

    fun checkAndSaveAvailability(shortLink: ShortLink): AvailabilityChange?

    fun checkAvailabilityOfAllShortLinks(): EntityWithDtoSet<AvailabilityChange, AvailabilityChangeDto>
}
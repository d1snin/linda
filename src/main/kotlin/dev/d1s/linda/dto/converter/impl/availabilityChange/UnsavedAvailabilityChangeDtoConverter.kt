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

package dev.d1s.linda.dto.converter.impl.availabilityChange

import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.stdlib.checks.checkNotNull
import org.springframework.stereotype.Component

@Component
class UnsavedAvailabilityChangeDtoConverter : DtoConverter<UnsavedAvailabilityChangeDto, AvailabilityChange> {

    override fun convertToDto(entity: AvailabilityChange): UnsavedAvailabilityChangeDto = UnsavedAvailabilityChangeDto(
        entity.shortLink.id.checkNotNull("short link's id"),
        entity.available,
        entity.unavailabilityReason
    )
}
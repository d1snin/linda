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

package dev.d1s.linda.testUtil

import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto
import dev.d1s.linda.dto.redirect.RedirectAlterationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.dto.utm.UtmParameterAlterationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.teabag.testing.constant.VALID_STUB
import java.time.Instant

val availabilityChangeDto = AvailabilityChangeDto(
    VALID_STUB,
    VALID_STUB,
    true,
    null,
    Instant.EPOCH
)

val unsavedAvailabilityChangeDto = UnsavedAvailabilityChangeDto(
    VALID_STUB,
    true,
    null
)

val availabilityChangeDtoSet = setOf(availabilityChangeDto)

val redirectAlterationDto = RedirectAlterationDto(
    VALID_STUB,
    setOf()
)

val redirectDto = RedirectDto(
    VALID_STUB,
    VALID_STUB,
    Instant.EPOCH,
    setOf()
)

val redirectDtoSet = setOf(redirectDto)

val shortLinkCreationDto = ShortLinkCreationDto(
    TEST_URL,
    VALID_STUB,
    false,
    null,
    setOf(),
    setOf()
)

val shortLinkDto = ShortLinkDto(
    VALID_STUB,
    TEST_URL,
    VALID_STUB,
    false,
    null,
    setOf(),
    setOf(),
    Instant.EPOCH,
    setOf(),
    setOf()
)

val shortLinkDtoSet = setOf(shortLinkDto)

val shortLinkUpdateDto = ShortLinkUpdateDto(
    TEST_URL,
    VALID_STUB,
    false,
    null,
    setOf(),
    setOf(),
    setOf(),
    setOf()
)

val utmParameterAlterationDto = UtmParameterAlterationDto(
    UtmParameterType.CONTENT,
    VALID_STUB,
    false
)

val utmParameterDto = UtmParameterDto(
    VALID_STUB,
    UtmParameterType.CONTENT,
    VALID_STUB,
    false,
    setOf(),
    setOf(),
    Instant.EPOCH,
    setOf()
)

val utmParameterDtoSet = setOf(utmParameterDto)
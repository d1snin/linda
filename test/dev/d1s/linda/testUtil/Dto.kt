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

import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto
import dev.d1s.linda.dto.redirect.RedirectCreationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.redirect.RedirectUpdateDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.dto.utmParameter.UtmParameterAlterationDto
import dev.d1s.linda.dto.utmParameter.UtmParameterDto
import dev.d1s.teabag.testing.constant.VALID_STUB
import java.time.Instant

val availabilityChangeDto = AvailabilityChangeDto(
    VALID_STUB,
    Instant.EPOCH,
    VALID_STUB,
    null,
    true
)

val unsavedAvailabilityChangeDto = UnsavedAvailabilityChangeDto(
    VALID_STUB,
    null,
    true
)

val availabilityChangeDtoSet = setOf(availabilityChangeDto)

val redirectCreationDto = RedirectCreationDto(
    VALID_STUB,
    setOf()
)

val redirectUpdateDto = RedirectUpdateDto(
    VALID_STUB
)

val redirectDto = RedirectDto(
    VALID_STUB,
    Instant.EPOCH,
    VALID_STUB,
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
    Instant.EPOCH,
    TEST_URL,
    VALID_STUB,
    false,
    null,
    setOf(),
    setOf(),
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
    setOf()
)

val utmParameterAlterationDto = UtmParameterAlterationDto(
    testUtmParameterType,
    VALID_STUB,
    false
)

val utmParameterDto = UtmParameterDto(
    VALID_STUB,
    Instant.EPOCH,
    testUtmParameterType,
    VALID_STUB,
    false,
    setOf(),
    setOf(),
    setOf()
)

val utmParameterDtoSet = setOf(utmParameterDto)
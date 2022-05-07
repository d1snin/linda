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

import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.domain.alias.FriendlyAliases
import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.teabag.testing.constant.VALID_STUB
import java.time.Instant

const val TEST_URL = "https://d1s.dev/"

val shortLink = ShortLink(
    TEST_URL,
    VALID_STUB,
    true,
    mutableSetOf(),
    mutableSetOf()
).apply {
    id = VALID_STUB
    creationTime = Instant.EPOCH
}

val shortLinks = setOf(shortLink)

val redirect = Redirect(
    shortLink
).apply {
    id = VALID_STUB
    creationTime = Instant.EPOCH
}

val redirects = setOf(redirect)

val utmParameter = UtmParameter(
    UtmParameterType.CONTENT,
    VALID_STUB,
    false
).apply {
    id = VALID_STUB
    creationTime = Instant.EPOCH
}

val utmParameters = setOf(utmParameter)

val availabilityChange = AvailabilityChange(
    shortLink,
    null
)

val availabilityChanges = setOf(availabilityChange)

val friendlyAliases = FriendlyAliases(
    setOf(VALID_STUB),
    setOf(VALID_STUB)
)
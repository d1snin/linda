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

package dev.d1s.linda.dto.availability

import dev.d1s.linda.entity.availability.UnavailabilityReason
import java.time.Instant

class AvailabilityChangeDto(
    val id: String,
    val shortLink: String,
    val available: Boolean,
    val unavailabilityReason: UnavailabilityReason?,
    val creationTime: Instant
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvailabilityChangeDto

        if (id != other.id) return false
        if (shortLink != other.shortLink) return false
        if (available != other.available) return false
        if (unavailabilityReason != other.unavailabilityReason) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + shortLink.hashCode()
        result = 31 * result + available.hashCode()
        result = 31 * result + (unavailabilityReason?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "AvailabilityChangeDto(" +
                "id='$id', " +
                "shortLink='$shortLink', " +
                "available=$available, " +
                "unavailabilityReason=$unavailabilityReason, " +
                "creationTime=$creationTime)"
    }
}
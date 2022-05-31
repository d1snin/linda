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

package dev.d1s.linda.entity.availability

import dev.d1s.linda.entity.shortLink.ShortLink
import dev.d1s.linda.entity.common.Identifiable
import javax.persistence.*

@Entity
@Table(name = "availability_change")
data class AvailabilityChange(

    @ManyToOne(cascade = [CascadeType.MERGE])
    var shortLink: ShortLink,

    @Column
    var unavailabilityReason: UnavailabilityReason?

) : Identifiable() {

    val available get() = unavailabilityReason == null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvailabilityChange

        if (id != other.id) return false
        if (shortLink != other.shortLink) return false
        if (unavailabilityReason != other.unavailabilityReason) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + shortLink.hashCode()
        result = 31 * result + (unavailabilityReason?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "AvailabilityChange(id=$id, " +
                "creationTime=$creationTime, " +
                "shortLink=${shortLink.id}, " +
                "unavailabilityReason=$unavailabilityReason, " +
                "available=$available)"
    }
}
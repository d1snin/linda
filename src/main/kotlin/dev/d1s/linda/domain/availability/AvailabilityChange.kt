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

package dev.d1s.linda.domain.availability

import dev.d1s.linda.domain.ShortLink
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "availability_change")
class AvailabilityChange(
    @ManyToOne(cascade = [CascadeType.MERGE])
    var shortLink: ShortLink,

    @Column(nullable = false)
    var available: Boolean,

    @Column
    var unavailabilityReason: UnavailabilityReason?
) {
    @Id
    @Column
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    var id: String? = null

    @Column
    var creationTime: Instant? = null

    // I have no ANY idea why the hell creationTime is null after saving, so I just use @PrePersist
    @PrePersist
    fun setCreationTime() {
        creationTime = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvailabilityChange

        if (shortLink != other.shortLink) return false
        if (available != other.available) return false
        if (unavailabilityReason != other.unavailabilityReason) return false
        if (id != other.id) return false
        if (creationTime != other.creationTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shortLink.hashCode()
        result = 31 * result + available.hashCode()
        result = 31 * result + (unavailabilityReason?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (creationTime?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "AvailabilityChange(shortLink=$shortLink, available=$available, unavailabilityReason=$unavailabilityReason, id=$id, creationTime=$creationTime)"
    }
}
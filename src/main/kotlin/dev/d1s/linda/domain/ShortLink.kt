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

package dev.d1s.linda.domain

import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.util.mapToIdSet
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "short_link")
data class ShortLink(
    @Column(nullable = false)
    var url: String,

    @Column(nullable = false, unique = true)
    var alias: String,

    @Column(nullable = false)
    var allowUtmParameters: Boolean,

    @ManyToMany
    @JoinTable(
        name = "short_link_default_utm_parameter",
        joinColumns = [JoinColumn(name = "short_link_id")],
        inverseJoinColumns = [JoinColumn(name = "utm_parameter_id")]
    )
    var defaultUtmParameters: MutableSet<UtmParameter>,

    @ManyToMany
    @JoinTable(
        name = "short_link_allowed_utm_parameter",
        joinColumns = [JoinColumn(name = "short_link_id")],
        inverseJoinColumns = [JoinColumn(name = "utm_parameter_id")]
    )
    var allowedUtmParameters: MutableSet<UtmParameter>,
) : Identifiable {
    @Id
    @Column
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    override var id: String? = null

    @Column
    @CreationTimestamp
    override var creationTime: Instant? = null

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "shortLink")
    var redirects: Set<Redirect> = setOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "shortLink")
    var availabilityChanges: MutableSet<AvailabilityChange> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShortLink

        if (url != other.url) return false
        if (alias != other.alias) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + alias.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "ShortLink(" +
            "url='$url', " +
            "alias='$alias', " +
            "allowUtmParameters=$allowUtmParameters, " +
            "id=$id, " +
            "creationTime=$creationTime, " +
            "redirects=${
                redirects.mapToIdSet()
            }, " +
            "availabilityChanges=${
                availabilityChanges.mapToIdSet()
            }, " +
            "defaultUtmParameters=${
                defaultUtmParameters.mapToIdSet()
            }, " +
            "allowedUtmParameters=${
                allowedUtmParameters.mapToIdSet()
            })"
}
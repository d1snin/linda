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

package dev.d1s.linda.entity

import dev.d1s.linda.entity.availability.AvailabilityChange
import dev.d1s.linda.entity.common.Identifiable
import dev.d1s.linda.entity.utmParameter.UtmParameter
import java.time.Duration
import javax.persistence.*

@Entity
@Table(name = "short_link")
data class ShortLink(

    @Column(nullable = false)
    var target: String,

    @Column(nullable = false, unique = true)
    var alias: String,

    @Column(nullable = false)
    var allowUtmParameters: Boolean,

    @Column
    var deleteAfter: Duration?,

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
    var allowedUtmParameters: MutableSet<UtmParameter>

) : Identifiable() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "shortLink")
    var redirects: Set<Redirect> = setOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "shortLink")
    var availabilityChanges: MutableSet<AvailabilityChange> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShortLink

        if (id != other.id) return false
        if (target != other.target) return false
        if (alias != other.alias) return false
        if (allowUtmParameters != other.allowUtmParameters) return false
        if (deleteAfter != other.deleteAfter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + alias.hashCode()
        result = 31 * result + allowUtmParameters.hashCode()
        result = 31 * result + (deleteAfter?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ShortLink(id=$id, creationTime=$creationTime, target='$target', alias='$alias', allowUtmParameters=$allowUtmParameters, deleteAfter=$deleteAfter, defaultUtmParameters=$defaultUtmParameters, allowedUtmParameters=$allowedUtmParameters, redirects=$redirects, availabilityChanges=$availabilityChanges)"
    }
}
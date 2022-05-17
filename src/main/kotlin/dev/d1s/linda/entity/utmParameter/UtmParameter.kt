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

package dev.d1s.linda.entity.utmParameter

import dev.d1s.linda.entity.Identifiable
import dev.d1s.linda.entity.Redirect
import dev.d1s.linda.entity.ShortLink
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "utm_parameter")
data class UtmParameter(
    @Column(nullable = false)
    var type: UtmParameterType,

    @Column(nullable = false)
    var parameterValue: String,

    @Column(nullable = false)
    var allowOverride: Boolean
) : Identifiable {
    @Id
    @Column
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    override var id: String? = null

    @Column
    @CreationTimestamp
    override var creationTime: Instant? = null

    @ManyToMany(mappedBy = "utmParameters")
    var redirects: MutableSet<Redirect> = mutableSetOf()

    @ManyToMany(mappedBy = "defaultUtmParameters")
    var defaultForShortLinks: MutableSet<ShortLink> = mutableSetOf()

    @ManyToMany(mappedBy = "allowedUtmParameters")
    var allowedForShortLinks: MutableSet<ShortLink> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UtmParameter

        if (type != other.type) return false
        if (parameterValue != other.parameterValue) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + parameterValue.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "${id ?: "unsaved"} " +
            "(${type.rawParameter}=$parameterValue)"
}
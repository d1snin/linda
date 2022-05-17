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

import dev.d1s.linda.entity.Redirect
import dev.d1s.linda.entity.ShortLink
import dev.d1s.linda.entity.common.Identifiable
import org.hibernate.Hibernate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "utm_parameter")
data class UtmParameter(

    @Column(nullable = false)
    var type: UtmParameterType,

    @Column(nullable = false)
    var parameterValue: String,

    @Column(nullable = false)
    var allowOverride: Boolean

) : Identifiable() {

    @ManyToMany(mappedBy = "utmParameters")
    var redirects: MutableSet<Redirect> = mutableSetOf()

    @ManyToMany(mappedBy = "defaultUtmParameters")
    var defaultForShortLinks: MutableSet<ShortLink> = mutableSetOf()

    @ManyToMany(mappedBy = "allowedUtmParameters")
    var allowedForShortLinks: MutableSet<ShortLink> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UtmParameter

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = "${id ?: "unsaved"} " +
            "(${type.rawParameter}=$parameterValue)"
}
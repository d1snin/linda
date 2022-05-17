/*
 * Copyright 2022 Linda project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.linda.entity

import dev.d1s.linda.entity.common.Identifiable
import dev.d1s.linda.entity.utmParameter.UtmParameter
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "redirect")
data class Redirect(

    @ManyToOne(cascade = [CascadeType.MERGE])
    var shortLink: ShortLink

) : Identifiable() {

    @ManyToMany
    @JoinTable(
        name = "redirect_utm_parameter",
        joinColumns = [JoinColumn(name = "redirect_id")],
        inverseJoinColumns = [JoinColumn(name = "utm_parameter_id")]
    )
    var utmParameters: MutableSet<UtmParameter> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Redirect

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "Redirect(shortLink=$shortLink, utmParameters=$utmParameters)"
    }
}
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

package dev.d1s.linda.domain

import dev.d1s.linda.domain.utm.UtmParameter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "redirect")
class Redirect(
    @ManyToOne(cascade = [CascadeType.MERGE])
    var shortLink: ShortLink
) {
    @Id
    @Column
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    var id: String? = null

    @Column
    @CreationTimestamp
    var creationTime: Instant? = null

    @ManyToMany
    @JoinTable(
        name = "redirect_utm",
        joinColumns = [JoinColumn(name = "redirect_id")],
        inverseJoinColumns = [JoinColumn(name = "utm_id")]
    )
    var utmParameters: MutableSet<UtmParameter> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Redirect) return false

        if (shortLink != other.shortLink) return false
        if (id != other.id) return false
        if (creationTime != other.creationTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shortLink.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (creationTime?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Redirect(shortLink=$shortLink, id=$id, creationTime=$creationTime, utmParameters=$utmParameters)"
    }
}
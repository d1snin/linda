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

package dev.d1s.linda.entity.shortLink

import dev.d1s.linda.entity.common.Identifiable
import dev.d1s.linda.entity.redirect.Redirect
import javax.persistence.*

@Entity
@Table(name = "template_variable")
data class TemplateVariable(

    @Column(nullable = false)
    var variableName: String,

    @Column(nullable = false)
    var variableValue: String

) : Identifiable() {

    @ManyToOne(cascade = [CascadeType.MERGE])
    lateinit var redirect: Redirect

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TemplateVariable

        if (id != other.id) return false
        if (variableName != other.variableName) return false
        if (variableValue != other.variableValue) return false
        if (redirect != other.redirect) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + variableName.hashCode()
        result = 31 * result + variableValue.hashCode()
        result = 31 * result + redirect.hashCode()
        return result
    }

    override fun toString(): String {
        return "TemplateVariable(id=$id, " +
                "creationTime=$creationTime, " +
                "variableName='$variableName', " +
                "variableValue='$variableValue', " +
                "redirect=${redirect.id})"
    }
}
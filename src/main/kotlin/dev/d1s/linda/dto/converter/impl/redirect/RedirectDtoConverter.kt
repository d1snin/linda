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

package dev.d1s.linda.dto.converter.impl.redirect

import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.shortLink.TemplateVariableDto
import dev.d1s.linda.entity.redirect.Redirect
import dev.d1s.linda.entity.shortLink.TemplateVariable
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.stdlib.collection.mapToSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RedirectDtoConverter : DtoConverter<RedirectDto, Redirect> {

    @set:Autowired
    lateinit var templateVariableDtoConverter: DtoConverter<TemplateVariableDto, TemplateVariable>

    override fun convertToDto(entity: Redirect): RedirectDto =
        RedirectDto(
            requireNotNull(entity.id),
            requireNotNull(entity.creationTime),
            requireNotNull(entity.shortLink.id),
            entity.utmParameters.mapToSet {
                requireNotNull(it.id)
            },
            entity.templateVariables.mapToSet {
                templateVariableDtoConverter.convertToDto(it)
            }
        )
}
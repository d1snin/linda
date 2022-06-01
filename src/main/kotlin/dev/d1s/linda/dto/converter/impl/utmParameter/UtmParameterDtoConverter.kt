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

package dev.d1s.linda.dto.converter.impl.utmParameter

import dev.d1s.linda.dto.utmParameter.UtmParameterDto
import dev.d1s.linda.entity.utmParameter.UtmParameter
import dev.d1s.linda.util.mapToIdSet
import dev.d1s.teabag.dto.DtoConverter
import org.springframework.stereotype.Component

@Component
class UtmParameterDtoConverter : DtoConverter<UtmParameterDto, UtmParameter> {

    override fun convertToDto(entity: UtmParameter): UtmParameterDto = entity.run {
        UtmParameterDto(
            requireNotNull(id),
            requireNotNull(creationTime),
            type,
            parameterValue,
            allowOverride,
            redirects.mapToIdSet(),
            defaultForShortLinks.mapToIdSet(),
            allowedForShortLinks.mapToIdSet()
        )
    }
}
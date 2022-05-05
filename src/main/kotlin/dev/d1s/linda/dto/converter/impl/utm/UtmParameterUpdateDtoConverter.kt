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

package dev.d1s.linda.dto.converter.impl.utm

import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.dto.utm.UtmParameterUpdateDto
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.byId
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.stdlib.collection.mapToMutableSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UtmParameterUpdateDtoConverter : DtoConverter<UtmParameterUpdateDto, UtmParameter> {

    @Autowired
    private lateinit var redirectService: RedirectService

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    override fun convertToEntity(dto: UtmParameterUpdateDto): UtmParameter = UtmParameter(
        dto.type,
        dto.parameterValue,
        dto.allowOverride,
        dto.defaultForShortLinks.mapToMutableSet {
            shortLinkService.find(byId(it))
        },
        dto.allowedForShortLinks.mapToMutableSet {
            shortLinkService.find(byId(it))
        }
    ).apply {
        redirects = dto.redirects.mapToMutableSet {
            redirectService.findById(it)
        }
    }
}
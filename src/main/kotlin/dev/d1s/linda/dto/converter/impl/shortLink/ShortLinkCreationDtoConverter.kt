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

package dev.d1s.linda.dto.converter.impl.shortLink

import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.service.AliasGeneratorService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.stdlib.collection.mapToMutableSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ShortLinkCreationDtoConverter : DtoConverter<ShortLinkCreationDto, ShortLink> {

    @Autowired
    private lateinit var aliasGeneratorService: AliasGeneratorService

    @Autowired
    private lateinit var utmParameterService: UtmParameterService

    override fun convertToEntity(dto: ShortLinkCreationDto): ShortLink =
        ShortLink(
            dto.url,
            aliasGeneratorService
                .getAliasGenerator(dto.aliasGeneratorId)
                .generateAlias(dto),
            dto.allowUtmParameters,
            dto.deleteAfter,
            dto.defaultUtmParameters.mapToMutableSet(
                utmParameterService::findById
            ),
            dto.allowedUtmParameters.mapToMutableSet(
                utmParameterService::findById
            )
        )
}
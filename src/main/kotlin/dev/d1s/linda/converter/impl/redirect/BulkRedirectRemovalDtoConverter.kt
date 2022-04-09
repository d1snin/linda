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

package dev.d1s.linda.converter.impl.redirect

import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.dto.BulkRemovalDto
import dev.d1s.linda.service.RedirectService
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.stdlib.collection.mapToSet
import dev.d1s.teabag.stdlib.exception.operationNotSupported
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BulkRedirectRemovalDtoConverter : DtoConverter<BulkRemovalDto, Set<Redirect>> {

    @Autowired
    private lateinit var redirectService: RedirectService

    override fun convertToDto(entity: Set<Redirect>): Nothing =
        operationNotSupported()

    override fun convertToEntity(dto: BulkRemovalDto): Set<Redirect> = dto.identifiers.mapToSet {
        redirectService.findById(it)
    }
}
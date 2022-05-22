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

package dev.d1s.linda.service

import dev.d1s.linda.dto.utmParameter.UtmParameterDto
import dev.d1s.linda.entity.utmParameter.UtmParameter
import dev.d1s.linda.entity.utmParameter.UtmParameterType
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoSet
import java.util.*

interface UtmParameterService {

    fun findAll(requireDto: Boolean = false): EntityWithDtoSet<UtmParameter, UtmParameterDto>

    fun findById(id: String, requireDto: Boolean = false): EntityWithDto<UtmParameter, UtmParameterDto>

    fun findByTypeAndValue(
        type: UtmParameterType,
        value: String
    ): Optional<UtmParameter>

    fun findByTypeAndValueOrThrow(
        type: UtmParameterType,
        value: String,
        requireDto: Boolean = false
    ): EntityWithDto<UtmParameter, UtmParameterDto>

    fun create(utmParameter: UtmParameter): EntityWithDto<UtmParameter, UtmParameterDto>

    fun update(id: String, utmParameter: UtmParameter): EntityWithDto<UtmParameter, UtmParameterDto>

    fun removeById(id: String)
}
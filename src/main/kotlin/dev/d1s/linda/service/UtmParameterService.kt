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

import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import java.util.*

interface UtmParameterService {

    fun findAll(): Set<UtmParameter>

    fun findById(id: String): UtmParameter

    fun findByTypeAndValue(type: UtmParameterType, value: String): Optional<UtmParameter>

    fun findByTypeAndValueOrThrow(type: UtmParameterType, value: String): UtmParameter

    fun create(utmParameter: UtmParameter): UtmParameter

    fun update(id: String, utmParameter: UtmParameter): UtmParameter

    fun removeById(id: String)
}
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

package dev.d1s.linda.service

import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.utmParameter.UtmParameter
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoSet
import dev.d1s.linda.dto.redirect.RedirectDto

interface RedirectService {

    fun findAll(requireDto: Boolean = false): EntityWithDtoSet<Redirect, RedirectDto>

    fun findById(id: String, requireDto: Boolean = false): EntityWithDto<Redirect, RedirectDto>

    fun create(redirect: Redirect): EntityWithDto<Redirect, RedirectDto>

    fun update(id: String, redirect: Redirect): EntityWithDto<Redirect, RedirectDto>

    fun assignUtmParametersAndSave(redirect: Redirect, utmParameters: Set<UtmParameter>): Redirect

    fun removeById(id: String)
}
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
import dev.d1s.linda.domain.utm.UtmParameter

interface RedirectService {

    fun findAll(): Set<Redirect>

    fun findById(id: String): Redirect

    fun create(redirect: Redirect): Redirect

    fun update(id: String, redirect: Redirect): Redirect

    fun assignUtmParameterAndSave(redirect: Redirect, utmParameter: UtmParameter): Redirect

    fun removeById(id: String)
}
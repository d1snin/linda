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

package dev.d1s.linda.testUtil

import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.DtoSetConverterFacade
import dev.d1s.teabag.dto.util.converterForSet
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic

inline fun <D : Any, E : Any> withStaticConverterFacadeMock(
    dtoConverter: DtoConverter<D, E>,
    block: (DtoSetConverterFacade<D, E>) -> Unit
) {
    mockkStatic("dev.d1s.teabag.dto.util.DtoConverterExtKt") {
        val mock = mockk<DtoSetConverterFacade<D, E>>()

        every {
            dtoConverter.converterForSet()
        } returns mock

        block(mock)
    }
}
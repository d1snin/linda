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

package dev.d1s.linda.converter.utm

import dev.d1s.linda.converter.impl.utm.UtmParameterDtoConverter
import dev.d1s.linda.testUtil.mockUtmParameter
import dev.d1s.linda.testUtil.mockUtmParameterDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [UtmParameterDtoConverter::class])
internal class UtmParameterDtoConverterTest {

    @Autowired
    private lateinit var converter: UtmParameterDtoConverter

    @Test
    fun `should convert to dto`() {
        expectThat(
            converter.convertToDto(mockUtmParameter(true))
        ) isEqualTo mockUtmParameterDto()
    }
}
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

package dev.d1s.linda.dto.converter.utm

import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.dto.converter.impl.utm.UtmParameterCreationDtoConverter
import dev.d1s.linda.dto.utm.UtmParameterCreationDto
import dev.d1s.linda.testUtil.mockUtmParameter
import dev.d1s.teabag.testing.constant.VALID_STUB
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [UtmParameterCreationDtoConverter::class])
internal class UtmParameterCreationDtoConverterTest {

    @Autowired
    private lateinit var utmParameterCreationDtoConverter: UtmParameterCreationDtoConverter

    private val utmParameter = mockUtmParameter()

    @Test
    fun `should return valid converted utm parameter`() {
        expectThat(
            utmParameterCreationDtoConverter.convertToEntity(
                UtmParameterCreationDto(
                    UtmParameterType.CAMPAIGN,
                    VALID_STUB
                )
            )
        ) isEqualTo utmParameter
    }
}
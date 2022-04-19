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

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.dto.converter.impl.utm.UtmParameterUpdateDtoConverter
import dev.d1s.linda.dto.utm.UtmParameterUpdateDto
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.testUtil.mockRedirect
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [UtmParameterUpdateDtoConverter::class])
internal class UtmParameterUpdateDtoConverterTest {

    @Autowired
    private lateinit var utmParameterUpdateDtoConverter: UtmParameterUpdateDtoConverter

    @MockkBean
    private lateinit var redirectService: RedirectService

    private val redirect = mockRedirect()

    @BeforeEach
    fun setup() {
        every {
            redirectService.findById(VALID_STUB)
        } returns redirect
    }

    @Test
    fun `should return valid entity`() {
        expectThat(
            utmParameterUpdateDtoConverter.convertToEntity(
                UtmParameterUpdateDto(
                    UtmParameterType.CAMPAIGN,
                    VALID_STUB,
                    setOf(VALID_STUB)
                )
            )
        ) isEqualTo UtmParameter(
            UtmParameterType.CAMPAIGN,
            VALID_STUB
        ).apply {
            redirects = mutableSetOf(redirect)
        }
    }
}
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

package dev.d1s.linda.converter.shortLink

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.converter.impl.shortLink.ShortLinkDtoConverter
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.testUtil.mockRedirect
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.linda.testUtil.mockShortLinkDto
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [ShortLinkDtoConverter::class])
internal class ShortLinkDtoConverterTest {

    @Autowired
    private lateinit var converter: ShortLinkDtoConverter

    @MockkBean
    private lateinit var redirectService: RedirectService

    private val shortLink = mockShortLink(true)

    private val shortLinkDto = mockShortLinkDto()

    @BeforeEach
    fun setup() {
        every {
            redirectService.findById(VALID_STUB)
        } returns mockRedirect(true)
    }

    @Test
    fun `should convert to dto`() {
        expectThat(
            converter.convertToDto(shortLink)
        ) isEqualTo shortLinkDto
    }

    @Test
    fun `should convert to entity`() {
        expectThat(
            converter.convertToEntity(shortLinkDto)
        ) isEqualTo shortLink

        verify {
            redirectService.findById(VALID_STUB)
        }
    }
}
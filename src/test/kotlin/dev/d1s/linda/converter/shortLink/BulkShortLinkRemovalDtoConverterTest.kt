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
import dev.d1s.linda.converter.impl.shortLink.BulkShortLinkRemovalDtoConverter
import dev.d1s.linda.dto.BulkRemovalDto
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.linda.testUtil.mockShortLinkFindingStrategy
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [BulkShortLinkRemovalDtoConverter::class])
internal class BulkShortLinkRemovalDtoConverterTest {

    @Autowired
    private lateinit var converter: BulkShortLinkRemovalDtoConverter

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    private val shortLinkFindingStrategy = mockShortLinkFindingStrategy()

    private val shortLink = mockShortLink(true)

    @BeforeEach
    fun setup() {
        every {
            shortLinkService.find(shortLinkFindingStrategy)
        } returns shortLink
    }

    @Test
    fun `should throw IllegalStateException`() {
        assertThrows<IllegalStateException> {
            converter.convertToDto(setOf())
        }
    }

    @Test
    fun `should convert to entity`() {
        expectThat(
            converter.convertToEntity(
                BulkRemovalDto(setOf(VALID_STUB))
            )
        ) isEqualTo setOf(shortLink)
    }
}
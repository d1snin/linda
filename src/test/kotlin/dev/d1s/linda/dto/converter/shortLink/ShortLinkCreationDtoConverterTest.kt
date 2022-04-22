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

package dev.d1s.linda.dto.converter.shortLink

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.dto.converter.impl.shortLink.ShortLinkCreationDtoConverter
import dev.d1s.linda.service.AliasGeneratorService
import dev.d1s.linda.testUtil.mockAliasGenerator
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.linda.testUtil.mockShortLinkCreationDto
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.spyk
import io.mockk.verifyAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [ShortLinkCreationDtoConverter::class])
internal class ShortLinkCreationDtoConverterTest {

    @Autowired
    private lateinit var converter: ShortLinkCreationDtoConverter

    @MockkBean
    private lateinit var aliasGeneratorService: AliasGeneratorService

    private val aliasGenerator = spyk(
        mockAliasGenerator()
    )

    private val shortLink = mockShortLink()

    private val shortLinkCreationDto = mockShortLinkCreationDto()

    @BeforeEach
    fun setup() {
        every {
            aliasGeneratorService.getAliasGenerator(VALID_STUB)
        } returns aliasGenerator
    }

    @Test
    fun `should convert to entity`() {
        expectThat(
            converter.convertToEntity(
                shortLinkCreationDto
            )
        ) isEqualTo shortLink

        verifyAll {
            aliasGeneratorService.getAliasGenerator(VALID_STUB)
            aliasGenerator.generateAlias(shortLinkCreationDto)
        }
    }
}
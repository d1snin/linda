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

package dev.d1s.linda.generator

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.generator.impl.Crc32AliasGenerator
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.testUtil.mockShortLinkCreationDto
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [Crc32AliasGenerator::class])
internal class Crc32AliasGeneratorTest {

    @Autowired
    private lateinit var generator: Crc32AliasGenerator

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    private val shortLinkCreationDto = mockShortLinkCreationDto()

    @BeforeEach
    fun setup() {
        every {
            shortLinkService.doesAliasExist(any())
        } returns false
    }

    @Test
    fun `should return valid alias which length is equal to 8`() {
        val alias = assertDoesNotThrow {
            generator.generateAlias(shortLinkCreationDto)
        }

        expectThat(
            alias.length
        ).isEqualTo(8)

        verify {
            shortLinkService.doesAliasExist(alias)
        }
    }
}
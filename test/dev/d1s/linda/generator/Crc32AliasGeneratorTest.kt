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
import dev.d1s.linda.testUtil.prepare
import dev.d1s.linda.testUtil.shortLinkCreationDto
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.endsWith
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [Crc32AliasGenerator::class])
class Crc32AliasGeneratorTest {

    @Autowired
    private lateinit var generator: Crc32AliasGenerator

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @BeforeEach
    fun setup() {
        shortLinkService.prepare()
    }

    @Test
    fun `should generate valid alias`() {
        generator.generateAlias(shortLinkCreationDto)
            .expectValidAlias(CRC32_LENGTH)
    }

    @Test
    fun `should generate valid alias with appended occurrences count`() {
        every {
            shortLinkService.doesAliasExist(any())
        } returns true andThen false

        val result = generator.generateAlias(
            shortLinkCreationDto
        )

        expectThat(result).endsWith(EXPECTED_OCCURRENCES_COUNT)

        result.expectValidAlias(
            CRC32_LENGTH + EXPECTED_OCCURRENCES_COUNT.length
        )
    }

    private fun String.expectValidAlias(length: Int) {
        expectThat(this.length) isEqualTo length

        verify {
            shortLinkService.doesAliasExist(any())
        }
    }

    private companion object {
        private const val CRC32_LENGTH = 8
        private const val EXPECTED_OCCURRENCES_COUNT = "1"
    }
}
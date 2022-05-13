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
import dev.d1s.linda.generator.impl.ZeroWidthAliasGenerator
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.testUtil.prepare
import dev.d1s.linda.testUtil.shortLinkCreationDto
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.endsWith
import kotlin.streams.toList

@SpringBootTest
@ContextConfiguration(classes = [ZeroWidthAliasGenerator::class])
class ZeroWidthAliasGeneratorTest {

    @Autowired
    private lateinit var generator: ZeroWidthAliasGenerator

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @BeforeEach
    fun setup() {
        shortLinkService.prepare()
    }

    @Test
    fun `should generate valid alias`() {
        val alias = generator.generateAlias(shortLinkCreationDto)
        alias.expectCorrectAlias()

        verify {
            shortLinkService.doesAliasExist(alias)
        }
    }

    private fun String.expectCorrectAlias() {
        expectThat(
            ZeroWidthAliasGenerator.zeroWidthCharacters.map {
                it.codePoints().findFirst().asInt
            }
        ).contains(
            this.codePoints().toList()
        )

        expectThat(
            this
        ).endsWith(
            ZeroWidthAliasGenerator.ZERO_WIDTH_SPACE
        )
    }
}
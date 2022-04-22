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

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.domain.alias.FriendlyAliases
import dev.d1s.linda.generator.impl.FriendlyAliasGenerator
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.testUtil.mockShortLinkCreationDto
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ResourceLoader
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

@SpringBootTest
@ContextConfiguration(
    classes = [
        FriendlyAliasGenerator::class,
        FriendlyAliasGeneratorTest.FriendlyAliasGeneratorTestConfiguration::class
    ]
)
internal class FriendlyAliasGeneratorTest {

    @Autowired
    private lateinit var generator: FriendlyAliasGenerator

    @MockkBean(relaxed = true)
    @Suppress("unused")
    private lateinit var resourceLoader: ResourceLoader

    @Autowired // mocked
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    private val shortLinkCreationDto = mockShortLinkCreationDto()

    @BeforeEach
    fun setup() {
        every {
            objectMapper.readValue(any<File>(), FriendlyAliases::class.java)
        } returns friendlyAliases

        every {
            shortLinkService.doesAliasExist(any())
        } returns false
    }

    @Test
    fun `should return valid alias`() {
        expectThat(
            generator.generateAlias(shortLinkCreationDto)
        ) isEqualTo "v-v"

        verify {
            shortLinkService.doesAliasExist("v-v")
        }
    }

    private companion object {
        private val friendlyAliases = FriendlyAliases(
            setOf(VALID_STUB),
            setOf(VALID_STUB)
        )
    }

    // not using MockkBean due to the @PostConstruct usage
    @TestConfiguration
    internal class FriendlyAliasGeneratorTestConfiguration {

        @Bean
        internal fun objectMapper() = mockk<ObjectMapper> {
            every {
                readValue(any<File>(), FriendlyAliases::class.java)
            } returns friendlyAliases
        }
    }
}
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

package dev.d1s.linda.dto.converter.redirect

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.dto.converter.impl.redirect.RedirectAlterationDtoConverter
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.testUtil.mockRedirectAlterationDto
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.linda.testUtil.mockShortLinkFindingStrategy
import dev.d1s.linda.testUtil.mockUtmParameter
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
@ContextConfiguration(classes = [RedirectAlterationDtoConverter::class])
internal class RedirectAlterationDtoConverterTest {

    @Autowired
    private lateinit var redirectAlterationDtoConverter: RedirectAlterationDtoConverter

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @MockkBean
    private lateinit var utmParameterService: UtmParameterService

    private val shortLink = mockShortLink()

    private val utmParameter = mockUtmParameter()

    private val alteration = mockRedirectAlterationDto()

    @BeforeEach
    fun setup() {
        every {
            shortLinkService.find(mockShortLinkFindingStrategy())
        } returns shortLink

        every {
            utmParameterService.findById(VALID_STUB)
        } returns utmParameter
    }

    @Test
    fun `should return valid converted redirect`() {
        expectThat(
            redirectAlterationDtoConverter.convertToEntity(
                alteration
            )
        ) isEqualTo Redirect(shortLink).apply {
            utmParameters = mutableSetOf(utmParameter)
        }
    }
}
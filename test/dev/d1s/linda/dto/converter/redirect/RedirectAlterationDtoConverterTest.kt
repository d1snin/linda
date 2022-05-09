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
import dev.d1s.linda.strategy.shortLink.byId
import dev.d1s.linda.testUtil.prepare
import dev.d1s.linda.testUtil.redirectAlterationDto
import dev.d1s.linda.testUtil.shortLink
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [RedirectAlterationDtoConverter::class])
class RedirectAlterationDtoConverterTest {

    @Autowired
    private lateinit var converter: RedirectAlterationDtoConverter

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @MockkBean
    @Suppress("unused")
    private lateinit var utmParameterService: UtmParameterService

    @BeforeEach
    fun setup() {
        shortLinkService.prepare()
    }

    @Test
    fun `should convert redirect alteration dto to entity`() {
        expectThat(
            converter.convertToEntity(redirectAlterationDto)
        ) isEqualTo Redirect(shortLink)

        verify {
            shortLinkService.find(byId(VALID_STUB))
        }
    }
}
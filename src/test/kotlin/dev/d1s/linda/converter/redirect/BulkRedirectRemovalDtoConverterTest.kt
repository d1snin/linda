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

package dev.d1s.linda.converter.redirect

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.converter.impl.redirect.BulkRedirectRemovalDtoConverter
import dev.d1s.linda.dto.BulkRemovalDto
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.testUtil.mockRedirect
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [BulkRedirectRemovalDtoConverter::class])
internal class BulkRedirectRemovalDtoConverterTest {

    @Autowired
    private lateinit var converter: BulkRedirectRemovalDtoConverter

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
    fun `should throw IllegalStateException`() {
        assertThrows<IllegalStateException> {
            converter.convertToDto(setOf())
        }
    }

    @Test
    fun `should return valid entities`() {
        expectThat(
            converter.convertToEntity(
                BulkRemovalDto(setOf(VALID_STUB))
            )
        ) isEqualTo setOf(redirect)

        verify {
            redirectService.findById(VALID_STUB)
        }
    }
}
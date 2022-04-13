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

package dev.d1s.linda.service

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import dev.d1s.linda.exception.impl.notFound.RedirectNotFoundException
import dev.d1s.linda.repository.RedirectRepository
import dev.d1s.linda.service.impl.RedirectServiceImpl
import dev.d1s.linda.testUtil.mockRedirect
import dev.d1s.linda.testUtil.mockUtmParameter
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [RedirectServiceImpl::class])
internal class RedirectServiceImplTest {

    @SpykBean
    private lateinit var redirectService: RedirectServiceImpl

    @MockkBean
    private lateinit var redirectRepository: RedirectRepository

    private val redirect = mockRedirect(true)

    private val redirects = setOf(redirect)

    private val redirectsList = redirects.toList()

    @BeforeEach
    fun setup() {
        every {
            redirectRepository.findAll()
        } returns redirectsList

        every {
            redirectRepository.findById(VALID_STUB)
        } returns Optional.of(redirect)

        every {
            redirectRepository.findById(INVALID_STUB)
        } returns Optional.empty()

        every {
            redirectRepository.save(redirect)
        } returns redirect

        justRun {
            redirectRepository.deleteById(VALID_STUB)
        }
    }

    @Test
    fun `should find all redirects`() {
        expectThat(
            redirectService.findAll()
        ) isEqualTo redirects

        verify {
            redirectRepository.findAll()
        }
    }

    @Test
    fun `should find by id`() {
        expectThat(
            redirectService.findById(VALID_STUB)
        ) isEqualTo redirect

        verify {
            redirectRepository.findById(VALID_STUB)
        }
    }

    @Test
    fun `should throw RedirectNotFoundException`() {
        assertThrows<RedirectNotFoundException> {
            redirectService.findById(INVALID_STUB)
        }

        verify {
            redirectRepository.findById(INVALID_STUB)
        }
    }

    @Test
    fun `should create redirect`() {
        expectThat(
            redirectService.create(redirect)
        ) isEqualTo redirect

        verify {
            redirectRepository.save(redirect)
        }
    }

    @Test
    fun `should assign utm parameter to the redirect`() {
        val redirect = spyk(mockRedirect())
        val utmParameter = spyk(mockUtmParameter())

        every {
            redirectRepository.save(redirect)
        } returns redirect

        assertDoesNotThrow {
            redirectService.assignUtmParameter(redirect, utmParameter)
        }

        expectThat(redirect.utmParameters).contains(utmParameter)
        expectThat(utmParameter.redirects).contains(redirect)

        verifyAll {
            redirectRepository.save(redirect)
        }
    }

    @Test
    fun `should remove by id`() {
        assertDoesNotThrow {
            redirectService.removeById(VALID_STUB)
        }

        verify {
            redirectRepository.deleteById(VALID_STUB)
        }
    }
}
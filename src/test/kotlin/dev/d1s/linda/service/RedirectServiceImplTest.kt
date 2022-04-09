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
import dev.d1s.linda.exception.impl.RedirectNotFoundException
import dev.d1s.linda.repository.RedirectRepository
import dev.d1s.linda.service.impl.RedirectServiceImpl
import dev.d1s.linda.testUtil.mockRedirect
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.linda.testUtil.mockShortLinkFindingStrategy
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [RedirectServiceImpl::class])
internal class RedirectServiceImplTest {

    @SpykBean
    private lateinit var redirectService: RedirectServiceImpl

    @MockkBean
    private lateinit var redirectRepository: RedirectRepository

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    private val redirect = mockRedirect(true)

    private val redirectWithNoConf = mockRedirect(false)

    private val redirects = setOf(redirect)

    private val redirectsList = redirects.toList()

    private val shortLinkFindingStrategy =
        mockShortLinkFindingStrategy()

    private val shortLink = mockShortLink(true)

    @BeforeEach
    fun setup() {
        every {
            redirectRepository.findAll()
        } returns redirectsList

        every {
            shortLinkService.find(shortLinkFindingStrategy)
        } returns shortLink

        every {
            redirectRepository.findById(VALID_STUB)
        } returns Optional.of(redirect)

        every {
            redirectRepository.findById(INVALID_STUB)
        } returns Optional.empty()

        every {
            redirectRepository.save(redirectWithNoConf)
        } returns redirect

        justRun {
            redirectRepository.delete(redirect)
        }

        justRun {
            redirectRepository.deleteAll()
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
    fun `should find all by short link`() {
        expectThat(
            redirectService.findAllByShortLink(shortLinkFindingStrategy)
        ) isEqualTo redirects

        verify {
            shortLinkService.find(shortLinkFindingStrategy)
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
            redirectService.create(shortLink)
        ) isEqualTo redirect

        verify {
            redirectRepository.save(redirectWithNoConf)
        }
    }

    @Test
    fun `should create redirect by shortLinkFindingStrategy`() {
        expectThat(
            redirectService.create(shortLinkFindingStrategy)
        ) isEqualTo redirect
    }

    @Test
    fun `should remove redirect`() {
        expectThat(
            redirectService.remove(redirect)
        ) isEqualTo redirect

        verify {
            redirectRepository.delete(redirect)
        }
    }

    @Test
    fun `should remove by id`() {
        expectThat(
            redirectService.remove(VALID_STUB)
        ) isEqualTo redirect
    }

    @Test
    fun `should remove all provided redirects`() {
        expectThat(
            redirectService.removeAll(redirects)
        ) isEqualTo redirects

        verify {
            redirectService.remove(redirect)
        }
    }

    @Test
    fun `should remove all`() {
        assertDoesNotThrow {
            redirectService.removeAll()
        }

        verify {
            redirectRepository.deleteAll()
        }
    }

    @Test
    fun `should remove all by short link`() {
        expectThat(
            redirectService.removeAllByShortLink(shortLinkFindingStrategy)
        ) isEqualTo redirects
    }
}
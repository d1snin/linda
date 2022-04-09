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
import dev.d1s.linda.repository.ShortLinkRepository
import dev.d1s.linda.service.impl.ShortLinkServiceImpl
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.linda.strategy.shortLink.byId
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [ShortLinkServiceImpl::class])
internal class ShortLinkServiceImplTest {

    @SpykBean
    private lateinit var shortLinkService: ShortLinkServiceImpl

    @MockkBean
    private lateinit var shortLinkRepository: ShortLinkRepository

    private val shortLink = mockShortLink(true)

    private val shortLinks = setOf(shortLink)

    private val shortLinksList = shortLinks.toList()

    @BeforeEach
    fun setup() {
        every {
            shortLinkRepository.findAll()
        } returns shortLinksList

        every {
            shortLinkRepository.findById(VALID_STUB)
        } returns Optional.of(shortLink)

        every {
            shortLinkRepository.findById(INVALID_STUB)
        } returns Optional.empty()

        every {
            shortLinkRepository.findShortLinkByAliasEquals(VALID_STUB)
        } returns Optional.of(shortLink)

        every {
            shortLinkRepository.save(shortLink)
        } returns shortLink
    }

    @Test
    fun `should find all`() {
        expectThat(
            shortLinkService.findAll()
        ) isEqualTo shortLinks

        verify {
            shortLinkRepository.findAll()
        }
    }

    @Test
    fun `should find by id`() {
        expectThat(
            shortLinkService.find(byId(VALID_STUB))
        ) isEqualTo shortLink

        verify {
            shortLinkRepository.findById(VALID_STUB)
        }
    }

    @Test
    fun `should find by alias`() {
        expectThat(
            shortLinkService.find(byAlias(VALID_STUB))
        ) isEqualTo shortLink

        verify {
            shortLinkRepository.findShortLinkByAliasEquals(VALID_STUB)
        }
    }

    @Test
    fun `should create short link`() {
        expectThat(
            shortLinkService.create(shortLink)
        ) isEqualTo shortLink

        verify {
            shortLinkRepository.save(shortLink)
        }
    }

    // tests for remove operations are missing due to
    // https://github.com/linda-project/linda/issues/23
}
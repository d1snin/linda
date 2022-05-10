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
import dev.d1s.linda.exception.notAllowed.impl.DefaultUtmParameterOverrideNotAllowedException
import dev.d1s.linda.exception.notAllowed.impl.IllegalUtmParametersException
import dev.d1s.linda.exception.notAllowed.impl.UtmParametersNotAllowedException
import dev.d1s.linda.exception.notFound.impl.RedirectNotFoundException
import dev.d1s.linda.repository.RedirectRepository
import dev.d1s.linda.service.impl.RedirectServiceImpl
import dev.d1s.linda.testUtil.*
import dev.d1s.teabag.stdlib.collection.mapToMutableSet
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [RedirectServiceImpl::class])
class RedirectServiceImplTest {

    @Autowired
    private lateinit var redirectServiceImpl: RedirectServiceImpl

    @MockkBean
    private lateinit var redirectRepository: RedirectRepository

    private val changedUtmParameters = utmParameters
        .mapToMutableSet {
            it.copy().apply {
                parameterValue = INVALID_STUB
            }
        }

    @BeforeEach
    fun setup() {
        redirectRepository.prepare()
    }

    @Test
    fun `should find all redirects`() {
        expectThat(
            redirectServiceImpl.findAll()
        ) isEqualTo redirects

        verify {
            redirectRepository.findAll()
        }
    }

    @Test
    fun `should find redirect by id`() {
        expectThat(
            redirectServiceImpl.findById(VALID_STUB)
        ) isEqualTo redirect

        verify {
            redirectRepository.findById(VALID_STUB)
        }
    }

    @Test
    fun `should throw RedirectNotFoundException`() {
        assertThrows<RedirectNotFoundException> {
            redirectServiceImpl.findById(INVALID_STUB)
        }

        verify {
            redirectRepository.findById(INVALID_STUB)
        }
    }

    @Test
    fun `should create redirect`() {
        expectThat(
            redirectServiceImpl.create(redirect)
        ) isEqualTo redirect
    }

    @Test
    fun `should throw UtmParametersNotAllowedException`() {
        val testUtmParameters = utmParameters
        val testShortLink = shortLink
        val testRedirect = redirect.copy()
            .apply {
                shortLink = testShortLink
                utmParameters = testUtmParameters.toMutableSet()
            }

        assertThrows<UtmParametersNotAllowedException> {
            redirectServiceImpl.create(testRedirect)
        }
    }

    @Test
    fun `should throw IllegalUtmParametersException`() {
        val testShortLink = shortLink.copy()
            .apply {
                allowUtmParameters = true
                allowedUtmParameters = utmParameters.toMutableSet()
            }

        val testRedirect = redirect.copy()
            .apply {
                shortLink = testShortLink
                utmParameters = changedUtmParameters
            }

        assertThrows<IllegalUtmParametersException> {
            redirectServiceImpl.create(testRedirect)
        }
    }

    @Test
    fun `should throw DefaultUtmParameterOverrideNotAllowedException`() {
        val testShortLink = shortLink.copy()
            .apply {
                allowUtmParameters = true
                defaultUtmParameters = utmParameters.toMutableSet()
            }

        val testRedirect = redirect.copy()
            .apply {
                shortLink = testShortLink
                utmParameters = changedUtmParameters
            }

        assertThrows<DefaultUtmParameterOverrideNotAllowedException> {
            redirectServiceImpl.create(testRedirect)
        }
    }

    @Test
    fun `should update redirect`() {
        val anotherShortLink = shortLink.copy().apply {
            alias = INVALID_STUB
        }

        val updatedRedirect = redirectServiceImpl.update(
            VALID_STUB,
            redirect.copy().apply {
                shortLink = anotherShortLink
            }
        )

        expectThat(
            updatedRedirect.shortLink
        ) isEqualTo anotherShortLink

        verify {
            redirectRepository.save(updatedRedirect)
        }
    }

    @Test
    fun `should assign utm parameters to redirect and save`() {
        val testRedirect = redirect.copy()
        val testUtmParameters = utmParameters.map {
            it.copy()
        }.toMutableSet()

        val savedRedirect = redirectServiceImpl.assignUtmParametersAndSave(
            testRedirect, testUtmParameters
        )

        val assignedUtmParameters = savedRedirect.utmParameters

        expectThat(
            assignedUtmParameters
        ).containsExactly(testUtmParameters)

        assignedUtmParameters.forEach {
            expectThat(it.redirects).contains(testRedirect)
        }
    }

    @Test
    fun `should remove redirect by id`() {
        assertDoesNotThrow {
            redirectServiceImpl.removeById(VALID_STUB)
        }

        verify {
            redirectRepository.deleteById(VALID_STUB)
        }
    }
}
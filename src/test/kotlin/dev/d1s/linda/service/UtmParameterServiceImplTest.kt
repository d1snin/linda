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
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.exception.impl.alreadyExists.UtmParameterAlreadyExistsException
import dev.d1s.linda.exception.impl.notFound.UtmParameterNotFoundException
import dev.d1s.linda.repository.UtmParameterRepository
import dev.d1s.linda.service.impl.UtmParameterServiceImpl
import dev.d1s.linda.testUtil.mockUtmParameter
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
@ContextConfiguration(classes = [UtmParameterServiceImpl::class])
internal class UtmParameterServiceImplTest {

    @SpykBean
    private lateinit var utmParameterService: UtmParameterServiceImpl

    @MockkBean
    private lateinit var utmParameterRepository: UtmParameterRepository

    private val utmParameter = mockUtmParameter(true)

    private val utmParameters = setOf(utmParameter)

    private val utmParametersList = utmParameters.toList()

    @BeforeEach
    fun setup() {
        every {
            utmParameterRepository.findAll()
        } returns utmParametersList

        every {
            utmParameterRepository.findById(VALID_STUB)
        } returns Optional.of(utmParameter)

        every {
            utmParameterRepository.findById(INVALID_STUB)
        } returns Optional.empty()

        every {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                UtmParameterType.CAMPAIGN,
                VALID_STUB
            )
        } returns Optional.of(utmParameter)

        every {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                UtmParameterType.CAMPAIGN,
                INVALID_STUB
            )
        } returns Optional.empty()

        every {
            utmParameterRepository.save(any())
        } returns utmParameter

        justRun {
            utmParameterRepository.deleteById(VALID_STUB)
        }
    }

    @Test
    fun `should find all`() {
        expectThat(
            utmParameterService.findAll()
        ) isEqualTo utmParameters

        verify {
            utmParameterRepository.findAll()
        }
    }

    @Test
    fun `should find by id`() {
        expectThat(
            utmParameterService.findById(VALID_STUB)
        ) isEqualTo utmParameter

        verify {
            utmParameterRepository.findById(VALID_STUB)
        }
    }

    @Test
    fun `should find by type and value`() {
        expectThat(
            utmParameterService.findByTypeAndValue(
                UtmParameterType.CAMPAIGN,
                VALID_STUB
            )
        ) isEqualTo Optional.of(utmParameter)

        verify {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                UtmParameterType.CAMPAIGN,
                VALID_STUB
            )
        }
    }

    @Test
    fun `should throw UtmParameterNotFoundException`() {
        assertThrows<UtmParameterNotFoundException> {
            utmParameterService.findById(INVALID_STUB)
        }

        verify {
            utmParameterRepository.findById(INVALID_STUB)
        }
    }

    @Test
    fun `should create utm parameter`() {
        val parameter = UtmParameter(
            UtmParameterType.CAMPAIGN,
            INVALID_STUB
        )

        expectThat(
            utmParameterService.create(parameter)
        ) isEqualTo utmParameter
    }

    @Test
    fun `should throw UtmParameterAlreadyExistsException`() {
        assertThrows<UtmParameterAlreadyExistsException> {
            utmParameterService.create(utmParameter)
        }

        verify {
            utmParameterService.findByTypeAndValue(
                UtmParameterType.CAMPAIGN,
                VALID_STUB
            )
        }
    }

    @Test
    fun `should remove by id`() {
        assertDoesNotThrow {
            utmParameterService.removeById(VALID_STUB)
        }

        verify {
            utmParameterRepository.deleteById(VALID_STUB)
        }
    }
}
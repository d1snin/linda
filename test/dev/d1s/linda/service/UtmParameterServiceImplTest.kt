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
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.exception.alreadyExists.impl.UtmParameterAlreadyExistsException
import dev.d1s.linda.exception.notFound.impl.UtmParameterNotFoundException
import dev.d1s.linda.repository.UtmParameterRepository
import dev.d1s.linda.service.impl.UtmParameterServiceImpl
import dev.d1s.linda.testUtil.prepare
import dev.d1s.linda.testUtil.utmParameter
import dev.d1s.linda.testUtil.utmParameters
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [UtmParameterServiceImpl::class])
class UtmParameterServiceImplTest {

    @Autowired
    private lateinit var utmParameterServiceImpl: UtmParameterServiceImpl

    @MockkBean
    private lateinit var utmParameterRepository: UtmParameterRepository

    @MockkBean
    private lateinit var redirectService: RedirectService

    @BeforeEach
    fun setup() {
        utmParameterRepository.prepare()
        redirectService.prepare()
    }

    @Test
    fun `should find all utm parameters`() {
        expectThat(
            utmParameterServiceImpl.findAll()
        ) isEqualTo utmParameters

        verify {
            utmParameterRepository.findAll()
        }
    }

    @Test
    fun `should find utm parameter by id`() {
        expectThat(
            utmParameterServiceImpl.findById(VALID_STUB)
        ) isEqualTo utmParameter

        verify {
            utmParameterRepository.findById(VALID_STUB)
        }
    }

    @Test
    fun `should throw UtmParameterNotFoundException when finding by invalid id`() {
        assertThrows<UtmParameterNotFoundException> {
            utmParameterServiceImpl.findById(INVALID_STUB)
        }

        verify {
            utmParameterRepository.findById(INVALID_STUB)
        }
    }

    @Test
    fun `should find utm parameter by type and value`() {
        expectThat(
            utmParameterServiceImpl.findByTypeAndValue(
                UtmParameterType.CONTENT,
                VALID_STUB
            )
        ) isEqualTo Optional.of(utmParameter)

        verify {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                UtmParameterType.CONTENT,
                VALID_STUB
            )
        }
    }

    @Test
    fun `should return empty optional when finding by invalid type and value`() {
        expectThat(
            utmParameterServiceImpl.findByTypeAndValue(
                UtmParameterType.CONTENT,
                INVALID_STUB
            )
        ) isEqualTo Optional.empty()

        verify {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                UtmParameterType.CONTENT,
                INVALID_STUB
            )
        }
    }

    @Test
    fun `should throw UtmParameterNotFoundException when finding by invalid type and value`() {
        assertThrows<UtmParameterNotFoundException> {
            utmParameterServiceImpl.findByTypeAndValueOrThrow(
                UtmParameterType.CONTENT,
                INVALID_STUB
            )
        }

        verify {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                UtmParameterType.CONTENT,
                INVALID_STUB
            )
        }
    }

    @Test
    fun `should create utm parameter`() {
        every {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                UtmParameterType.CONTENT,
                VALID_STUB
            )
        } returns Optional.empty()

        expectThat(
            utmParameterServiceImpl.create(utmParameter)
        ) isEqualTo utmParameter

        verify {
            utmParameterRepository.save(utmParameter)
        }
    }

    @Test
    fun `should throw UtmParameterAlreadyExistsException`() {
        assertThrows<UtmParameterAlreadyExistsException> {
            utmParameterServiceImpl.create(utmParameter)
        }
    }

    @Test
    fun `should update utm parameter`() {
        val anotherUtmParameter = utmParameter.copy().apply {
            parameterValue = INVALID_STUB
        }

        val updatedUtmParameter = utmParameterServiceImpl.update(
            VALID_STUB,
            anotherUtmParameter
        )

        expectThat(
            updatedUtmParameter.parameterValue
        ) isEqualTo INVALID_STUB

        verify {
            utmParameterRepository.save(updatedUtmParameter)
        }
    }

    @Test
    fun `should remove utm parameter by id`() {
        assertDoesNotThrow {
            utmParameterServiceImpl.removeById(VALID_STUB)
        }

        verify {
            utmParameterRepository.deleteById(VALID_STUB)
        }
    }
}
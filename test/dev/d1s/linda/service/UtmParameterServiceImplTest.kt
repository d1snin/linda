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
import dev.d1s.linda.constant.lp.UTM_PARAMETER_CREATED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_REMOVED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_UPDATED_GROUP
import dev.d1s.linda.entity.utmParameter.UtmParameter
import dev.d1s.linda.dto.utmParameter.UtmParameterDto
import dev.d1s.linda.event.data.utmParameter.CommonUtmParameterEventData
import dev.d1s.linda.event.data.utmParameter.UtmParameterUpdatedEventData
import dev.d1s.linda.exception.alreadyExists.impl.UtmParameterAlreadyExistsException
import dev.d1s.linda.exception.notFound.impl.UtmParameterNotFoundException
import dev.d1s.linda.repository.UtmParameterRepository
import dev.d1s.linda.service.impl.UtmParameterServiceImpl
import dev.d1s.linda.testUtil.*
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.verify
import io.mockk.verifyAll
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

    @MockkBean
    private lateinit var utmParameterDtoConverter: DtoConverter<UtmParameterDto, UtmParameter>

    @MockkBean(relaxed = true)
    private lateinit var publisher: AsyncLongPollingEventPublisher


    @BeforeEach
    fun setup() {
        utmParameterRepository.prepare()
        redirectService.prepare()
        utmParameterDtoConverter.prepare()
    }

    @Test
    fun `should find all utm parameters`() {
        withStaticConverterFacadeMock(utmParameterDtoConverter) { converter ->
            converter.prepare()

            expectThat(
                utmParameterServiceImpl.findAll(true)
            ) isEqualTo (utmParameters to utmParameterDtoSet)

            verifyAll {
                utmParameterRepository.findAll()
                converter.convertToDtoSet(utmParameters)
            }
        }
    }

    @Test
    fun `should find utm parameter by id`() {
        expectThat(
            utmParameterServiceImpl.findById(VALID_STUB, true)
        ) isEqualTo (utmParameter to utmParameterDto)

        verifyAll {
            utmParameterRepository.findById(VALID_STUB)
            utmParameterDtoConverter.convertToDto(utmParameter)
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
                testUtmParameterType,
                VALID_STUB
            )
        ) isEqualTo Optional.of(utmParameter)

        verify {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                testUtmParameterType,
                VALID_STUB
            )
        }
    }

    @Test
    fun `should return empty optional when finding by invalid type and value`() {
        expectThat(
            utmParameterServiceImpl.findByTypeAndValue(
                testUtmParameterType,
                INVALID_STUB
            )
        ) isEqualTo Optional.empty()

        verify {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                testUtmParameterType,
                INVALID_STUB
            )
        }
    }

    @Test
    fun `should throw UtmParameterNotFoundException when finding by invalid type and value`() {
        assertThrows<UtmParameterNotFoundException> {
            utmParameterServiceImpl.findByTypeAndValueOrThrow(
                testUtmParameterType,
                INVALID_STUB
            )
        }

        verify {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                testUtmParameterType,
                INVALID_STUB
            )
        }
    }

    @Test
    fun `should create utm parameter`() {
        every {
            utmParameterRepository.findUtmParameterByTypeAndValue(
                testUtmParameterType,
                VALID_STUB
            )
        } returns Optional.empty()

        expectThat(
            utmParameterServiceImpl.create(utmParameter)
        ) isEqualTo (utmParameter to utmParameterDto)

        verifyAll {
            // verification fails without this
            utmParameterRepository.findUtmParameterByTypeAndValue(
                testUtmParameterType,
                VALID_STUB
            )

            utmParameterRepository.save(utmParameter)
            utmParameterDtoConverter.convertToDto(utmParameter)
            publisher.publish(
                UTM_PARAMETER_CREATED_GROUP,
                VALID_STUB,
                CommonUtmParameterEventData(utmParameterDto)
            )
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

        val (updatedUtmParameter, dto) = utmParameterServiceImpl.update(
            VALID_STUB,
            anotherUtmParameter
        )

        expectThat(
            updatedUtmParameter.parameterValue
        ) isEqualTo INVALID_STUB

        verifyAll {
            // verification fails without this
            utmParameterRepository.findById(VALID_STUB)

            utmParameterRepository.save(updatedUtmParameter)
            utmParameterDtoConverter.convertToDto(updatedUtmParameter)
            publisher.publish(
                UTM_PARAMETER_UPDATED_GROUP,
                utmParameter.id,
                UtmParameterUpdatedEventData(
                    utmParameterDto,
                    dto!!
                )
            )
        }
    }

    @Test
    fun `should remove utm parameter by id`() {
        assertDoesNotThrow {
            utmParameterServiceImpl.removeById(VALID_STUB)
        }

        verifyAll {
            // verification fails without this
            utmParameterRepository.findById(VALID_STUB)

            utmParameterRepository.delete(utmParameter)
            publisher.publish(
                UTM_PARAMETER_REMOVED_GROUP,
                VALID_STUB,
                CommonUtmParameterEventData(utmParameterDto)
            )
        }
    }
}
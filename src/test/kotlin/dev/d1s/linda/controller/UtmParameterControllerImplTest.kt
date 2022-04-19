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

package dev.d1s.linda.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.constant.lp.UTM_PARAMETER_CREATED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_REMOVED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_UPDATED_GROUP
import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.controller.impl.UtmParameterControllerImpl
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.dto.utm.UtmParameterCreationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.linda.dto.utm.UtmParameterUpdateDto
import dev.d1s.linda.event.data.UtmParameterEventData
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.testConfiguration.LocalValidatorFactoryBeanConfiguration
import dev.d1s.linda.testUtil.*
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.data.toPage
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.justRun
import io.mockk.verifyAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.*

@ContextConfiguration(
    classes = [
        UtmParameterControllerImpl::class,
        JacksonAutoConfiguration::class,
        LocalValidatorFactoryBeanConfiguration::class
    ]
)
@WebMvcTest(
    controllers = [UtmParameterControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
internal class UtmParameterControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var utmParameterService: UtmParameterService

    @MockkBean
    private lateinit var utmParameterDtoConverter: DtoConverter<UtmParameterDto, UtmParameter>

    @MockkBean
    private lateinit var utmParameterCreationDtoConverter: DtoConverter<UtmParameterCreationDto, UtmParameter>

    @MockkBean
    private lateinit var utmParameterUpdateDtoConverter: DtoConverter<UtmParameterUpdateDto, UtmParameter>

    @MockkBean(relaxed = true)
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val utmParameter = mockUtmParameter(true)

    private val utmParameters = setOf(utmParameter)

    private val utmParameterDto = mockUtmParameterDto()

    private val utmParametersDto = setOf(utmParameterDto)

    private val utmParameterCreationDto = mockUtmParameterCreationDto()

    private val utmParameterUpdateDto = mockUtmParameterUpdateDto()

    @BeforeEach
    fun setup() {
        every {
            utmParameterService.findAll()
        } returns utmParameters

        every {
            utmParameterService.findById(VALID_STUB)
        } returns utmParameter

        every {
            utmParameterService.create(utmParameter)
        } returns utmParameter

        every {
            utmParameterService.update(VALID_STUB, utmParameter)
        } returns utmParameter

        every {
            utmParameterDtoConverter.convertToDto(utmParameter)
        } returns utmParameterDto

        every {
            utmParameterCreationDtoConverter.convertToEntity(utmParameterCreationDto)
        } returns utmParameter

        every {
            utmParameterUpdateDtoConverter.convertToEntity(utmParameterUpdateDto)
        } returns utmParameter

        justRun {
            utmParameterService.removeById(VALID_STUB)
        }
    }

    @Test
    fun `should find all utm parameters`() {
        withStaticMocks(utmParameterDtoConverter, utmParametersDto, utmParameters) { page, converter ->
            mockMvc.get(UTM_PARAMETERS_FIND_ALL_MAPPING) {
                param("page", "0")
                param("size", "0")
            }.andExpect {
                status {
                    isOk()
                }

                content {
                    json(objectMapper.writeValueAsString(page))
                }
            }

            verifyAll {
                utmParameterService.findAll()
                utmParametersDto.toPage(0, 0)
                converter.convertToDtoSet(utmParameters)
            }
        }
    }

    @Test
    fun `should find by id`() {
        mockMvc.get(UTM_PARAMETERS_FIND_BY_ID_MAPPING.setId()).andExpect {
            status {
                isOk()
            }

            content {
                json(objectMapper.writeValueAsString(utmParameterDto))
            }
        }

        verifyAll {
            utmParameterService.findById(VALID_STUB)
            utmParameterDtoConverter.convertToDto(utmParameter)
        }
    }

    @Test
    fun `should create utm parameter`() {
        mockMvc.post(UTM_PARAMETERS_CREATE_MAPPING) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(utmParameterCreationDto)
        }.andExpect {
            status {
                isCreated()
            }

            content {
                json(objectMapper.writeValueAsString(utmParameterDto))
            }
        }

        verifyAll {
            utmParameterCreationDtoConverter.convertToEntity(utmParameterCreationDto)
            utmParameterService.create(utmParameter)
            utmParameterDtoConverter.convertToDto(utmParameter)
            publisher.publish(
                UTM_PARAMETER_CREATED_GROUP,
                VALID_STUB,
                UtmParameterEventData(utmParameterDto)
            )
        }
    }

    @Test
    fun `should update utm parameter`() {
        mockMvc.put(UTM_PARAMETERS_UPDATE_MAPPING.setId()) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(utmParameterUpdateDto)
        }.andExpect {
            status {
                isOk()
            }

            content {
                json(objectMapper.writeValueAsString(utmParameterDto))
            }
        }

        verifyAll {
            utmParameterUpdateDtoConverter.convertToEntity(utmParameterUpdateDto)
            utmParameterService.update(VALID_STUB, utmParameter)
            utmParameterDtoConverter.convertToDto(utmParameter)
            publisher.publish(
                UTM_PARAMETER_UPDATED_GROUP,
                VALID_STUB,
                UtmParameterEventData(
                    utmParameterDto
                )
            )
        }
    }

    @Test
    fun `should remove by id`() {
        mockMvc.delete(UTM_PARAMETERS_REMOVE_BY_ID_MAPPING.setId())
            .andExpect {
                status {
                    isNoContent()
                }
            }

        verifyAll {
            utmParameterService.removeById(VALID_STUB)
            publisher.publish(
                UTM_PARAMETER_REMOVED_GROUP,
                VALID_STUB,
                UtmParameterEventData(null)
            )
        }
    }
}
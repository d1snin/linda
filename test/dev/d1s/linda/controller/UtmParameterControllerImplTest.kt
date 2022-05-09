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

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.configuration.properties.SslConfigurationProperties
import dev.d1s.linda.constant.lp.UTM_PARAMETER_CREATED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_REMOVED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_UPDATED_GROUP
import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.controller.impl.UtmParameterControllerImpl
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.dto.utm.UtmParameterAlterationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.linda.event.data.UtmParameterEventData
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.testUtil.*
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.stdlib.text.replacePlaceholder
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.verifyAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.*

@ContextConfiguration(
    classes = [UtmParameterControllerImpl::class,
        JacksonAutoConfiguration::class,
        ObjectMapperHolder::class
    ]
)
@WebMvcTest(
    controllers = [UtmParameterControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
class UtmParameterControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var utmParameterService: UtmParameterService

    @MockkBean
    private lateinit var utmParameterDtoConverter: DtoConverter<UtmParameterDto, UtmParameter>

    @MockkBean
    private lateinit var utmParameterAlterationDtoConverter: DtoConverter<UtmParameterAlterationDto, UtmParameter>

    @MockkBean(relaxed = true)
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Suppress("unused")
    @MockkBean(relaxed = true)
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    @BeforeEach
    fun setup() {
        utmParameterService.prepare()
        utmParameterDtoConverter.prepare()
        utmParameterAlterationDtoConverter.prepare()
    }

    @Test
    fun `should find all utm parameters`() {
        withStaticConverterFacadeMock(utmParameterDtoConverter) { converter ->
            converter.prepare()

            mockMvc.get(UTM_PARAMETERS_FIND_ALL_MAPPING).andExpect {
                ok()

                jsonObject(utmParameterDtoSet)
            }

            verifyAll {
                utmParameterService.findAll()
                converter.convertToDtoSet(utmParameters)
            }
        }
    }

    @Test
    fun `should find utm parameter by id`() {
        mockMvc.get(
            UTM_PARAMETERS_FIND_BY_ID_MAPPING.setId()

        ).andExpect {
            ok()

            jsonObject(utmParameterDto)
        }

        verifyAll {
            utmParameterService.findById(VALID_STUB)
            utmParameterDtoConverter.convertToDto(utmParameter)
        }
    }

    @Test
    fun `should find utm parameter by type and value`() {
        mockMvc.get(
            UTM_PARAMETERS_FIND_BY_TYPE_AND_VALUE_MAPPING
                .replacePlaceholder(
                    "type" to UtmParameterType.CONTENT.name,
                    "value" to VALID_STUB
                )

        ).andExpect {
            ok()

            jsonObject(utmParameterDto)
        }

        verifyAll {
            utmParameterService.findByTypeAndValueOrThrow(UtmParameterType.CONTENT, VALID_STUB)
            utmParameterDtoConverter.convertToDto(utmParameter)
        }
    }

    @Test
    fun `should create utm parameter`() {
        mockMvc.post(UTM_PARAMETERS_CREATE_MAPPING) {
            jsonObjectBody(utmParameterAlterationDto)

        }.andExpect {
            status {
                isCreated()
            }

            jsonObject(utmParameterDto)
        }

        verifyAll {
            utmParameterAlterationDtoConverter.convertToEntity(
                utmParameterAlterationDto
            )

            utmParameterService.create(utmParameter)

            utmParameterDtoConverter.convertToDto(utmParameter)

            publisher.publish(
                UTM_PARAMETER_CREATED_GROUP,
                VALID_STUB,
                UtmParameterEventData(
                    utmParameterDto
                )
            )
        }
    }

    @Test
    fun `should update utm parameter`() {
        mockMvc.put(
            UTM_PARAMETERS_UPDATE_MAPPING.setId()
        ) {
            jsonObjectBody(utmParameterAlterationDto)

        }.andExpect {
            ok()

            jsonObject(utmParameterDto)
        }

        verifyAll {
            utmParameterAlterationDtoConverter.convertToEntity(
                utmParameterAlterationDto
            )

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
    fun `should remove utm parameter by id`() {
        mockMvc.delete(
            UTM_PARAMETERS_REMOVE_BY_ID_MAPPING.setId()

        ).andExpect {
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
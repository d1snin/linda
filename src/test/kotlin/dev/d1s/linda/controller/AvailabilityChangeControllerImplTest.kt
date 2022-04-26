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
import dev.d1s.linda.configuration.properties.SslConfigurationProperties
import dev.d1s.linda.constant.lp.AVAILABILITY_CHANGE_REMOVED_GROUP
import dev.d1s.linda.constant.mapping.api.AVAILABILITY_CHANGES_FIND_ALL_MAPPING
import dev.d1s.linda.constant.mapping.api.AVAILABILITY_CHANGES_FIND_BY_ID_MAPPING
import dev.d1s.linda.constant.mapping.api.AVAILABILITY_CHANGES_REMOVE_BY_ID_MAPPING
import dev.d1s.linda.constant.mapping.api.AVAILABILITY_CHANGES_TRIGGER_CHECKS
import dev.d1s.linda.controller.impl.AvailabilityChangeControllerImpl
import dev.d1s.linda.dto.converter.impl.availability.AvailabilityChangeDtoConverter
import dev.d1s.linda.event.data.AvailabilityChangeEventData
import dev.d1s.linda.service.AvailabilityChangeService
import dev.d1s.linda.testUtil.*
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.data.toPage
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
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@ContextConfiguration(classes = [AvailabilityChangeControllerImpl::class, JacksonAutoConfiguration::class])
@WebMvcTest(
    controllers = [AvailabilityChangeControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
internal class AvailabilityChangeControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var availabilityChangeService: AvailabilityChangeService

    @MockkBean
    private lateinit var availabilityChangeDtoConverter: AvailabilityChangeDtoConverter

    @MockkBean(relaxed = true)
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Suppress("unused")
    @MockkBean(relaxed = true)
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val availabilityChange = mockAvailabilityChange()

    private val availabilityChangeDto = mockAvailabilityChangeDto()

    private val availabilityChanges = setOf(availabilityChange)

    private val availabilityChangeDtos = setOf(availabilityChangeDto)

    @BeforeEach
    fun setup() {
        every {
            availabilityChangeService.findAll()
        } returns availabilityChanges

        every {
            availabilityChangeService.findById(VALID_STUB)
        } returns availabilityChange

        every {
            availabilityChangeService.checkAvailabilityOfAllShortLinks()
        } returns availabilityChanges

        justRun {
            availabilityChangeService.removeById(VALID_STUB)
        }

        every {
            availabilityChangeDtoConverter.convertToDto(availabilityChange)
        } returns availabilityChangeDto
    }

    @Test
    fun `should find all`() {
        withStaticMocks(
            availabilityChangeDtoConverter,
            availabilityChangeDtos,
            availabilityChanges
        ) { page, converter ->
            mockMvc.get(AVAILABILITY_CHANGES_FIND_ALL_MAPPING) {
                setPagination()
            }.andExpect {
                status {
                    isOk()
                }

                content {
                    json(objectMapper.writeValueAsString(page))
                }
            }

            verifyAll {
                availabilityChangeService.findAll()
                availabilityChangeDtos.toPage(0, 0)
                converter.convertToDtoSet(availabilityChanges)
            }
        }
    }

    @Test
    fun `should find by id`() {
        mockMvc.get(AVAILABILITY_CHANGES_FIND_BY_ID_MAPPING.setId())
            .andExpect {
                status {
                    isOk()
                }

                content {
                    json(objectMapper.writeValueAsString(availabilityChangeDto))
                }
            }

        verifyAll {
            availabilityChangeService.findById(VALID_STUB)
            availabilityChangeDtoConverter.convertToDto(availabilityChange)
        }
    }

    @Test
    fun `should trigger checks`() {
        withStaticMocks(availabilityChangeDtoConverter, availabilityChangeDtos, availabilityChanges) { _, converter ->
            mockMvc.post(AVAILABILITY_CHANGES_TRIGGER_CHECKS).andExpect {
                status {
                    isOk() // I'm not sure if the status should be 201
                }

                content {
                    json(objectMapper.writeValueAsString(availabilityChangeDtos))
                }
            }

            verifyAll {
                availabilityChangeService.checkAvailabilityOfAllShortLinks()
            }
        }
    }

    @Test
    fun `should remove by id`() {
        mockMvc.delete(AVAILABILITY_CHANGES_REMOVE_BY_ID_MAPPING.setId())
            .andExpect {
                status {
                    isNoContent()
                }
            }

        verifyAll {
            availabilityChangeService.removeById(VALID_STUB)
            publisher.publish(
                AVAILABILITY_CHANGE_REMOVED_GROUP,
                VALID_STUB,
                AvailabilityChangeEventData(null)
            )
        }
    }
}
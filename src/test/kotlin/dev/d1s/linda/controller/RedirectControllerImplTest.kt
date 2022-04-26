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
import dev.d1s.linda.constant.lp.REDIRECT_CREATED_GROUP
import dev.d1s.linda.constant.lp.REDIRECT_REMOVED_GROUP
import dev.d1s.linda.constant.lp.REDIRECT_UPDATED_GROUP
import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.controller.impl.RedirectControllerImpl
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.dto.redirect.RedirectAlterationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.event.data.RedirectEventData
import dev.d1s.linda.service.RedirectService
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

@ContextConfiguration(classes = [RedirectControllerImpl::class, JacksonAutoConfiguration::class])
@WebMvcTest(
    controllers = [RedirectControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
internal class RedirectControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var redirectService: RedirectService

    @MockkBean
    private lateinit var redirectDtoConverter: DtoConverter<RedirectDto, Redirect>

    @MockkBean
    private lateinit var redirectAlterationDtoConverter: DtoConverter<RedirectAlterationDto, Redirect>

    @MockkBean(relaxed = true)
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Suppress("unused")
    @MockkBean(relaxed = true)
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val redirect = mockRedirect(true)

    private val redirects = setOf(redirect)

    private val redirectDto = mockRedirectDto()

    private val redirectsDto = setOf(redirectDto)

    private val alteration = mockRedirectAlterationDto()

    @BeforeEach
    fun setup() {
        every {
            redirectService.findAll()
        } returns redirects

        every {
            redirectService.findById(VALID_STUB)
        } returns redirect

        every {
            redirectService.create(redirect)
        } returns redirect

        every {
            redirectService.update(VALID_STUB, redirect)
        } returns redirect

        every {
            redirectAlterationDtoConverter.convertToEntity(alteration)
        } returns redirect

        every {
            redirectDtoConverter.convertToDto(redirect)
        } returns redirectDto

        justRun {
            redirectService.removeById(VALID_STUB)
        }
    }

    @Test
    fun `should return all redirects`() {
        withStaticMocks(redirectDtoConverter, redirectsDto, redirects) { page, converter ->
            mockMvc.get(REDIRECTS_FIND_ALL_MAPPING) {
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
                redirectService.findAll()
                redirectsDto.toPage(0, 0)
                converter.convertToDtoSet(redirects)
            }
        }
    }

    @Test
    fun `should find by id`() {
        mockMvc.get(REDIRECTS_FIND_BY_ID_MAPPING.setId())
            .andExpect {
                status {
                    isOk()
                }

                content {
                    json(objectMapper.writeValueAsString(redirectDto))
                }
            }

        verifyAll {
            redirectService.findById(VALID_STUB)
            redirectDtoConverter.convertToDto(redirect)
        }
    }

    @Test
    fun `should create redirect`() {
        mockMvc.post(REDIRECTS_CREATE_MAPPING) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(alteration)
        }.andExpect {
            status {
                isCreated()
            }

            content {
                json(objectMapper.writeValueAsString(redirectDto))
            }
        }

        verifyAll {
            redirectAlterationDtoConverter.convertToEntity(
                alteration
            )
            redirectService.create(redirect)
            redirectDtoConverter.convertToDto(redirect)
            publisher.publish(
                REDIRECT_CREATED_GROUP,
                VALID_STUB,
                RedirectEventData(
                    redirectDto
                )
            )
        }
    }

    @Test
    fun `should update redirect`() {
        mockMvc.put(REDIRECTS_UPDATE_MAPPING.setId()) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(alteration)
        }.andExpect {
            status {
                isOk()
            }

            content {
                json(objectMapper.writeValueAsString(redirectDto))
            }
        }

        verifyAll {
            redirectAlterationDtoConverter.convertToEntity(
                alteration
            )
            redirectService.update(VALID_STUB, redirect)
            redirectDtoConverter.convertToDto(redirect)
            publisher.publish(
                REDIRECT_UPDATED_GROUP,
                VALID_STUB,
                RedirectEventData(
                    redirectDto
                )
            )
        }
    }

    @Test
    fun `should remove redirect`() {
        mockMvc.delete(REDIRECTS_REMOVE_BY_ID_MAPPING.setId())
            .andExpect {
                status {
                    isNoContent()
                }
            }

        verifyAll {
            redirectService.removeById(VALID_STUB)
            publisher.publish(
                REDIRECT_REMOVED_GROUP,
                VALID_STUB,
                RedirectEventData(null)
            )
        }
    }
}
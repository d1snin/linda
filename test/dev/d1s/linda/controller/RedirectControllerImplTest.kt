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
import dev.d1s.teabag.dto.DtoConverter
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
    classes = [RedirectControllerImpl::class,
        JacksonAutoConfiguration::class,
        ObjectMapperHolder::class
    ]
)
@WebMvcTest(
    controllers = [RedirectControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
class RedirectControllerImplTest {

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

    @BeforeEach
    fun setup() {
        redirectService.prepare()
        redirectDtoConverter.prepare()
        redirectAlterationDtoConverter.prepare()
    }

    @Test
    fun `should find all redirects`() {
        withStaticConverterFacadeMock(redirectDtoConverter) { converter ->
            converter.prepare()

            mockMvc.get(REDIRECTS_FIND_ALL_MAPPING).andExpect {
                ok()

                jsonObject(redirectDtoSet)
            }

            verifyAll {
                redirectService.findAll()
                converter.convertToDtoSet(redirects)
            }
        }
    }

    @Test
    fun `should find redirect by id`() {
        mockMvc.get(
            REDIRECTS_FIND_BY_ID_MAPPING.setId()
        ).andExpect {
            ok()

            jsonObject(redirectDto)
        }

        verifyAll {
            redirectService.findById(VALID_STUB)
            redirectDtoConverter.convertToDto(redirect)
        }
    }

    @Test
    fun `should create redirect`() {
        mockMvc.post(REDIRECTS_CREATE_MAPPING) {
            jsonObjectBody(redirectAlterationDto)

        }.andExpect {
            status {
                isCreated()
            }

            jsonObject(redirectDto)
        }

        verifyAll {
            redirectAlterationDtoConverter.convertToEntity(
                redirectAlterationDto
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
        mockMvc.put(
            REDIRECTS_UPDATE_MAPPING.setId()
        ) {
            jsonObjectBody(redirectAlterationDto)

        }.andExpect {
            ok()

            jsonObject(redirectDto)
        }

        verifyAll {
            redirectAlterationDtoConverter.convertToEntity(
                redirectAlterationDto
            )

            redirectService.update(
                VALID_STUB,
                redirect
            )

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
    fun `should remove redirect by id`() {
        mockMvc.delete(
            REDIRECTS_REMOVE_BY_ID_MAPPING.setId()
        ).andExpect {
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
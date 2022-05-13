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
import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.controller.impl.RedirectControllerImpl
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.dto.redirect.RedirectAlterationDto
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.testUtil.*
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.verify
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
    private lateinit var redirectAlterationDtoConverter: DtoConverter<RedirectAlterationDto, Redirect>

    @Suppress("unused")
    @MockkBean(relaxed = true)
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    @BeforeEach
    fun setup() {
        redirectService.prepare()
        redirectAlterationDtoConverter.prepare()
    }

    @Test
    fun `should find all redirects`() {
        mockMvc.get(REDIRECTS_FIND_ALL_MAPPING).andExpect {
            ok()

            jsonObject(redirectDtoSet)
        }

        verify {
            redirectService.findAll(true)
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

        verify {
            redirectService.findById(VALID_STUB, true)
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

        verify {
            redirectService.removeById(VALID_STUB)
        }
    }
}
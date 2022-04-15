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
import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.controller.impl.ShortLinkControllerImpl
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import dev.d1s.linda.testConfiguration.LocalValidatorFactoryBeanConfiguration
import dev.d1s.linda.testUtil.*
import dev.d1s.teabag.data.toPage
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
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
        ShortLinkControllerImpl::class,
        JacksonAutoConfiguration::class,
        LocalValidatorFactoryBeanConfiguration::class
    ]
)
@WebMvcTest(
    controllers = [ShortLinkControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
internal class ShortLinkControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @MockkBean
    private lateinit var shortLinkDtoConverter: DtoConverter<ShortLinkDto, ShortLink>

    @MockkBean
    private lateinit var shortLinkCreationDtoConverter: DtoConverter<ShortLinkCreationDto, ShortLink>

    @MockkBean
    private lateinit var shortLinkUpdateDtoConverter: DtoConverter<ShortLinkUpdateDto, ShortLink>

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val shortLink = mockShortLink(true)

    private val shortLinks = setOf(shortLink)

    private val shortLinkDto = mockShortLinkDto()

    private val shortLinksDto = setOf(shortLinkDto)

    private val shortLinkFindingStrategy = mockShortLinkFindingStrategy()

    private val shortLinkCreationDto = mockShortLinkCreationDto()

    private val shortLinkUpdateDto = mockShortLinkUpdateDto()

    @BeforeEach
    fun setup() {
        every {
            shortLinkService.findAll()
        } returns shortLinks

        every {
            shortLinkService.find(shortLinkFindingStrategy)
        } returns shortLink

        every {
            shortLinkDtoConverter.convertToDto(shortLink)
        } returns shortLinkDto

        every {
            shortLinkCreationDtoConverter
                .convertToEntity(shortLinkCreationDto)
        } returns shortLink

        every {
            shortLinkUpdateDtoConverter.convertToEntity(
                shortLinkUpdateDto
            )
        } returns shortLink

        every {
            shortLinkService.create(shortLink)
        } returns shortLink

        every {
            shortLinkService.update(VALID_STUB, shortLink)
        } returns shortLink

        justRun {
            shortLinkService.removeById(VALID_STUB)
        }
    }

    @Test
    fun `should find all short links`() {
        withStaticMocks(shortLinkDtoConverter, shortLinksDto, shortLinks) { page, converter ->
            mockMvc.get(SHORT_LINKS_FIND_ALL_MAPPING) {
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
                shortLinkService.findAll()
                shortLinksDto.toPage(0, 0)
                converter.convertToDtoSet(shortLinks)
            }
        }
    }

    @Test
    fun `should find by id`() {
        mockMvc.get(SHORT_LINKS_FIND_MAPPING.setId()) {
            param("strategy", ShortLinkFindingStrategyType.BY_ID.name)
        }.andExpect {
            status {
                isOk()
            }

            content {
                json(objectMapper.writeValueAsString(shortLinkDto))
            }
        }

        verifyAll {
            shortLinkService.find(shortLinkFindingStrategy)
            shortLinkDtoConverter.convertToDto(shortLink)
        }
    }

    @Test
    fun `should create short link`() {
        mockMvc.post(SHORT_LINKS_CREATE_MAPPING) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(shortLinkCreationDto)
        }.andExpect {
            status {
                isCreated()
            }

            content {
                json(objectMapper.writeValueAsString(shortLinkDto))
            }
        }

        verifyAll {
            shortLinkCreationDtoConverter.convertToEntity(shortLinkCreationDto)
            shortLinkService.create(shortLink)
            shortLinkDtoConverter.convertToDto(shortLink)
        }
    }

    @Test
    fun `should update short link`() {
        mockMvc.put(SHORT_LINKS_UPDATE_MAPPING.setId()) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(shortLinkUpdateDto)
        }.andExpect {
            status {
                isOk()
            }

            content {
                json(objectMapper.writeValueAsString(shortLinkDto))
            }
        }

        verifyAll {
            shortLinkUpdateDtoConverter.convertToEntity(shortLinkUpdateDto)
            shortLinkService.update(VALID_STUB, shortLink)
            shortLinkDtoConverter.convertToDto(shortLink)
        }
    }

    @Test
    fun `should remove short link`() {
        mockMvc.delete(SHORT_LINKS_REMOVE_MAPPING.setId())
            .andExpect {
                status {
                    isNoContent()
                }
            }

        verify {
            shortLinkService.removeById(VALID_STUB)
        }
    }
}
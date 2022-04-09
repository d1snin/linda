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
import dev.d1s.linda.converter.impl.shortLink.ShortLinkCreationDtoConverter
import dev.d1s.linda.converter.impl.shortLink.ShortLinkDtoConverter
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.BulkRemovalDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import dev.d1s.linda.testUtil.*
import dev.d1s.teabag.data.toPage
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.util.converterForSet
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import io.mockk.verifyAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@ContextConfiguration(classes = [ShortLinkControllerImpl::class, JacksonAutoConfiguration::class])
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
    private lateinit var shortLinkDtoConverter: ShortLinkDtoConverter

    @MockkBean
    private lateinit var shortLinkCreationDtoConverter: ShortLinkCreationDtoConverter

    @MockkBean
    private lateinit var bulkShortLinkRemovalDtoConverter: DtoConverter<BulkRemovalDto, Set<ShortLink>>

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val shortLink = mockShortLink(true)

    private val shortLinks = setOf(shortLink)

    private val shortLinkDto = mockShortLinkDto()

    private val shortLinksDto = setOf(shortLinkDto)

    private val dtoSetConverterFacade = mockDtoSetConverterFacade<ShortLinkDto, ShortLink>()

    private val shortLinkFindingStrategy = mockShortLinkFindingStrategy()

    private val shortLinkCreationDto = mockShortLinkCreationDto()

    private val bulkRemovalDto = mockBulkRemovalDto()

    // the SHITTIEST way to disable JSR 380 constraints. Excluding ValidationAutoConfiguration does not work.
    // see: https://www.jvt.me/posts/2020/05/18/disable-valid-annotation-spring-test/
    @MockkBean(relaxed = true)
    @Suppress("unused")
    private lateinit var validator: LocalValidatorFactoryBean

    @BeforeEach
    fun setup() {
        every {
            shortLinkService.findAll()
        } returns shortLinks

        every {
            dtoSetConverterFacade.convertToDtoSet(shortLinks)
        } returns shortLinksDto

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
            shortLinkService.create(shortLink)
        } returns shortLink

        every {
            shortLinkService.remove(shortLinkFindingStrategy)
        } returns shortLink

        every {
            shortLinkService.removeAll()
        } returns shortLinks

        every {
            bulkShortLinkRemovalDtoConverter
                .convertToEntity(bulkRemovalDto)
        } returns shortLinks

        every {
            shortLinkService.removeAll(shortLinks)
        } returns shortLinks
    }

    @Test
    fun `should find all short links`() {
        this.withStaticMocks {
            mockMvc.get(SHORT_LINKS_FIND_ALL_MAPPING) {
                param("page", "0")
                param("size", "0")
            }.andExpect {
                status {
                    isOk()
                }

                content {
                    json(objectMapper.writeValueAsString(it))
                }
            }

            verifyAll {
                shortLinkService.findAll()
                shortLinksDto.toPage(0, 0)
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
                isOk() // should be 201: https://github.com/linda-project/linda/issues/9
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
    fun `should remove short link`() {
        mockMvc.delete(SHORT_LINKS_REMOVE_MAPPING.setId()) {
            param("strategy", ShortLinkFindingStrategyType.BY_ID.name)
        }.andExpect {
            status {
                isNoContent()
            }
        }

        verify {
            shortLinkService.remove(shortLinkFindingStrategy)
        }
    }

    @Test
    fun `should remove all short links`() {
        mockMvc.delete(SHORT_LINKS_REMOVE_ALL_MAPPING).andExpect {
            status {
                isNoContent()
            }
        }

        verify {
            shortLinkService.removeAll()
        }
    }

    @Test
    fun `should remove all short links by provided ids`() {
        mockMvc.delete(SHORT_LINKS_BULK_REMOVE_MAPPING) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bulkRemovalDto)
        }.andExpect {
            status {
                isNoContent()
            }
        }

        verify {
            bulkShortLinkRemovalDtoConverter.convertToEntity(bulkRemovalDto)
            shortLinkService.removeAll(shortLinks)
        }
    }

    private inline fun withStaticMocks(crossinline block: (Page<ShortLinkDto>) -> Unit) {
        mockkStatic("dev.d1s.teabag.dto.util.DtoConverterExtKt") {
            every {
                shortLinkDtoConverter.converterForSet()
            } returns dtoSetConverterFacade

            mockToPageFun(shortLinksDto) {
                block(it)
            }
        }
    }
}
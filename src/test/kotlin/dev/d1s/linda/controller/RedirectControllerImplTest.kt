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
import dev.d1s.linda.controller.impl.RedirectControllerImpl
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.dto.BulkRemovalDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import dev.d1s.linda.strategy.shortLink.byType
import dev.d1s.linda.testUtil.*
import dev.d1s.teabag.data.toPage
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.util.converterForSet
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.*
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
    private lateinit var bulkRedirectRemovalDtoConverter: DtoConverter<BulkRemovalDto, Set<Redirect>>

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val redirect = mockRedirect(true)

    private val redirects = setOf(redirect)

    private val redirectDto = mockRedirectDto()

    private val redirectsDto = setOf(redirectDto)

    private val dtoSetConverterFacade = mockDtoSetConverterFacade<RedirectDto, Redirect>()

    private val shortLinkFindingStrategy = mockShortLinkFindingStrategy()

    private val bulkRemovalDto = mockBulkRemovalDto()

    @BeforeEach
    fun setup() {
        every {
            redirectService.findAll()
        } returns redirects

        every {
            dtoSetConverterFacade.convertToDtoSet(redirects)
        } returns redirectsDto

        every {
            redirectService.findAllByShortLink(shortLinkFindingStrategy)
        } returns redirects

        every {
            redirectService.findById(VALID_STUB)
        } returns redirect

        every {
            redirectDtoConverter.convertToDto(redirect)
        } returns redirectDto

        every {
            redirectService.remove(VALID_STUB)
        } returns redirect

        justRun {
            redirectService.removeAll()
        }

        every {
            bulkRedirectRemovalDtoConverter.convertToEntity(bulkRemovalDto)
        } returns redirects

        every {
            redirectService.removeAll(redirects)
        } returns redirects

        every {
            redirectService.removeAllByShortLink(shortLinkFindingStrategy)
        } returns redirects
    }

    @Test
    fun `should return all redirects`() {
        this.withStaticMocks {
            mockMvc.get(REDIRECTS_FIND_ALL_MAPPING) {
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
                redirectService.findAll()
                redirectsDto.toPage(0, 0)
            }
        }
    }

    @Test
    fun `should find all by short link`() {
        this.withStaticMocks {
            mockkStatic("dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyKt") {
                every {
                    byType(ShortLinkFindingStrategyType.BY_ID, VALID_STUB)
                } returns shortLinkFindingStrategy

                mockMvc.get(REDIRECTS_FIND_ALL_BY_SHORT_LINK_MAPPING.setId()) {
                    param("strategy", ShortLinkFindingStrategyType.BY_ID.name)
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
                    redirectService.findAllByShortLink(shortLinkFindingStrategy)
                    redirectsDto.toPage(0, 0)
                }
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
    fun `should remove redirect`() {
        mockMvc.delete(REDIRECTS_REMOVE_BY_ID_MAPPING.setId())
            .andExpect {
                status {
                    isNoContent()
                }
            }

        verify {
            redirectService.remove(VALID_STUB)
        }
    }

    @Test
    fun `should remove all`() {
        mockMvc.delete(REDIRECTS_REMOVE_ALL_MAPPING).andExpect {
            status {
                isNoContent()
            }
        }

        verify {
            redirectService.removeAll()
        }
    }

    @Test
    fun `should remove all by provided ids`() {
        mockMvc.delete(REDIRECTS_BULK_REMOVE_MAPPING) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bulkRemovalDto)
        }.andExpect {
            status {
                isNoContent()
            }
        }

        verifyAll {
            bulkRedirectRemovalDtoConverter.convertToEntity(
                bulkRemovalDto
            )

            redirectService.removeAll(redirects)
        }
    }

    @Test
    fun `should remove all by short link`() {
        mockMvc.delete(
            REDIRECTS_REMOVE_ALL_BY_SHORT_LINK_MAPPING
                .setId()
        ) {
            param("strategy", ShortLinkFindingStrategyType.BY_ID.name)
        }.andExpect {
            status {
                isNoContent()
            }
        }

        verify {
            redirectService.removeAllByShortLink(shortLinkFindingStrategy)
        }
    }

    private inline fun withStaticMocks(crossinline block: (Page<RedirectDto>) -> Unit) {
        mockkStatic("dev.d1s.teabag.dto.util.DtoConverterExtKt") {
            every {
                redirectDtoConverter.converterForSet()
            } returns dtoSetConverterFacade

            mockToPageFun(redirectsDto) {
                block(it)
            }
        }
    }
}
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
import dev.d1s.linda.constant.lp.SHORT_LINK_CREATED_GROUP
import dev.d1s.linda.constant.lp.SHORT_LINK_REMOVED_GROUP
import dev.d1s.linda.constant.lp.SHORT_LINK_UPDATED_GROUP
import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.controller.impl.ShortLinkControllerImpl
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.event.data.ShortLinkEventData
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType
import dev.d1s.linda.strategy.shortLink.byType
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
    classes = [ShortLinkControllerImpl::class,
        JacksonAutoConfiguration::class,
        ObjectMapperHolder::class
    ]
)
@WebMvcTest(
    controllers = [ShortLinkControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
class ShortLinkControllerImplTest {

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

    @MockkBean(relaxed = true)
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Suppress("unused")
    @MockkBean(relaxed = true)
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    @BeforeEach
    fun setup() {
        shortLinkService.prepare()
        shortLinkDtoConverter.prepare()
        shortLinkCreationDtoConverter.prepare()
        shortLinkUpdateDtoConverter.prepare()
    }

    @Test
    fun `should find all short links`() {
        withStaticConverterFacadeMock(shortLinkDtoConverter) { converter ->
            converter.prepare()

            mockMvc.get(SHORT_LINKS_FIND_ALL_MAPPING).andExpect {
                ok()

                jsonObject(shortLinkDtoSet)
            }

            verifyAll {
                shortLinkService.findAll()
                converter.convertToDtoSet(shortLinks)
            }
        }
    }

    @Test
    fun `should find short link by id`() {
        this.findShortLink(ShortLinkFindingStrategyType.BY_ID)
    }

    @Test
    fun `should find short link by alias`() {
        this.findShortLink(ShortLinkFindingStrategyType.BY_ALIAS)
    }

    @Test
    fun `should create short link`() {
        mockMvc.post(SHORT_LINKS_CREATE_MAPPING) {
            jsonObjectBody(shortLinkCreationDto)

        }.andExpect {
            status {
                isCreated()
            }

            jsonObject(shortLinkDto)
        }

        verifyAll {
            shortLinkCreationDtoConverter.convertToEntity(
                shortLinkCreationDto
            )

            shortLinkService.create(shortLink)

            shortLinkDtoConverter.convertToDto(shortLink)

            publisher.publish(
                SHORT_LINK_CREATED_GROUP,
                VALID_STUB,
                ShortLinkEventData(
                    shortLinkDto
                )
            )
        }
    }

    @Test
    fun `should update short link`() {
        mockMvc.put(
            SHORT_LINKS_UPDATE_MAPPING.setId()
        ) {
            jsonObjectBody(shortLinkUpdateDto)

        }.andExpect {
            ok()

            jsonObject(shortLinkDto)
        }

        verifyAll {
            shortLinkUpdateDtoConverter.convertToEntity(
                shortLinkUpdateDto
            )

            shortLinkService.update(
                VALID_STUB,
                shortLink
            )

            shortLinkDtoConverter.convertToDto(shortLink)

            publisher.publish(
                SHORT_LINK_UPDATED_GROUP,
                VALID_STUB,
                ShortLinkEventData(
                    shortLinkDto
                )
            )
        }
    }

    @Test
    fun `should remove short link`() {
        mockMvc.delete(
            SHORT_LINKS_REMOVE_MAPPING.setId()
        ).andExpect {
            status {
                isNoContent()
            }
        }

        verifyAll {
            shortLinkService.removeById(VALID_STUB)

            publisher.publish(
                SHORT_LINK_REMOVED_GROUP,
                VALID_STUB,
                ShortLinkEventData(null)
            )
        }
    }

    private fun findShortLink(strategy: ShortLinkFindingStrategyType) {
        mockMvc.get(
            SHORT_LINKS_FIND_MAPPING.setId()
        ) {
            setShortLinkFindingStrategy(strategy)

        }.andExpect {
            ok()

            jsonObject(shortLinkDto)
        }

        verifyAll {
            shortLinkService.find(byType(strategy, VALID_STUB))
            shortLinkDtoConverter.convertToDto(shortLink)
        }
    }
}
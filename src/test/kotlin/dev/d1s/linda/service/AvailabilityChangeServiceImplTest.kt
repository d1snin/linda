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

package dev.d1s.linda.service

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import dev.d1s.linda.configuration.properties.AvailabilityChecksConfigurationProperties
import dev.d1s.linda.constant.lp.AVAILABILITY_CHANGE_CREATED_GROUP
import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.event.data.AvailabilityChangeEventData
import dev.d1s.linda.exception.notFound.impl.AvailabilityChangeNotFoundException
import dev.d1s.linda.repository.AvailabilityChangeRepository
import dev.d1s.linda.service.impl.AvailabilityChangeServiceImpl
import dev.d1s.linda.testUtil.mockAvailabilityChange
import dev.d1s.linda.testUtil.mockAvailabilityChangeDto
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import java.net.URI
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [AvailabilityChangeServiceImpl::class])
internal class AvailabilityChangeServiceImplTest {

    @SpykBean
    private lateinit var availabilityChangeService: AvailabilityChangeServiceImpl

    @MockkBean
    private lateinit var availabilityChangeRepository: AvailabilityChangeRepository

    @MockkBean
    private lateinit var availabilityChangeDtoConverter: DtoConverter<AvailabilityChangeDto, AvailabilityChange>

    @MockkBean(relaxed = true)
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @MockkBean
    private lateinit var restTemplate: RestTemplate

    @MockkBean
    private lateinit var properties: AvailabilityChecksConfigurationProperties

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    private val requestFactory = mockk<ClientHttpRequestFactory>()

    private val availabilityChange = mockAvailabilityChange(false)

    private val availabilityChanges = setOf(availabilityChange)

    private val availabilityChangeDto = mockAvailabilityChangeDto()

    private val uri = URI.create(VALID_STUB)

    private val response = mockk<ClientHttpResponse> {
        every {
            rawStatusCode
        } returns HttpStatus.OK.value()

        justRun {
            close()
        }
    }

    private val shortLink = mockShortLink(true)

    private val shortLinks = setOf(shortLink)

    @BeforeEach
    fun setup() {
        every {
            restTemplate.requestFactory
        } returns requestFactory

        every {
            availabilityChangeRepository.findAll()
        } returns availabilityChanges.toList()

        every {
            availabilityChangeRepository.findLast(VALID_STUB)
        } returns Optional.empty()

        every {
            availabilityChangeRepository.findById(VALID_STUB)
        } returns Optional.of(availabilityChange)

        every {
            availabilityChangeRepository.findById(INVALID_STUB)
        } returns Optional.empty()

        every {
            availabilityChangeRepository.save(any())
        } returns availabilityChange

        every {
            availabilityChangeDtoConverter.convertToDto(availabilityChange)
        } returns availabilityChangeDto

        justRun {
            availabilityChangeRepository.deleteById(VALID_STUB)
        }

        every {
            requestFactory.createRequest(
                uri,
                HttpMethod.GET
            ).execute()
        } returns response

        every {
            shortLinkService.findAll()
        } returns shortLinks

        every {
            properties.badStatusCodeIntRanges
        } returns setOf(0..0)
    }

    @Test
    fun `should find all`() {
        expectThat(
            availabilityChangeService.findAll()
        ) isEqualTo availabilityChanges

        verify {
            availabilityChangeRepository.findAll()
        }
    }

    @Test
    fun `should find by id`() {
        expectThat(
            availabilityChangeService.findById(VALID_STUB)
        ) isEqualTo availabilityChange

        verify {
            availabilityChangeRepository.findById(VALID_STUB)
        }
    }

    @Test
    fun `should throw AvailabilityChangeNotFoundException`() {
        assertThrows<AvailabilityChangeNotFoundException> {
            availabilityChangeService.findById(INVALID_STUB)
        }

        verify {
            availabilityChangeRepository.findById(INVALID_STUB)
        }
    }

    @Test
    fun `should find last`() {
        every {
            availabilityChangeRepository.findLast(VALID_STUB)
        } returns Optional.of(availabilityChange)

        expectThat(
            availabilityChangeService.findLast(VALID_STUB)
        ) isEqualTo availabilityChange

        verify {
            availabilityChangeRepository.findLast(VALID_STUB)
        }
    }

    @Test
    fun `should return null when trying to find last`() {
        expectThat(
            availabilityChangeService.findLast(VALID_STUB)
        ).isNull()

        verify {
            availabilityChangeRepository.findLast(VALID_STUB)
        }
    }

    @Test
    fun `should create an availability change`() {
        expectThat(
            availabilityChangeService.create(availabilityChange)
        ) isEqualTo availabilityChange

        verifyAll {
            availabilityChangeRepository.save(availabilityChange)
            availabilityChangeDtoConverter.convertToDto(availabilityChange)
            publisher.publish(
                AVAILABILITY_CHANGE_CREATED_GROUP,
                VALID_STUB,
                AvailabilityChangeEventData(availabilityChangeDto)
            )
        }
    }

    @Test
    fun `should remove by id`() {
        assertDoesNotThrow {
            availabilityChangeService.removeById(VALID_STUB)
        }

        verify {
            availabilityChangeRepository.deleteById(VALID_STUB)
        }
    }

    @Test
    fun `should check the availability`() {
        expectThat(
            availabilityChangeService.checkAvailability(shortLink)
        ) isEqualTo AvailabilityChange(
            shortLink,
            null
        )
    }

    @Test
    fun `should check and save availability`() {
        expectThat(
            availabilityChangeService.checkAndSaveAvailability(shortLink)
        ) isEqualTo availabilityChange
    }

    @Test
    fun `should check an availability of all short links`() {
        expectThat(
            availabilityChangeService.checkAvailabilityOfAllShortLinks()
        ) isEqualTo setOf(availabilityChange)
    }
}
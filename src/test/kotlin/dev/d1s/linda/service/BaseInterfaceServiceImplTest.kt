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
import dev.d1s.linda.configuration.properties.BaseInterfaceConfigurationProperties
import dev.d1s.linda.configuration.properties.SslConfigurationProperties
import dev.d1s.linda.constant.mapping.BASE_INTERFACE_CONFIRMATION_SEGMENT
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.exception.notFound.impl.UtmParameterNotFoundException
import dev.d1s.linda.service.impl.BaseInterfaceServiceImpl
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.linda.testUtil.mockRedirect
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.linda.testUtil.mockUtmParameter
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import dev.d1s.teabag.web.configureSsl
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.servlet.view.RedirectView
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*
import java.util.concurrent.Executor

@SpringBootTest
@ContextConfiguration(classes = [BaseInterfaceServiceImpl::class])
internal class BaseInterfaceServiceImplTest {

    @Autowired
    private lateinit var baseInterfaceService: BaseInterfaceServiceImpl

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @MockkBean
    private lateinit var redirectService: RedirectService

    @MockkBean
    private lateinit var utmParameterService: UtmParameterService

    @MockkBean
    private lateinit var properties: BaseInterfaceConfigurationProperties

    @Suppress("unused")
    @MockkBean(relaxed = true)
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    @MockkBean
    private lateinit var taskExecutor: Executor

    private val shortLink = mockShortLink(true)

    private val utmParameter = mockUtmParameter()

    private val redirect = mockRedirect()

    private val builder = mockk<ServletUriComponentsBuilder>(relaxed = true)

    @BeforeEach
    fun setup() {
        every {
            shortLinkService.find(byAlias(VALID_STUB))
        } returns shortLink

        every {
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
        } returns Optional.of(utmParameter)

        every {
            properties.automaticUtmCreation
        } returns true

        every {
            properties.requireConfirmation
        } returns true

        every {
            utmParameterService.create(utmParameter)
        } returns utmParameter

        every {
            redirectService.create(redirect)
        } returns redirect

        val slot = slot<Runnable>()

        every {
            taskExecutor.execute(capture(slot))
        } answers {
            slot.captured.run()
        }

        every {
            builder.toUriString()
        } returns INVALID_STUB
    }

    @Test
    fun `should redirect to the confirmation url`() {
        mockkStatic("org.springframework.web.servlet.support.ServletUriComponentsBuilder") {
            every {
                ServletUriComponentsBuilder.fromCurrentRequest()
            } returns builder

            mockkStatic("dev.d1s.teabag.web.ServletUriComponentsBuilderKt") {
                justRun {
                    builder.configureSsl(false)
                }

                expectThat(
                    this.createRedirectView(false)
                ) isEqualTo RedirectView(INVALID_STUB).url

                verifyAll {
                    builder.configureSsl(false)
                    builder.path(BASE_INTERFACE_CONFIRMATION_SEGMENT)
                    builder.replaceQueryParams(any())
                    builder.toUriString()
                }
            }
        }
    }

    @Test
    fun `should create RedirectView`() {
        expectThat(
            this.createRedirectView()
        ) isEqualTo RedirectView(VALID_STUB).url

        verifyAll {
            shortLinkService.find(byAlias(VALID_STUB))
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
            redirectService.create(any())
        }
    }

    @Test
    fun `should create RedirectView and utm parameters`() {
        every {
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
        } returns Optional.empty()

        expectThat(
            this.createRedirectView()
        ) isEqualTo RedirectView(VALID_STUB).url

        verifyAll {
            shortLinkService.find(byAlias(VALID_STUB))
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
            utmParameterService.create(utmParameter)
            redirectService.create(any())
        }
    }

    @Test
    fun `should throw UtmParameterNotFoundException`() {
        every {
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
        } returns Optional.empty()

        every {
            properties.automaticUtmCreation
        } returns false

        assertThrows<UtmParameterNotFoundException> {
            this.createRedirectView()
        }

        verifyAll {
            shortLinkService.find(byAlias(VALID_STUB))
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
        }
    }

    private fun createRedirectView(confirmed: Boolean = true) =
        baseInterfaceService.createRedirectView(
            VALID_STUB,
            null,
            null,
            VALID_STUB,
            null,
            null,
            confirmed
        ).url
}
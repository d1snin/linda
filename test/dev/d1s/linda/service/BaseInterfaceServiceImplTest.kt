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
import dev.d1s.linda.constant.utmParameter.UTM_CONTENT
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.service.impl.BaseInterfaceServiceImpl
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.linda.testUtil.*
import dev.d1s.teabag.testing.constant.VALID_STUB
import dev.d1s.teabag.web.configureSsl
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkStatic
import io.mockk.verifyAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [BaseInterfaceServiceImpl::class])
class BaseInterfaceServiceImplTest {

    @Autowired
    private lateinit var baseInterfaceServiceImpl: BaseInterfaceServiceImpl

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
    private lateinit var metaTagsBridgingService: MetaTagsBridgingService

    private val utmParameterDomains = utmParameters

    private val expectedRedirect = Redirect(shortLink).apply {
        this.utmParameters = utmParameterDomains.toMutableSet()
    }

    @BeforeEach
    fun setup() {
        shortLinkService.prepare()
        redirectService.prepare()
        utmParameterService.prepare()
        properties.prepare()
        metaTagsBridgingService.prepare()

        every {
            redirectService.create(expectedRedirect)
        } returns (redirect to redirectDto)
    }

    @Test
    fun `should create redirect view pointing to the redirect confirmation endpoint`() {
        this.withStaticMocks {
            expectThat(
                this.createRedirectView(false)
            ) isEqualTo VALID_STUB

            verifyAll {
                shortLinkService.find(byAlias(VALID_STUB))
                metaTagsBridgingService.buildHtmlDocument(shortLink)
                servletUriComponentsBuilderMock.configureSsl(false)
                servletUriComponentsBuilderMock.path(BASE_INTERFACE_CONFIRMATION_SEGMENT)
                servletUriComponentsBuilderMock.replaceQueryParams(
                    LinkedMultiValueMap<String, String>().apply {
                        add(UTM_CONTENT, VALID_STUB)
                    }
                )
                servletUriComponentsBuilderMock.build(false).toUriString()
            }
        }
    }

    @Test
    fun `should create redirect view pointing to the origin url`() {
        expectThat(
            this.createRedirectView(true)
        ) isEqualTo TEST_URL

        verifyAll {
            shortLinkService.find(byAlias(VALID_STUB))
            metaTagsBridgingService.buildHtmlDocument(shortLink)
            utmParameterService.findByTypeAndValueOrThrow(
                testUtmParameterType,
                VALID_STUB
            )
            redirectService.create(expectedRedirect)
        }
    }

    private inline fun withStaticMocks(
        block: () -> Unit
    ) {
        mockkStatic("org.springframework.web.servlet.support.ServletUriComponentsBuilder") {
            every {
                ServletUriComponentsBuilder.fromCurrentRequest()
            } returns servletUriComponentsBuilderMock

            mockkStatic("dev.d1s.teabag.web.ServletUriComponentsBuilderKt") {
                justRun {
                    servletUriComponentsBuilderMock.configureSsl(false)
                }

                block()
            }
        }
    }

    private fun createRedirectView(confirmed: Boolean) =
        baseInterfaceServiceImpl.createRedirectPage(
            VALID_STUB,
            null,
            null,
            null,
            null,
            VALID_STUB,
            confirmed
        ).headers.location.toString()
}
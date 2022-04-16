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
import dev.d1s.linda.configuration.properties.BaseInterfaceConfigurationProperties
import dev.d1s.linda.constant.utm.UTM_CAMPAIGN
import dev.d1s.linda.controller.impl.BaseInterfaceControllerImpl
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.exception.impl.notFound.UtmParameterNotFoundException
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.linda.testUtil.mockRedirect
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.linda.testUtil.mockUtmParameter
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.verifyAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.util.NestedServletException
import strikt.api.expectThat
import strikt.assertions.isA
import java.util.*

@ContextConfiguration(classes = [BaseInterfaceControllerImpl::class])
@WebMvcTest(
    controllers = [BaseInterfaceControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
class BaseInterfaceControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @MockkBean
    private lateinit var redirectService: RedirectService

    @MockkBean
    private lateinit var utmParameterService: UtmParameterService

    @MockkBean
    private lateinit var properties: BaseInterfaceConfigurationProperties

    private val shortLink = mockShortLink(true)

    private val utmParameter = mockUtmParameter()

    private val redirect = mockRedirect()

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
            utmParameterService.create(utmParameter)
        } returns utmParameter

        every {
            redirectService.create(redirect)
        } returns redirect

        every {
            redirectService.assignUtmParameterAndSave(redirect, utmParameter)
        } returns redirect
    }

    @Test
    fun `should perform redirect`() {
        this.performRedirect()

        verifyAll {
            shortLinkService.find(byAlias(VALID_STUB))
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
            redirectService.create(redirect)
            redirectService.assignUtmParameterAndSave(redirect, utmParameter)
        }
    }

    @Test
    fun `should perform redirect and create utm parameter`() {
        every {
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
        } returns Optional.empty()

        this.performRedirect()

        verifyAll {
            shortLinkService.find(byAlias(VALID_STUB))
            utmParameterService.findByTypeAndValue(UtmParameterType.CAMPAIGN, VALID_STUB)
            utmParameterService.create(utmParameter)
            redirectService.create(redirect)
            redirectService.assignUtmParameterAndSave(redirect, utmParameter)
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

        val exception = assertThrows<NestedServletException> {
            this.performRedirect()
        }

        expectThat(exception.rootCause).isA<UtmParameterNotFoundException>()
    }

    private fun performRedirect() {
        mockMvc.get("/$VALID_STUB") {
            param(UTM_CAMPAIGN, VALID_STUB)
        }.andExpect {
            status {
                isFound()
            }

            redirectedUrl(VALID_STUB)
        }
    }
}
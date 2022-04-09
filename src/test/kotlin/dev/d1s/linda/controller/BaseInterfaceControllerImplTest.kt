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
import dev.d1s.linda.controller.impl.BaseInterfaceControllerImpl
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.linda.testUtil.mockShortLink
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.verifyAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

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

    @MockkBean(relaxed = true)
    private lateinit var redirectService: RedirectService

    private val shortLink = mockShortLink()

    @BeforeEach
    fun setup() {
        every {
            shortLinkService.find(byAlias(VALID_STUB))
        } returns shortLink
    }

    @Test
    fun `should perform redirect`() {
        mockMvc.get("/$VALID_STUB").andExpect {
            status {
                isFound()
            }

            redirectedUrl(VALID_STUB)

            verifyAll {
                shortLinkService.find(byAlias(VALID_STUB))
                redirectService.create(shortLink)
            }
        }
    }
}
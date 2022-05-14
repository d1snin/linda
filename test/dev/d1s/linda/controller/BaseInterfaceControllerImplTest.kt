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
import dev.d1s.linda.constant.mapping.BASE_INTERFACE_MAPPING
import dev.d1s.linda.constant.utmParameter.UTM_CONTENT
import dev.d1s.linda.controller.impl.BaseInterfaceControllerImpl
import dev.d1s.linda.service.BaseInterfaceService
import dev.d1s.linda.testUtil.TEST_URL
import dev.d1s.linda.testUtil.baseInterfaceConfirmationMappingWithAlias
import dev.d1s.linda.testUtil.prepare
import dev.d1s.linda.testUtil.setAlias
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ContextConfiguration(
    classes = [BaseInterfaceControllerImpl::class]
)
@WebMvcTest(
    controllers = [BaseInterfaceControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]

)
class BaseInterfaceControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var baseInterfaceService: BaseInterfaceService

    @BeforeEach
    fun setup() {
        baseInterfaceService.prepare()
    }

    @Test
    fun `should redirect to the redirect confirmation endpoint`() {
        this.sendRequestAndVerifyLocation(false)
    }

    @Test
    fun `should redirect to the origin url`() {
        this.sendRequestAndVerifyLocation(true)
    }

    private fun sendRequestAndVerifyLocation(
        confirmed: Boolean
    ) {
        mockMvc.get(
            if (confirmed) {
                baseInterfaceConfirmationMappingWithAlias
            } else {
                BASE_INTERFACE_MAPPING.setAlias()
            }
        ) {
            param(UTM_CONTENT, VALID_STUB)

        }.andExpect {
            status {
                isFound()
            }

            redirectedUrl(
                if (confirmed) {
                    TEST_URL
                } else {
                    baseInterfaceConfirmationMappingWithAlias
                }
            )
        }

        verify {
            baseInterfaceService.createRedirectPage(
                VALID_STUB,
                null,
                null,
                null,
                null,
                VALID_STUB,
                confirmed
            )
        }
    }
}
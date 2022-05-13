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

package dev.d1s.linda.generator

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.constant.parameter.CUSTOM_ALIAS_PARAMETER
import dev.d1s.linda.exception.alreadyExists.impl.AliasAlreadyExistsException
import dev.d1s.linda.exception.customAlias.impl.CustomAliasNotDefinedException
import dev.d1s.linda.exception.customAlias.impl.EmptyCustomAliasException
import dev.d1s.linda.generator.impl.CustomAliasGenerator
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.testUtil.prepare
import dev.d1s.linda.testUtil.shortLinkCreationDto
import dev.d1s.teabag.testing.constant.VALID_STUB
import dev.d1s.teabag.testing.spring.web.http.mockRequest
import dev.d1s.teabag.web.currentRequest
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [CustomAliasGenerator::class])
class CustomAliasGeneratorTest {

    @Autowired
    private lateinit var generator: CustomAliasGenerator

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @BeforeEach
    fun setup() {
        shortLinkService.prepare()
    }

    @Test
    fun `should generate custom alias`() {
        this.withMockedCurrentRequest {
            expectThat(
                generator.generateAlias(shortLinkCreationDto)
            ) isEqualTo VALID_STUB

            verify {
                shortLinkService.doesAliasExist(VALID_STUB)
            }
        }
    }

    @Test
    fun `should throw CustomAliasNotDefinedException`() {
        this.withMockedCurrentRequest {
            it.removeParameter(CUSTOM_ALIAS_PARAMETER)

            assertThrows<CustomAliasNotDefinedException> {
                generator.generateAlias(shortLinkCreationDto)
            }
        }
    }

    @Test
    fun `should throw EmptyCustomAliasException`() {
        this.withMockedCurrentRequest {
            it.setParameter(CUSTOM_ALIAS_PARAMETER, "")

            assertThrows<EmptyCustomAliasException> {
                generator.generateAlias(shortLinkCreationDto)
            }
        }
    }

    @Test
    fun `should throw AliasAlreadyExistsException`() {
        this.withMockedCurrentRequest {
            every {
                shortLinkService.doesAliasExist(VALID_STUB)
            } returns true

            assertThrows<AliasAlreadyExistsException> {
                generator.generateAlias(shortLinkCreationDto)
            }

            verify {
                shortLinkService.doesAliasExist(VALID_STUB)
            }
        }
    }

    private inline fun withMockedCurrentRequest(block: (MockHttpServletRequest) -> Unit) {
        mockkStatic("dev.d1s.teabag.web.CurrentRequestKt") {
            val request = mockRequest

            request.addParameter(CUSTOM_ALIAS_PARAMETER, VALID_STUB)

            every {
                currentRequest
            } returns request

            block(request)
        }
    }
}
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
import dev.d1s.linda.constant.mapping.api.*
import dev.d1s.linda.controller.impl.AvailabilityChangeControllerImpl
import dev.d1s.linda.service.AvailabilityChangeService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.testUtil.*
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@ContextConfiguration(
    classes = [AvailabilityChangeControllerImpl::class,
        JacksonAutoConfiguration::class,
        ObjectMapperHolder::class
    ]
)
@WebMvcTest(
    controllers = [AvailabilityChangeControllerImpl::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
class AvailabilityChangeControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var availabilityChangeService: AvailabilityChangeService

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @BeforeEach
    fun setup() {
        availabilityChangeService.prepare()
        shortLinkService.prepare()
    }

    @Test
    fun `should find all availability changes`() {
        mockMvc.get(AVAILABILITY_CHANGES_FIND_ALL_MAPPING).andExpect {
            ok()

            jsonObject(
                availabilityChangeDtoSet
            )
        }

        verify {
            availabilityChangeService.findAll(true)
        }
    }

    @Test
    fun `should find availability change by id`() {
        mockMvc.get(
            AVAILABILITY_CHANGES_FIND_BY_ID_MAPPING.setId()
        ).andExpect {
            ok()

            jsonObject(
                availabilityChangeDto
            )
        }

        verify {
            availabilityChangeService.findById(VALID_STUB, true)
        }
    }

    @Test
    fun `should trigger availability checks for all short links`() {
        mockMvc.post(
            AVAILABILITY_CHANGES_TRIGGER_CHECKS
        ).andExpect {
            ok()

            jsonObject(
                availabilityChangeDtoSet
            )
        }

        verify {
            availabilityChangeService.checkAvailabilityOfAllShortLinks()
        }
    }

    @Test
    fun `should trigger availability check for exact short link`() {
        mockMvc.post(
            AVAILABILITY_CHANGES_TRIGGER_CHECK_FOR_SHORT_LINK.setId()

        ).andExpect {
            ok()

            jsonObject(
                unsavedAvailabilityChangeDto
            )
        }

        verify {
            availabilityChangeService.checkAvailability(VALID_STUB)
        }
    }

    @Test
    fun `should remove availability change by id`() {
        mockMvc.delete(
            AVAILABILITY_CHANGES_REMOVE_BY_ID_MAPPING.setId()
        ).andExpect {
            status {
                isNoContent()
            }
        }

        verify {
            availabilityChangeService.removeById(VALID_STUB)
        }
    }
}
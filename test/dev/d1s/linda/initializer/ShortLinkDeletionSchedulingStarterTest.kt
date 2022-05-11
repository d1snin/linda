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

package dev.d1s.linda.initializer

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.testUtil.prepare
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [ShortLinkDeletionSchedulingStarter::class])
class ShortLinkDeletionSchedulingStarterTest {

    @Autowired
    private lateinit var shortLinkDeletionSchedulingStarter: ShortLinkDeletionSchedulingStarter

    @MockkBean
    private lateinit var shortLinkService: ShortLinkService

    @BeforeEach
    fun setup() {
        shortLinkService.prepare()
    }

    @Test
    fun `should schedule all ephemeral short links for deletion`() {
        assertDoesNotThrow {
            // this method is most likely being called by Spring, but I don't really care, it doesn't break anything.
            shortLinkDeletionSchedulingStarter.afterPropertiesSet()
        }

        verify {
            shortLinkService.scheduleAllEphemeralShortLinksForDeletion()
        }
    }
}
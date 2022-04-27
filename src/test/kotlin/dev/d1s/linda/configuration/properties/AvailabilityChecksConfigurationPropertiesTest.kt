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

package dev.d1s.linda.configuration.properties

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isTrue

@SpringBootTest
@ContextConfiguration(classes = [AvailabilityChecksConfigurationProperties::class])
@TestPropertySource(properties = ["linda.availability-checks.bad-status-code-ranges=-1..-1,400..526"])
internal class AvailabilityChecksConfigurationPropertiesTest {

    @Autowired
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") // I have no idea why.
    private lateinit var properties: AvailabilityChecksConfigurationProperties

    @Test
    fun `should return valid default values`() {
        val nonBeanProperties = AvailabilityChecksConfigurationProperties()

        expectThat(
            nonBeanProperties.eagerAvailabilityCheck
        ).isTrue()
    }

    @Test
    fun `should return valid bad status code int ranges`() {
        expectThat(
            properties.badStatusCodeIntRanges
        ).containsExactly(
            -1..-1, 400..526
        )
    }
}
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

package dev.d1s.linda.constant.mapping.api

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class AvailabilityChangeMappingConstantsTest {

    @Test
    fun `should return valid availability change related constants`() {
        expectThat(AVAILABILITY_CHANGES_BASE_MAPPING) isEqualTo
                "/api/availabilityChanges"

        expectThat(AVAILABILITY_CHANGES_FIND_ALL_MAPPING) isEqualTo
                "/api/availabilityChanges"

        expectThat(AVAILABILITY_CHANGES_FIND_BY_ID_MAPPING) isEqualTo
                "/api/availabilityChanges/{identifier}"

        expectThat(AVAILABILITY_CHANGES_TRIGGER_CHECKS) isEqualTo
                "/api/availabilityChanges"

        expectThat(AVAILABILITY_CHANGES_TRIGGER_CHECK_FOR_SHORT_LINK) isEqualTo
                "/api/availabilityChanges/shortLinks/{identifier}"

        expectThat(AVAILABILITY_CHANGES_REMOVE_BY_ID_MAPPING) isEqualTo
                "/api/availabilityChanges/{identifier}"
    }
}
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

package dev.d1s.linda.constant.property

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class DefaultPropertyValueConstantsTest {

    @Test
    fun `should return valid default property values`() {
        expectThat(DEFAULT_BAD_STATUS_CODE_RANGE) isEqualTo
                "-1..-1,400..526"

        expectThat(DEFAULT_EAGER_AVAILABILITY_CHECK).isTrue()

        expectThat(DEFAULT_BASE_INTERFACE_ENABLED).isTrue()

        expectThat(DEFAULT_REQUIRE_CONFIRMATION).isTrue()

        expectThat(DEFAULT_FALLBACK_TO_HTTPS).isFalse()
    }
}
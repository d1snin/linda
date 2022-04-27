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

internal class ShortLinkMappingConstantsTest {

    @Test
    fun `should return valid mapping`() {
        expectThat(SHORT_LINKS_BASE_MAPPING) isEqualTo "/api/shortLinks"
        expectThat(SHORT_LINKS_FIND_ALL_MAPPING) isEqualTo "/api/shortLinks"
        expectThat(SHORT_LINKS_FIND_MAPPING) isEqualTo "/api/shortLinks/{identifier}"
        expectThat(SHORT_LINKS_CREATE_MAPPING) isEqualTo "/api/shortLinks"
        expectThat(SHORT_LINKS_REMOVE_MAPPING) isEqualTo "/api/shortLinks/{identifier}"
    }
}
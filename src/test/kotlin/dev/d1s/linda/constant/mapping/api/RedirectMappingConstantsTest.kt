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

internal class RedirectMappingConstantsTest {

    @Test
    fun `should return valid mapping`() {
        expectThat(REDIRECTS_BASE_MAPPING) isEqualTo "/api/redirects"
        expectThat(REDIRECTS_FIND_ALL_MAPPING) isEqualTo "/api/redirects"
        expectThat(REDIRECTS_FIND_BY_ID_MAPPING) isEqualTo "/api/redirects/{identifier}"
        expectThat(REDIRECTS_FIND_ALL_BY_SHORT_LINK_MAPPING) isEqualTo "/api/redirects/shortLinks/{identifier}"
        expectThat(REDIRECTS_REMOVE_BY_ID_MAPPING) isEqualTo "/api/redirects/{identifier}"
        expectThat(REDIRECTS_REMOVE_ALL_MAPPING) isEqualTo "/api/redirects"
        expectThat(REDIRECTS_BULK_REMOVE_MAPPING) isEqualTo "/api/redirects/part"
        expectThat(REDIRECTS_REMOVE_ALL_BY_SHORT_LINK_MAPPING) isEqualTo "/api/redirects/shortLinks/{identifier}"
    }
}
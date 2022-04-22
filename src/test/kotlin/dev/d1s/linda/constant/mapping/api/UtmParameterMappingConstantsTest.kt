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

internal class UtmParameterMappingConstantsTest {

    @Test
    fun `should return valid mapping`() {
        expectThat(UTM_PARAMETERS_BASE_MAPPING) isEqualTo "/api/utmParameters"
        expectThat(UTM_PARAMETERS_FIND_ALL_MAPPING) isEqualTo "/api/utmParameters"
        expectThat(UTM_PARAMETERS_FIND_BY_ID_MAPPING) isEqualTo "/api/utmParameters/{identifier}"
        expectThat(UTM_PARAMETERS_FIND_BY_TYPE_AND_VALUE_MAPPING) isEqualTo "/api/utmParameters/{type}/{value}"
        expectThat(UTM_PARAMETERS_CREATE_MAPPING) isEqualTo "/api/utmParameters"
        expectThat(UTM_PARAMETERS_REMOVE_BY_ID_MAPPING) isEqualTo "/api/utmParameters/{identifier}"
    }
}
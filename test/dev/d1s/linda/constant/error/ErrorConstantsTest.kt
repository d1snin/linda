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

package dev.d1s.linda.constant.error

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ErrorConstantsTest {

    @Test
    fun `should return valid values`() {
        expectThat(ERROR_ALIAS_ALREADY_EXISTS) isEqualTo
                "The provided alias already exists."

        expectThat(ERROR_UTM_PARAMETER_ALREADY_EXISTS) isEqualTo
                "UTM parameter already exists."

        expectThat(CUSTOM_ALIAS_NOT_DEFINED_ERROR) isEqualTo
                "Custom alias is not defined. Specify it using the 'customAlias' request parameter."

        expectThat(EMPTY_CUSTOM_ALIAS_ERROR) isEqualTo
                "Custom alias must not be empty."

        expectThat(DEFAULT_UTM_PARAMETER_OVERRIDE_ERROR) isEqualTo
                "It's not allowed to override the default UTM parameters (%s)"

        expectThat(DEFAULT_UTM_PARAMETERS_NOT_ALLOWED_ERROR) isEqualTo
                "Default UTM parameters (%s) are not allowed since the allowUtmParameters property is set to true."

        expectThat(ILLEGAL_UTM_PARAMETERS_ERROR) isEqualTo
                "Provided UTM parameters (%S) are not allowed for this short link."

        expectThat(UTM_PARAMETERS_NOT_ALLOWED_ERROR) isEqualTo
                "UTM parameters are not allowed for this short link."

        expectThat(ALIAS_GENERATOR_NOT_FOUND_ERROR) isEqualTo
                "Requested alias generator (%s) was not found."

        expectThat(AVAILABILITY_CHANGE_NOT_FOUND_ERROR) isEqualTo
                "Requested availability change (%s) was not found."

        expectThat(REDIRECT_NOT_FOUND_ERROR) isEqualTo
                "Requested redirect (%s) was not found."

        expectThat(SHORT_LINK_NOT_FOUND_ERROR) isEqualTo
                "Requested short link (%s) was not found."

        expectThat(UTM_PARAMETER_NOT_FOUND_ERROR) isEqualTo
                "Requested UTM parameter (%s) was not found."

        expectThat(AVAILABILITY_CHECK_IN_PROGRESS_ERROR) isEqualTo
                "Availability check is already in progress."
    }
}
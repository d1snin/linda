/*
 * Copyright 2022 Linda project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.linda.dto.shortLink

import java.time.Duration
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class ShortLinkCreationDto(
    // see https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
    @field:Pattern(
        regexp = "^https?://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]",
        message = "The provided URL must be valid."
    )
    val url: String,

    @field:NotBlank(message = "aliasGeneratorId must not be blank.")
    val aliasGeneratorId: String,

    @field:NotNull(message = "allowUtmParameters field must not be null.")
    val allowUtmParameters: Boolean,

    val deleteAfter: Duration?,

    @field:NotNull(message = "defaultUtmParameters field must not be null")
    val defaultUtmParameters: Set<String>,

    @field:NotNull(message = "allowedUtmParameters field must not be null")
    val allowedUtmParameters: Set<String>
)
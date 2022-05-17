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

package dev.d1s.linda.dto.shortLink

import java.time.Duration
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class ShortLinkUpdateDto(

    @field:Pattern(
        regexp = "^https?://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]",
        message = "The provided target must be valid URL."
    )
    val target: String,

    @field:NotBlank(message = "aliasGeneratorId must not be blank.")
    val alias: String,

    @field:NotNull(message = "allowUtmParameters field must not be null.")
    val allowUtmParameters: Boolean,

    val deleteAfter: Duration?,

    @field:NotNull(message = "defaultUtmParameters field must not be null")
    val defaultUtmParameters: Set<String>,

    @field:NotNull(message = "allowedUtmParameters field must not be null")
    val allowedUtmParameters: Set<String>,

    @field:NotNull(message = "redirects field must not be null.")
    val redirects: Set<String>,

    @field:NotNull(message = "availabilityChanges field must not be null.")
    val availabilityChanges: Set<String>
)
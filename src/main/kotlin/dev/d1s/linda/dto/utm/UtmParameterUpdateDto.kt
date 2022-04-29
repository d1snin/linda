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

package dev.d1s.linda.dto.utm

import dev.d1s.linda.domain.utm.UtmParameterType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UtmParameterUpdateDto(
    @field:NotNull(message = "type must not be null.")
    val type: UtmParameterType,

    @field:NotBlank(message = "parameterValue must not be blank.")
    val parameterValue: String,

    @field:NotNull(message = "allowOverride field must not be null.")
    val allowOverride: Boolean,

    @field:NotNull(message = "defaultForShortLinks field must not be null.")
    val defaultForShortLinks: Set<String>,

    @field:NotNull(message = "allowedForShortLinks field must not be null.")
    val allowedForShortLinks: Set<String>,

    @field:NotNull(message = "redirects field must not be null.")
    val redirects: Set<String>
)
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

import dev.d1s.linda.constant.regex.HTTP_URL_REGEX
import dev.d1s.linda.entity.alias.AliasType
import java.time.Duration
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class ShortLinkCreationDto(

    override val alias: String?,

    @field:Pattern(regexp = HTTP_URL_REGEX)
    override val target: String,

    @field:NotNull
    override val aliasType: AliasType,

    val aliasGeneratorId: String?,

    @field:NotNull
    override val allowUtmParameters: Boolean,

    @field:NotNull
    override val allowRedirects: Boolean,

    override val maxRedirects: Int?,

    override val disableAfter: Duration?,

    @field:NotNull
    override val defaultUtmParameters: Set<String>,

    @field:NotNull
    override val allowedUtmParameters: Set<String>

) : CommonShortLinkDto
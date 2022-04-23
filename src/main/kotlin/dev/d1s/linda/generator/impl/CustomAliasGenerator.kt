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

package dev.d1s.linda.generator.impl

import dev.d1s.linda.constant.parameter.CUSTOM_ALIAS_PARAMETER
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.exception.customAlias.CustomAliasNotDefinedException
import dev.d1s.linda.exception.customAlias.EmptyCustomAliasException
import dev.d1s.linda.exception.impl.alreadyExists.AliasAlreadyExistsException
import dev.d1s.linda.generator.AliasGenerator
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.teabag.web.currentRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CustomAliasGenerator : AliasGenerator {

    override val identifier = "custom"

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    override fun generateAlias(creation: ShortLinkCreationDto): String {
        val customAlias = currentRequest.getParameter(CUSTOM_ALIAS_PARAMETER)
            ?: throw CustomAliasNotDefinedException

        if (customAlias.isEmpty()) {
            throw EmptyCustomAliasException
        }

        if (shortLinkService.doesAliasExist(customAlias)) {
            throw AliasAlreadyExistsException
        }

        return customAlias
    }
}
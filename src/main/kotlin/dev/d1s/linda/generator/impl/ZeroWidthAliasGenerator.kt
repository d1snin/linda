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

import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.generator.AliasGenerator
import dev.d1s.linda.service.ShortLinkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ZeroWidthAliasGenerator : AliasGenerator {

    override val identifier = "zero-width"

    @set:Autowired
    lateinit var shortLinkService: ShortLinkService

    override fun generateAlias(creation: ShortLinkCreationDto): String {
        var aliasCandidate = ZERO_WIDTH_SPACE

        while (shortLinkService.doesAliasExist(aliasCandidate)) {
            aliasCandidate = "${zeroWidthCharacters.random()}$aliasCandidate"
        }

        return aliasCandidate
    }

    companion object {
        const val ZERO_WIDTH_SPACE = "\u200b" // each alias must end on this character.
        private const val ZERO_WIDTH_NON_JOINER = "\u200c"
        private const val ZERO_WIDTH_JOINER = "\u200d"
        private const val ZERO_WIDTH_WORD_JOINER = "\u2060"

        val zeroWidthCharacters = setOf(
            ZERO_WIDTH_SPACE,
            ZERO_WIDTH_NON_JOINER,
            ZERO_WIDTH_JOINER,
            ZERO_WIDTH_WORD_JOINER
        )
    }
}
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

package dev.d1s.linda.generator.impl

import com.fasterxml.jackson.databind.ObjectMapper
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.entity.alias.FriendlyAliases
import dev.d1s.linda.generator.AliasGenerator
import dev.d1s.linda.service.ShortLinkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class FriendlyAliasGenerator : AliasGenerator {

    override val identifier = "friendly"

    @set:Autowired
    lateinit var resourceLoader: ResourceLoader

    @set:Autowired
    lateinit var objectMapper: ObjectMapper

    @set:Autowired
    lateinit var shortLinkService: ShortLinkService

    private lateinit var aliases: FriendlyAliases

    override fun generateAlias(creation: ShortLinkCreationDto): String {
        var adjectiveCandidate = aliases.adjectives.random()

        while (true) {
            val builtAlias = adjectiveCandidate.appendAnimal()

            if (shortLinkService.doesAliasExist(builtAlias)) {
                adjectiveCandidate = adjectiveCandidate.appendAdjective()
            } else {
                return builtAlias
            }
        }
    }

    @PostConstruct
    private fun initAliases() {
        aliases = objectMapper.readValue(
            resourceLoader.getResource(WORDLIST_LOCATION).file,
            FriendlyAliases::class.java
        )
    }

    private fun String.appendAnimal() = "$this$DELIMITER${aliases.animals.random()}"

    private fun String.appendAdjective() = "$this$DELIMITER${
        aliases.adjectives.firstOrNull { adjective ->
            !this.split(DELIMITER).contains(adjective)
        } ?: aliases.adjectives.random()
    }"

    private companion object {
        private const val DELIMITER = "-"
        private const val WORDLIST_LOCATION = "classpath:wordlist.json"
    }
}
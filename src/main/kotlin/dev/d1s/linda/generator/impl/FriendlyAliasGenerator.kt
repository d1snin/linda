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
import dev.d1s.linda.domain.alias.FriendlyAliases
import dev.d1s.linda.generator.AliasGenerator
import dev.d1s.linda.service.ShortLinkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class FriendlyAliasGenerator : AliasGenerator {

    override val identity = "friendly"

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    private lateinit var aliases: FriendlyAliases

    override fun generateAlias(): String {
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
            resourceLoader.getResource("classpath:wordlist.json").file,
            FriendlyAliases::class.java
        )
    }

    private fun String.appendAnimal() = "$this-${aliases.animals.random()}"

    private fun String.appendAdjective() = "$this-${
        aliases.adjectives.first { adjective ->
            this.split("-").any {
                it == adjective
            }
        }
    }"
}
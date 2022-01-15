package uno.d1s.linda.generator.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import uno.d1s.linda.domain.alias.FriendlyAliases
import uno.d1s.linda.generator.AliasGenerator
import uno.d1s.linda.service.ShortLinkService
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

            if (shortLinkService.isAliasExist(builtAlias)) {
                adjectiveCandidate = adjectiveCandidate.appendAdjective()
            } else {
                return builtAlias
            }
        }
    }

    @PostConstruct
    private fun initAliases() {
        aliases = objectMapper.readValue(
            resourceLoader.getResource("classpath:wordlist.json").file.readText(),
            FriendlyAliases::class.java
        )
    }

    private fun String.appendAnimal() = "$this-${aliases.animals.random()}"

    private fun String.appendAdjective() = "$this-${
        aliases.adjectives.filter { adjective ->
            this.split("-").any {
                it == adjective
            }
        }
    }"
}
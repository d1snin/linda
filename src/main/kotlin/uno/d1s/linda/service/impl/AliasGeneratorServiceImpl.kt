package uno.d1s.linda.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uno.d1s.linda.exception.impl.AliasGeneratorNotFoundException
import uno.d1s.linda.generator.AliasGenerator
import uno.d1s.linda.service.AliasGeneratorService
import uno.d1s.linda.service.ShortLinkService

@Service
class AliasGeneratorServiceImpl : AliasGeneratorService {

    @Autowired
    private lateinit var aliasGenerators: List<AliasGenerator>

    override fun getAliasGenerator(identifier: String): AliasGenerator = aliasGenerators.firstOrNull {
        it.identity == identifier
    } ?: throw AliasGeneratorNotFoundException
}
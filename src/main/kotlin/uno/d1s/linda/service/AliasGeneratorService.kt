package uno.d1s.linda.service

import uno.d1s.linda.generator.AliasGenerator

interface AliasGeneratorService {

    fun getAliasGenerator(identifier: String): AliasGenerator
}
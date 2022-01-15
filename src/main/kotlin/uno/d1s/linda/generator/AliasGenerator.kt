package uno.d1s.linda.generator

import java.util.function.Supplier

interface AliasGenerator {

    val identity: String

    fun generateAlias(): String
}
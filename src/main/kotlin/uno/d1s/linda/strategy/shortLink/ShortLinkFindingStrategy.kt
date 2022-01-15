package uno.d1s.linda.strategy.shortLink

import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType.*
import uno.d1s.linda.util.thisOrDefaultType

sealed class ShortLinkFindingStrategy {

    class ById(val id: String) : ShortLinkFindingStrategy()
    class ByAlias(val alias: String) : ShortLinkFindingStrategy()
    class ByUrl(val url: String) : ShortLinkFindingStrategy()
}

enum class ShortLinkFindingStrategyType {
    BY_ID, BY_ALIAS, BY_URL
}

fun byId(id: String) = ShortLinkFindingStrategy.ById(id)
fun byAlias(alias: String) = ShortLinkFindingStrategy.ByAlias(alias)
fun byUrl(url: String) = ShortLinkFindingStrategy.ByUrl(url)
fun byType(type: ShortLinkFindingStrategyType?, identifier: String) = when (type.thisOrDefaultType) {
    BY_ID -> ShortLinkFindingStrategy.ById(identifier)
    BY_ALIAS -> ShortLinkFindingStrategy.ByAlias(identifier)
    BY_URL -> ShortLinkFindingStrategy.ByUrl(identifier)
}

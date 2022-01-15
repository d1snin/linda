package uno.d1s.linda.util

import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategyType

val ShortLinkFindingStrategyType?.thisOrDefaultType
    get() =
        this ?: ShortLinkFindingStrategyType.BY_ID
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

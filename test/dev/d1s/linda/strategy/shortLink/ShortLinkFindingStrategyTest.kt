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

package dev.d1s.linda.strategy.shortLink

import dev.d1s.teabag.testing.constant.VALID_STUB
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ShortLinkFindingStrategyTest {

    @Test
    fun `should create ById finding strategy`() {
        expectThat(
            byId(VALID_STUB)
        ) isEqualTo ShortLinkFindingStrategy.ById(VALID_STUB)
    }

    @Test
    fun `should create ByAlias finding strategy`() {
        expectThat(
            byAlias(VALID_STUB)
        ) isEqualTo ShortLinkFindingStrategy.ByAlias(VALID_STUB)
    }

    @Test
    fun `should determine the strategy based on the given strategy type`() {
        expectThat(
            byType(ShortLinkFindingStrategyType.BY_ID, VALID_STUB)
        ) isEqualTo ShortLinkFindingStrategy.ById(VALID_STUB)

        expectThat(
            byType(ShortLinkFindingStrategyType.BY_ALIAS, VALID_STUB)
        ) isEqualTo ShortLinkFindingStrategy.ByAlias(VALID_STUB)
    }
}
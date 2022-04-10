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

package dev.d1s.linda.testUtil

import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.generator.AliasGenerator
import dev.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy
import dev.d1s.teabag.data.toPage
import dev.d1s.teabag.dto.DtoSetConverterFacade
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.springframework.data.domain.Page
import java.time.Instant

// used to prevent stackoverflow
private val configuredMockRedirect = mockRedirect(true)

internal fun mockShortLink(configure: Boolean = false): ShortLink = ShortLink(
    VALID_STUB,
    VALID_STUB
).apply {
    if (configure) {
        id = VALID_STUB
        creationTime = Instant.EPOCH
        redirects = setOf(
            configuredMockRedirect
        )
    }
}

internal fun mockShortLinkDto() = ShortLinkDto(
    VALID_STUB,
    VALID_STUB,
    VALID_STUB,
    Instant.EPOCH,
    setOf(VALID_STUB)
)

internal fun mockShortLinkFindingStrategy() =
    ShortLinkFindingStrategy.ById(VALID_STUB)

internal fun mockShortLinkCreationDto() = ShortLinkCreationDto(
    VALID_STUB,
    VALID_STUB
)

internal fun mockRedirect(configure: Boolean = false) = Redirect(
    mockShortLink(true)
).apply {
    if (configure) {
        id = VALID_STUB
        creationTime = Instant.EPOCH
    }
}

internal fun mockRedirectDto() = RedirectDto(
    VALID_STUB,
    VALID_STUB,
    Instant.EPOCH
)

internal fun <D : Any, E : Any> mockDtoSetConverterFacade():
        DtoSetConverterFacade<D, E> = mockk()

internal fun <T> mockToPageFun(
    iterable: Iterable<T>, block: (page: Page<T>) -> Unit
) {
    val mockPage = mockk<Page<T>>(relaxed = true) {
        every {
            content
        } returns iterable.toList()
    }

    mockkStatic("dev.d1s.teabag.data.IterableExtKt") {
        every {
            iterable.toPage(0, 0)
        } returns mockPage

        block(mockPage)
    }
}

internal fun mockAliasGenerator() = object : AliasGenerator {
    override val identity = VALID_STUB
    override fun generateAlias() = VALID_STUB
}
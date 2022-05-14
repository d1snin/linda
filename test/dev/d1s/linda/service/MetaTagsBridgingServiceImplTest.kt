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

package dev.d1s.linda.service

import dev.d1s.linda.service.impl.MetaTagsBridgingServiceImpl
import dev.d1s.linda.testUtil.TEST_URL
import dev.d1s.linda.testUtil.elementsMock
import dev.d1s.linda.testUtil.shortLink
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

@SpringBootTest
@ContextConfiguration(classes = [MetaTagsBridgingServiceImpl::class])
class MetaTagsBridgingServiceImplTest {

    @Autowired
    private lateinit var metaTagsBridgingServiceImpl: MetaTagsBridgingServiceImpl

    @Test
    fun `should fetch meta tags`() {
        this.withStaticJsoup {
            expectThat(
                metaTagsBridgingServiceImpl.fetchMetaTags(shortLink)
            ) isEqualTo elementsMock

            verify {
                jsoupCall()
            }
        }
    }

    @Test
    fun `should return null on exception`() {
        mockkStatic(Jsoup::class) {
            every {
                jsoupCall()
            } throws Exception()

            expectThat(
                metaTagsBridgingServiceImpl.fetchMetaTags(shortLink)
            ).isNull()

            verify {
                jsoupCall()
            }
        }
    }

    @Test
    fun `should fetch meta tags and then convert them to string`() {
        this.withStaticJsoup {
            expectThat(
                metaTagsBridgingServiceImpl.fetchMetaTagsAsString(shortLink)
            ) isEqualTo VALID_STUB

            verify {
                elementsMock.toString()
            }
        }
    }

    private fun withStaticJsoup(block: () -> Unit) {
        mockkStatic(Jsoup::class) {
            every {
                jsoupCall()
            } returns elementsMock

            block()
        }
    }

    private fun jsoupCall() =
        Jsoup.connect(TEST_URL)
            .get()
            .getElementsByTag("meta")
}
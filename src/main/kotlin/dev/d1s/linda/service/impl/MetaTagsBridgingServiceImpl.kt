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

package dev.d1s.linda.service.impl

import dev.d1s.linda.service.MetaTagsBridgingService
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class MetaTagsBridgingServiceImpl : MetaTagsBridgingService {

    @Autowired
    private lateinit var metaTagsBridgingService: MetaTagsBridgingServiceImpl

    private val log = logging()

    override fun fetchMetaTags(target: String): Elements? =
        try {
            Jsoup.connect(target)
                .get()
                .getElementsByTag(META_TAG)
                .also {
                    log.debug {
                        "fetched meta tags from $target: $it"
                    }
                }
        } catch (_: Exception) {
            // connection issues are handled by availability checks.
            null
        }

    override fun fetchMetaTagsAsString(target: String): String? =
        metaTagsBridgingService.fetchMetaTags(target)?.toString()

    @Cacheable(cacheNames = [HTML_DOCUMENT_CACHE])
    override fun buildHtmlDocument(target: String): String? =
        metaTagsBridgingService.fetchMetaTagsAsString(target)?.let {
            createHTML().html {
                head {
                    comment(" The following meta tags are taken from $target ")

                    unsafe {
                        +it
                    }
                }
            }
        }

    private companion object {
        private const val META_TAG = "meta"
        private const val HTML_DOCUMENT_CACHE = "html-document"
    }
}
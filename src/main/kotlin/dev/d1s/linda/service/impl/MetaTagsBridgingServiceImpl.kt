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

import dev.d1s.linda.domain.ShortLink
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

    override fun fetchMetaTags(shortLink: ShortLink): Elements? =
        try {
            Jsoup.connect(shortLink.url)
                .get()
                .getElementsByTag(META_TAG)
                .also {
                    log.debug {
                        "fetched meta tags from $shortLink: $it"
                    }
                }
        } catch (_: Exception) {
            // connection issues are handled by availability checks.
            null
        }

    override fun fetchMetaTagsAsString(shortLink: ShortLink): String? =
        metaTagsBridgingService.fetchMetaTags(shortLink)?.toString()

    @Cacheable(cacheNames = [HTML_DOCUMENT_CACHE], key = "#shortLink.id")
    override fun buildHtmlDocument(shortLink: ShortLink): String? =
        metaTagsBridgingService.fetchMetaTagsAsString(shortLink)?.let {
            createHTML().html {
                head {
                    comment(" The following meta tags are taken from ${shortLink.url} ")

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
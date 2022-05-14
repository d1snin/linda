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

import dev.d1s.linda.configuration.properties.BaseInterfaceConfigurationProperties
import dev.d1s.linda.configuration.properties.SslConfigurationProperties
import dev.d1s.linda.constant.mapping.BASE_INTERFACE_CONFIRMATION_SEGMENT
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.utmParameter.UtmParameterType
import dev.d1s.linda.service.*
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.teabag.web.buildFromCurrentRequest
import dev.d1s.teabag.web.configureSsl
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import java.net.URI

@Service
@ConditionalOnProperty("linda.base-interface.enabled", matchIfMissing = true)
class BaseInterfaceServiceImpl : BaseInterfaceService {

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Autowired
    private lateinit var redirectService: RedirectService

    @Autowired
    private lateinit var utmParameterService: UtmParameterService

    @Autowired
    private lateinit var properties: BaseInterfaceConfigurationProperties

    @Autowired
    private lateinit var sslConfigurationProperties: SslConfigurationProperties

    @Autowired
    private lateinit var metaTagsBridgingService: MetaTagsBridgingService

    private val log = logging()

    override fun createRedirectPage(
        alias: String,
        utmSource: String?,
        utmMedium: String?,
        utmCampaign: String?,
        utmTerm: String?,
        utmContent: String?,
        confirmed: Boolean
    ): ResponseEntity<String> {
        val utmMap = mapOf(
            UtmParameterType.SOURCE to utmSource,
            UtmParameterType.MEDIUM to utmMedium,
            UtmParameterType.CAMPAIGN to utmCampaign,
            UtmParameterType.TERM to utmTerm,
            UtmParameterType.CONTENT to utmContent
        )

        log.debug {
            "redirecting from $alias with utm parameters: $utmMap"
        }

        val requireConfirmation = properties.requireConfirmation

        val (shortLink, _) = shortLinkService.find(byAlias(alias))

        val htmlPage = metaTagsBridgingService.buildHtmlDocument(shortLink)

        if (!confirmed && requireConfirmation) {
            log.debug {
                "redirect is unconfirmed"
            }

            return redirect(
                buildFromCurrentRequest {
                    configureSsl(sslConfigurationProperties.fallbackToHttps)
                    path(BASE_INTERFACE_CONFIRMATION_SEGMENT)
                    replaceQueryParams(
                        LinkedMultiValueMap<String, String>().apply {
                            utmMap.forEach { entry ->
                                entry.value?.let {
                                    add(entry.key.rawParameter, it)
                                }
                            }
                        }
                    )
                    build(false) // already encoded
                        .toUriString()
                }.also {
                    log.debug {
                        "responding with redirect to the confirmation endpoint: $it"
                    }
                },
                htmlPage
            )
        }

        val utmParameters = buildSet {
            utmMap.forEach { (type, nullableValue) ->
                nullableValue?.let { value ->
                    val (utmParameter, _) = utmParameterService.findByTypeAndValueOrThrow(type, value)

                    add(utmParameter)
                }
            }
        }

        redirectService.create(
            Redirect(shortLink).apply {
                this.utmParameters = utmParameters.toMutableSet()
            }
        )

        return this.redirect(shortLink.url, htmlPage)
    }

    private fun redirect(location: String, html: String?) =
        ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(location))
            .contentType(MediaType.TEXT_HTML)
            .body(html)
}
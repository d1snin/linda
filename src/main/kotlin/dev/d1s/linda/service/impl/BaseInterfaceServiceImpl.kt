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
import dev.d1s.linda.service.BaseInterfaceService
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.teabag.web.buildFromCurrentRequest
import dev.d1s.teabag.web.configureSsl
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.servlet.view.RedirectView

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

    private val log = logging()

    override fun createRedirectView(
        alias: String,
        utmSource: String?,
        utmMedium: String?,
        utmCampaign: String?,
        utmTerm: String?,
        utmContent: String?,
        confirmed: Boolean
    ): RedirectView {
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

        if (!confirmed && requireConfirmation) {
            log.debug {
                "redirect is unconfirmed"
            }

            return RedirectView(
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
                }
            )
        }

        val (shortLink, _) = shortLinkService.find(byAlias(alias))

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

        return RedirectView(shortLink.url)
    }
}
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
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.exception.impl.notFound.UtmParameterNotFoundException
import dev.d1s.linda.service.BaseInterfaceService
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.strategy.shortLink.byAlias
import dev.d1s.teabag.web.buildFromCurrentRequest
import dev.d1s.teabag.web.configureSsl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.servlet.view.RedirectView
import java.util.concurrent.Executor

@Service
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
    private lateinit var taskExecutor: Executor

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

        if (!confirmed && properties.requireConfirmation) {
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
                    toUriString()
                }
            )
        }

        val shortLink = shortLinkService.find(byAlias(alias))

        val utmParameters = buildSet {
            utmMap.forEach {
                it.value?.let { value ->
                    val utmParameter = utmParameterService.findByTypeAndValue(it.key, value)

                    if (utmParameter.isPresent) {
                        add(utmParameter.get())
                    } else {
                        if (properties.automaticUtmCreation) {
                            add(
                                utmParameterService.create(
                                    UtmParameter(it.key, value)
                                )
                            )
                        } else {
                            throw UtmParameterNotFoundException
                        }
                    }
                }
            }
        }

        taskExecutor.execute {
            redirectService.create(
                Redirect(shortLink).apply {
                    this.utmParameters = utmParameters.toMutableSet()
                }
            )
        }

        return RedirectView(shortLink.url)
    }
}
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

package dev.d1s.linda.controller.impl

import dev.d1s.linda.configuration.properties.BaseInterfaceConfigurationProperties
import dev.d1s.linda.controller.BaseInterfaceController
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.exception.impl.notFound.UtmParameterNotFoundException
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.ShortLinkService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.strategy.shortLink.byAlias
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.view.RedirectView

@Controller
@ConditionalOnProperty("linda.base-interface.enabled", matchIfMissing = true)
class BaseInterfaceControllerImpl : BaseInterfaceController {

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Autowired
    private lateinit var redirectService: RedirectService

    @Autowired
    private lateinit var utmParameterService: UtmParameterService

    @Autowired
    private lateinit var properties: BaseInterfaceConfigurationProperties

    override fun redirect(
        alias: String,
        utmSource: String?,
        utmMedium: String?,
        utmCampaign: String?,
        utmTerm: String?,
        utmContent: String?
    ): RedirectView {
        val shortLink = shortLinkService.find(byAlias(alias))

        val utmParameters = buildSet {
            mapOf(
                UtmParameterType.SOURCE to utmSource,
                UtmParameterType.MEDIUM to utmMedium,
                UtmParameterType.CAMPAIGN to utmCampaign,
                UtmParameterType.TERM to utmTerm,
                UtmParameterType.CONTENT to utmContent
            ).forEach {
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

        val redirect = redirectService.create(Redirect(shortLink))

        utmParameters.forEach {
            redirectService.assignUtmParameterAndSave(redirect, it)
        }

        return RedirectView(shortLink.url)
    }
}
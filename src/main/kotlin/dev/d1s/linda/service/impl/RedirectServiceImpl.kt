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

package dev.d1s.linda.service.impl

import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.exception.notAllowed.impl.DefaultUtmParameterOverrideNotAllowedException
import dev.d1s.linda.exception.notAllowed.impl.IllegalUtmParametersException
import dev.d1s.linda.exception.notAllowed.impl.UtmParametersNotAllowedException
import dev.d1s.linda.exception.notFound.impl.RedirectNotFoundException
import dev.d1s.linda.repository.RedirectRepository
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.util.mapToIdSet
import dev.d1s.teabag.log4j.logger
import dev.d1s.teabag.log4j.util.lazyDebug
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RedirectServiceImpl : RedirectService {

    @Autowired
    private lateinit var redirectRepository: RedirectRepository

    @Lazy
    @Autowired
    private lateinit var redirectService: RedirectServiceImpl

    private val log = logger()

    @Transactional(readOnly = true)
    override fun findAll(): Set<Redirect> =
        redirectRepository.findAll().toSet().also {
            log.lazyDebug {
                "found all redirects: ${
                    it.mapToIdSet()
                }"
            }
        }

    @Transactional(readOnly = true)
    override fun findById(id: String): Redirect =
        redirectRepository.findById(id).orElseThrow {
            RedirectNotFoundException(id)
        }.also {
            log.lazyDebug {
                "found redirect by id: $it"
            }
        }

    @Transactional
    override fun create(redirect: Redirect): Redirect =
        redirectService.assignUtmParametersAndSave(
            redirect.apply {
                validate()

                val defaultUtmParameters = shortLink.defaultUtmParameters

                defaultUtmParameters.forEach { defaultUtmParameter ->
                    utmParameters.forEach { utmParameter ->
                        if (utmParameter.type == defaultUtmParameter.type
                            && !defaultUtmParameter.allowOverride
                            && utmParameter !in defaultUtmParameters
                        ) {
                            throw DefaultUtmParameterOverrideNotAllowedException(defaultUtmParameters)
                        }
                    }

                    if (
                        defaultUtmParameter.type !in utmParameters.map {
                            it.type
                        }
                    ) {
                        utmParameters += defaultUtmParameter
                    }
                }
            },
            redirect.utmParameters
        ).also {
            log.lazyDebug {
                "created availability change: $it"
            }
        }

    @Transactional
    override fun update(id: String, redirect: Redirect): Redirect {
        redirect.validate()

        val foundRedirect = redirectService.findById(id)

        foundRedirect.shortLink = redirect.shortLink

        return redirectService.assignUtmParametersAndSave(
            foundRedirect,
            redirect.utmParameters
        ).also {
            log.lazyDebug {
                "updated redirect: $it"
            }
        }
    }

    @Transactional
    override fun assignUtmParametersAndSave(redirect: Redirect, utmParameters: Set<UtmParameter>): Redirect {
        val originUtmParameters = redirect.utmParameters

        originUtmParameters.forEach {
            if (!utmParameters.contains(it)) {
                originUtmParameters.remove(it)
            }
        }

        redirect.utmParameters = utmParameters.toMutableSet()

        utmParameters.forEach {
            it.redirects += redirect
        }

        return redirectRepository.save(redirect)
    }

    @Transactional
    override fun removeById(id: String) {
        redirectRepository.deleteById(id)

        log.lazyDebug {
            "removed redirect with id $id"
        }
    }

    private fun Redirect.validate() {
        if (utmParameters.isNotEmpty()) {
            if (!shortLink.allowUtmParameters) {
                throw UtmParametersNotAllowedException
            }

            val allowedUtmParameters = shortLink.allowedUtmParameters

            if (allowedUtmParameters.isNotEmpty()) {
                if (!allowedUtmParameters.containsAll(utmParameters)) {
                    throw IllegalUtmParametersException(
                        utmParameters.filter {
                            it !in allowedUtmParameters
                        }.toSet()
                    )
                }
            }
        }
    }
}
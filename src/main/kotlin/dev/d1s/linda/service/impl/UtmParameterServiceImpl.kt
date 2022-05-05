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

import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.exception.alreadyExists.impl.UtmParameterAlreadyExistsException
import dev.d1s.linda.exception.notAllowed.impl.DefaultUtmParametersNotAllowedException
import dev.d1s.linda.exception.notFound.impl.UtmParameterNotFoundException
import dev.d1s.linda.repository.UtmParameterRepository
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.util.mapToIdSet
import dev.d1s.teabag.log4j.logger
import dev.d1s.teabag.log4j.util.lazyDebug
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UtmParameterServiceImpl : UtmParameterService {

    @Autowired
    private lateinit var utmParameterRepository: UtmParameterRepository

    @Autowired
    private lateinit var redirectService: RedirectService

    @Lazy
    @Autowired
    private lateinit var utmParameterService: UtmParameterServiceImpl

    private val log = logger()

    @Transactional(readOnly = true)
    override fun findAll(): Set<UtmParameter> =
        utmParameterRepository.findAll().toSet().also {
            log.lazyDebug {
                "found all utm parameters: ${
                    it.mapToIdSet()
                }"
            }
        }

    @Transactional(readOnly = true)
    override fun findById(id: String): UtmParameter =
        utmParameterRepository.findById(id).orElseThrow {
            UtmParameterNotFoundException(id)
        }.also {
            log.lazyDebug {
                "found utm parameter by id $id"
            }
        }

    @Transactional(readOnly = true)
    override fun findByTypeAndValue(type: UtmParameterType, value: String): Optional<UtmParameter> =
        utmParameterRepository.findUtmParameterByTypeAndValue(type, value).also {
            log.lazyDebug {
                "utm parameter by type and value ($type, $value): $it"
            }
        }

    @Transactional
    override fun create(utmParameter: UtmParameter): UtmParameter {
        utmParameter.validate()

        if (utmParameterService.findByTypeAndValue(utmParameter.type, utmParameter.parameterValue).isPresent) {
            throw UtmParameterAlreadyExistsException
        }

        utmParameterService.assignDefaultUtmParameterShortLinks(utmParameter, utmParameter.defaultForShortLinks)
        utmParameterService.assignAllowedUtmParameterShortLinks(utmParameter, utmParameter.allowedForShortLinks)

        return utmParameterService.assignRedirectsAndSave(
            utmParameter,
            utmParameter.redirects
        ).also {
            log.lazyDebug {
                "created utm parameter: $it"
            }
        }
    }

    @Transactional
    override fun update(id: String, utmParameter: UtmParameter): UtmParameter {
        utmParameter.validate()

        val foundUtmParameter = utmParameterService.findById(id)

        foundUtmParameter.type = utmParameter.type
        foundUtmParameter.parameterValue = utmParameter.parameterValue
        foundUtmParameter.redirects = utmParameter.redirects

        utmParameterService.assignDefaultUtmParameterShortLinks(foundUtmParameter, utmParameter.defaultForShortLinks)
        utmParameterService.assignAllowedUtmParameterShortLinks(foundUtmParameter, utmParameter.allowedForShortLinks)

        return utmParameterService.assignRedirectsAndSave(
            foundUtmParameter,
            foundUtmParameter.redirects
        ).also {
            log.lazyDebug {
                "updated utm parameter: $it"
            }
        }
    }

    @Transactional
    override fun assignRedirectsAndSave(utmParameter: UtmParameter, redirects: Set<Redirect>): UtmParameter {
        utmParameter.redirects.addAll(redirects)

        redirects.forEach {
            it.utmParameters.add(utmParameter)
        }

        return utmParameterRepository.save(utmParameter)
    }

    override fun assignDefaultUtmParameterShortLinks(utmParameter: UtmParameter, shortLinks: Set<ShortLink>) {
        utmParameter.defaultForShortLinks = shortLinks.toMutableSet()

        shortLinks.forEach {
            it.defaultUtmParameters.add(utmParameter)
        }
    }

    override fun assignAllowedUtmParameterShortLinks(utmParameter: UtmParameter, shortLinks: Set<ShortLink>) {
        utmParameter.allowedForShortLinks = shortLinks.toMutableSet()

        shortLinks.forEach {
            it.allowedUtmParameters.add(utmParameter)
        }
    }

    @Transactional
    override fun removeById(id: String) {
        utmParameterRepository.deleteById(id)

        log.lazyDebug {
            "removed utm parameter with id $id"
        }
    }

    private fun UtmParameter.validate() {
        defaultForShortLinks.forEach {
            if (!it.allowUtmParameters) {
                throw DefaultUtmParametersNotAllowedException(setOf(this))
            }
        }
    }
}
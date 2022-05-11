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

import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.exception.alreadyExists.impl.UtmParameterAlreadyExistsException
import dev.d1s.linda.exception.notFound.impl.UtmParameterNotFoundException
import dev.d1s.linda.repository.UtmParameterRepository
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.util.mapToIdSet
import org.lighthousegames.logging.logging
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

    private val log = logging()

    @Transactional(readOnly = true)
    override fun findAll(): Set<UtmParameter> =
        utmParameterRepository.findAll().toSet().also {
            log.debug {
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
            log.debug {
                "found utm parameter by id $id"
            }
        }

    @Transactional(readOnly = true)
    override fun findByTypeAndValue(type: UtmParameterType, value: String): Optional<UtmParameter> =
        utmParameterRepository.findUtmParameterByTypeAndValue(type, value).also {
            log.debug {
                "utm parameter by type and value ($type, $value): $it"
            }
        }

    override fun findByTypeAndValueOrThrow(type: UtmParameterType, value: String): UtmParameter =
        utmParameterService.findByTypeAndValue(type, value).orElseThrow {
            UtmParameterNotFoundException(type, value)
        }

    @Transactional
    override fun create(utmParameter: UtmParameter): UtmParameter {
        if (utmParameterService.findByTypeAndValue(utmParameter.type, utmParameter.parameterValue).isPresent) {
            throw UtmParameterAlreadyExistsException
        }

        return utmParameterRepository.save(
            utmParameter
        ).also {
            log.debug {
                "created utm parameter: $it"
            }
        }
    }

    @Transactional
    override fun update(id: String, utmParameter: UtmParameter): UtmParameter {
        val foundUtmParameter = utmParameterService.findById(id)

        foundUtmParameter.type = utmParameter.type
        foundUtmParameter.parameterValue = utmParameter.parameterValue
        foundUtmParameter.allowOverride = utmParameter.allowOverride

        return utmParameterRepository.save(
            foundUtmParameter
        ).also {
            log.debug {
                "updated utm parameter: $it"
            }
        }
    }

    @Transactional
    override fun removeById(id: String) {
        utmParameterRepository.deleteById(id)

        log.debug {
            "removed utm parameter with id $id"
        }
    }
}
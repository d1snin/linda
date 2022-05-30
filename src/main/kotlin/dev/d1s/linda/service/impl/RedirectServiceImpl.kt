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

import dev.d1s.advice.exception.BadRequestException
import dev.d1s.advice.exception.NotFoundException
import dev.d1s.linda.constant.error.DEFAULT_UTM_PARAMETER_OVERRIDE_ERROR
import dev.d1s.linda.constant.error.ILLEGAL_UTM_PARAMETERS_ERROR
import dev.d1s.linda.constant.error.REDIRECT_NOT_FOUND_ERROR
import dev.d1s.linda.constant.error.UTM_PARAMETERS_NOT_ALLOWED_ERROR
import dev.d1s.linda.constant.lp.REDIRECT_CREATED_GROUP
import dev.d1s.linda.constant.lp.REDIRECT_REMOVED_GROUP
import dev.d1s.linda.constant.lp.REDIRECT_UPDATED_GROUP
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.entity.redirect.Redirect
import dev.d1s.linda.entity.utmParameter.UtmParameter
import dev.d1s.linda.event.data.EntityUpdatedEventData
import dev.d1s.linda.repository.RedirectRepository
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.util.mapToIdSet
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoSet
import dev.d1s.teabag.dto.util.convertToDtoIf
import dev.d1s.teabag.dto.util.convertToDtoSetIf
import dev.d1s.teabag.dto.util.converterForSet
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RedirectServiceImpl : RedirectService {

    @set:Autowired
    lateinit var redirectRepository: RedirectRepository

    @set:Autowired
    lateinit var publisher: AsyncLongPollingEventPublisher

    @set:Autowired
    lateinit var redirectDtoConverter: DtoConverter<RedirectDto, Redirect>

    @Lazy
    @set:Autowired
    lateinit var redirectService: RedirectServiceImpl

    private val redirectSetDtoConverter by lazy {
        redirectDtoConverter.converterForSet()
    }

    private val log = logging()

    @Transactional(readOnly = true)
    override fun findAll(requireDto: Boolean): EntityWithDtoSet<Redirect, RedirectDto> {
        val redirects = redirectRepository.findAll().toSet()

        log.debug {
            "found all redirects: ${
                redirects.mapToIdSet()
            }"
        }

        return redirects to redirectSetDtoConverter
            .convertToDtoSetIf(redirects, requireDto)
    }

    @Transactional(readOnly = true)
    override fun findById(id: String, requireDto: Boolean): EntityWithDto<Redirect, RedirectDto> {
        val redirect = redirectRepository.findById(id).orElseThrow {
            NotFoundException(
                REDIRECT_NOT_FOUND_ERROR.format(id)
            )
        }

        log.debug {
            "found redirect by id: $redirect"
        }

        return redirect to redirectDtoConverter
            .convertToDtoIf(redirect, requireDto)
    }

    @Transactional
    override fun create(redirect: Redirect): EntityWithDto<Redirect, RedirectDto> {
        val createdRedirect = redirectService.assignUtmParametersAndSave(
            redirect.apply {
                this.validate()

                val defaultUtmParameters = shortLink.defaultUtmParameters

                defaultUtmParameters.forEach { defaultUtmParameter ->
                    utmParameters.forEach { utmParameter ->
                        if (utmParameter.type == defaultUtmParameter.type
                            && !defaultUtmParameter.allowOverride
                            && utmParameter !in defaultUtmParameters
                        ) {
                            throw BadRequestException(
                                DEFAULT_UTM_PARAMETER_OVERRIDE_ERROR.format(defaultUtmParameter)
                            )
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
        )

        val dto = redirectDtoConverter.convertToDto(redirect)

        publisher.publish(
            REDIRECT_CREATED_GROUP,
            dto.shortLink,
            dto
        )

        return createdRedirect to dto
    }

    @Transactional
    override fun update(id: String, redirect: Redirect): EntityWithDto<Redirect, RedirectDto> {
        val (foundRedirect, oldRedirectDto) = redirectService.findById(id, true)

        foundRedirect.shortLink = redirect.shortLink

        val savedRedirect = redirectRepository.save(foundRedirect)

        log.debug {
            "updated redirect: $savedRedirect"
        }

        val dto = redirectDtoConverter.convertToDto(savedRedirect)

        publisher.publish(
            REDIRECT_UPDATED_GROUP,
            id,
            EntityUpdatedEventData(
                oldRedirectDto!!,
                dto
            )
        )

        return savedRedirect to dto
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
        val (redirectToRemove, redirectDto) = redirectService.findById(id, true)

        redirectRepository.delete(redirectToRemove)

        publisher.publish(
            REDIRECT_REMOVED_GROUP,
            id,
            redirectDto!!
        )

        log.debug {
            "removed redirect with id $id"
        }
    }

    private fun Redirect.validate() {
        if (utmParameters.isNotEmpty()) {
            if (!shortLink.allowUtmParameters) {
                throw BadRequestException(UTM_PARAMETERS_NOT_ALLOWED_ERROR)
            }

            val allowedUtmParameters = shortLink.allowedUtmParameters

            if (allowedUtmParameters.isNotEmpty()) {
                if (!allowedUtmParameters.containsAll(utmParameters)) {
                    throw BadRequestException(
                        ILLEGAL_UTM_PARAMETERS_ERROR.format(
                            utmParameters.filter {
                                it !in allowedUtmParameters
                            }
                        )
                    )
                }
            }
        }
    }
}
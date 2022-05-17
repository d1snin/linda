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

import dev.d1s.linda.constant.lp.UTM_PARAMETER_CREATED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_REMOVED_GROUP
import dev.d1s.linda.constant.lp.UTM_PARAMETER_UPDATED_GROUP
import dev.d1s.linda.entity.utmParameter.UtmParameter
import dev.d1s.linda.entity.utmParameter.UtmParameterType
import dev.d1s.teabag.dto.EntityWithDto
import dev.d1s.teabag.dto.EntityWithDtoSet
import dev.d1s.linda.dto.utmParameter.UtmParameterDto
import dev.d1s.linda.event.data.utmParameter.CommonUtmParameterEventData
import dev.d1s.linda.event.data.utmParameter.UtmParameterUpdatedEventData
import dev.d1s.linda.exception.alreadyExists.impl.UtmParameterAlreadyExistsException
import dev.d1s.linda.exception.notFound.impl.UtmParameterNotFoundException
import dev.d1s.linda.repository.UtmParameterRepository
import dev.d1s.linda.service.RedirectService
import dev.d1s.linda.service.UtmParameterService
import dev.d1s.linda.util.mapToIdSet
import dev.d1s.lp.server.publisher.AsyncLongPollingEventPublisher
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.util.convertToDtoIf
import dev.d1s.teabag.dto.util.convertToDtoSetIf
import dev.d1s.teabag.dto.util.converterForSet
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

    @Autowired
    private lateinit var utmParameterDtoConverter: DtoConverter<UtmParameterDto, UtmParameter>

    @Autowired
    private lateinit var publisher: AsyncLongPollingEventPublisher

    @Lazy
    @Autowired
    private lateinit var utmParameterService: UtmParameterServiceImpl

    private val utmParameterDtoSetConverter by lazy {
        utmParameterDtoConverter.converterForSet()
    }

    private val log = logging()

    @Transactional(readOnly = true)
    override fun findAll(requireDto: Boolean): EntityWithDtoSet<UtmParameter, UtmParameterDto> {
        val utmParameters = utmParameterRepository.findAll().toSet()

        log.debug {
            "found all utm parameters: ${
                utmParameters.mapToIdSet()
            }"
        }

        return utmParameters to utmParameterDtoSetConverter
            .convertToDtoSetIf(utmParameters, requireDto)
    }

    @Transactional(readOnly = true)
    override fun findById(id: String, requireDto: Boolean): EntityWithDto<UtmParameter, UtmParameterDto> {
        val utmParameter = utmParameterRepository.findById(id).orElseThrow {
            UtmParameterNotFoundException(id)
        }

        log.debug {
            "found utm parameter by id $id: $utmParameter"
        }

        return utmParameter to utmParameterDtoConverter
            .convertToDtoIf(utmParameter, requireDto)
    }

    @Transactional(readOnly = true)
    override fun findByTypeAndValue(
        type: UtmParameterType,
        value: String
    ): Optional<UtmParameter> {
        val utmParameter = utmParameterRepository.findUtmParameterByTypeAndValue(type, value)

        log.debug {
            "utm parameter by type and value ($type, $value): $utmParameter"
        }

        return utmParameter
    }

    override fun findByTypeAndValueOrThrow(
        type: UtmParameterType,
        value: String,
        requireDto: Boolean
    ): EntityWithDto<UtmParameter, UtmParameterDto> {
        val utmParameter = utmParameterService.findByTypeAndValue(type, value).orElseThrow {
            UtmParameterNotFoundException(type, value)
        }

        return utmParameter to utmParameterDtoConverter
            .convertToDtoIf(utmParameter, requireDto)
    }

    @Transactional
    override fun create(utmParameter: UtmParameter): EntityWithDto<UtmParameter, UtmParameterDto> {
        if (utmParameterService.findByTypeAndValue(utmParameter.type, utmParameter.parameterValue).isPresent) {
            throw UtmParameterAlreadyExistsException
        }

        val savedUtmParameter = utmParameterRepository.save(
            utmParameter
        )

        log.debug {
            "created utm parameter: $savedUtmParameter"
        }

        val dto = utmParameterDtoConverter.convertToDto(
            savedUtmParameter
        )

        publisher.publish(
            UTM_PARAMETER_CREATED_GROUP,
            utmParameter.id!!,
            CommonUtmParameterEventData(dto)
        )

        return savedUtmParameter to dto
    }

    @Transactional
    override fun update(id: String, utmParameter: UtmParameter): EntityWithDto<UtmParameter, UtmParameterDto> {
        val (foundUtmParameter, oldUtmParameterDto) = utmParameterService.findById(id, true)

        foundUtmParameter.type = utmParameter.type
        foundUtmParameter.parameterValue = utmParameter.parameterValue
        foundUtmParameter.allowOverride = utmParameter.allowOverride

        val savedUtmParameter = utmParameterRepository.save(
            foundUtmParameter
        )

        log.debug {
            "updated utm parameter: $savedUtmParameter"
        }

        val dto = utmParameterDtoConverter.convertToDto(
            savedUtmParameter
        )

        publisher.publish(
            UTM_PARAMETER_UPDATED_GROUP,
            id,
            UtmParameterUpdatedEventData(
                oldUtmParameterDto!!,
                dto
            )
        )

        return savedUtmParameter to dto
    }

    @Transactional
    override fun removeById(id: String) {
        val (utmParameterForRemoval, dto) =
            utmParameterService.findById(id, true)

        utmParameterRepository.delete(utmParameterForRemoval)

        log.debug {
            "removed utm parameter with id $id"
        }

        publisher.publish(
            UTM_PARAMETER_REMOVED_GROUP,
            id,
            CommonUtmParameterEventData(dto!!)
        )
    }
}
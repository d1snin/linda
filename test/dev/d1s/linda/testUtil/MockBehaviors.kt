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

package dev.d1s.linda.testUtil

import dev.d1s.linda.configuration.properties.AvailabilityChecksConfigurationProperties
import dev.d1s.linda.configuration.properties.BaseInterfaceConfigurationProperties
import dev.d1s.linda.constant.mapping.BASE_INTERFACE_CONFIRMATION_MAPPING
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto
import dev.d1s.linda.dto.redirect.RedirectAlterationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.dto.utm.UtmParameterAlterationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.linda.generator.AliasGenerator
import dev.d1s.linda.repository.AvailabilityChangeRepository
import dev.d1s.linda.repository.RedirectRepository
import dev.d1s.linda.repository.ShortLinkRepository
import dev.d1s.linda.repository.UtmParameterRepository
import dev.d1s.linda.service.*
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.DtoSetConverterFacade
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.justRun
import io.mockk.slot
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.view.RedirectView
import java.net.URI
import java.util.*

val baseInterfaceConfirmationMappingWithAlias =
    BASE_INTERFACE_CONFIRMATION_MAPPING.setAlias()

fun AvailabilityChangeService.prepare() {
    every {
        findAll()
    } returns availabilityChanges

    every {
        findById(VALID_STUB)
    } returns availabilityChange

    every {
        findLast(VALID_STUB)
    } returns availabilityChange

    every {
        create(availabilityChange)
    } returns availabilityChange

    justRun {
        removeById(VALID_STUB)
    }

    every {
        checkAvailability(shortLink)
    } returns availabilityChange

    every {
        checkAndSaveAvailability(shortLink)
    } returns availabilityChange

    every {
        checkAvailabilityOfAllShortLinks()
    } returns availabilityChanges
}

fun BaseInterfaceService.prepare() {
    every {
        createRedirectView(
            VALID_STUB,
            null,
            null,
            null,
            null,
            VALID_STUB,
            false
        )
    } returns RedirectView(
        baseInterfaceConfirmationMappingWithAlias
    )

    every {
        createRedirectView(
            VALID_STUB,
            null,
            null,
            null,
            null,
            VALID_STUB,
            true
        )
    } returns RedirectView(
        TEST_URL
    )
}

fun RedirectService.prepare() {
    every {
        findAll()
    } returns redirects

    every {
        findById(VALID_STUB)
    } returns redirect

    every {
        create(redirect)
    } returns redirect

    every {
        update(VALID_STUB, redirect)
    } returns redirect

    every {
        assignUtmParametersAndSave(redirect, utmParameters)
    } returns redirect

    justRun {
        removeById(VALID_STUB)
    }
}

fun ShortLinkService.prepare() {
    every {
        findAll()
    } returns shortLinks

    every {
        find(any())
    } returns shortLink

    every {
        create(shortLink)
    } returns shortLink

    every {
        update(VALID_STUB, shortLink)
    } returns shortLink

    justRun {
        assignUtmParameters(shortLink, shortLink, any())
    }

    justRun {
        removeById(VALID_STUB)
    }

    every {
        doesAliasExist(any())
    } returns false
}

fun UtmParameterService.prepare() {
    every {
        findAll()
    } returns utmParameters

    every {
        findById(VALID_STUB)
    } returns utmParameter

    every {
        findByTypeAndValue(UtmParameterType.CONTENT, VALID_STUB)
    } returns Optional.of(utmParameter)

    every {
        findByTypeAndValue(UtmParameterType.CONTENT, INVALID_STUB)
    } returns Optional.empty()

    every {
        findByTypeAndValueOrThrow(UtmParameterType.CONTENT, VALID_STUB)
    } returns utmParameter

    every {
        create(utmParameter)
    } returns utmParameter

    every {
        update(VALID_STUB, utmParameter)
    } returns utmParameter

    justRun {
        removeById(VALID_STUB)
    }
}

fun AliasGeneratorService.prepare() {
    every {
        getAliasGenerator(VALID_STUB)
    } returns aliasGeneratorMock
}

fun AliasGenerator.prepare() {
    every {
        identifier
    } returns VALID_STUB

    every {
        generateAlias(shortLinkCreationDto)
    } returns VALID_STUB
}

fun AvailabilityChangeRepository.prepare() {
    every {
        findAll()
    } returns availabilityChanges.toList()

    every {
        findById(VALID_STUB)
    } returns Optional.of(availabilityChange)

    every {
        findById(INVALID_STUB)
    } returns Optional.empty()

    every {
        findLast(VALID_STUB)
    } returns Optional.of(availabilityChange)

    every {
        findLast(INVALID_STUB)
    } returns Optional.empty()

    val slot = slot<AvailabilityChange>()

    every {
        save(
            capture(slot)
        )
    } answers {
        slot.captured
    }

    justRun {
        deleteById(VALID_STUB)
    }
}

fun RedirectRepository.prepare() {
    every {
        findAll()
    } returns redirects.toList()

    every {
        findById(VALID_STUB)
    } returns Optional.of(
        // copy is required for update operation
        redirect.copy().setAutoGeneratedValues()
    )

    every {
        findById(INVALID_STUB)
    } returns Optional.empty()

    val slot = slot<Redirect>()

    every {
        save(
            capture(slot)
        )
    } answers {
        slot.captured
    }

    justRun {
        deleteById(VALID_STUB)
    }
}

fun ShortLinkRepository.prepare() {
    every {
        findAll()
    } returns shortLinks.toList()

    every {
        findById(VALID_STUB)
    } returns Optional.of(
        shortLink.copy().setAutoGeneratedValues()
    )

    every {
        findById(INVALID_STUB)
    } returns Optional.empty()

    every {
        findShortLinkByAliasEquals(VALID_STUB)
    } returns Optional.of(shortLink)

    every {
        findShortLinkByAliasEquals(INVALID_STUB)
    } returns Optional.empty()

    val slot = slot<ShortLink>()

    every {
        save(
            capture(slot)
        )
    } answers {
        slot.captured
    }

    justRun {
        deleteById(VALID_STUB)
    }
}

fun UtmParameterRepository.prepare() {
    every {
        findAll()
    } returns utmParameters.toList()

    every {
        findById(VALID_STUB)
    } returns Optional.of(
        utmParameter.copy().setAutoGeneratedValues()
    )

    every {
        findById(INVALID_STUB)
    } returns Optional.empty()

    every {
        findUtmParameterByTypeAndValue(
            UtmParameterType.CONTENT,
            VALID_STUB
        )
    } returns Optional.of(utmParameter)

    every {
        findUtmParameterByTypeAndValue(
            UtmParameterType.CONTENT,
            INVALID_STUB
        )
    } returns Optional.empty()

    val slot = slot<UtmParameter>()

    every {
        save(
            capture(slot)
        )
    } answers {
        slot.captured
    }

    justRun {
        deleteById(VALID_STUB)
    }
}

fun RestTemplate.prepare() {
    every {
        requestFactory
    } returns clientHttpRequestFactoryMock
}

fun ClientHttpRequestFactory.prepare() {
    every {
        createRequest(
            URI.create(TEST_URL),
            HttpMethod.GET
        ).execute()
    } returns clientHttpResponseMock
}

fun AvailabilityChecksConfigurationProperties.prepare() {
    every {
        badStatusCodeIntRanges
    } returns setOf(-1..-1)

    every {
        eagerAvailabilityCheck
    } returns true
}

fun BaseInterfaceConfigurationProperties.prepare() {
    every {
        enabled
    } returns true

    every {
        requireConfirmation
    } returns true
}

@JvmName("prepareDtoConverterAvailabilityChangeDtoAvailabilityChange")
fun DtoConverter<AvailabilityChangeDto, AvailabilityChange>.prepare() {
    every {
        convertToDto(availabilityChange)
    } returns availabilityChangeDto
}

@JvmName("prepareDtoSetConverterFacadeAvailabilityChangeDtoAvailabilityChange")
fun DtoSetConverterFacade<AvailabilityChangeDto, AvailabilityChange>.prepare() {
    every {
        convertToDtoSet(availabilityChanges)
    } returns availabilityChangeDtoSet
}

@JvmName("prepareDtoConverterUnsavedAvailabilityChangeDtoAvailabilityChange")
fun DtoConverter<UnsavedAvailabilityChangeDto, AvailabilityChange>.prepare() {
    every {
        convertToDto(availabilityChange)
    } returns unsavedAvailabilityChangeDto
}

@JvmName("prepareDtoConverterRedirectDtoRedirect")
fun DtoConverter<RedirectDto, Redirect>.prepare() {
    every {
        convertToDto(redirect)
    } returns redirectDto
}

@JvmName("prepareDtoSetConverterFacadeRedirectDtoRedirect")
fun DtoSetConverterFacade<RedirectDto, Redirect>.prepare() {
    every {
        convertToDtoSet(redirects)
    } returns redirectDtoSet
}

@JvmName("prepareDtoConverterRedirectAlterationDtoRedirect")
fun DtoConverter<RedirectAlterationDto, Redirect>.prepare() {
    every {
        convertToEntity(redirectAlterationDto)
    } returns redirect
}

@JvmName("prepareDtoConverterShortLinkDtoShortLink")
fun DtoConverter<ShortLinkDto, ShortLink>.prepare() {
    every {
        convertToDto(shortLink)
    } returns shortLinkDto
}

@JvmName("prepareDtoSetConverterFacadeShortLinkDtoShortLink")
fun DtoSetConverterFacade<ShortLinkDto, ShortLink>.prepare() {
    every {
        convertToDtoSet(shortLinks)
    } returns shortLinkDtoSet
}

@JvmName("prepareDtoConverterShortLinkCreationDtoShortLink")
fun DtoConverter<ShortLinkCreationDto, ShortLink>.prepare() {
    every {
        convertToEntity(shortLinkCreationDto)
    } returns shortLink
}

@JvmName("prepareDtoConverterShortLinkUpdateDtoShortLink")
fun DtoConverter<ShortLinkUpdateDto, ShortLink>.prepare() {
    every {
        convertToEntity(shortLinkUpdateDto)
    } returns shortLink
}

@JvmName("prepareDtoConverterUtmParameterDtoUtmParameter")
fun DtoConverter<UtmParameterDto, UtmParameter>.prepare() {
    every {
        convertToDto(utmParameter)
    } returns utmParameterDto
}

@JvmName("prepareDtoSetConverterFacadeUtmParameterDtoUtmParameter")
fun DtoSetConverterFacade<UtmParameterDto, UtmParameter>.prepare() {
    every {
        convertToDtoSet(utmParameters)
    } returns utmParameterDtoSet
}

@JvmName("prepareDtoConverterUtmParameterAlterationDtoUtmParameter")
fun DtoConverter<UtmParameterAlterationDto, UtmParameter>.prepare() {
    every {
        convertToEntity(utmParameterAlterationDto)
    } returns utmParameter
}
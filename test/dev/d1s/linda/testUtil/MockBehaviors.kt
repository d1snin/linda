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
import dev.d1s.linda.domain.utmParameter.UtmParameter
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.availability.UnsavedAvailabilityChangeDto
import dev.d1s.linda.dto.redirect.RedirectAlterationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.dto.utmParameter.UtmParameterAlterationDto
import dev.d1s.linda.dto.utmParameter.UtmParameterDto
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.util.*

val baseInterfaceConfirmationMappingWithAlias =
    BASE_INTERFACE_CONFIRMATION_MAPPING.setAlias()

fun AvailabilityChangeService.prepare() {
    every {
        findAll(any())
    } returns (availabilityChanges to availabilityChangeDtoSet)

    every {
        findById(VALID_STUB, any())
    } returns (availabilityChange to availabilityChangeDto)

    every {
        findLast(VALID_STUB)
    } returns availabilityChange

    every {
        create(availabilityChange)
    } returns (availabilityChange to availabilityChangeDto)

    justRun {
        removeById(VALID_STUB)
    }

    every {
        checkAvailability(shortLink)
    } returns (availabilityChange to unsavedAvailabilityChangeDto)

    every {
        checkAvailability(VALID_STUB)
    } returns (availabilityChange to unsavedAvailabilityChangeDto)

    every {
        checkAndSaveAvailability(shortLink)
    } returns availabilityChange

    every {
        checkAvailabilityOfAllShortLinks()
    } returns (availabilityChanges to availabilityChangeDtoSet)
}

fun testResponseEntity(location: String) = ResponseEntity.status(HttpStatus.FOUND)
    .location(URI.create(location))
    .body(VALID_STUB)

fun BaseInterfaceService.prepare() {
    every {
        createRedirectPage(
            VALID_STUB,
            null,
            null,
            null,
            null,
            VALID_STUB,
            false
        )
    } returns testResponseEntity(
        baseInterfaceConfirmationMappingWithAlias
    )

    every {
        createRedirectPage(
            VALID_STUB,
            null,
            null,
            null,
            null,
            VALID_STUB,
            true
        )
    } returns testResponseEntity(
        TEST_URL
    )
}

fun MetaTagsBridgingService.prepare() {
    every {
        fetchMetaTags(shortLink)
    } returns elementsMock

    every {
        fetchMetaTagsAsString(shortLink)
    } returns VALID_STUB

    every {
        buildHtmlDocument(shortLink)
    } returns VALID_STUB
}

fun RedirectService.prepare() {
    every {
        findAll(any())
    } returns (redirects to redirectDtoSet)

    every {
        findById(VALID_STUB, any())
    } returns (redirect to redirectDto)

    every {
        create(redirect)
    } returns (redirect to redirectDto)

    every {
        update(VALID_STUB, redirect)
    } returns (redirect to redirectDto)

    every {
        assignUtmParametersAndSave(redirect, utmParameters)
    } returns redirect

    justRun {
        removeById(VALID_STUB)
    }
}

fun ShortLinkService.prepare() {
    every {
        findAll(any())
    } returns (shortLinks to shortLinkDtoSet)

    every {
        find(any(), any())
    } returns (shortLink to shortLinkDto)

    every {
        create(shortLink)
    } returns (shortLink to shortLinkDto)

    every {
        update(VALID_STUB, shortLink)
    } returns (shortLink to shortLinkDto)

    justRun {
        assignUtmParameters(shortLink, shortLink, any())
    }

    justRun {
        removeById(VALID_STUB)
    }

    every {
        doesAliasExist(any())
    } returns false

    every {
        isExpired(shortLink)
    } returns false

    justRun {
        scheduleForDeletion(shortLink)
    }

    justRun {
        scheduleAllEphemeralShortLinksForDeletion()
    }
}

fun UtmParameterService.prepare() {
    every {
        findAll(any())
    } returns (utmParameters to utmParameterDtoSet)

    every {
        findById(VALID_STUB, any())
    } returns (utmParameter to utmParameterDto)

    every {
        findByTypeAndValue(testUtmParameterType, VALID_STUB)
    } returns Optional.of(utmParameter)

    every {
        findByTypeAndValue(testUtmParameterType, INVALID_STUB)
    } returns Optional.empty()

    every {
        findByTypeAndValueOrThrow(testUtmParameterType, VALID_STUB, any())
    } returns (utmParameter to utmParameterDto)

    every {
        create(utmParameter)
    } returns (utmParameter to utmParameterDto)

    every {
        update(VALID_STUB, utmParameter)
    } returns (utmParameter to utmParameterDto)

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
        delete(availabilityChange)
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
        delete(redirect)
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
        findByAlias(VALID_STUB)
    } returns Optional.of(shortLink)

    every {
        findByAlias(INVALID_STUB)
    } returns Optional.empty()

    every {
        findByDeleteAfterIsNotNull()
    } returns shortLinks

    val slot = slot<ShortLink>()

    every {
        save(
            capture(slot)
        )
    } answers {
        slot.captured
    }

    justRun {
        delete(shortLink)
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
            testUtmParameterType,
            VALID_STUB
        )
    } returns Optional.of(utmParameter)

    every {
        findUtmParameterByTypeAndValue(
            testUtmParameterType,
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
        delete(utmParameter)
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
        convertToDto(any())
    } returns availabilityChangeDto
}

@JvmName("prepareDtoSetConverterFacadeAvailabilityChangeDtoAvailabilityChange")
fun DtoSetConverterFacade<AvailabilityChangeDto, AvailabilityChange>.prepare() {
    every {
        convertToDtoSet(any())
    } returns availabilityChangeDtoSet
}

@JvmName("prepareDtoConverterUnsavedAvailabilityChangeDtoAvailabilityChange")
fun DtoConverter<UnsavedAvailabilityChangeDto, AvailabilityChange>.prepare() {
    every {
        convertToDto(any())
    } returns unsavedAvailabilityChangeDto
}

@JvmName("prepareDtoConverterRedirectDtoRedirect")
fun DtoConverter<RedirectDto, Redirect>.prepare() {
    every {
        convertToDto(any())
    } returns redirectDto
}

@JvmName("prepareDtoSetConverterFacadeRedirectDtoRedirect")
fun DtoSetConverterFacade<RedirectDto, Redirect>.prepare() {
    every {
        convertToDtoSet(any())
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
        convertToDto(any())
    } returns shortLinkDto
}

@JvmName("prepareDtoSetConverterFacadeShortLinkDtoShortLink")
fun DtoSetConverterFacade<ShortLinkDto, ShortLink>.prepare() {
    every {
        convertToDtoSet(any())
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
        convertToDto(any())
    } returns utmParameterDto
}

@JvmName("prepareDtoSetConverterFacadeUtmParameterDtoUtmParameter")
fun DtoSetConverterFacade<UtmParameterDto, UtmParameter>.prepare() {
    every {
        convertToDtoSet(any())
    } returns utmParameterDtoSet
}

@JvmName("prepareDtoConverterUtmParameterAlterationDtoUtmParameter")
fun DtoConverter<UtmParameterAlterationDto, UtmParameter>.prepare() {
    every {
        convertToEntity(utmParameterAlterationDto)
    } returns utmParameter
}
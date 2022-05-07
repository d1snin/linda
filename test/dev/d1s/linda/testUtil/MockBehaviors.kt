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

import dev.d1s.linda.constant.mapping.BASE_INTERFACE_CONFIRMATION_MAPPING
import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.ShortLink
import dev.d1s.linda.domain.availability.AvailabilityChange
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.domain.utm.UtmParameterType
import dev.d1s.linda.dto.availability.AvailabilityChangeDto
import dev.d1s.linda.dto.redirect.RedirectAlterationDto
import dev.d1s.linda.dto.redirect.RedirectDto
import dev.d1s.linda.dto.shortLink.ShortLinkCreationDto
import dev.d1s.linda.dto.shortLink.ShortLinkDto
import dev.d1s.linda.dto.shortLink.ShortLinkUpdateDto
import dev.d1s.linda.dto.utm.UtmParameterAlterationDto
import dev.d1s.linda.dto.utm.UtmParameterDto
import dev.d1s.linda.service.*
import dev.d1s.teabag.dto.DtoConverter
import dev.d1s.teabag.dto.DtoSetConverterFacade
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.justRun
import org.springframework.web.servlet.view.RedirectView
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
        doesAliasExist(VALID_STUB)
    } returns true

    every {
        doesAliasExist(INVALID_STUB)
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
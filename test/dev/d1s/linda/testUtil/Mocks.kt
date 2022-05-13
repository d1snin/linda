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

import dev.d1s.linda.generator.AliasGenerator
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

val aliasGeneratorMock =
    mockk<AliasGenerator>().apply {
        prepare()
    }

val clientHttpResponseMock =
    mockk<ClientHttpResponse> {
        every {
            rawStatusCode
        } returns HttpStatus.OK.value()

        justRun {
            close()
        }
    }

val clientHttpRequestFactoryMock =
    mockk<ClientHttpRequestFactory>().apply {
        prepare()
    }

val servletUriComponentsBuilderMock =
    mockk<ServletUriComponentsBuilder>(relaxed = true) {
        every {
            build(false).toUriString()
        } returns VALID_STUB
    }
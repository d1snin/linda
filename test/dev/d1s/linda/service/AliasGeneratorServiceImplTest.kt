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

package dev.d1s.linda.service

import com.ninjasquad.springmockk.MockkBean
import dev.d1s.linda.exception.notFound.impl.AliasGeneratorNotFoundException
import dev.d1s.linda.generator.AliasGenerator
import dev.d1s.linda.service.impl.AliasGeneratorServiceImpl
import dev.d1s.linda.testUtil.prepare
import dev.d1s.teabag.testing.constant.INVALID_STUB
import dev.d1s.teabag.testing.constant.VALID_STUB
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [AliasGeneratorServiceImpl::class])
class AliasGeneratorServiceImplTest {

    @Autowired
    private lateinit var aliasGeneratorServiceImpl: AliasGeneratorServiceImpl

    @MockkBean
    private lateinit var aliasGenerator: AliasGenerator

    @BeforeEach
    fun setup() {
        aliasGenerator.prepare()
    }

    @Test
    fun `should return valid alias generator`() {
        expectThat(
            aliasGeneratorServiceImpl.getAliasGenerator(VALID_STUB)
        ) isEqualTo aliasGenerator
    }

    @Test
    fun `should throw AliasGeneratorNotFoundException`() {
        assertThrows<AliasGeneratorNotFoundException> {
            aliasGeneratorServiceImpl.getAliasGenerator(INVALID_STUB)
        }
    }
}
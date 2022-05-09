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

package dev.d1s.linda.dto.converter.shortLink

import dev.d1s.linda.dto.converter.impl.shortLink.ShortLinkDtoConverter
import dev.d1s.linda.testUtil.shortLink
import dev.d1s.linda.testUtil.shortLinkDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [ShortLinkDtoConverter::class])
class ShortLinkDtoConverterTest {

    @Autowired
    private lateinit var converter: ShortLinkDtoConverter

    @Test
    fun `should convert short link to dto`() {
        expectThat(
            converter.convertToDto(shortLink)
        ) isEqualTo shortLinkDto
    }
}
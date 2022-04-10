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

package dev.d1s.linda.exception.mapper

import dev.d1s.advice.domain.ErrorResponseData
import dev.d1s.teabag.testing.constant.VALID_STUB
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@ContextConfiguration(classes = [DataAccessExceptionMapper::class])
internal class DataAccessExceptionMapperTest {

    @Autowired
    private lateinit var dataAccessExceptionMapper: DataAccessExceptionMapper

    private val exception = mockk<DataAccessException>() {
        every {
            message
        } returns VALID_STUB
    }

    @Test
    fun `should return valid ErrorResponseData`() {
        expectThat(
            dataAccessExceptionMapper.map(exception)
        ) isEqualTo ErrorResponseData(HttpStatus.BAD_REQUEST, VALID_STUB)
    }
}
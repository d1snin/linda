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

import dev.d1s.advice.entity.ErrorResponseData
import dev.d1s.advice.mapper.ExceptionMapper
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class DataAccessExceptionMapper : ExceptionMapper {

    override fun map(exception: Exception): ErrorResponseData? =
        (exception as? DataAccessException)?.let {
            ErrorResponseData(HttpStatus.BAD_REQUEST, exception.message!!)
        }
}
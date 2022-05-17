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

package dev.d1s.linda.exception.unprocessableEntity.impl

import dev.d1s.advice.domain.ErrorResponseData
import dev.d1s.advice.exception.HttpStatusException
import dev.d1s.linda.constant.error.ERROR_UTM_PARAMETER_ALREADY_EXISTS
import org.springframework.http.HttpStatus

object UtmParameterAlreadyExistsException : HttpStatusException(
    ErrorResponseData(HttpStatus.UNPROCESSABLE_ENTITY, ERROR_UTM_PARAMETER_ALREADY_EXISTS)
)
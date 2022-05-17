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

package dev.d1s.linda.exception.notAllowed.impl

import dev.d1s.linda.constant.error.DEFAULT_UTM_PARAMETERS_NOT_ALLOWED_ERROR
import dev.d1s.linda.entity.utmParameter.UtmParameter
import dev.d1s.linda.exception.notAllowed.ActionNotAllowedException

class DefaultUtmParametersNotAllowedException(utmParameters: Set<UtmParameter>) : ActionNotAllowedException(
    DEFAULT_UTM_PARAMETERS_NOT_ALLOWED_ERROR.format(utmParameters)
)
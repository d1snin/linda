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

package dev.d1s.linda.constant.mapping.api

const val UTM_PARAMETERS_BASE_MAPPING = "$API_BASE_MAPPING/utmParameters"
const val UTM_PARAMETERS_FIND_ALL_MAPPING = UTM_PARAMETERS_BASE_MAPPING
const val UTM_PARAMETERS_FIND_BY_ID_MAPPING = "$UTM_PARAMETERS_BASE_MAPPING/{identifier}"
const val UTM_PARAMETERS_FIND_BY_TYPE_AND_VALUE_MAPPING = "$UTM_PARAMETERS_BASE_MAPPING/{type}/{value}"
const val UTM_PARAMETERS_CREATE_MAPPING = UTM_PARAMETERS_BASE_MAPPING
const val UTM_PARAMETERS_UPDATE_MAPPING = "$UTM_PARAMETERS_BASE_MAPPING/{identifier}"
const val UTM_PARAMETERS_REMOVE_BY_ID_MAPPING = "$UTM_PARAMETERS_BASE_MAPPING/{identifier}"
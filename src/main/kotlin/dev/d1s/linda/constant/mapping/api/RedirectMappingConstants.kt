/*
 * Copyright 2022 Linda project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.linda.constant.mapping.api

const val REDIRECTS_BASE_MAPPING = "$API_BASE_MAPPING/redirects"
const val REDIRECTS_FIND_ALL_MAPPING = REDIRECTS_BASE_MAPPING
const val REDIRECTS_FIND_BY_ID_MAPPING = "$REDIRECTS_BASE_MAPPING$IDENTIFIER_SEGMENT"
const val REDIRECTS_CREATE_MAPPING = REDIRECTS_BASE_MAPPING
const val REDIRECTS_UPDATE_MAPPING = "$REDIRECTS_BASE_MAPPING$IDENTIFIER_SEGMENT"
const val REDIRECTS_REMOVE_BY_ID_MAPPING = "$REDIRECTS_BASE_MAPPING$IDENTIFIER_SEGMENT"
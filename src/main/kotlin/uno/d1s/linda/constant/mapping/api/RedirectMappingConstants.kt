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

package uno.d1s.linda.constant.mapping.api

const val REDIRECTS_BASE_MAPPING = "$API_BASE_MAPPING/redirects"
const val REDIRECTS_FIND_ALL_MAPPING = REDIRECTS_BASE_MAPPING
const val REDIRECTS_FIND_BY_ID_MAPPING = "$REDIRECTS_BASE_MAPPING/{identifier}"
const val REDIRECTS_FIND_ALL_BY_SHORT_LINK_MAPPING = "$REDIRECTS_BASE_MAPPING/$SHORT_LINKS_BASE_NAME/{identifier}"
const val REDIRECTS_REMOVE_MAPPING = "$REDIRECTS_BASE_MAPPING/{identifier}"
const val REDIRECTS_REMOVE_ALL_MAPPING = REDIRECTS_BASE_MAPPING
const val REDIRECTS_BULK_REMOVE_MAPPING = "$REDIRECTS_BASE_MAPPING/part"
const val REDIRECTS_REMOVE_ALL_BY_SHORT_LINK_MAPPING = "$REDIRECTS_BASE_MAPPING/$SHORT_LINKS_BASE_NAME/{identifier}"
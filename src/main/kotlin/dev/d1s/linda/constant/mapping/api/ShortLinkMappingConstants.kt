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

const val SHORT_LINKS_BASE_NAME = "shortLinks"
const val SHORT_LINKS_BASE_MAPPING = "$API_BASE_MAPPING/$SHORT_LINKS_BASE_NAME"
const val SHORT_LINKS_FIND_ALL_MAPPING = SHORT_LINKS_BASE_MAPPING
const val SHORT_LINKS_FIND_MAPPING = "$SHORT_LINKS_BASE_MAPPING/{identifier}"
const val SHORT_LINKS_CREATE_MAPPING = SHORT_LINKS_BASE_MAPPING
const val SHORT_LINKS_UPDATE_MAPPING = "$SHORT_LINKS_BASE_MAPPING/{identifier}"
const val SHORT_LINKS_REMOVE_MAPPING = "$SHORT_LINKS_BASE_MAPPING/{identifier}"
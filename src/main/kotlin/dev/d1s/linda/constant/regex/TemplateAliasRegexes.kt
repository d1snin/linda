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

package dev.d1s.linda.constant.regex

// {example_template_variable}
const val TEMPLATE_VARIABLE_REGEX = "\\{([a-z]|([a-z]-[a-z]))+}"

// example-{template-declaration}
const val TEMPLATE_REGEX = "^(([a-z]+-)|($TEMPLATE_VARIABLE_REGEX-))+([a-z]+|($TEMPLATE_VARIABLE_REGEX))$"

// example-(.+)
const val TEMPLATE_VARIABLE_REPLACEMENT_REGEX = "(.+)"

const val TEMPLATE_VARIABLE_SEPARATOR_REGEX = "-(?!\\!)"

// I use "!" for escaping since "\" should always be encoded in URLs.
const val TEMPLATE_VARIABLE_SEPARATOR_ESCAPE = "(?<=-)!"
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

package dev.d1s.linda.constant.error

const val ERROR_ALIAS_ALREADY_EXISTS =
    "The provided alias already exists."
const val ERROR_UTM_PARAMETER_ALREADY_EXISTS =
    "UTM parameter already exists."

const val CUSTOM_ALIAS_NOT_DEFINED_ERROR =
    "Custom alias is not defined. Specify it using the 'customAlias' request parameter."
const val EMPTY_CUSTOM_ALIAS_ERROR =
    "Custom alias must not be empty."

const val DEFAULT_UTM_PARAMETER_OVERRIDE_ERROR =
    "It's not allowed to override the default UTM parameters (%s)"
const val DEFAULT_UTM_PARAMETERS_NOT_ALLOWED_ERROR =
    "Default UTM parameters (%s) are not allowed since the allowUtmParameters property is set to true."
const val ILLEGAL_UTM_PARAMETERS_ERROR =
    "Provided UTM parameters (%S) are not allowed for this short link."
const val UTM_PARAMETERS_NOT_ALLOWED_ERROR =
    "UTM parameters are not allowed for this short link."

const val ALIAS_GENERATOR_NOT_FOUND_ERROR =
    "Requested alias generator (%s) was not found."
const val AVAILABILITY_CHANGE_NOT_FOUND_ERROR =
    "Requested availability change (%s) was not found."
const val REDIRECT_NOT_FOUND_ERROR =
    "Requested redirect (%s) was not found."
const val SHORT_LINK_NOT_FOUND_ERROR =
    "Requested short link (%s) was not found."
const val UTM_PARAMETER_NOT_FOUND_ERROR =
    "Requested UTM parameter (%s) was not found."

const val AVAILABILITY_CHECK_IN_PROGRESS_ERROR =
    "Availability check is already in progress."
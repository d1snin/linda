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

import dev.d1s.linda.constant.regex.TEMPLATE_REGEX

const val ALIAS_ALREADY_EXISTS_ERROR =
    "The provided alias (%s) already exists."

const val NO_ALIAS_OR_ALIAS_GENERATOR_PRESENT_ERROR =
    "No alias or alias generator present."

const val NO_ALIAS_PRESENT_ERROR =
    "Alias must be defined if the alias type is set to TEMPLATE."

const val ALIAS_UNRESOLVED_ERROR =
    "Alias (%s) was not resolved."

const val ALIAS_TEMPLATE_COLLISION_ERROR =
    "Alias (%s) collides with other ones."

const val NO_ALIAS_TEMPLATE_VARIABLES_DEFINED_ERROR =
    "Alias (%s) must contain at least one template variable."

const val UNUSED_TEMPLATE_VARIABLE_ERROR =
    "Unused template variable: %s"

const val DUPLICATED_TEMPLATE_VARIABLES_ERROR =
    "Alias (%s) must not contain duplicated template variables."

const val BAD_TEMPLATE_ERROR =
    "Alias (%s) does not match the regex: $TEMPLATE_REGEX. Example usage: example-{template-declaration}"

const val UTM_PARAMETER_ALREADY_EXISTS_ERROR =
    "UTM parameter already exists."

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

const val REDIRECTS_NOT_ALLOWED_ERROR =
    "Short link (%s) does not allow redirects."

const val SHORT_LINK_NOT_FOUND_ERROR =
    "Requested short link (%s) was not found."

const val ILLEGAL_SHORT_LINK_MAX_REDIRECTS_ERROR = "'maxRedirects' field must be null or greater than 0."

const val UTM_PARAMETER_NOT_FOUND_ERROR =
    "Requested UTM parameter (%s) was not found."

const val AVAILABILITY_CHECK_IN_PROGRESS_ERROR =
    "Availability check is already in progress."

const val CAN_NOT_CHECK_AVAILABILITY_ERROR =
    "Can not change the availability of this short link since the target is not resolved."
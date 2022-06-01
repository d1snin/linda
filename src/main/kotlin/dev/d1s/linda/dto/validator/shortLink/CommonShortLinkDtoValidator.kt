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

package dev.d1s.linda.dto.validator.shortLink

import dev.d1s.advice.exception.BadRequestException
import dev.d1s.linda.constant.error.*
import dev.d1s.linda.constant.regex.TEMPLATE_REGEX
import dev.d1s.linda.constant.regex.TEMPLATE_VARIABLE_REGEX
import dev.d1s.linda.dto.shortLink.CommonShortLinkDto
import dev.d1s.linda.entity.alias.AliasType
import dev.d1s.teabag.dto.DtoValidator
import org.springframework.stereotype.Component

@Component
class CommonShortLinkDtoValidator : DtoValidator<CommonShortLinkDto> {

    private val templateRegex = TEMPLATE_REGEX.toRegex()

    private val templateVariableRegex = TEMPLATE_VARIABLE_REGEX.toRegex()

    override fun validate(dto: CommonShortLinkDto) {
        dto.run {
            checkUtmParameters()
            checkAlias()
            checkMaxRedirects()
        }
    }

    private fun CommonShortLinkDto.checkUtmParameters() {
        if (!allowUtmParameters && defaultUtmParameters.isNotEmpty()) {
            throw BadRequestException(
                DEFAULT_UTM_PARAMETERS_NOT_ALLOWED_ERROR
                    .format(defaultUtmParameters)
            )
        }
    }

    private fun CommonShortLinkDto.checkAlias() {
        if (aliasType == AliasType.TEMPLATE) {
            alias?.let {
                it.checkAliasFormat()
                checkTemplateVariables(it)
            } ?: throw BadRequestException(
                NO_ALIAS_PRESENT_ERROR
            )
        }
    }

    private fun String.checkAliasFormat() {
        if (!matches(templateRegex)) {
            throw BadRequestException(
                BAD_TEMPLATE_ERROR.format(this)
            )
        }
    }

    private fun CommonShortLinkDto.checkTemplateVariables(alias: String) {
        val aliasTemplateVars =
            templateVariableRegex.findAll(alias).map { res ->
                res.value
            }.toList()

        val targetTemplateVars =
            templateVariableRegex.findAll(target).map { res ->
                res.value
            }.toList()

        if (aliasTemplateVars.isEmpty()) {
            throw BadRequestException(
                NO_ALIAS_TEMPLATE_VARIABLES_DEFINED_ERROR.format(alias)
            )
        }

        if (aliasTemplateVars.size != aliasTemplateVars.toSet().size) {
            throw BadRequestException(
                DUPLICATED_TEMPLATE_VARIABLES_ERROR.format(alias)
            )
        }

        aliasTemplateVars.forEach { templateVar ->
            if (!targetTemplateVars.contains(templateVar)) {
                throw BadRequestException(
                    UNUSED_TEMPLATE_VARIABLE_ERROR.format(templateVar)
                )
            }
        }
    }

    private fun CommonShortLinkDto.checkMaxRedirects() {
        maxRedirects?.let {
            if (it <= 0) {
                throw BadRequestException(
                    ILLEGAL_SHORT_LINK_MAX_REDIRECTS_ERROR
                )
            }
        }
    }
}
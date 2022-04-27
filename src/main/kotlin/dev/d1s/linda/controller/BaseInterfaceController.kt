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

package dev.d1s.linda.controller

import dev.d1s.linda.constant.mapping.BASE_INTERFACE_CONFIRMATION_MAPPING
import dev.d1s.linda.constant.mapping.BASE_INTERFACE_MAPPING
import dev.d1s.linda.constant.utm.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView
import javax.validation.constraints.NotBlank

@Validated
interface BaseInterfaceController {

    @GetMapping(BASE_INTERFACE_MAPPING)
    fun redirect(
        @PathVariable @NotBlank(message = "alias must not be blank.") alias: String,
        @RequestParam(UTM_SOURCE, required = false) utmSource: String?,
        @RequestParam(UTM_MEDIUM, required = false) utmMedium: String?,
        @RequestParam(UTM_CAMPAIGN, required = false) utmCampaign: String?,
        @RequestParam(UTM_TERM, required = false) utmTerm: String?,
        @RequestParam(UTM_CONTENT, required = false) utmContent: String?
    ): RedirectView

    // I think I'm dumb.
    @GetMapping(BASE_INTERFACE_CONFIRMATION_MAPPING)
    fun confirmRedirect(
        @PathVariable @NotBlank(message = "alias must not be blank.") alias: String,
        @RequestParam(UTM_SOURCE, required = false) utmSource: String?,
        @RequestParam(UTM_MEDIUM, required = false) utmMedium: String?,
        @RequestParam(UTM_CAMPAIGN, required = false) utmCampaign: String?,
        @RequestParam(UTM_TERM, required = false) utmTerm: String?,
        @RequestParam(UTM_CONTENT, required = false) utmContent: String?
    ): RedirectView
}
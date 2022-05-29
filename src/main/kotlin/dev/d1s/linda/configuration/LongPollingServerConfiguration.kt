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

package dev.d1s.linda.configuration

import dev.d1s.linda.constant.lp.*
import dev.d1s.lp.server.configurer.LongPollingServerConfigurer
import org.springframework.context.annotation.Configuration

@Configuration
class LongPollingServerConfiguration : LongPollingServerConfigurer {

    override fun getAvailableGroups(): Set<String> = setOf(
        AVAILABILITY_CHANGE_CREATED_GROUP,
        AVAILABILITY_CHANGE_REMOVED_GROUP,
        AVAILABILITY_CHECK_PERFORMED_GROUP,
        GLOBAL_AVAILABILITY_CHECK_PERFORMED_GROUP,

        REDIRECT_CREATED_GROUP,
        REDIRECT_UPDATED_GROUP,
        REDIRECT_REMOVED_GROUP,

        SHORT_LINK_CREATED_GROUP,
        SHORT_LINK_UPDATED_GROUP,
        SHORT_LINK_REMOVED_GROUP,

        UTM_PARAMETER_CREATED_GROUP,
        UTM_PARAMETER_UPDATED_GROUP,
        UTM_PARAMETER_REMOVED_GROUP
    )
}
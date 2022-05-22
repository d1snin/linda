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

package dev.d1s.linda.initializer

import dev.d1s.linda.service.ShortLinkService
import org.lighthousegames.logging.logging
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ShortLinkDeletionSchedulingStarter : InitializingBean {

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    private val log = logging()

    override fun afterPropertiesSet() {
        log.debug {
            "scheduling all ephemeral short links for deletion on application launch right now"
        }

        shortLinkService.scheduleAllEphemeralShortLinksForDeletion()
    }
}
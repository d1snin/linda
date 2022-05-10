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

package dev.d1s.linda.configuration.properties

import dev.d1s.linda.constant.property.DEFAULT_BAD_STATUS_CODE_RANGE
import dev.d1s.linda.constant.property.DEFAULT_EAGER_AVAILABILITY_CHECK
import dev.d1s.teabag.stdlib.collection.mapToSet
import dev.d1s.teabag.stdlib.ranges.parseIntRange
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.util.StringUtils
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("linda.availability-checks")
data class AvailabilityChecksConfigurationProperties(
    // from HttpURLConnection.getResponseCode() javadocs:
    // Returns -1 if no code can be discerned from the response
    // (i.e., the response is not valid HTTP).
    private val badStatusCodeRanges: String =
        DEFAULT_BAD_STATUS_CODE_RANGE,

    val eagerAvailabilityCheck: Boolean =
        DEFAULT_EAGER_AVAILABILITY_CHECK,
) {
    lateinit var badStatusCodeIntRanges: Set<IntRange>

    @PostConstruct
    private fun initBadStatusCodeRanges() {
        badStatusCodeIntRanges =
            StringUtils.commaDelimitedListToSet(badStatusCodeRanges)
                .mapToSet {
                    it.parseIntRange()
                }
    }
}
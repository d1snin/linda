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

package dev.d1s.linda.testConfiguration

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@TestConfiguration
internal class LocalValidatorFactoryBeanConfiguration {

    // the SHITTIEST way to disable JSR 380 constraints. Excluding ValidationAutoConfiguration does not work.
    // see: https://www.jvt.me/posts/2020/05/18/disable-valid-annotation-spring-test/
    @Bean
    fun validator() =
        mockk<LocalValidatorFactoryBean>(relaxed = true)
}
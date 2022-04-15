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

package dev.d1s.linda.service.impl

import dev.d1s.linda.domain.Redirect
import dev.d1s.linda.domain.utm.UtmParameter
import dev.d1s.linda.exception.impl.notFound.RedirectNotFoundException
import dev.d1s.linda.repository.RedirectRepository
import dev.d1s.linda.service.RedirectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RedirectServiceImpl : RedirectService {

    @Autowired
    private lateinit var redirectRepository: RedirectRepository

    @Lazy
    @Autowired
    private lateinit var redirectService: RedirectServiceImpl

    @Transactional(readOnly = true)
    override fun findAll(): Set<Redirect> =
        redirectRepository.findAll().toSet()

    @Transactional(readOnly = true)
    override fun findById(id: String): Redirect =
        redirectRepository.findById(id).orElseThrow {
            RedirectNotFoundException
        }

    @Transactional
    override fun create(redirect: Redirect): Redirect =
        redirectRepository.save(redirect)

    @Transactional
    override fun update(id: String, redirect: Redirect): Redirect {
        val foundRedirect = redirectService.findById(id)

        foundRedirect.shortLink = redirect.shortLink
        foundRedirect.utmParameters = redirect.utmParameters

        return redirectRepository.save(foundRedirect)
    }

    @Transactional
    override fun assignUtmParameter(redirect: Redirect, utmParameter: UtmParameter) {
        redirect.utmParameters.add(utmParameter)
        utmParameter.redirects.add(redirect)
        redirectRepository.save(redirect)
    }

    @Transactional
    override fun removeById(id: String) =
        redirectRepository.deleteById(id)
}
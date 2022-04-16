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
import kotlin.properties.Delegates

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
    override fun create(redirect: Redirect): Redirect {
        var result: Redirect by Delegates.notNull()

        redirect.utmParameters.forEach {
            result = redirectService.assignUtmParameterAndSave(redirect, it)
        }

        return result
    }

    @Transactional
    override fun update(id: String, redirect: Redirect): Redirect {
        var foundRedirect = redirectService.findById(id)

        foundRedirect.shortLink = redirect.shortLink
        foundRedirect.utmParameters = redirect.utmParameters

        foundRedirect.utmParameters.forEach {
            foundRedirect = redirectService.assignUtmParameterAndSave(foundRedirect, it)
        }

        return foundRedirect
    }

    @Transactional
    override fun assignUtmParameterAndSave(redirect: Redirect, utmParameter: UtmParameter): Redirect {
        redirect.utmParameters.add(utmParameter)
        utmParameter.redirects.add(redirect)
        return redirectRepository.save(redirect)
    }

    @Transactional
    override fun removeById(id: String) =
        redirectRepository.deleteById(id)
}
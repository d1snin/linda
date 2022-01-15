package uno.d1s.linda.service

import uno.d1s.linda.domain.Redirect
import uno.d1s.linda.domain.ShortLink
import uno.d1s.linda.dto.redirect.BulkRedirectRemovalDto
import uno.d1s.linda.strategy.shortLink.ShortLinkFindingStrategy

interface RedirectService {

    fun findAll(): List<Redirect>

    fun findAllByShortLink(shortLinkFindingStrategy: ShortLinkFindingStrategy): List<Redirect>

    fun findById(id: String): Redirect

    fun create(shortLink: ShortLink): Redirect

    fun create(shortLinkFindingStrategy: ShortLinkFindingStrategy): Redirect

    fun remove(redirect: Redirect): Redirect

    fun remove(id: String): Redirect

    fun removeAll(redirects: List<Redirect>): List<Redirect>

    fun removeAll(bulkRedirectRemovalDto: BulkRedirectRemovalDto): List<Redirect>

    fun removeAll(): List<Redirect>

    fun removeAllByShortLink(shortLinkFindingStrategy: ShortLinkFindingStrategy): List<Redirect>
}
package uno.d1s.linda.cache.idProvider

import org.springframework.stereotype.Component
import uno.d1s.caching.provider.IdProvider
import uno.d1s.linda.domain.ShortLink

@Component
class ShortLinkIdProvider : IdProvider {

    override fun getId(any: Any): String =
        (any as ShortLink).id!!
}
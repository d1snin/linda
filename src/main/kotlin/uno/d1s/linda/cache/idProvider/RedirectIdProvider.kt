package uno.d1s.linda.cache.idProvider

import org.springframework.stereotype.Component
import uno.d1s.caching.provider.IdProvider
import uno.d1s.linda.domain.Redirect

@Component
class RedirectIdProvider : IdProvider {

    override fun getId(any: Any): String =
        (any as Redirect).id!!
}
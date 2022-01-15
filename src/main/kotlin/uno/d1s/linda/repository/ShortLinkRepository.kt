package uno.d1s.linda.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uno.d1s.linda.domain.ShortLink
import java.util.*

@Repository
interface ShortLinkRepository : JpaRepository<ShortLink, String> {

    fun findShortLinkByAliasEquals(alias: String): Optional<ShortLink>

    fun findShortLinkByUrlEquals(url: String): Optional<ShortLink>
}
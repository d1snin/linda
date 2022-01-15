package uno.d1s.linda.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uno.d1s.linda.domain.Redirect
import java.util.*

@Repository
interface RedirectRepository : JpaRepository<Redirect, String> {

    fun findAllByShortLinkIdEquals(shortLinkId: String): Optional<List<Redirect>>

    fun findAllByShortLinkAliasEquals(shortLinkAlias: String): Optional<List<Redirect>>

    fun findAllByShortLinkUrlEquals(shortLinkUrl: String): Optional<List<Redirect>>
}
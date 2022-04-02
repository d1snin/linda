package uno.d1s.linda.generator.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uno.d1s.linda.generator.AliasGenerator
import uno.d1s.linda.service.ShortLinkService
import java.util.concurrent.ThreadLocalRandom

@Component
class RandomCharSequenceAliasGenerator : AliasGenerator {

    override val identity = "random-char-sequence"

    companion object {
        const val INITIAL_LENGTH = 4
    }

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    override fun generateAlias(): String {
        var length = INITIAL_LENGTH
        var aliasCandidate = this.getRandomCharSequence(length)

        while (true) {
            if (shortLinkService.doesAliasExist(aliasCandidate)) {
                aliasCandidate = this.getRandomCharSequence(++length)
            } else {
                return aliasCandidate
            }
        }
    }

    private fun getRandomCharSequence(length: Int) = buildString {
        // thread safe random
        val rnd = ThreadLocalRandom.current()

        for (i in 0 until length) {
            // a-z
            val randomChar = 'a' + rnd.nextInt(26)

            // 1-9
            val randomInt = rnd.nextInt(10)

            if (rnd.nextBoolean()) {
                append(randomInt)
            } else {
                append(randomChar.let {
                    if (rnd.nextBoolean()) {
                        it.uppercaseChar()
                    } else {
                        it
                    }
                })
            }
        }
    }
}
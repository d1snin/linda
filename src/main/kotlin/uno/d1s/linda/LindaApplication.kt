package uno.d1s.linda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LindaApplication

fun main(args: Array<String>) {
    runApplication<LindaApplication>(*args)
}
package uno.d1s.linda.exception.impl

import uno.d1s.linda.exception.DomainNotFoundException

object AliasGeneratorNotFoundException :
    DomainNotFoundException("The requested generator was not found.")
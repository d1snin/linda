package uno.d1s.linda.exception.impl

import uno.d1s.linda.exception.DomainNotFoundException

object ShortLinkNotFoundException :
    DomainNotFoundException("The requested short link was not found.")
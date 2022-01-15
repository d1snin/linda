package uno.d1s.linda.exception.impl

import uno.d1s.linda.exception.DomainNotFoundException

object RedirectNotFoundException :
    DomainNotFoundException("The requested redirect was not found.")
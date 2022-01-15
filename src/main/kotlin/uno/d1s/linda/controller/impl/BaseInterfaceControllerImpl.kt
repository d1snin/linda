package uno.d1s.linda.controller.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.view.RedirectView
import uno.d1s.linda.controller.BaseInterfaceController
import uno.d1s.linda.service.RedirectService
import uno.d1s.linda.service.ShortLinkService
import uno.d1s.linda.strategy.shortLink.byAlias

@Controller
class BaseInterfaceControllerImpl : BaseInterfaceController {

    @Autowired
    private lateinit var shortLinkService: ShortLinkService

    @Autowired
    private lateinit var redirectService: RedirectService

    override fun redirect(alias: String): RedirectView {
        val shortLink = shortLinkService.find(byAlias(alias))
        redirectService.create(shortLink)
        return RedirectView(shortLink.url)
    }
}
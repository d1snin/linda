package uno.d1s.linda.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.view.RedirectView
import uno.d1s.linda.constant.mapping.BASE_INTERFACE_MAPPING
import javax.validation.constraints.NotBlank

@Validated
interface BaseInterfaceController {

    @GetMapping(BASE_INTERFACE_MAPPING)
    fun redirect(@PathVariable @NotBlank alias: String): RedirectView
}
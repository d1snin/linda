package uno.d1s.linda.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("linda.base")
class BaseInterfaceConfigurationProperties(
    var enabled: Boolean = true
)
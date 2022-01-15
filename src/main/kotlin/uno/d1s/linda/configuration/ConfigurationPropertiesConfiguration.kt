package uno.d1s.linda.configuration

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackages = ["uno.d1s.linda.configuration.properties"])
class ConfigurationPropertiesConfiguration
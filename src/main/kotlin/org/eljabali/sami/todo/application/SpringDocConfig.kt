package org.eljabali.sami.todo.application

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@io.swagger.v3.oas.annotations.security.SecurityScheme(
    name = "basicScheme",
    type = SecuritySchemeType.HTTP,
    scheme = "basic",
)
class SpringDocConfig {

    @Bean
    fun openApi(appProperties: AppProperties): OpenAPI =
        OpenAPI().info(Info().title("TodoList API").version(appProperties.version))
}

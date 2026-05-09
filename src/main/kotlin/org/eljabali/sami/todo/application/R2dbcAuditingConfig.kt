package org.eljabali.sami.todo.application

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import reactor.core.publisher.Mono

@Configuration
@EnableR2dbcAuditing
class R2dbcAuditingConfig {
    @Bean
    fun auditorAware(): ReactiveAuditorAware<String> =
        ReactiveAuditorAware {
            ReactiveSecurityContextHolder
                .getContext()
                .flatMap { ctx ->
                    val auth = ctx.authentication
                    if (auth != null && auth.isAuthenticated) {
                        Mono.just(auth.name)
                    } else {
                        Mono.empty()
                    }
                }
        }
}

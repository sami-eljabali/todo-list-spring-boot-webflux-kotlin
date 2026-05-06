package org.eljabali.sami.todo.application

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun corsConfigurationSource(appProperties: AppProperties): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = appProperties.allowedOriginUrls?.split(",")
            allowedMethods = listOf("GET", "POST")
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        val user = User.withDefaultPasswordEncoder()
        val users = listOf<UserDetails>(
            user.username("user").password("password").roles("USER").build(),
            user.username("admin").password("password").roles("USER", "ADMIN").build(),
        )
        return MapReactiveUserDetailsService(users)
    }

    @Bean
    fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http {
            csrf { disable() }
            httpBasic { }
            authorizeExchange {
                authorize(pathMatchers(HttpMethod.GET, "/v1/todos/**"), permitAll)
                authorize(pathMatchers(HttpMethod.DELETE, "/v1/todos/**"), hasRole("ADMIN"))
                authorize("/v1/todos/**", authenticated)
                authorize(anyExchange, permitAll)
            }
        }
}

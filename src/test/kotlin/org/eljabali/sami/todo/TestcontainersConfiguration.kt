package org.eljabali.sami.todo

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer

/**
 * Serves the PostgreSQL database used by the tests.
 *
 * The [ServiceConnection] annotation lets Spring Boot derive the `spring.r2dbc.*` settings from
 * the running container, so the tests do not need any `@DynamicPropertySource` plumbing.
 * The schema is applied by the container itself, before the application connects.
 */
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer = PostgreSQLContainer("postgres:16")
}

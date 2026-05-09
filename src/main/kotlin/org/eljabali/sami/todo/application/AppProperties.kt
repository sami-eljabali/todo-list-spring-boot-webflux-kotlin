package org.eljabali.sami.todo.application

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val allowedOriginUrls: String? = "http://localhost:3000",
    val version: String = "1.0",
    val baseUrl: String = "http://localhost:8080",
)

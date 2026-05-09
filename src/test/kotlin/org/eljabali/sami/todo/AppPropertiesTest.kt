package org.eljabali.sami.todo

import org.assertj.core.api.Assertions.assertThat
import org.eljabali.sami.todo.application.AppProperties
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Configuration

class AppPropertiesTest {
    companion object {
        @Configuration
        @EnableConfigurationProperties(value = [AppProperties::class])
        class TestConfig
    }

    private val contextRunner = ApplicationContextRunner()

    @Test
    fun testAppProperties() {
        contextRunner
            .withUserConfiguration(TestConfig::class.java)
            .run {
                val bean: AppProperties = it.getBean(AppProperties::class.java)
                assertThat(bean.baseUrl).isEqualTo("http://localhost:8080")
            }
    }

    @Test
    fun testAppPropertiesWithCustomProperties() {
        contextRunner
            .withUserConfiguration(TestConfig::class.java)
            .withPropertyValues("app.baseUrl=http://test")
            .run {
                val bean: AppProperties = it.getBean(AppProperties::class.java)
                assertThat(bean.baseUrl).isEqualTo("http://test")
            }
    }
}

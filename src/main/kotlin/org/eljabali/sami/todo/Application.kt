package org.eljabali.sami.todo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
@ConfigurationPropertiesScan
@PropertySource(value = ["classpath:git.properties"], ignoreResourceNotFound = true)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

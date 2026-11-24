package org.eljabali.sami.todo

import org.springframework.boot.fromApplication
import org.springframework.boot.with

/**
 * Starts the whole application with a Testcontainers backed PostgreSQL, so it can be launched from
 * the IDE without a local database installation.
 */
fun main(args: Array<String>) {
    fromApplication<Application>().with(TestcontainersConfiguration::class).run(*args)
}

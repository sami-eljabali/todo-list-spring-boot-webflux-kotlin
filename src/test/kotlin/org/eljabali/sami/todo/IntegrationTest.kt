package org.eljabali.sami.todo

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.eljabali.sami.todo.domain.model.Status
import org.eljabali.sami.todo.domain.model.Todo
import org.eljabali.sami.todo.interfaces.Uris
import org.eljabali.sami.todo.shared.model.CreateTodoCommand
import org.eljabali.sami.todo.shared.model.UpdateStatusCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.kotlin.test.test
import java.net.URI

/**
 * End to end test of the running application: a real HTTP server, a real PostgreSQL database
 * served by Testcontainers and the real HTTP basic authentication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
class IntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    private lateinit var client: WebClient
    private lateinit var adminClient: WebClient

    @BeforeEach
    fun setup() {
        val baseUrl = "http://localhost:$port"
        client =
            WebClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeaders { it.setBasicAuth("user", "password") }
                .build()
        adminClient =
            WebClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeaders { it.setBasicAuth("admin", "password") }
                .build()
    }

    @Test
    fun `loads the seeded todos`() {
        client
            .get()
            .uri(Uris.TODOS)
            .retrieve()
            .bodyToFlux(Todo::class.java)
            .test()
            .consumeNextWith { it.title shouldContain "Kotlin Coroutines Example" }
            .verifyComplete()
    }

    @Test
    fun `creates, reads, updates and deletes a todo`() {
        var location: URI? = null

        client
            .post()
            .uri(Uris.TODOS)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(CreateTodoCommand(title = "test todo"))
            .retrieve()
            .toBodilessEntity()
            .test()
            .consumeNextWith {
                it.headers.location shouldNotBe null
                location = it.headers.location
            }.verifyComplete()

        val path = location!!.path
        path shouldStartWith Uris.TODOS

        client
            .get()
            .uri(path)
            .retrieve()
            .bodyToMono(Todo::class.java)
            .test()
            .consumeNextWith {
                it.title shouldBe "test todo"
                it.status shouldBe Status.TODO
                it.createdAt.shouldNotBeNull()
                it.createdBy shouldBe "user"
            }.verifyComplete()

        client
            .put()
            .uri("$path/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(UpdateStatusCommand(status = Status.WORK_IN_PROGRESS))
            .retrieve()
            .toBodilessEntity()
            .test()
            .expectNextMatches { it.statusCode.value() == 204 }
            .verifyComplete()

        client
            .get()
            .uri(path)
            .retrieve()
            .bodyToMono(Todo::class.java)
            .test()
            .consumeNextWith { it.status shouldBe Status.WORK_IN_PROGRESS }
            .verifyComplete()

        // Deleting a todo requires the ADMIN role.
        client
            .delete()
            .uri(path)
            .retrieve()
            .toBodilessEntity()
            .test()
            .expectErrorMatches {
                // WebClient turns 4xx and 5xx responses into WebClientResponseException.
                it is WebClientResponseException && it.statusCode == HttpStatus.FORBIDDEN
            }.verify()

        adminClient
            .delete()
            .uri(path)
            .retrieve()
            .toBodilessEntity()
            .test()
            .expectNextMatches { it.statusCode.value() == 204 }
            .verifyComplete()

        client
            .get()
            .uri(path)
            .retrieve()
            .toEntity(Todo::class.java)
            .test()
            .expectErrorMatches {
                it is WebClientResponseException && it.statusCode == HttpStatus.NOT_FOUND
            }.verify()
    }
}

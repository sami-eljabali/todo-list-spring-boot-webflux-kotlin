package org.eljabali.sami.todo

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.eljabali.sami.todo.domain.model.Status
import org.eljabali.sami.todo.domain.model.Todo
import org.eljabali.sami.todo.domain.repository.TodoRepository
import org.eljabali.sami.todo.interfaces.TodoController
import org.eljabali.sami.todo.interfaces.Uris
import org.eljabali.sami.todo.shared.model.CreateTodoCommand
import org.eljabali.sami.todo.shared.model.UpdateStatusCommand
import org.eljabali.sami.todo.shared.model.UpdateTodoCommand
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.security.autoconfigure.web.reactive.ReactiveWebSecurityAutoConfiguration
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(
    controllers = [TodoController::class],
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveWebSecurityAutoConfiguration::class],
)
class TodoControllerTest {

    @Autowired
    private lateinit var client: WebTestClient

    @MockkBean
    private lateinit var todos: TodoRepository

    private val todo = Todo(id = 1L, title = "test title", status = Status.TODO)

    @Test
    fun `get all todos`() {
        every { todos.findAll() }.returns(flowOf(todo))
        client.get().uri(Uris.TODOS).exchange().expectStatus().isOk
        verify(exactly = 1) { todos.findAll() }
    }

    @Test
    fun `get single todo`() {
        coEvery { todos.findById(any<Long>()) }.returns(todo)
        client.get().uri("${Uris.TODOS}/1").exchange().expectStatus().isOk
        coVerify(exactly = 1) { todos.findById(any<Long>()) }
    }

    @Test
    fun `create a todo`() {
        coEvery { todos.save(any<Todo>()) }.returns(todo)
        client.post()
            .uri(Uris.TODOS).contentType(MediaType.APPLICATION_JSON).bodyValue(CreateTodoCommand(title = "test title"))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location("${Uris.TODOS}/1")
        coVerify(exactly = 1) { todos.save(any<Todo>()) }
    }

    @Test
    fun `update a todo`() {
        coEvery { todos.findById(any<Long>()) }.returns(todo)
        coEvery { todos.save(any<Todo>()) }.returns(todo.copy(title = "update title"))
        client.put()
            .uri("${Uris.TODOS}/1").bodyValue(UpdateTodoCommand(title = "update title"))
            .exchange()
            .expectStatus().isNoContent
        coVerify(exactly = 1) { todos.findById(any<Long>()) }
        coVerify(exactly = 1) { todos.save(any<Todo>()) }
    }

    @Test
    fun `mark a todo as completed`() {
        coEvery { todos.findById(any<Long>()) }.returns(todo)
        coEvery { todos.save(any<Todo>()) }.returns(todo.copy(status = Status.DONE))
        client.put()
            .uri("${Uris.TODOS}/1/status").bodyValue(UpdateStatusCommand(status = Status.DONE))
            .exchange()
            .expectStatus().isNoContent
        coVerify(exactly = 1) { todos.findById(any<Long>()) }
        coVerify(exactly = 1) { todos.save(any<Todo>()) }
    }

    @Test
    fun `delete a todo`() {
        coEvery { todos.findById(any<Long>()) }.returns(todo)
        coEvery { todos.delete(any<Todo>()) } just Runs
        client.delete().uri("${Uris.TODOS}/1").exchange().expectStatus().isNoContent
        coVerify(exactly = 1) { todos.findById(any<Long>()) }
        coVerify(exactly = 1) { todos.delete(any<Todo>()) }
    }

    @Test
    fun `get single todo with non-existing id`() {
        coEvery { todos.findById(any<Long>()) }.returns(null)
        client.get().uri("${Uris.TODOS}/1").exchange().expectStatus().isNotFound
        coVerify(exactly = 1) { todos.findById(any<Long>()) }
    }
}

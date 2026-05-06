package org.eljabali.sami.todo.interfaces

import kotlinx.coroutines.flow.Flow
import org.eljabali.sami.todo.domain.exception.TodoNotFoundException
import org.eljabali.sami.todo.domain.model.Todo
import org.eljabali.sami.todo.domain.repository.TodoRepository
import org.eljabali.sami.todo.shared.model.CreateTodoCommand
import org.eljabali.sami.todo.shared.model.UpdateStatusCommand
import org.eljabali.sami.todo.shared.model.UpdateTodoCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class TodoController(private val todos: TodoRepository) {

    @GetMapping(Uris.TODOS)
    fun findAll(): Flow<Todo> = todos.findAll()

    @GetMapping(Uris.TODOS_ID)
    suspend fun findOne(@PathVariable id: Long): Todo =
        todos.findById(id) ?: throw TodoNotFoundException(id)

    @PostMapping(Uris.TODOS)
    suspend fun save(@RequestBody body: CreateTodoCommand): ResponseEntity<Any> {
        val saved = todos.save(Todo(title = body.title))
        return ResponseEntity.created(URI.create("${Uris.TODOS}/${saved.id}")).build()
    }

    @PutMapping(Uris.TODOS_ID)
    suspend fun update(
        @PathVariable id: Long,
        @RequestBody body: UpdateTodoCommand,
    ): ResponseEntity<Any> {
        val todo = todos.findById(id) ?: throw TodoNotFoundException(id)
        todos.save(todo.copy(title = body.title))
        return ResponseEntity.noContent().build()
    }

    @PutMapping(Uris.TODOS_ID_STATUS)
    suspend fun updateStatus(
        @PathVariable id: Long,
        @RequestBody body: UpdateStatusCommand,
    ): ResponseEntity<Any> {
        val todo = todos.findById(id) ?: throw TodoNotFoundException(id)
        todos.save(todo.copy(status = body.status))
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping(Uris.TODOS_ID)
    suspend fun deleteById(@PathVariable id: Long): ResponseEntity<Any> {
        val todo = todos.findById(id) ?: throw TodoNotFoundException(id)
        todos.delete(todo)
        return ResponseEntity.noContent().build()
    }
}

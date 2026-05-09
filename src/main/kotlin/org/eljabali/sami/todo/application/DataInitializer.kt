package org.eljabali.sami.todo.application

import kotlinx.coroutines.runBlocking
import org.eljabali.sami.todo.domain.model.Todo
import org.eljabali.sami.todo.domain.repository.TodoRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
@Profile("!prod & !staging")
class DataInitializer(
    private val todos: TodoRepository,
) {
    @EventListener(value = [ApplicationReadyEvent::class])
    fun init() {
        runBlocking {
            todos.deleteAll()
            todos.save(Todo(title = "Kotlin Coroutines Example"))
        }
    }
}

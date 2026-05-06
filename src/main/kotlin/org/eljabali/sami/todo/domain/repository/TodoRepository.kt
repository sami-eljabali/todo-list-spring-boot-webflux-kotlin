package org.eljabali.sami.todo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.eljabali.sami.todo.domain.model.Status
import org.eljabali.sami.todo.domain.model.Todo
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TodoRepository : CoroutineCrudRepository<Todo, Long> {
    fun findByStatus(status: Status): Flow<Todo>
}

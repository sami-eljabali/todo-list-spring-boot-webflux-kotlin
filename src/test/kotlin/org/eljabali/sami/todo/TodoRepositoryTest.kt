package org.eljabali.sami.todo

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import org.eljabali.sami.todo.domain.model.Status
import org.eljabali.sami.todo.domain.model.Todo
import org.eljabali.sami.todo.domain.repository.TodoRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest
import org.springframework.context.annotation.Import

@DataR2dbcTest
@Import(TestcontainersConfiguration::class)
class TodoRepositoryTest {
    @Autowired
    lateinit var todos: TodoRepository

    @BeforeEach
    suspend fun setup() = todos.deleteAll()

    // JUnit 6 supports suspend test functions natively.
    @Test
    suspend fun testInsertAndQuery() {
        val saved = todos.save(Todo(title = "test title"))
        saved.id shouldNotBe null

        val found = todos.findById(saved.id!!)
        found shouldNotBe null
        found!!.title shouldBe "test title"
        found.status shouldBe Status.TODO

        todos.save(found.copy(title = "update title", status = Status.DONE))

        val updated = todos.findById(saved.id)
        updated!!.title shouldBe "update title"
        updated.status shouldBe Status.DONE
    }

    @Test
    suspend fun testFindByStatus() {
        todos.save(Todo(title = "done task", status = Status.DONE))
        todos.save(Todo(title = "pending task", status = Status.TODO))

        val doneTodos = todos.findByStatus(Status.DONE)

        doneTodos.count() shouldBe 1
        doneTodos.first().status shouldBe Status.DONE
    }
}

package org.eljabali.sami.todo

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.eljabali.sami.todo.domain.model.Status
import org.eljabali.sami.todo.domain.model.Todo
import org.eljabali.sami.todo.domain.repository.TodoRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile

@DataR2dbcTest
@Testcontainers
class TodoRepositoryTest {
    companion object {
        @Container
        private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:16")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("init.sql"),
                "/docker-entrypoint-initdb.d/init.sql",
            )

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}"
            }
            registry.add("spring.r2dbc.username") { postgres.username }
            registry.add("spring.r2dbc.password") { postgres.password }
        }
    }

    @Autowired
    lateinit var todos: TodoRepository

    @BeforeEach
    fun setup() = runTest {
        todos.deleteAll()
    }

    @Test
    fun testRepositoryExists() {
        assertNotNull(todos)
    }

    @Test
    fun testInsertAndQuery() = runTest {
        val saved = todos.save(Todo(title = "test title"))

        saved.id shouldNotBe null

        val found = todos.findById(saved.id!!)
        assertNotNull(found)
        found!!.title shouldBe "test title"
        found.status shouldBe Status.TODO

        todos.save(found.copy(title = "update title", status = Status.DONE))

        val updated = todos.findById(saved.id!!)
        updated!!.title shouldBe "update title"
        updated.status shouldBe Status.DONE
    }

    @Test
    fun testFindByStatus() = runTest {
        todos.save(Todo(title = "done task", status = Status.DONE))
        todos.save(Todo(title = "pending task", status = Status.TODO))

        val doneTodos = todos.findByStatus(Status.DONE)

        doneTodos.count() shouldBe 1
        doneTodos.first().status shouldBe Status.DONE
    }
}

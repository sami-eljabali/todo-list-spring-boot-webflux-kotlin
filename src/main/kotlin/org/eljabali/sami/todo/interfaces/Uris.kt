package org.eljabali.sami.todo.interfaces

object Uris {
    private const val V1 = "/v1"

    const val TODOS = "$V1/todos"
    const val TODOS_ID = "$TODOS/{id}"
    const val TODOS_ID_STATUS = "$TODOS_ID/status"
}

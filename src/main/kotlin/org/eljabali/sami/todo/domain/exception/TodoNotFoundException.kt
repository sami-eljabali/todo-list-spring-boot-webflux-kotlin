package org.eljabali.sami.todo.domain.exception

class TodoNotFoundException(id: Long) : RuntimeException("Todo #$id not found.")

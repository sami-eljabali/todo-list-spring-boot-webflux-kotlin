package org.eljabali.sami.todo.shared.model

import org.eljabali.sami.todo.domain.model.Status

data class UpdateStatusCommand(
    val status: Status,
)

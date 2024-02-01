package de.piller.techtalks.springboothtmx.todo.model

import jakarta.validation.constraints.NotBlank

data class TodoItemDTO(@NotBlank var title: String = "default")
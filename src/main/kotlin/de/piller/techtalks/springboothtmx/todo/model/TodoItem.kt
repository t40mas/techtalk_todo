package de.piller.techtalks.springboothtmx.todo.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank

@Entity
class TodoItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var title: @NotBlank String,
    var completed: Boolean = false
)
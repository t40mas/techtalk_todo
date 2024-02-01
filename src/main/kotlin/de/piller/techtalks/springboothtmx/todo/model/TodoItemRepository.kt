package de.piller.techtalks.springboothtmx.todo.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface TodoItemRepository : JpaRepository<TodoItem, Long> {
    fun countAllByCompleted(completed: Boolean): Int
    fun findAllByCompleted(completed: Boolean): List<TodoItem>
}

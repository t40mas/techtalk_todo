package de.piller.techtalks.springboothtmx.todo.controller

import de.piller.techtalks.springboothtmx.todo.model.TodoItem
import de.piller.techtalks.springboothtmx.todo.model.TodoItemDTO
import de.piller.techtalks.springboothtmx.todo.model.TodoItemRepository
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.stream.Collectors


@Controller
@RequestMapping("/")
class TodoItemController(var repository: TodoItemRepository) {

    @GetMapping
    fun index(model: Model, principal: Principal): String {
        println("used: index")
        addAttributesForIndex(model, ListFilter.ALL)
        return "index"
    }

    @GetMapping("/active")
    fun indexActive(model: Model): String {
        println("used: indexActice")
        addAttributesForIndex(model, ListFilter.ACTIVE)
        return "index"
    }

    @GetMapping("/completed")
    fun indexCompleted(model: Model): String {
        println("used: indexCompleted")
        addAttributesForIndex(model, ListFilter.COMPLETED)
        return "index"
    }

    @PostMapping
    fun addNewTodoItem(@Valid @ModelAttribute("item") formData: TodoItemDTO): String {
        println("used: addNewTodoItem")
        repository.save(TodoItem(title = formData.title))
        return "redirect:/"
    }

    @PutMapping("/{id}/toggle")
    fun toggleSelection(@PathVariable("id") id: Long): String {
        println("used: toggleSelection")
        val todoItem = repository.findById(id).orElseThrow { RuntimeException("No item found for id: $id") }
        todoItem.completed = !todoItem.completed
        repository.save(todoItem)
        return "redirect:/"
    }

    @PutMapping("/toggle-all")
    fun toggleAll(): String {
        println("used: toggleAll")
        val todoItems = repository.findAll()
        for (todoItem in todoItems) {
            todoItem.completed = !todoItem.completed
            repository.save(todoItem)
        }
        return "redirect:/"
    }

    @DeleteMapping("/{id}")
    fun deleteTodoItem(@PathVariable("id") id: Long): String {
        println("used: deleteTodoItem")
        repository.deleteById(id)
        return "redirect:/"
    }

    @DeleteMapping("/completed")
    fun deleteCompletedItems(): String {
        println("used: deleteCompletedItems")
        val items = repository.findAllByCompleted(true)
        for (item in items) {
            item.id?.let { repository.deleteById(it) }
        }
        return "redirect:/"
    }

    @PostMapping(headers = ["HX-Request"])
    fun htmxAddTodoItem(formData: TodoItemDTO, model: Model, response: HttpServletResponse): String {
        println("used: htmxAddTodoItem")
        val item = repository.save(TodoItem(null, formData.title))
        model.addAttribute("item", toDto(item))
        response.setHeader("HX-Trigger", "itemAdded")
        return "fragments :: todoItem"
    }

    @PutMapping(value = ["/{id}/toggle"], headers = ["HX-Request"])
    fun htmxToggleTodoItem(@PathVariable("id") id: Long, model: Model, response: HttpServletResponse): String {
        println("used: htmxToggleTodoItem")
        val todoItem = repository.findById(id).orElseThrow { RuntimeException("No item found for id: $id") }
        todoItem.completed = !todoItem.completed
        repository.save(todoItem)
        model.addAttribute("item", toDto(todoItem))
        response.setHeader("HX-Trigger", "itemCompletionToggled")
        return "fragments :: todoItem"
    }

    @DeleteMapping(value = ["/{id}"], headers = ["HX-Request"])
    @ResponseBody
    fun htmxDeleteTodoItem(
        @PathVariable("id") id: Long, response: HttpServletResponse
    ): String {
        println("used: htmxDeleteTodoItem")
        repository.deleteById(id)
        response.setHeader("HX-Trigger", "itemDeleted")
        return ""
    }

    @GetMapping(value = ["/active-items-count"], headers = ["HX-Request"])
    fun htmxActiveItemsCount(model: Model): String {
        println("used: htmxActiveItemsCount")
        model.addAttribute("numberOfActiveItems", getNumberOfActiveItems())
        return "fragments :: active-items-count"
    }

    private fun addAttributesForIndex(
        model: Model, listFilter: ListFilter
    ) {
        model.addAttribute("item", TodoItemDTO())
        model.addAttribute("filter", listFilter)
        model.addAttribute("todos", getTodoItems(listFilter))
        model.addAttribute("totalNumberOfItems", repository.count())
        model.addAttribute("numberOfActiveItems", getNumberOfActiveItems())
        model.addAttribute("numberOfCompletedItems", getNumberOfCompletedItems())
    }

    private fun getTodoItems(filter: ListFilter): List<TodoItemDto> {
        return when (filter) {
            ListFilter.ALL -> convertToDto(repository.findAll())
            ListFilter.ACTIVE -> convertToDto(repository.findAllByCompleted(false))
            ListFilter.COMPLETED -> convertToDto(repository.findAllByCompleted(true))
        }
    }

    private fun convertToDto(todoItems: List<TodoItem>): List<TodoItemDto> {
        return todoItems.stream().map { todoItem: TodoItem -> this.toDto(todoItem) }.collect(Collectors.toList())
    }

    private fun toDto(todoItem: TodoItem): TodoItemDto {
        return TodoItemDto(todoItem.id, todoItem.title, todoItem.completed)
    }

    private fun getNumberOfActiveItems(): Int {
        return repository.countAllByCompleted(false)
    }

    private fun getNumberOfCompletedItems(): Int {
        return repository.countAllByCompleted(true)
    }

    data class TodoItemDto(val id: Long?, var title: String, var completed: Boolean)

    enum class ListFilter {
        ALL, ACTIVE, COMPLETED
    }

}
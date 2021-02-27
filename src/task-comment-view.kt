package model.view

import model.Employee
import model.TaskComment

fun taskComment(author: Employee, text: String): TaskComment =
    object : TaskComment {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val author: Employee = author
        override val text: String = text
    }
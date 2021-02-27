package model.view

import common.TaskStatus
import model.Employee
import model.Task
import model.TaskComment

fun task(
    name: String,
    description: String,
    assignedEmployee: Employee?,
    status: TaskStatus,
    comments: MutableList<TaskComment>
): Task = object : Task {
    override val id: Long get() = throw IllegalStateException("Can't request id from a view")

    override val name: String = name
    override val description: String = description
    override var assignedEmployee: Employee? = assignedEmployee
    override var status: TaskStatus = status
    override val comments: MutableList<TaskComment> = comments
}
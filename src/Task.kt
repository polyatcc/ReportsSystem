package model

import common.Entity
import common.TaskStatus

interface Task: Entity {
    val name: String
    val description: String
    var assignedEmployee: Employee?
    var status: TaskStatus
    val comments: MutableList<TaskComment>
}
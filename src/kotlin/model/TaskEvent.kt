package model

import common.Entity
import common.TaskStatus
import java.time.Instant

interface TaskEvent : Entity {
    val task: Task
    val initiator: Employee
    val timestamp: Instant
}

interface TaskCreatedEvent : TaskEvent

interface StatusChangedEvent : TaskEvent {
    val newStatus: TaskStatus
}

interface EmployeeAssignedEvent : TaskEvent {
    val assignedEmployee: Employee
}

interface CommentAddedEvent : TaskEvent {
    val comment: TaskComment
}
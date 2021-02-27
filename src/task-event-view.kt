package model.view

import common.TaskStatus
import model.*
import java.time.Instant

fun taskCreatedEvent(
    task: Task,
    initiator: Employee,
    timestamp: Instant
): TaskCreatedEvent =
    object : TaskCreatedEvent {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val task: Task = task
        override val initiator: Employee = initiator
        override val timestamp: Instant = timestamp
    }

fun statusChangedEvent(
    task: Task,
    initiator: Employee,
    timestamp: Instant,
    newStatus: TaskStatus
): StatusChangedEvent =
    object : StatusChangedEvent {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val task: Task = task
        override val initiator: Employee = initiator
        override val timestamp: Instant = timestamp
        override val newStatus: TaskStatus = newStatus
    }

fun employeeAssignedEvent(
    task: Task,
    initiator: Employee,
    timestamp: Instant,
    assignedEmployee: Employee
): EmployeeAssignedEvent =
    object : EmployeeAssignedEvent {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val task: Task = task
        override val initiator: Employee = initiator
        override val timestamp: Instant = timestamp
        override val assignedEmployee: Employee = assignedEmployee
    }

fun commentAddedEvent(
    task: Task,
    initiator: Employee,
    timestamp: Instant,
    comment: TaskComment
): CommentAddedEvent =
    object : CommentAddedEvent {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val task: Task = task
        override val initiator: Employee = initiator
        override val timestamp: Instant = timestamp
        override val comment: TaskComment = comment
    }
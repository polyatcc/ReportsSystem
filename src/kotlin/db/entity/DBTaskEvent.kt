package db.entity

import common.TaskStatus
import common.Entity
import java.time.Instant

sealed class DBTaskEvent(
    override val id: Long,
    val taskId: Long,
    val initiatorId: Long,
    val timestamp: Instant
) : Entity

class DBTaskCreatedEvent(
    id: Long,
    taskId: Long,
    initiatorId: Long,
    timestamp: Instant
) : DBTaskEvent(id, taskId, initiatorId, timestamp)

class DBStatusChangedEvent(
    id: Long,
    taskId: Long,
    initiatorId: Long,
    timestamp: Instant,
    val newStatus: TaskStatus
) : DBTaskEvent(id, taskId, initiatorId, timestamp)

class DBEmployeeAssignedEvent(
    id: Long,
    taskId: Long,
    initiatorId: Long,
    timestamp: Instant,
    val assignedEmployeeId: Long
) : DBTaskEvent(id, taskId, initiatorId, timestamp)

class DBCommentAddedEvent(
    id: Long,
    taskId: Long,
    initiatorId: Long,
    timestamp: Instant,
    val commentId: Long
) : DBTaskEvent(id, taskId, initiatorId, timestamp)
package dao

import common.TaskStatus
import dao.util.EntityListProxy
import db.DB
import db.entity.*
import model.*
import java.time.Instant

object TasksDAO : DAO<DBTask, Task> {

    private var freeId: Long = 0L
    private var freeCommentId: Long = 0L
    private var freeEventId: Long = 0L

    private fun toTaskComment(entity: DBTaskComment): TaskComment = object : TaskComment {
        override val id: Long = entity.id
        override val author: Employee
            get() = EmployeesDAO[entity.authorId]
        override val text: String = entity.text
    }

    override operator fun get(id: Long): Task {
        val entity = DB.Tasks.getById(id)
        return object : Task {
            override val id: Long = entity.id
            override val name: String = entity.name
            override val description: String = entity.description
            override var assignedEmployee: Employee?
                get() = entity.assignedEmployeeId?.let(EmployeesDAO::get)
                set(value) {
                    entity.assignedEmployeeId = value?.id
                }
            override var status: TaskStatus
                get() = entity.status
                set(value) {
                    entity.status = value
                }
            override val comments: MutableList<TaskComment> = EntityListProxy(entity.commentIds) {
                toTaskComment(DB.TaskComments.getById(it))
            }
        }
    }

    override fun insert(item: Task): Task {
        val id = freeId++
        DB.Tasks[id] = DBTask(
            id, item.name, item.description,
            item.assignedEmployee?.id, item.status,
            item.comments.mapTo(mutableListOf()) { it.id }
        )
        return get(id)
    }

    fun getComment(id: Long) = toTaskComment(DB.TaskComments.getById(id))

    fun insertComment(item: TaskComment): TaskComment {
        val id = freeCommentId++
        DB.TaskComments[id] = DBTaskComment(id, item.author.id, item.text)
        return getComment(id)
    }

    fun getEvent(id: Long) = when (val entity = DB.TaskEvents.getById(id)) {
        is DBTaskCreatedEvent -> object : TaskCreatedEvent {
            override val id: Long = entity.id
            override val task: Task
                get() = get(entity.taskId)
            override val initiator: Employee
                get() = EmployeesDAO[entity.initiatorId]
            override val timestamp: Instant = entity.timestamp
        }
        is DBStatusChangedEvent -> object : StatusChangedEvent {
            override val id: Long = entity.id
            override val task: Task
                get() = get(entity.taskId)
            override val initiator: Employee
                get() = EmployeesDAO[entity.initiatorId]
            override val timestamp: Instant = entity.timestamp
            override val newStatus: TaskStatus = entity.newStatus
        }
        is DBEmployeeAssignedEvent -> object : EmployeeAssignedEvent {
            override val id: Long = entity.id
            override val task: Task
                get() = get(entity.taskId)
            override val initiator: Employee
                get() = EmployeesDAO[entity.initiatorId]
            override val timestamp: Instant = entity.timestamp
            override val assignedEmployee: Employee
                get() = EmployeesDAO[entity.assignedEmployeeId]
        }
        is DBCommentAddedEvent -> object : CommentAddedEvent {
            override val id: Long = entity.id
            override val task: Task
                get() = get(entity.taskId)
            override val initiator: Employee
                get() = EmployeesDAO[entity.initiatorId]
            override val timestamp: Instant = entity.timestamp
            override val comment: TaskComment
                get() = getComment(entity.commentId)
        }
    }

    fun insertEvent(item: TaskEvent) {
        val id = freeEventId++
        DB.TaskEvents[id] = when (item) {
            is TaskCreatedEvent ->
                DBTaskCreatedEvent(id, item.task.id, item.initiator.id, item.timestamp)
            is StatusChangedEvent ->
                DBStatusChangedEvent(id, item.task.id, item.initiator.id, item.timestamp, item.newStatus)
            is EmployeeAssignedEvent ->
                DBEmployeeAssignedEvent(id, item.task.id, item.initiator.id, item.timestamp, item.assignedEmployee.id)
            is CommentAddedEvent ->
                DBCommentAddedEvent(id, item.task.id, item.initiator.id, item.timestamp, item.comment.id)
            else -> error("Invalid branch")
        }
    }

    override fun access(item: Task): DBTask {
        return DB.Tasks.getById(item.id)
    }

    fun accessEvent(item: TaskEvent): DBTaskEvent {
        return DB.TaskEvents.getById(item.id)
    }

}
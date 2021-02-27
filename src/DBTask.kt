package db.entity

import common.Entity
import common.TaskStatus

class DBTask(
    override val id: Long,
    val name: String,
    val description: String,
    var assignedEmployeeId: Long?,
    var status: TaskStatus,
    val commentIds: MutableList<Long>
): Entity
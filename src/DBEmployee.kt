package db.entity

import common.Entity

sealed class DBEmployee(
    override val id: Long,
    val name: String,
    val subordinatesIds: MutableList<Long>
) : Entity

class DBTeamLeader(
    id: Long,
    name: String,
    subordinatesIds: MutableList<Long>
) : DBEmployee(id, name, subordinatesIds)

class DBRegularEmployee(
    id: Long,
    name: String,
    subordinatesIds: MutableList<Long>,
    var supervisorId: Long
) : DBEmployee(id, name, subordinatesIds)
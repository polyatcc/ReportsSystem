package db.entity

import common.Entity

class DBTaskComment(
    override val id: Long,
    val authorId: Long,
    val text: String
) : Entity
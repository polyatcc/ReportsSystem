package db.entity

import common.Entity
import java.time.LocalDate

sealed class DBReport(
    override val id: Long,
    val date: LocalDate,
    val authorId: Long,
    var text: String
): Entity

class DBDailyReport(
    id: Long,
    date: LocalDate,
    authorId: Long,
    text: String
) : DBReport(id, date, authorId, text)

class DBSprintReport(
    id: Long,
    date: LocalDate,
    authorId: Long,
    text: String,
    var finalized: Boolean
) : DBReport(id, date, authorId, text)
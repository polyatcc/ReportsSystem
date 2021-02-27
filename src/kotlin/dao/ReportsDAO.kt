package dao

import db.DB
import db.entity.DBDailyReport
import db.entity.DBReport
import db.entity.DBSprintReport
import model.DailyReport
import model.Employee
import model.Report
import model.SprintReport
import java.time.LocalDate

object ReportsDAO : DAO<DBReport, Report> {

    private var freeId: Long = 0L

    override operator fun get(id: Long): Report = when (val entity = DB.Reports.getById(id)) {
        is DBDailyReport -> object : DailyReport {
            override val id: Long = entity.id
            override val date: LocalDate = entity.date
            override val author: Employee
                get() = EmployeesDAO[entity.authorId]
            override var text: String
                get() = entity.text
                set(value) {
                    entity.text = value
                }
        }
        is DBSprintReport -> object : SprintReport {
            override val id: Long = entity.id
            override val date: LocalDate = entity.date
            override val author: Employee
                get() = EmployeesDAO[entity.authorId]
            override var text: String
                get() = entity.text
                set(value) {
                    entity.text = value
                }
            override var finalized: Boolean
                get() = entity.finalized
                set(value) {
                    entity.finalized = value
                }
        }
    }

    override fun insert(item: Report): Report {
        val id = freeId++
        DB.Reports[id] = when (item) {
            is DailyReport -> DBDailyReport(id, item.date, item.author.id, item.text)
            is SprintReport -> DBSprintReport(id, item.date, item.author.id, item.text, item.finalized)
            else -> error("Invalid branch")
        }
        return get(id)
    }

    override fun access(item: Report): DBReport {
        return DB.Reports.getById(item.id)
    }
}
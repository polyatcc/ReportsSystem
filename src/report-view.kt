package model.view

import model.DailyReport
import model.Employee
import model.SprintReport
import java.time.LocalDate

fun dailyReport(date: LocalDate, author: Employee, text: String): DailyReport =
    object : DailyReport {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val date: LocalDate = date
        override val author: Employee = author
        override var text: String = text
    }

fun sprintReport(date: LocalDate, author: Employee, text: String, finalized: Boolean): SprintReport =
    object : SprintReport {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val date: LocalDate = date
        override val author: Employee = author
        override var text: String = text
        override var finalized = finalized
    }
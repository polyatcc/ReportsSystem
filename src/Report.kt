package model

import common.Entity
import java.time.LocalDate

interface Report: Entity {
    val date: LocalDate
    val author: Employee
    var text: String
}

interface DailyReport : Report

interface SprintReport : Report {
    var finalized: Boolean
}
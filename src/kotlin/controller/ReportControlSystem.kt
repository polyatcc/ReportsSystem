package controller

import common.after
import common.before
import controller.EmployeeControlSystem.getTeam
import controller.EmployeeControlSystem.isSubordinateTo
import dao.EmployeesDAO
import dao.ReportsDAO
import db.DB
import db.entity.DBDailyReport
import db.entity.DBSprintReport
import model.*
import model.view.dailyReport
import model.view.sprintReport
import java.time.LocalDate
import java.time.ZoneId

class ReportControlSystem(
    val sprintStart: LocalDate,
    val sprintEnd: LocalDate
) {

    init {
        require(sprintEnd > sprintStart)
        EmployeeControlSystem.getAllEmployees().forEach { employee ->
            ReportsDAO.insert(sprintReport(sprintStart, employee, "", false))
        }
    }

    fun getReportById(id: Long) = ReportsDAO[id]

    fun getAllReports() =
        DB.Reports.keys
            .map(ReportsDAO::get)

    fun Employee.getDailyReportFor(day: LocalDate): DailyReport? =
        DB.Reports.values
            .filterIsInstance<DBDailyReport>()
            .firstOrNull { it.authorId == id && it.date == day }
            ?.let { ReportsDAO[it.id] as DailyReport }

    fun Employee.getSprintReport(): SprintReport =
        DB.Reports.values
            .filterIsInstance<DBSprintReport>()
            .first { it.authorId == id && it.date == sprintStart }
            .let { ReportsDAO[it.id] as SprintReport }

    fun Employee.createDailyReportFor(day: LocalDate): DailyReport {
        require(day in sprintStart..sprintEnd) { "Can't write reports for days not in current sprint" }
        require(day <= LocalDate.now()) { "Can't create reports for future days" }
        if (day != sprintStart) {
            require(getDailyReportFor(day.minusDays(1)) != null) {
                "You should write report for previous day first"
            }
        }
        return ReportsDAO.insert(dailyReport(day, this, "")) as DailyReport
    }

    fun Employee.editReportFor(day: LocalDate, newText: String, accessOlderDays: Boolean = false) {
        val report = getDailyReportFor(day)
        require(report != null) { "Please create a report first" }
        if (!accessOlderDays) {
            require(day == LocalDate.now()) { "Can't edit older reports without special access" }
        }
        report.text = newText
    }

    fun Employee.editSprintReport(newText: String) {
        require(LocalDate.now() in sprintStart..sprintEnd) { "Can't edit sprint reports after sprint end" }
        require(getDailyReportFor(LocalDate.now()) != null) { "Can't edit sprint report with unfinished older reports" }
        val report = getSprintReport()
        require(!report.finalized) { "Can't edit finalized sprint reports" }
        report.text = newText
    }

    fun RegularEmployee.finalizeSprintReport() {
        require(LocalDate.now() == sprintEnd) { "Can't finalize sprint reports not on it's last day" }
        require(getDailyReportFor(LocalDate.now()) != null) { "You should create reports for all days first" }
        val report = getSprintReport()
        require(!report.finalized) { "Can't finalize already finalized report" }
        report.finalized = true
    }

    fun Employee.getReportsDuringSprint(): List<Report> =
        DB.Reports.values
            .filter { it.date in sprintStart..sprintEnd && it.authorId == id }
            .map { ReportsDAO[it.id] }

    fun Employee.getSubordinatesReportsDuringSprint(): List<Report> =
        DB.Reports.values
            .filter { it.date in sprintStart..sprintEnd && EmployeesDAO[it.authorId] isSubordinateTo this }
            .map { ReportsDAO[it.id] }

    fun TeamLeader.getFinishedSprintReports(): List<Report> =
        DB.Reports.values
            .filterIsInstance<DBSprintReport>()
            .filter {
                it.date in sprintStart..sprintEnd &&
                        EmployeesDAO[it.authorId] isSubordinateTo this &&
                        it.finalized
            }
            .map { ReportsDAO[it.id] }

    fun TeamLeader.finalizeSprintReport() {
        require(getFinishedSprintReports().size == getTeam().size - 1) {
            "Can't finish sprint report until all team finishes"
        }
        val report = getSprintReport()
        require(!report.finalized) { "Can't finalize already finalized report" }
        report.finalized = true
    }

    fun Employee.getSprintTasks(): List<Task> =
        TaskControlSystem.getTasksAssignedTo(
            this,
            after(sprintStart.atStartOfDay(ZoneId.systemDefault()).toInstant()) +
                    before(sprintEnd.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        )

}
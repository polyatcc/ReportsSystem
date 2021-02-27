
import common.TaskStatus
import common.after
import common.before
import controller.*
import model.RegularEmployee
import java.time.Instant
import java.time.LocalDate

fun main() {

    EmployeeControlSystem.apply {
        val teamLeaderA = createTeamLeader("Team Leader A", mutableListOf())
        val employee1 = createRegularEmployee("Supervisor 1", teamLeaderA)
        val employee2 = createRegularEmployee("Supervisor 2", teamLeaderA)

        val employee3 = createRegularEmployee("Employee 3", employee1)
        val employee4 = createRegularEmployee("Employee 4", employee1)
        val employee5 = createRegularEmployee("Employee 5", employee2)
        createRegularEmployee("Employee 6", teamLeaderA)

        /*
        A -> 1, 2, 6
        1 -> 3, 4
        2 -> 5
         */

        employee2.changeSupervisor(employee1)
        val teamLeader1 = employee1.promoteToTeamLeader(keepSubordinates = true)

        /*
        A -> 6
        1 -> 2, 3, 4
        2 -> 5
         */

        val teamLeaderB = createTeamLeader("Team Leader B", mutableListOf(employee4))
        teamLeaderA.replaceWith(teamLeaderB)
        removeEmployee(teamLeaderA)
        removeEmployee(employee2)

        /*
        1 -> 3, 5
        B -> 4, 6
         */

        getAllEmployees().forEach { employee ->
            if (employee is RegularEmployee) {
                println("${employee.name} <- ${employee.supervisor.name}")
            } else {
                println(employee.name)
            }
            employee.subordinates.forEach { subordinate ->
                println(" -> ${subordinate.name}")
            }
        }

        println()

        TaskControlSystem.apply {
            val task1 = teamLeader1.createTask("Task 1", "Task 1 description")
            val task4 = employee4.createTask("Task 4", "Task 4 description")
            teamLeader1.assignEmployee(task1, employee3)
            employee3.writeComment(task4, "Task 4 comment 3")
            task1.changeStatus(TaskStatus.Active)

            listOf(
                { task1.changeStatus(TaskStatus.Open) },
                { task4.changeStatus(TaskStatus.Open) },
                { employee4.assignEmployee(task4, teamLeaderB) }
            ).forEachIndexed { i, it ->
                try {
                    it()
                    throw IllegalStateException("Expected an error on $i")
                } catch (e: IllegalArgumentException) {
                }
            }

            val timestamp = Instant.now()
            teamLeader1.assignEmployee(task4, employee5)
            println("Filtered by modified:")
            getTasksFilteredByModified(after(timestamp) + before(Instant.now())).forEach {
                println(it.name)
            }

            /*
            Task1 was modified between [timestamp] and [now]
             */

            println("Assigned to subordinates of 1:")
            teamLeader1.getTasksAssignedToSubordinates().forEach {
                println(it.name)
            }

            /*
            Both Task1 and Task4 are assigned to subordinates of Supervisor 1
             */
        }

        println()

        val yesterday = LocalDate.now().minusDays(1)
        val today = LocalDate.now()
        ReportControlSystem(yesterday, today).apply {
            teamLeader1.createDailyReportFor(yesterday)
            employee3.createDailyReportFor(yesterday)
            /*
            Filling data for yesterday, so using [accessOlderDays = true]
             */
            teamLeader1.editReportFor(yesterday, "Report 1 for yesterday", true)
            employee3.editReportFor(yesterday, "Report 3 for yesterday", true)
            teamLeader1.createDailyReportFor(today)
            teamLeader1.editSprintReport("Ok")

            /*
            Only Task1 was assigned to employee 3
             */

            println("Tasks assigned to 3 during sprint:")
            employee3.getSprintTasks().forEach {
                println(it.name)
            }

            listOf(
                { employee5.createDailyReportFor(today) },
                { employee3.createDailyReportFor(yesterday.minusDays(1)) },
                { employee3.editReportFor(yesterday, "Fail") },
                { employee3.finalizeSprintReport() },
                { employee5.editSprintReport("Fail") }
            ).forEachIndexed { i, it ->
                try {
                    it()
                    throw IllegalStateException("Expected error on $i")
                } catch (e: IllegalArgumentException) {
                }
            }

            employee5.createDailyReportFor(yesterday)
            employee5.editReportFor(yesterday, "Report 5 for yesterday", true)

            employee3.createDailyReportFor(today)
            employee5.createDailyReportFor(today)
            employee3.finalizeSprintReport()

            try {
                teamLeader1.finalizeSprintReport()
                throw IllegalStateException("Expected error")
            } catch (e: IllegalArgumentException) {
            }

            println("Finished reports of team leader 1's subordinates:")
            teamLeader1.getFinishedSprintReports().forEach {
                println(it.author.name)
            }

            employee5.finalizeSprintReport()
            teamLeader1.finalizeSprintReport()
        }
    }

}
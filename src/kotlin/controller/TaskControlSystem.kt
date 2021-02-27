package controller

import common.TaskStatus
import common.TimeFilter
import controller.EmployeeControlSystem.isSubordinateTo
import dao.EmployeesDAO
import dao.TasksDAO
import db.DB
import db.entity.DBTaskCreatedEvent
import model.Employee
import model.Task
import model.view.*
import java.time.Instant

object TaskControlSystem {

    private fun getCreationEvent(taskId: Long): DBTaskCreatedEvent =
        DB.TaskEvents.values
            .filterIsInstance<DBTaskCreatedEvent>()
            .first { it.taskId == taskId }

    fun getTaskById(id: Long) = TasksDAO[id]

    fun getAllTasks() =
        DB.Tasks.keys
            .map(TasksDAO::get)

    fun getTasksFilteredByCreated(timeFilter: TimeFilter): List<Task> =
        DB.TaskEvents.values
            .filterIsInstance<DBTaskCreatedEvent>()
            .filter { timeFilter(it.timestamp) }
            .map { TasksDAO[it.taskId] }

    fun getTasksFilteredByModified(timeFilter: TimeFilter): List<Task> =
        DB.TaskEvents.values
            .filter { timeFilter(it.timestamp) }
            .distinctBy { it.taskId }
            .map { TasksDAO[it.taskId] }

    fun getTasksAssignedTo(employee: Employee?, timeFilter: TimeFilter): List<Task> =
        DB.Tasks.values
            .filter { it.assignedEmployeeId == employee?.id }
            .filter { timeFilter(getCreationEvent(it.id).timestamp) }
            .map { TasksDAO[it.id] }

    fun getTasksModifiedBy(employee: Employee): List<Task> =
        DB.TaskEvents.values
            .filter { it.initiatorId == employee.id }
            .distinctBy { it.taskId }
            .map { TasksDAO[it.taskId] }

    fun Employee.getTasksAssignedToSubordinates(): List<Task> =
        DB.Tasks.values
            .filter {
                it.assignedEmployeeId
                    ?.let(EmployeesDAO::get)
                    ?.isSubordinateTo(this) ?: false
            }
            .map { TasksDAO[it.id] }

    fun Employee.createTask(name: String, description: String): Task {
        val task = TasksDAO.insert(task(name, description, null, TaskStatus.Open, mutableListOf()))
        TasksDAO.insertEvent(taskCreatedEvent(task, this, Instant.now()))
        return task
    }

    fun Employee.assignEmployee(task: Task, employee: Employee) {
        require(employee isSubordinateTo this) { "An employee can not assign a non-subordinate to tasks" }
        task.assignedEmployee = employee
        TasksDAO.insertEvent(employeeAssignedEvent(task, this, Instant.now(), employee))
    }

    fun Task.changeStatus(newStatus: TaskStatus) {
        require(newStatus != TaskStatus.Open) { "A started task can't be reopened to the initial stage" }
        require(assignedEmployee != null) { "A task can't be modified with no assigned employees" }
        status = newStatus
        TasksDAO.insertEvent(statusChangedEvent(this, assignedEmployee!!, Instant.now(), newStatus))
    }

    fun Employee.writeComment(task: Task, text: String) {
        val comment = TasksDAO.insertComment(taskComment(this, text))
        task.comments.add(comment)
    }

}
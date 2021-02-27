package controller

import dao.EmployeesDAO
import db.DB
import model.Employee
import model.RegularEmployee
import model.TeamLeader
import model.view.regularEmployee
import model.view.teamLeader

object EmployeeControlSystem {

    fun getEmployeeById(id: Long) = EmployeesDAO[id]

    fun getEmployeeByName(name: String): Employee? =
        DB.Employees.values
            .firstOrNull { it.name == name }
            ?.let { EmployeesDAO[it.id] }

    fun getAllEmployees() =
        DB.Employees.keys
            .map(EmployeesDAO::get)

    fun getAllEmployeesByName(name: String): List<Employee> =
        DB.Employees.values
            .filter { it.name == name }
            .map { EmployeesDAO[it.id] }

    fun TeamLeader.getTeam(): List<Employee> =
        DB.Employees.values
            .map { EmployeesDAO[it.id] }
            .filter { it isSubordinateTo this }

    fun createRegularEmployee(name: String, supervisor: Employee): RegularEmployee {
        val employee = EmployeesDAO.insert(regularEmployee(name, mutableListOf(), supervisor)) as RegularEmployee
        EmployeesDAO.access(supervisor).subordinatesIds.add(employee.id)
        return employee
    }

    fun createTeamLeader(name: String, subordinates: MutableList<RegularEmployee>): TeamLeader {
        val teamLeader = EmployeesDAO.insert(teamLeader(name, subordinates)) as TeamLeader
        subordinates.forEach { it.changeSupervisor(teamLeader) }
        return teamLeader
    }

    /**
     * Can be only used with newly created [employee] or a known parent.
     * Does not perform tree consistency checks.
     */
    private fun Employee.transferSubordinatesTo(employee: Employee) {
        subordinates.forEach {
            it.supervisor = employee
            EmployeesDAO.access(employee).subordinatesIds.add(it.id)
        }
        EmployeesDAO.access(this).subordinatesIds.clear()
    }

    fun removeEmployee(employee: Employee) {
        when (employee) {
            is TeamLeader ->
                require(employee.subordinates.isEmpty()) { "Can not remove a team leader with subordinates" }
            is RegularEmployee -> {
                EmployeesDAO.access(employee.supervisor).subordinatesIds.remove(employee.id)
                employee.transferSubordinatesTo(employee.supervisor)
            }
        }
        DB.Employees.remove(employee.id)
    }

    infix fun Employee.isSubordinateTo(employee: Employee): Boolean = when (this) {
        is TeamLeader -> id == employee.id
        is RegularEmployee -> id == employee.id || supervisor.isSubordinateTo(employee)
        else -> error("Invalid branch")
    }

    val Employee.isSupervisor get() = subordinates.isNotEmpty()

    fun TeamLeader.replaceWith(newTeamLeader: TeamLeader): TeamLeader {
        EmployeesDAO.access(newTeamLeader).subordinatesIds.addAll(subordinates.map { it.id })
        newTeamLeader.subordinates.forEach {
            it.supervisor = newTeamLeader
        }
        EmployeesDAO.access(this).subordinatesIds.clear()
        return this
    }

    fun RegularEmployee.changeSupervisor(newSupervisor: Employee) {
        require(!(newSupervisor isSubordinateTo this)) { "An employee can't be supervised by superior" }
        EmployeesDAO.access(supervisor).subordinatesIds.remove(id)
        supervisor = newSupervisor
        EmployeesDAO.access(supervisor).subordinatesIds.add(id)
    }

    fun TeamLeader.demoteToRegularEmployee(supervisor: Employee): RegularEmployee {
        val regularEmployee = createRegularEmployee(name, supervisor)
        transferSubordinatesTo(regularEmployee)
        removeEmployee(this)
        return regularEmployee
    }

    fun RegularEmployee.promoteToTeamLeader(keepSubordinates: Boolean = false): TeamLeader {
        val teamLeader = createTeamLeader(name, mutableListOf())
        if (!keepSubordinates) {
            removeEmployee(this)
        } else {
            EmployeesDAO.access(supervisor).subordinatesIds.remove(id)
            transferSubordinatesTo(teamLeader)
            DB.Employees.remove(id)
        }
        return teamLeader
    }

}
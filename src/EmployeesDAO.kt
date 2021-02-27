package dao

import dao.util.EntityListProxy
import db.DB
import db.entity.DBEmployee
import db.entity.DBRegularEmployee
import db.entity.DBTeamLeader
import model.Employee
import model.RegularEmployee
import model.TeamLeader

object EmployeesDAO : DAO<DBEmployee, Employee> {

    private var freeId: Long = 0L

    override operator fun get(id: Long): Employee = when (val entity = DB.Employees.getById(id)) {
        is DBTeamLeader -> object : TeamLeader {
            override val id: Long = entity.id
            override val name: String = entity.name
            override val subordinates: MutableList<RegularEmployee> = EntityListProxy(entity.subordinatesIds) {
                EmployeesDAO[it] as RegularEmployee
            }
        }
        is DBRegularEmployee -> object : RegularEmployee {
            override val id: Long = entity.id
            override val name: String = entity.name
            override val subordinates: MutableList<RegularEmployee> = EntityListProxy(entity.subordinatesIds) {
                EmployeesDAO[it] as RegularEmployee
            }
            override var supervisor: Employee
                get() = EmployeesDAO[entity.supervisorId]
                set(value) {
                    entity.supervisorId = value.id
                }
        }
    }

    override fun insert(item: Employee): Employee {
        val id = freeId++
        DB.Employees[id] = when (item) {
            is TeamLeader -> DBTeamLeader(id, item.name, mutableListOf())
            is RegularEmployee -> DBRegularEmployee(id, item.name, mutableListOf(), item.supervisor.id)
            else -> error("Invalid branch")
        }
        return get(id)
    }

    override fun access(item: Employee): DBEmployee {
        return DB.Employees.getById(item.id)
    }

}
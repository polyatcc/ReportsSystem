package model.view

import model.Employee
import model.RegularEmployee
import model.TeamLeader

fun teamLeader(name: String, subordinates: MutableList<RegularEmployee>) =
    object : TeamLeader {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val name: String = name
        override val subordinates: MutableList<RegularEmployee> = subordinates
    }

fun regularEmployee(name: String, subordinates: MutableList<RegularEmployee>, supervisor: Employee) =
    object : RegularEmployee {
        override val id: Long get() = throw IllegalStateException("Can't request id from a view")

        override val name: String = name
        override val subordinates: MutableList<RegularEmployee> = subordinates
        override var supervisor: Employee = supervisor
    }
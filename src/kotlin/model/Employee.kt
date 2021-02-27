package model

import common.Entity

interface Employee : Entity {
    val name: String
    val subordinates: MutableList<RegularEmployee>
}

interface TeamLeader : Employee

interface RegularEmployee : Employee {
    var supervisor: Employee
}
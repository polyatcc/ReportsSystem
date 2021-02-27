package model

import common.Entity

interface TaskComment : Entity {
    val author: Employee
    val text: String
}
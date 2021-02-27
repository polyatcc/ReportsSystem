package db

import db.entity.*

object DB {

    object Employees : DBMap<DBEmployee>("Employees")

    object Tasks : DBMap<DBTask>("Tasks")
    object TaskComments : DBMap<DBTaskComment>("TaskComments")
    object TaskEvents : DBMap<DBTaskEvent>("TaskEvents")

    object Reports : DBMap<DBReport>("Reports")

    class NotFoundException(map: String, id: Long) :
        IllegalArgumentException("id $id not found in map '$map'")

}
package dao

interface DAO<DBT: Any, T : Any> {

    operator fun get(id: Long): T
    fun insert(item: T): T

    fun access(item: T): DBT

}
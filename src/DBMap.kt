package db

import common.Entity
import java.util.*

open class DBMap<V : Entity>(private val name: String) :
    MutableMap<Long, V> by TreeMap<Long, V>() {

    fun getById(id: Long): V {
        return get(id) ?: throw DB.NotFoundException(name, id)
    }

}
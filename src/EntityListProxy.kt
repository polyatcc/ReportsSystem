package dao.util

import common.Entity

class EntityListProxy<T : Entity>(
    private val entityIds: MutableList<Long>,
    private val transform: (Long) -> T
) : AbstractMutableList<T>() {

    override fun add(index: Int, element: T) {
        entityIds.add(index, element.id)
    }

    override fun removeAt(index: Int): T {
        return transform(entityIds.removeAt(index))
    }

    override operator fun set(index: Int, element: T): T {
        return transform(entityIds.set(index, element.id))
    }

    override val size: Int
        get() = entityIds.size

    override operator fun get(index: Int): T {
        return transform(entityIds[index])
    }

}
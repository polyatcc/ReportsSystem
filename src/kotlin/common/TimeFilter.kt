package common

import java.time.Instant

abstract class TimeFilter {
    abstract operator fun invoke(timestamp: Instant): Boolean

    operator fun plus(other: TimeFilter): TimeFilter = object : TimeFilter() {
        override operator fun invoke(timestamp: Instant): Boolean {
            return this@TimeFilter(timestamp) && other(timestamp)
        }
    }
}

fun before(limit: Instant) = object : TimeFilter() {
    override fun invoke(timestamp: Instant): Boolean {
        return timestamp <= limit
    }
}

fun after(limit: Instant) = object : TimeFilter() {
    override fun invoke(timestamp: Instant): Boolean {
        return timestamp >= limit
    }
}

fun at(limit: Instant) = object : TimeFilter() {
    override fun invoke(timestamp: Instant): Boolean {
        return timestamp == limit
    }
}
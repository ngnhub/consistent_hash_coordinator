package com.github.ngnhub.redis_coordinator.service

interface ServerStorage<T> {

    fun getAll(type: Class<T>): List<T>

    operator fun set(key: String, server: T)

    operator fun minus(key: String)

    operator fun get(key: String, type: Class<T>): T?
}

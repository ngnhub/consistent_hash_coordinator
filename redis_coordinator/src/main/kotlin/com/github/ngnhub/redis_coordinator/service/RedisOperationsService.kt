package com.github.ngnhub.redis_coordinator.service

interface RedisOperationsService {

    operator fun get(key: String): String

    fun getAll(serverKey: String): List<String>

    operator fun set(key: String, value: String)

    operator fun minus(key: String)
}

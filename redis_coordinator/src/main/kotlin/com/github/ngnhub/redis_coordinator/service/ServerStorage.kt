package com.github.ngnhub.redis_coordinator.service

import com.github.ngnhub.redis_coordinator.model.RedisServerDto

interface ServerStorage {

    fun getAll(): List<RedisServerDto>

    operator fun set(key: String, server: RedisServerDto)

    operator fun minus(key: String)

    operator fun get(key: String): RedisServerDto?
}

package com.github.ngnhub.redis_coordinator.service

import com.github.ngnhub.redis_coordinator.model.RedisServerDto

/**
 * This storage is not related to the consistent hash ring.
 * Keeps servers independently, just for the convenience.
 */
interface ServerStorage {

    operator fun get(key: String): RedisServerDto?

    fun getAll(): List<RedisServerDto>

    operator fun set(key: String, server: RedisServerDto)

    operator fun minus(key: String)
}

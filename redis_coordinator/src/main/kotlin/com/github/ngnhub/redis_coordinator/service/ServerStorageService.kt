package com.github.ngnhub.redis_coordinator.service

import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.model.RedisServerDto

interface ServerStorageService {

    fun addServer(redisServerDto: RedisServerDto)

    operator fun get(key: String): RedisServer // todo nullable

    operator fun minus(key: String)

    fun isAlive(host: String, port: Int): Boolean
}

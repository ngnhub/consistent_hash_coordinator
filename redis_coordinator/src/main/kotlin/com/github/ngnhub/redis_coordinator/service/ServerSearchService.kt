package com.github.ngnhub.redis_coordinator.service

import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.model.RedisServerDto

interface ServerSearchService {

    operator fun get(key: String): RedisServer

    fun addServer(redisServerDto: RedisServerDto)

    operator fun minus(key: String)

    fun isAlive(host: String, port: Int): Boolean
}

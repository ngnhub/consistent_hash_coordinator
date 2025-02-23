package com.github.ngnhub.redis_coordinator.service

import com.github.ngnhub.redis_coordinator.model.RedisServer

interface ServerStorageService<S : RedisServer, D> {

    fun addServer(redisServerDto: D)

    operator fun get(key: String): S

    operator fun minus(key: String)

    fun isAlive(host: String, port: Int): Boolean
}
